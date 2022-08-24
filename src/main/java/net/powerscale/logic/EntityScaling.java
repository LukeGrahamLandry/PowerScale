package net.powerscale.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.powerscale.config.Config;

import java.util.List;

public class EntityScaling {
    public static void scale(Entity entity, World world) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity;
            PatternMatching.LocationData locationData = PatternMatching.LocationData.create(world, livingEntity.getBlockPos());
            PatternMatching.EntityData entityData = PatternMatching.EntityData.create(livingEntity);
            List<Config.AttributeModifier> attributeModifiers = PatternMatching.getAttributeModifiersForEntity(locationData, entityData);

            EntityScaling.apply(attributeModifiers, livingEntity);

            for (ItemStack itemStack: livingEntity.getItemsEquipped()) {
                ItemScaling.scale(itemStack, world, livingEntity.getBlockPos(), entityData.entityId());
            }
        }
    }

    private static void apply(List<Config.AttributeModifier> attributeModifiers, LivingEntity entity) {
        float relativeHealth = entity.getHealth() / entity.getMaxHealth();
        for (Config.AttributeModifier modifier: attributeModifiers) {
            if (modifier.attribute == null) {
                continue;
            }
            EntityAttribute attribute = Registry.ATTRIBUTE.get(new Identifier(modifier.attribute));
            if (!entity.getAttributes().hasAttribute(attribute)) {
                continue;
            }

            float modifierValue = modifier.randomizedValue();

            switch (modifier.operation) {
                case ADD -> {
                    EntityAttributeInstance entityAttribute = entity.getAttributeInstance(attribute);
                    if (entityAttribute != null) {
                        entityAttribute.setBaseValue(entityAttribute.getBaseValue() + modifierValue);
                    }
                }
                case MULTIPLY -> {
                    double defaultValue = entity.getAttributeValue(attribute);
                    if (defaultValue > 0) {
                        entity.getAttributeInstance(attribute).setBaseValue(defaultValue * modifierValue);
                    }
                }
            }
        }
        entity.setHealth(relativeHealth * entity.getMaxHealth());
    }
}
