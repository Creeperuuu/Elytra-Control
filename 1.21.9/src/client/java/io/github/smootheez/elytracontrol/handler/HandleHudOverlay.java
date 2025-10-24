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

/**
 * Client-side class responsible for rendering a custom Elytra disable overlay
 * on the player's HUD. The overlay shows an Elytra icon with a red cross and/or
 * text, depending on user configuration.
 *
 * This HUD element only renders when Elytra control is disabled.
 */
@Environment(EnvType.CLIENT)
public class HandleHudOverlay implements HudElement {

    /** Default Minecraft Elytra icon texture. */
    private static final ResourceLocation ELYTRA_ICON =
            ResourceLocation.withDefaultNamespace("textures/item/elytra.png");

    /** Custom red cross overlay icon texture for Elytra disable indicator. */
    private static final ResourceLocation CROSS_ICON =
            ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/cross_icon.png");

    /** Mod configuration object for accessing HUD and control settings. */
    private final ElytraControlConfig config = ConfigManager.getConfig(ElytraControlConfig.class);

    /**
     * Called each frame to render this HUD element.
     *
     * @param context      The current GUI graphics context.
     * @param tickCounter  Provides timing information for smooth rendering.
     */
    @Override
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
        // Only render overlay if Elytra disable mode is currently active
        if (!ElytraControlUtils.isShouldDisableElytra()) return;

        // Get current position and icon mode from config, then render
        renderOverlay(context, config.getOverlayPosition().getValue(), config.getLockIconMode().getValue());
    }

    /**
     * Renders the overlay on the screen based on the chosen position and display mode.
     *
     * @param guiGraphics  The GUI graphics renderer.
     * @param position     The screen position of the overlay (e.g., TOP_LEFT, BOTTOM_RIGHT).
     * @param lockIconMode The visual mode for displaying icons and/or text.
     */
    private void renderOverlay(GuiGraphics guiGraphics, OverlayPosition position, LockIconMode lockIconMode) {
        // Get the Minecraft font and screen dimensions
        var font = Minecraft.getInstance().font;
        var screenWidth = guiGraphics.guiWidth();
        var screenHeight = guiGraphics.guiHeight();

        // Create the translatable text component for "Elytra Disabled"
        var translatableDisableText = Component.translatable("overlay." + Constants.MOD_ID + ".disable_text");

        // Determine basic layout metrics
        var iconSize = 16;
        var textWidth = font.width(translatableDisableText);

        // Compute where to draw the icon and/or text on screen
        var positions = calculatePositions(position, lockIconMode, screenWidth, screenHeight, iconSize, textWidth, font.lineHeight);

        // Text color: red with full opacity (ARGB)
        int textColor = ARGB.color(255, 0xFF, 0x13, 0x13);

        // Draw Elytra + cross icon if the mode includes an icon
        if (lockIconMode == LockIconMode.ICON_TEXT || lockIconMode == LockIconMode.ICON_ONLY) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ELYTRA_ICON,
                    positions.iconX(), positions.iconY(),
                    0, 0, iconSize, iconSize, iconSize, iconSize);

            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CROSS_ICON,
                    positions.iconX(), positions.iconY(),
                    0, 0, iconSize, iconSize, iconSize, iconSize);
        }

        // Draw "Elytra Disabled" text if the mode includes text
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

    /**
     * A simple record to store precomputed X/Y positions for both icon and text.
     */
    private record Positions(int iconX, int iconY, int textX, int textY) {}

    /**
     * Calculates where the icon and text should appear on screen based on the chosen
     * overlay position and visual mode.
     *
     * @param position     Screen corner or edge where the overlay should appear.
     * @param lockIconMode Whether to display icon, text, both, or none.
     * @param screenWidth  Total width of the player's screen.
     * @param screenHeight Total height of the player's screen.
     * @param iconSize     Size of the Elytra icon (in pixels).
     * @param textWidth    Width of the text string in pixels.
     * @param lineHeight   Height of the font's text line.
     * @return             A {@link Positions} record containing the computed coordinates.
     */
    private Positions calculatePositions(OverlayPosition position,
                                         LockIconMode lockIconMode,
                                         int screenWidth,
                                         int screenHeight,
                                         int iconSize,
                                         int textWidth,
                                         int lineHeight) {

        // Base margins from screen edges
        int baseX = 3;
        int baseY = 3;

        // Default starting positions for icon and text
        int iconX = baseX;
        int iconY = baseY;
        int textX = baseX;
        int textY = iconY + iconSize / 2 - lineHeight / 2;

        // Screen center coordinates (used for middle positions)
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Determine overlay layout based on position and icon/text mode
        switch (position) {
            case TOP_LEFT:
                // Align text to the right of the icon if both are shown
                if (lockIconMode == LockIconMode.ICON_TEXT)
                    textX = iconX + iconSize + 5;
                break;

            case TOP_MIDDLE:
                // Center the overlay horizontally at the top
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
                    default:
                        break;
                }
                break;

            case TOP_RIGHT:
                // Right-aligned top overlay
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
                // Vertically center along right side
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
                    default:
                        break;
                }
                break;

            case BOTTOM_RIGHT:
                // Bottom-right corner overlay
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
                    default:
                        break;
                }
                break;

            case BOTTOM_LEFT:
                // Bottom-left corner overlay
                iconY = screenHeight - baseY - iconSize;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = screenHeight - baseY - lineHeight;
                        break;
                    default:
                        break;
                }
                break;

            case LEFT_MIDDLE:
                // Vertically center along left side
                iconY = centerY - iconSize / 2;
                switch (lockIconMode) {
                    case ICON_TEXT:
                        textX = iconX + iconSize + 5;
                        textY = iconY + iconSize / 2 - lineHeight / 2;
                        break;
                    case TEXT_ONLY:
                        textY = centerY - lineHeight / 2;
                        break;
                    default:
                        break;
                }
                break;
        }

        // Return the final calculated coordinates
        return new Positions(iconX, iconY, textX, textY);
    }
}
