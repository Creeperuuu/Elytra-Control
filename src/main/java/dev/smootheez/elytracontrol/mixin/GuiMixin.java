package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.handler.*;
import net.minecraft.client.gui.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL") )
    private void onRenderHud(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        GameHudHandler.onRenderHud(guiGraphics);
    }
}
