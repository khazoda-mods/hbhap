package com.khazoda.hbhap.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
  @Shadow
  @Final
  private static EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;

  @WrapWithCondition(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
  private boolean hbhap$showEffectParticles(Level level, ParticleOptions particleOptions, double d, double e, double f, double g, double h, double i) {
    LivingEntity self = (LivingEntity) (Object) this;
    return self != Minecraft.getInstance().player || !self.getEntityData().get(DATA_EFFECT_AMBIENCE_ID);
  }
}
