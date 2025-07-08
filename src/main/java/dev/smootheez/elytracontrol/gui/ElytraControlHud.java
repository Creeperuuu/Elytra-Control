package dev.smootheez.elytracontrol.gui;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.elytracontrol.config.*;
import dev.smootheez.elytracontrol.config.option.*;
import dev.smootheez.elytracontrol.event.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.*;
import net.minecraft.client.gl.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.render.*;
import net.minecraft.text.*;
import net.minecraft.util.*;

@Environment(EnvType.CLIENT)
public class ElytraControlHud implements HudElement {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Identifier elytraLockTexture = Identifier.of(Constants.MOD_ID, "textures/gui/elytra_lock.png");
    private final Text elytraLockNotifier = Text.translatable("notifier." + Constants.MOD_ID + ".toggleElytraLock");

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (!EndTickEvent.elytraToggle)
            lockIconMode(drawContext, ElytraControlConfig.lockIconMode.getValue());
    }

    private void lockIconMode(DrawContext drawContext, LockIconMode lockIconMode) {
        int iconSize = 16;
        int padding = 2;
        int baseX = client.getWindow().getScaledWidth() / 2 + 98;
        int baseY = client.getWindow().getScaledHeight() - 19;

        switch (lockIconMode) {
            case ICON_ONLY:
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, elytraLockTexture,
                        baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                break;
            case TEXT_ONLY:
                drawContext.drawText(client.textRenderer, elytraLockNotifier,
                        baseX, baseY + (iconSize - client.textRenderer.fontHeight), 0xFF1313, false);
                break;
            case ICON_TEXT:
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, elytraLockTexture,
                        baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);

                drawContext.drawText(client.textRenderer, elytraLockNotifier,
                        baseX + iconSize + padding, baseY + (iconSize - client.textRenderer.fontHeight), 0xFF1313, false);
                break;
            case NONE:
            default:
                break;

        }
    }
}
