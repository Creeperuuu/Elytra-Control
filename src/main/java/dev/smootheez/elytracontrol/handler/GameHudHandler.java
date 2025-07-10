package dev.smootheez.elytracontrol.handler;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.elytracontrol.config.*;
import dev.smootheez.elytracontrol.config.option.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;

public class GameHudHandler {
    private static final Minecraft client = Minecraft.getInstance();

    private static final ResourceLocation elytraIcon = new ResourceLocation("textures/item/elytra.png");
    private static final ResourceLocation crossIcon = new ResourceLocation(Constants.MOD_ID, "textures/gui/cross_icon.png");

    public static void onRenderHud(GuiGraphics guiGraphics) {
        if (MinecraftClientHandler.isShouldDisableFlying())
            renderDisableOverlay(guiGraphics, ElytraControlConfig.LOCK_ICON_MODE.getValue(), ElytraControlConfig.OVERLAY_POSITION.getValue());
    }

    private static void renderDisableOverlay(GuiGraphics guiGraphics, LockIconMode lockIconMode, OverlayPosition position) {
        var font = client.font;
        var screenWidth = guiGraphics.guiWidth();
        var screenHeight = guiGraphics.guiHeight();

        var disableText = "overlay.elytracontrol.disableText";

        var iconSize = 16;
        var textWidth = font.width(Component.translatable(disableText));

        var baseX = 3;
        var baseY = 3;

        var iconX = baseX;
        var iconY = baseY;

        var textX = baseX;
        var textY = iconY + iconSize / 2 - font.lineHeight / 2;

        switch (position) {
            case TOP_LEFT:
                if (lockIconMode == LockIconMode.ICON_TEXT)
                    textX = iconX + iconSize + 5;
                break;
            case TOP_MIDDLE:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconX = screenWidth / 2 - (textWidth + iconSize) / 2;
                        textX = iconX + iconSize + 5;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth / 2 - textWidth / 2;
                        break;
                    case ICON_ONLY:
                        iconX = screenWidth / 2 - iconSize / 2;
                        break;
                    default:
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
                    default:
                        break;
                }
                break;
            case RIGHT_MIDDLE:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconX = screenWidth - iconSize - baseX;
                        iconY = screenHeight / 2 - iconSize / 2;
                        textX = iconX - textWidth - 5;
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = screenHeight / 2 - font.lineHeight / 2;
                        break;
                    case ICON_ONLY:
                        iconX = screenWidth - iconSize - baseX;
                        iconY = screenHeight / 2 - iconSize / 2;
                        break;
                    default:
                        break;
                }
                break;
            case BOTTOM_RIGHT:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconX = screenWidth - iconSize - baseX;
                        iconY = screenHeight - baseY - iconSize;
                        textX = iconX - textWidth - 5;
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = screenHeight - baseY - font.lineHeight;
                        break;
                    case ICON_ONLY:
                        iconX = screenWidth - iconSize - baseX;
                        iconY = screenHeight - baseY - iconSize;
                        break;
                    default:
                        break;
                }
                break;
            case BOTTOM_LEFT:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconY = screenHeight - baseY - iconSize;
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = screenHeight - baseY - font.lineHeight;
                        break;
                    case ICON_ONLY:
                        iconY = screenHeight - baseY - iconSize;
                        break;
                    default:
                        break;
                }
                break;
            case LEFT_MIDDLE:
                switch (lockIconMode) {
                    case ICON_TEXT:
                        iconY = screenHeight / 2 - iconSize / 2;
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = screenHeight / 2 - font.lineHeight / 2;
                        break;
                    case ICON_ONLY:
                        iconY = screenHeight / 2 - iconSize / 2;
                        break;
                }
                break;
            default:
                break;
        }

        var textColor = 0xFF1313;

        switch (lockIconMode) {
            case ICON_TEXT:
                guiGraphics.blit(elytraIcon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                guiGraphics.blit(crossIcon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                guiGraphics.drawString(
                        font,
                        Component.translatable(disableText),
                        textX,
                        textY,
                        textColor,
                        false
                );
                break;
            case ICON_ONLY:
                guiGraphics.blit(elytraIcon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                guiGraphics.blit(crossIcon, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                break;
            case TEXT_ONLY:
                guiGraphics.drawString(
                        font,
                        Component.translatable(disableText),
                        textX,
                        textY,
                        textColor,
                        false
                );
                break;
            case NONE:
            default:
                break;
        }
    }
}
