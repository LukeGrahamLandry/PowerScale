package net.powerscale.config;

public class Default {
    public static Config config = createDefaultConfig();

    private static Config createDefaultConfig() {
        // Surface
        Config.Dimension overworld = new Config.Dimension();
        overworld.world_matches.dimension_regex = "minecraft:overworld";

        Config.Zone cold_biomes = new Config.Zone();
        cold_biomes.zone_matches.biome_regex = "frozen|snowy|ice";
        cold_biomes.rewards.weapons = new Config.ItemModifier[] {
                createItemModifier(
                        "minecraft:bow",
                        null,
                        new Config.AttributeModifier[]{
                                createProjectileMultiplier(1.3F, 0)
                        }
                ),
        };
        cold_biomes.entities = new Config.EntityModifier[] {
                createEntityModifier(
                        "stray|skeleton",
                        new Config.AttributeModifier[]{
                                createHealthMultiplier(1.25F, 0.25F),
                                createArmorBonus(4)
                        },
                        null,
                        1.5F)
        };

        Config.Zone desert = new Config.Zone();
        desert.zone_matches.biome_regex = "desert";
        desert.rewards.weapons = new Config.ItemModifier[]{
                createItemModifier(
                        null,
                        "chests/desert_pyramid",
                        new Config.AttributeModifier[]{
                                createDamageMultiplier(1.3F, 0),
                        }
                ),
        };
        desert.entities = new Config.EntityModifier[] {
                createEntityModifier(
                        "skeleton",
                        new Config.AttributeModifier[]{
                                createHealthMultiplier(1.75F, 0.25F),
                                createArmorBonus(4)
                        },
                        null,
                        1.5F),
                createEntityModifier(
                        "husk",
                        new Config.AttributeModifier[]{
                                createDamageMultiplier(1.5F,0),
                                createHealthMultiplier(2F, 0.5F)
                        },
                        null,
                        1.5F)
        };
        overworld.zones = new Config.Zone[] { cold_biomes, desert };

        // Nether
        Config.Dimension nether = new Config.Dimension();
        nether.world_matches.dimension_regex = "minecraft:the_nether";
        nether.rewards.weapons = new Config.ItemModifier[]{
                createItemModifier(new Config.AttributeModifier[]{
                        createDamageMultiplier(1.25F, 0.25F),
                        createProjectileMultiplier(1.25F, 0.25F)
                }),
        };
        nether.rewards.armor = new Config.ItemModifier[]{
                createItemModifier(new Config.AttributeModifier[]{
                        createArmorMultiplier(1.2F),
                        createHealthBonus(2)
                }),
        };
        Config.SpawnerModifier blazeSpawners = new Config.SpawnerModifier();
        blazeSpawners = new Config.SpawnerModifier();
        blazeSpawners.min_spawn_delay_multiplier = 0.5F;
        blazeSpawners.max_spawn_delay_multiplier = 0.5F;
        blazeSpawners.spawn_count_multiplier = 2F;
        blazeSpawners.max_nearby_entities_multiplier = 3F;
        nether.entities = new Config.EntityModifier[] {
            createEntityModifier(".*",
                    new Config.AttributeModifier[]{
                        createDamageMultiplier(1.5F, 0),
                        createArmorBonus(2),
                        createHealthMultiplier(1.4F, 0.1F)
                    },
                    null,
                    2F),
            createEntityModifier("blaze",
                    new Config.AttributeModifier[]{
                        createHealthMultiplier(1.5F, 0),
                        createArmorBonus(2)
                    },
                    blazeSpawners,
                    1)
        };

        Config.Dimension end = new Config.Dimension();
        end.world_matches.dimension_regex = "minecraft:the_end";
        end.rewards.weapons = new Config.ItemModifier[]{
                createItemModifier(new Config.AttributeModifier[]{
                        createDamageMultiplier(1.75F, 0.25F),
                        createProjectileMultiplier(1.75F, 0.25F)
                }),
        };
        end.rewards.armor = new Config.ItemModifier[]{
                createItemModifier(new Config.AttributeModifier[]{
                        createArmorMultiplier(1.5F),
                        createHealthBonus(4)
                }),
        };
        end.entities = new Config.EntityModifier[] {
                createEntityModifier("^((?!dragon).)*$",
                        new Config.AttributeModifier[]{
                                createDamageMultiplier(2F, 0),
                                createArmorBonus(4),
                                createHealthMultiplier(1.8F, 0.2F)
                        },
                        null,
                        3F),
                createEntityModifier("dragon",
                        new Config.AttributeModifier[]{
                                createHealthMultiplier(2F, 0),
                                createArmorBonus(10)
                        },
                        null,
                        2F)
        };

        Config.Dimension anyDimension = new Config.Dimension();
        Config.ItemModifier epics = createItemModifier(
                new Config.AttributeModifier[]{
                        createDamageMultiplier(1.2F,0),
                        createProjectileMultiplier(1.2F, 0)
                }
        );
        epics.item_matches.rarity_regex = "epic";
        Config.ItemModifier rares = createItemModifier(
                new Config.AttributeModifier[]{
                        createDamageMultiplier(1.1F,0),
                        createProjectileMultiplier(1.1F, 0)
                }
        );
        rares.item_matches.rarity_regex = "rare";
        anyDimension.rewards.weapons = new Config.ItemModifier[] { rares, epics };

        Config config = new Config();
        config.dimensions = new Config.Dimension[] { overworld, nether, end, anyDimension };
        return config;
    }

