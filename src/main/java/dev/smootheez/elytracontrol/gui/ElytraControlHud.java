package dev.smootheez.elytracontrol.gui;

import dev.smootheez.elytracontrol.Constants;
import dev.smootheez.elytracontrol.config.ElytraControlConfig;
import dev.smootheez.elytracontrol.config.option.LockIconMode;
import dev.smootheez.elytracontrol.event.EndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElytraControlHud implements HudLayerRegistrationCallback {
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final Identifier elytraLockTexture = Identifier.of(Constants.MOD_ID, "textures/gui/elytra_lock.png");
    private final Text elytraLockNotifier = Text.translatable("notifier." + Constants.MOD_ID + ".toggleElytraLock");

    @Override
    public void register(LayeredDrawerWrapper layeredDrawer) {
        layeredDrawer.attachLayerAfter(IdentifiedLayer.MISC_OVERLAYS, elytraLockTexture, (context, tickCounter) -> {
            if (!EndTickEvent.elytraToggle) {
                lockIconMode(context, ElytraControlConfig.lockIconMode.getValue());
            }
        });
    }

    private void lockIconMode(DrawContext drawContext, LockIconMode lockIconMode) {
        int iconSize = 16;
        int padding = 2;
        int baseX = client.getWindow().getScaledWidth() / 2 + 98;
        int baseY = client.getWindow().getScaledHeight() - 19;

        switch (lockIconMode) {
            case ICON_ONLY:
                drawContext.drawTexture(RenderLayer::getGuiTextured, elytraLockTexture,
                        baseX, baseY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                break;
            case TEXT_ONLY:
                drawContext.drawText(client.textRenderer, elytraLockNotifier,
                        baseX, baseY + (iconSize - client.textRenderer.fontHeight), 0xFF1313, false);
                break;
            case ICON_TEXT:
                drawContext.drawTexture(RenderLayer::getGuiTextured, elytraLockTexture,
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
