package net.powerscale.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Monster;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.powerscale.PowerScale;
import net.powerscale.config.Config;
import net.powerscale.logic.PatternMatching;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(MobSpawnerLogic.class)
public abstract class MobSpawnerLogicMixin {
    @Shadow private int spawnRange;
    @Shadow private int spawnCount;
    @Shadow private int maxNearbyEntities;
    @Shadow private int minSpawnDelay;
    @Shadow private int maxSpawnDelay;
    @Shadow private int requiredPlayerRange;

    @Shadow private MobSpawnerEntry spawnEntry;

    @Shadow public abstract World getWorld();

    @Shadow public abstract BlockPos getPos();

    private boolean initialized = false;

    private static String modifiedKey = "modified_by_" + PowerScale.MODID;

    @Inject(method = "update", at = @At("HEAD"))
    private void pre_serverTick(CallbackInfo ci) {
        if(!initialized) {
            initialized = true;

            if(this.spawnEntry.getEntityNbt().contains(modifiedKey)) {
                return;
            }

            try {
                String entityId = this.spawnEntry.getEntityNbt().getString("id");
                EntityType<?> entityType = Registry.ENTITY_TYPE.get(new Identifier(entityId));
                Entity testEntity = entityType.create(getWorld());
                boolean isMonster = testEntity instanceof Monster;
                PatternMatching.EntityData entityData = new PatternMatching.EntityData(entityId, isMonster);
                PatternMatching.LocationData locationData = PatternMatching.LocationData.create(getWorld(), getPos());
                List<Config.SpawnerModifier> modifiers = PatternMatching.getModifiersForSpawner(locationData, entityData);
//                if (modifiers.size() > 0) {
//                    System.out.println("Scaling spawner of: " + entityId + " at: " + pos);
//                }
                scaleSpawner(modifiers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void scaleSpawner(List<Config.SpawnerModifier> modifiers) {
        for(Config.SpawnerModifier modifier: modifiers) {
            this.spawnRange = Math.round(spawnRange * modifier.spawn_range_multiplier);
            this.spawnCount = Math.round(spawnCount * modifier.spawn_count_multiplier);
            this.maxNearbyEntities = Math.round(maxNearbyEntities * modifier.max_nearby_entities_multiplier);
            this.minSpawnDelay = Math.round(minSpawnDelay * modifier.min_spawn_delay_multiplier);
            this.maxSpawnDelay = Math.round(maxSpawnDelay * modifier.max_spawn_delay_multiplier);
            this.requiredPlayerRange = Math.round(requiredPlayerRange * modifier.required_player_range_multiplier);
        }
        if (modifiers.size() > 0) {
            this.spawnEntry.getEntityNbt().putBoolean(modifiedKey, true);
//            System.out.println("Spawner scaled");
//            System.out.println(" spawnRange:" + spawnRange
//                    + " spawnCount:" + spawnCount
//                    + " maxNearbyEntities:" + maxNearbyEntities
//                    + " minSpawnDelay:" + minSpawnDelay
//                    + " maxSpawnDelay:" + maxSpawnDelay
//                    + " requiredPlayerRange:" + requiredPlayerRange);
        }
    }
}
