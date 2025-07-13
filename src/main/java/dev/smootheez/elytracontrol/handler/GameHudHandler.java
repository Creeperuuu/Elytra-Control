package dev.smootheez.elytracontrol.handler;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.elytracontrol.config.*;
import dev.smootheez.elytracontrol.config.option.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;

public class GameHudHandler {
    private static final Minecraft client = Minecraft.getInstance();

    private static final ResourceLocation ELYTRA_ICON = ResourceLocation.withDefaultNamespace("textures/item/elytra.png");
    private static final ResourceLocation CROSS_ICON = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/cross_icon.png");

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
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = centerY - font.lineHeight / 2;
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
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textX = screenWidth - textWidth - baseX;
                        textY = screenHeight - baseY - font.lineHeight;
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
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = screenHeight - baseY - font.lineHeight;
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
                        textY = iconY + iconSize / 2 - font.lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = centerY - font.lineHeight / 2;
                        break;
                    case ICON_ONLY, NONE:
                        break;
                }
                break;
        }

        var textColor = 0xFF1313;

        if (lockIconMode == LockIconMode.ICON_TEXT || lockIconMode == LockIconMode.ICON_ONLY) {
            guiGraphics.blit(RenderType::guiTextured, ELYTRA_ICON, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
            guiGraphics.blit(RenderType::guiTextured, CROSS_ICON, iconX, iconY, 0, 0, iconSize, iconSize, iconSize, iconSize);
        }
        if (lockIconMode == LockIconMode.ICON_TEXT || lockIconMode == LockIconMode.TEXT_ONLY) {
            guiGraphics.drawString(
                    font,
                    Component.translatable(disableText),
                    textX,
                    textY,
                    textColor,
                    false
            );
        }
    }
}
