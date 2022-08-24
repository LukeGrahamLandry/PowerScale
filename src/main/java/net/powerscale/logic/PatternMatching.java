package net.powerscale.logic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeKeys;
import net.powerscale.config.Config;
import net.powerscale.config.ConfigManager;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {

    public record LocationData(String dimensionId, @Nullable BlockPos position, @Nullable String biome) {
        public static LocationData create(World world, BlockPos position) {
            String dimensionId = world.getRegistryKey().getValue().toString();
            String biome = null;
            if (position != null) {
                biome = world.getBiome(position).getKey().orElse(BiomeKeys.PLAINS).getValue().toString();
            }
            return new LocationData(dimensionId, position, biome);
        }
        public boolean matches(Config.Dimension.Filters filters) {
            if (filters == null) {
                return true;
            }
            boolean result = PatternMatching.matches(dimensionId, filters.dimension_regex);
            // System.out.println("PatternMatching - dimension:" + dimensionId + " matches: " + filters.dimension_regex + " - " + result);
            return result;
        }

        public boolean matches(Config.Zone.Filters filters) {
            if (filters == null) {
                return true;
            }
            boolean result = PatternMatching.matches(biome, filters.biome_regex);
            // System.out.println("PatternMatching - biome:" + biome + " matches: " + filters.biome_regex + " - " + result);
            return result;
        }
    }

    public record ItemData(
            ItemKind kind,
            String lootTableId,
            String itemId,
            String rarity) {

        public boolean matches(Config.ItemModifier.Filters filters) {
            if (filters == null) {
                return true;
            }
            boolean result = PatternMatching.matches(itemId, filters.item_id_regex)
                    && PatternMatching.matches(lootTableId, filters.loot_table_regex)
                    && PatternMatching.matches(rarity, filters.rarity_regex);
            // System.out.println("PatternMatching - item:" + itemId + " matches all" + " - " + result);
            return result;
        }
    }

    public enum ItemKind {
        ARMOR, WEAPONS
    }

    public static List<Config.AttributeModifier> getModifiersForItem(LocationData locationData, ItemData itemData) {
        ArrayList<Config.AttributeModifier> attributeModifiers = new ArrayList<Config.AttributeModifier>();
        List<Location> locations = getLocationsMatching(locationData);
        for (Location location: locations) {
            if (location.rewards != null) {
                Config.ItemModifier[] itemModifiers = null;
                switch (itemData.kind) {
                    case ARMOR -> {
                        itemModifiers = location.rewards.armor;
                    }
                    case WEAPONS -> {
                        itemModifiers = location.rewards.weapons;
                    }
                }
                if (itemModifiers == null) {
                    continue;
                }
                for(Config.ItemModifier entry: itemModifiers) {
                    if (itemData.matches(entry.item_matches)) {
                        attributeModifiers.addAll(Arrays.asList(entry.attributes));
                    }
                }
            }

        }
        return attributeModifiers;
    }

    public record EntityData(String entityId, boolean isHostile) {
        public static EntityData create(LivingEntity entity) {
            String entityId = Registry.ENTITY_TYPE.getId(entity.getType()).toString();
            boolean isHostile = entity instanceof Monster;
            return new EntityData(entityId, isHostile);
        }
        public boolean matches(Config.EntityModifier.Filters filters) {
            if (filters == null) {
                return true;
            }
            boolean matchesAttitude = true;
            if (filters.attitude != null) {
                switch (filters.attitude) {
                    case FRIENDLY -> {
                        matchesAttitude = !isHostile;
                    }
                    case HOSTILE -> {
                        matchesAttitude = isHostile;
                    }
                    case ANY -> {
                        matchesAttitude = true;
                    }
                }
            }
            boolean result = matchesAttitude && PatternMatching.matches(entityId, filters.entity_id_regex);

            // System.out.println("PatternMatching - dimension:" + entityId + " matches: " + filters.entity_id_regex + " - " + result);
            return result;
        }
    }

    public static List<Config.AttributeModifier> getAttributeModifiersForEntity(LocationData locationData, EntityData entityData) {
        ArrayList<Config.AttributeModifier> attributeModifiers = new ArrayList<Config.AttributeModifier>();
        for (Config.EntityModifier modifier: getModifiersForEntity(locationData, entityData)) {
            attributeModifiers.addAll(Arrays.asList(modifier.attributes));
        }
        return attributeModifiers;
    }

    public static List<Config.SpawnerModifier> getModifiersForSpawner(LocationData locationData, EntityData entityData) {
        ArrayList<Config.SpawnerModifier> spawnerModifiers = new ArrayList<Config.SpawnerModifier>();
        for (Config.EntityModifier modifier: getModifiersForEntity(locationData, entityData)) {
            if(modifier.spawners != null) {
                spawnerModifiers.add(modifier.spawners);
            }
        }
        return spawnerModifiers;
    }

    public static List<Config.EntityModifier> getModifiersForEntity(LocationData locationData, EntityData entityData) {
        ArrayList<Config.EntityModifier> entityModifiers = new ArrayList<Config.EntityModifier>();
        List<Location> locations = getLocationsMatching(locationData);
        for (Location location : locations) {
            for(Config.EntityModifier entityModifier: location.entities) {
                if (entityData.matches(entityModifier.entity_matches)) {
                    entityModifiers.add(entityModifier);
                }
            }
        }
        return entityModifiers;
    }

    public record Location(Config.EntityModifier[] entities,
                           Config.Rewards rewards) { }

    public static List<Location> getLocationsMatching(LocationData locationData) {
        ArrayList<Location> locations = new ArrayList<Location>();
        for (Config.Dimension entry : ConfigManager.currentConfig.dimensions) {
            if (locationData.matches(entry.world_matches)) {
                locations.add(new Location(entry.entities, entry.rewards));
                if (entry.zones != null) {
                    for(Config.Zone zone: entry.zones) {
                        if(locationData.matches(zone.zone_matches)) {
                            locations.add(new Location(zone.entities, zone.rewards));
                        }
                    }
                }
            }
        }
        return locations;
    }

    private static boolean matches(String subject, String nullableRegex) {
        if (subject == null) {
            subject = "";
        }
        if (nullableRegex == null || nullableRegex.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }
}
