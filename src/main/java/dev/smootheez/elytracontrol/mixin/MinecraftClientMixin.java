package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.handler.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Unique
    private final Minecraft client = (Minecraft) (Object) this;
    @Unique
    private final MinecraftClientHandler clientHandler = new MinecraftClientHandler(this.client);

    @Inject(method = "handleKeybinds", at = @At("HEAD") )
    private void handleKeyBinds(CallbackInfo info) {
        clientHandler.handleKeyBinds();
    }

    @Inject(method = "tick", at = @At("TAIL") )
    private void onEndClientTick(CallbackInfo info) {
        clientHandler.onEndClientTick();
    }
}
