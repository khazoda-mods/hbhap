package com.khazoda.hbhap.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
abstract class GuiMixin {
  @Redirect(method = "renderEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"))
  private boolean hbhap$hideAmbientEffectIcons(MobEffectInstance instance) {
    return instance.showIcon() && !instance.isAmbient();
  }
}
