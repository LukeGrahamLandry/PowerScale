package net.powerscale.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.powerscale.logic.ExperienceScaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(method = "getXpToDrop", at = @At(value = "RETURN"), cancellable = true)
    private void modifyDroppedXP(PlayerEntity player, CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        int xp = cir.getReturnValue();
        cir.setReturnValue(ExperienceScaling.scale(entity.world, entity, xp));
    }
}