    private static Config.ItemModifier createItemModifier(Config.AttributeModifier[] attributeModifiers) {
        return createItemModifier(null, null, attributeModifiers);
    }

    private static Config.ItemModifier createItemModifier(String itemIdRegex, String lootTableRegex, Config.AttributeModifier[] attributeModifiers) {
        Config.ItemModifier itemModifier = new Config.ItemModifier();
        itemModifier.item_matches = new Config.ItemModifier.Filters();
        if (itemIdRegex != null) {
            itemModifier.item_matches.item_id_regex = itemIdRegex;
        }
        if (lootTableRegex != null) {
            itemModifier.item_matches.loot_table_regex = lootTableRegex;
        }
        itemModifier.attributes = attributeModifiers;
        return itemModifier;
    }

    private static Config.AttributeModifier createDamageMultiplier(float value, float randomness) {
        Config.AttributeModifier modifier = new Config.AttributeModifier("generic.attack_damage", value);
        modifier.randomness = randomness;
        return modifier;
    }

    private static Config.AttributeModifier createProjectileMultiplier(float value, float randomness) {
        Config.AttributeModifier modifier = new Config.AttributeModifier("generic.projectile_damage", value);
        modifier.randomness = randomness;
        return modifier;
    }

    private static Config.AttributeModifier createArmorMultiplier(float value) {
        return new Config.AttributeModifier("generic.armor", value);
    }

    private static Config.AttributeModifier createArmorBonus(float value) {
        Config.AttributeModifier modifier = new Config.AttributeModifier("generic.armor", value);
        modifier.operation = Config.Operation.ADD;
        return modifier;
    }

    private static Config.AttributeModifier createHealthMultiplier(float value, float randomness) {
        Config.AttributeModifier modifier = new Config.AttributeModifier("generic.max_health", value);
        modifier.randomness = randomness;
        return modifier;
    }

    private static Config.AttributeModifier createHealthBonus(float value) {
        Config.AttributeModifier modifier = new Config.AttributeModifier("generic.max_health", value);
        modifier.operation = Config.Operation.ADD;
        return modifier;
    }

    private static Config.EntityModifier createEntityModifier(String idRegex, Config.AttributeModifier[] attributeModifiers, Config.SpawnerModifier spawnerModifier, float xpMultiplier) {
        Config.EntityModifier entityModifier = new Config.EntityModifier();
        entityModifier.entity_matches = new Config.EntityModifier.Filters();
        entityModifier.entity_matches.entity_id_regex = idRegex;
        entityModifier.attributes = attributeModifiers;
        entityModifier.spawners = spawnerModifier;
        entityModifier.experience_multiplier = xpMultiplier;
        return entityModifier;
    }
}
