package io.github.smootheez.elytracontrol.mixin.client;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.minecraft.client.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Redirect(
            method = "aiStep",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;tryToStartFallFlying()Z")
    )
    private boolean disableElytra(LocalPlayer instance) {
        if (Boolean.TRUE.equals(ConfigManager.getConfig(ElytraControlConfig.class).getDisableElytra().getValue())) {
            DebugMode.sendLoggerInfo("[Mixin:LocalPlayer] Blocked tryToStartFallFlying - this should log every time you try to activate elytra");
            return false;
        }
        return true;
    }
}
