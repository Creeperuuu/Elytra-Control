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
        if (Boolean.FALSE.equals(ConfigManager.getConfig(ElytraControlConfig.class).getDisableElytra().getValue())) {
            DebugMode.sendLoggerInfo("Try to start fall flying normally");
            return instance.tryToStartFallFlying();
        }

        DebugMode.sendLoggerInfo("Redirecting fall flying because Elytra is disabled");
        return false;
    }
}
