package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.registry.*;
import net.minecraft.client.*;
import org.apache.commons.lang3.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Options.class)
public abstract class OptionsMixin {

    @Mutable
    @Shadow @Final public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Options;load()V"
    ))
    private void addCustomKeybind(CallbackInfo info) {
        this.keyMappings = ArrayUtils.addAll(
                this.keyMappings,
                KeyBindsRegistry.START_FLYING,
                KeyBindsRegistry.STOP_FLYING,
                KeyBindsRegistry.DISABLE_FLYING
        );
    }
}
