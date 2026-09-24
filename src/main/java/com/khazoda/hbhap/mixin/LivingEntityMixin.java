package com.khazoda.hbhap.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
  @Shadow
  @Final
  private static EntityDataAccessor<Boolean> DATA_EFFECT_AMBIENCE_ID;

  @Redirect(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
  private void hbhap$showEffectParticles(Level level, ParticleOptions particle, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
    LivingEntity self = (LivingEntity) (Object) this;
    if (self != Minecraft.getInstance().player || !self.getEntityData().get(DATA_EFFECT_AMBIENCE_ID)) {
      level.addParticle(particle, x, y, z, velocityX, velocityY, velocityZ);
    }
  }
}
