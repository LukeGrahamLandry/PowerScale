package net.powerscale.logic;

import net.minecraft.util.Identifier;
import net.powerscale.config.Config;
import net.powerscale.config.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {
    public record LocationData(String dimensionId) {
        public boolean matches(Config.Location.Filters filters) {
            if (filters == null) {
                return true;
            }
            var result =  PatternMatching.matches(dimensionId, filters.dimension_regex);
            System.out.println("PatternMatching - dimension:" + dimensionId + " matches: " + filters.dimension_regex + " - " + result);
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
            var result = PatternMatching.matches(itemId, filters.item_id_regex)
                    && PatternMatching.matches(lootTableId, filters.loot_table_regex)
                    && PatternMatching.matches(rarity, filters.rarity_regex);
            System.out.println("PatternMatching - item:" + itemId + " matches all" + " - " + result);
            return result;
        }
    }

    public enum ItemKind {
        ARMOR, WEAPONS
    }

    public static List<Config.AttributeModifier> getModifiersForArmor(Identifier dimensionId, Identifier lootTableId, Identifier itemId, String rarity) {
        return getModifiersForItem(
                new LocationData(dimensionId.toString()),
                new ItemData(ItemKind.ARMOR, lootTableId.toString(), itemId.toString(), rarity));
    }

    public static List<Config.AttributeModifier> getModifiersForWeapon(Identifier dimensionId, Identifier lootTableId, Identifier itemId, String rarity) {
        return getModifiersForItem(
                new LocationData(dimensionId.toString()),
                new ItemData(ItemKind.WEAPONS, lootTableId.toString(), itemId.toString(), rarity));
    }


    public static List<Config.AttributeModifier> getModifiersForItem(LocationData locationData, ItemData itemData) {
        var attributeModifiers = new ArrayList<Config.AttributeModifier>();
        var locations = getLocationsMatching(locationData);
        for (var location: locations) {
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
                for(var entry: itemModifiers) {
                    if (itemData.matches(entry.filters)) {
                        attributeModifiers.addAll(Arrays.asList(entry.modifiers));
                    }
                }
            }

        }
        return attributeModifiers;
    }

    public static List<Config.Location> getLocationsMatching(LocationData locationData) {
        var dimensionConfigs = new ArrayList<Config.Location>();
        for (var entry : ConfigManager.currentConfig.locations) {
            if (locationData.matches(entry.filters)) {
                dimensionConfigs.add(entry);
            }
        }
        return dimensionConfigs;
    }

    private static boolean matches(String subject, String nullableRegex) {
        if (nullableRegex == null || nullableRegex.isEmpty()) {
            return true;
        }
        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }
}