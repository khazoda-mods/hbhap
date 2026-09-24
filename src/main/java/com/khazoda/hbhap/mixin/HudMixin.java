package com.khazoda.hbhap.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >= 26.2
//import net.minecraft.client.gui.Hud;
//? if < 26.2
import net.minecraft.client.gui.Gui;

//? if >= 26.2
//@Mixin(Hud.class)
//? if < 26.2
@Mixin(Gui.class)
abstract class HudMixin {
    @ModifyExpressionValue(
        method = "extractEffects",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;showIcon()Z"
        )
    )
    private boolean hbhap$hideAmbientEffectIcons(boolean original, @Local(name="instance") MobEffectInstance instance) {
        return original && !instance.isAmbient();
    }
}
