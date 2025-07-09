package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.config.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.world.entity.player.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(Player.class)
public abstract class PlayerMixin {

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void preventFallFlying(CallbackInfoReturnable<Boolean> cir) {
        var player = (Player)(Object)this;
        var client = Minecraft.getInstance();
        var options = client.options;
        var clientPlayer = client.player;

        if (clientPlayer == null) return;
        var uuid = clientPlayer.getStringUUID();

        if (player.getStringUUID().equals(uuid) && ElytraControlConfig.ALLOW_FLYING.getValue()) {
            if (options.keyJump.isDown() && !ElytraControlConfig.DEFAULT_ELTRA_CONTROL.getValue())
                cir.setReturnValue(false);
        } else {
            cir.setReturnValue(false);
        }
    }
}
