package dev.smootheez.elytracontrol.mixin;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.elytracontrol.handler.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow private int screenWidth;

    @Shadow public abstract Font getFont();

    @Unique
    private final ResourceLocation elytraIcon = new ResourceLocation("textures/item/elytra.png");

    @Unique
    private final ResourceLocation crossIcon = new ResourceLocation(Constants.MOD_ID, "textures/gui/cross_icon.png");

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

        if (MinecraftClientHandler.isShouldDisableFlying()) {
            var iconSize = 16;
            var baseX = 3;
            var baseY = 3;

            int v = iconSize / 2 - this.getFont().lineHeight / 2;

            String disableText = "overlay.elytracontrol.disableText";
            guiGraphics.blit(elytraIcon, baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            guiGraphics.blit(crossIcon, baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            guiGraphics.drawString(
                    this.getFont(),
                    Component.translatable(disableText),
                    baseX + iconSize + 5,
                    baseY + v,
                    0xFFFFFF,
                    false
            );
        }
    }
}
