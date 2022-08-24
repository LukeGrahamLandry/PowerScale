package net.powerscale.logic;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.powerscale.config.Config;

public class ExperienceScaling {
    public static int scale(World world, LivingEntity entity, int experience) {
        PatternMatching.LocationData locationData = PatternMatching.LocationData.create(world, entity.getBlockPos());
        PatternMatching.EntityData entityData = PatternMatching.EntityData.create(entity);
        float multiplier = 1.0F;
        for (Config.EntityModifier modifier: PatternMatching.getModifiersForEntity(locationData, entityData)) {
            multiplier *= modifier.experience_multiplier;
        }
        int xp = Math.round((float)experience * multiplier);
        // System.out.println("Scaled XP from: " + experience + " to: " + xp);
        return xp;
    }
}
