package dev.smootheez.elytracontrol.mixin;

import net.minecraft.client.gui.*;
import net.minecraft.network.chat.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow private int screenWidth;

    @Shadow public abstract Font getFont();

    @Inject(method = "render", at = @At("TAIL") )
    private void onRenderHud(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        String text = "Developer Mode";
        guiGraphics.drawString(
                this.getFont(),
                Component.literal(text),
                (this.screenWidth - this.getFont().width(text)) / 2,
                10,
                0xFFFFFF,
                false
        );
    }
}
