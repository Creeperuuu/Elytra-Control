package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.elytracontrol.config.option.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;

@Environment(EnvType.CLIENT)
public class HandleHudOverlay implements HudElement {
    private static final ResourceLocation ELYTRA_ICON = ResourceLocation.withDefaultNamespace("textures/item/elytra.png");
    private static final ResourceLocation CROSS_ICON = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/cross_icon.png");
    private final ElytraControlConfig config = ConfigManager.getConfig(ElytraControlConfig.class);

    @Override
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
        if (!ElytraControlUtils.isShouldDisableElytra()) return;
        renderOverlay(context, config.getOverlayPosition().getValue(), config.getLockIconMode().getValue());
    }

    private void renderOverlay(GuiGraphics guiGraphics, OverlayPosition position, LockIconMode lockIconMode) {
        var font = Minecraft.getInstance().font;
        var screenWidth = guiGraphics.guiWidth();
        var screenHeight = guiGraphics.guiHeight();

        var translatableDisableText = Component.translatable("overlay." + Constants.MOD_ID + ".disable_text");

        var iconSize = 16;
        var textWidth = font.width(translatableDisableText);

        var positions = calculatePositions(position, lockIconMode, screenWidth, screenHeight, iconSize, textWidth, font.lineHeight);

        int textColor = ARGB.color(255, 0xFF, 0x13, 0x13); // full alpha + RGB

        if (lockIconMode == LockIconMode.ICON_TEXT || lockIconMode == LockIconMode.ICON_ONLY) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ELYTRA_ICON, positions.iconX(), positions.iconY(), 0, 0, iconSize, iconSize, iconSize, iconSize);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CROSS_ICON, positions.iconX(), positions.iconY(), 0, 0, iconSize, iconSize, iconSize, iconSize);
        }
        if (lockIconMode == LockIconMode.ICON_TEXT || lockIconMode == LockIconMode.TEXT_ONLY) {
            guiGraphics.drawString(
                    font,
                    translatableDisableText,
                    positions.textX(),
                    positions.textY(),
                    textColor,
                    false
            );
        }
    }

    private record Positions(int iconX, int iconY, int textX, int textY) {
    }

    private Positions calculatePositions(OverlayPosition position,
                                         LockIconMode lockIconMode,
                                         int screenWidth,
                                         int screenHeight,
                                         int iconSize,
                                         int textWidth,
                                         int lineHeight) {
        int baseX = 3;
        int baseY = 3;

        int iconX = baseX;
        int iconY = baseY;

        int textX = baseX;
        int textY = iconY + iconSize / 2 - lineHeight / 2;

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        switch (position) {
            case TOP_LEFT:
                if (lockIconMode == LockIconMode.ICON_TEXT) {
                    textX = iconX + iconSize + 5;
                }
                break;
            case TOP_MIDDLE:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconX = centerX - (textWidth + iconSize) / 2;
                        textX = iconX + iconSize + 5;
                        break;
                    case TEXT_ONLY:
                        textX = centerX - textWidth / 2;
                        break;
                    case ICON_ONLY:
                        iconX = centerX - iconSize / 2;
                        break;
                    case NONE:
                        break;
                }
                break;
            case TOP_RIGHT:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconX = screenWidth - iconSize - baseX;
                        textX = iconX - textWidth - 5;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        break;
                    case ICON_ONLY:
                        iconX = screenWidth - iconSize - baseX;
                        break;
                    case NONE:
                        break;
                }
                break;
            case RIGHT_MIDDLE:
                iconX = screenWidth - iconSize - baseX;
                iconY = centerY - iconSize / 2;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX - textWidth - 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = centerY - lineHeight / 2;
                        break;
                    case ICON_ONLY, NONE:
                        break;
                }
                break;
            case BOTTOM_RIGHT:
                iconX = screenWidth - iconSize - baseX;
                iconY = screenHeight - baseY - iconSize;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX - textWidth - 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = screenHeight - baseY - lineHeight;
                        break;
                    case ICON_ONLY, NONE:
                        break;
                }
                break;
            case BOTTOM_LEFT:
                iconY = screenHeight - baseY - iconSize;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = screenHeight - baseY - lineHeight;
                        break;
                    case ICON_ONLY, NONE:
                        break;
                }
                break;
            case LEFT_MIDDLE:
                iconY = centerY - iconSize / 2;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = centerY - lineHeight / 2;
                        break;
                    case ICON_ONLY, NONE:
                        break;
                }
                break;
        }
        return new Positions(iconX, iconY, textX, textY);
    }
}
