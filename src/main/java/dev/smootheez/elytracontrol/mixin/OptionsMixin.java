package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.*;
import net.minecraft.client.*;
import org.apache.commons.lang3.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.io.*;

@Mixin(Options.class)
public class OptionsMixin {
    @Mutable
    @Final
    @Shadow
    public KeyMapping[] keyMappings;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addCustomKeybind(Minecraft minecraft, File file, CallbackInfo info) {
        this.keyMappings = ArrayUtils.add(
                this.keyMappings,
                KeyBindsRegistry.CONTROL_FLYING
        );
    }
}
