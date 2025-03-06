package dev.smootheez.elytracontrol.event;

import dev.smootheez.elytracontrol.Constants;
import dev.smootheez.elytracontrol.config.ElytraControlConfig;
import dev.smootheez.elytracontrol.handler.EasyFlightHandler;
import dev.smootheez.elytracontrol.registry.KeyBinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class EndTickEvent implements ClientTickEvents.EndTick {
    public static boolean elytraToggle = true;
    public static boolean easyFlightToggle = true;
    public static String playerUUID;
    private int elytraTime = 0;
    private final ElytraControlConfig config = ElytraControlConfig.getInstance();

    @Override
    public void onEndTick(MinecraftClient client) {
        Random random = new Random();
        PlayerEntity player = client.player;

        if (player == null) return;

        int randomNumber = random.nextInt(3) + 1;
        KeyBinding keyJump = client.options.jumpKey;
        boolean fallFlyingEntity = player.isFallFlying();

        if (playerUUID == null) {
            playerUUID = player.getUuidAsString();
        }

        toggleElytraIfKeyPressed(player);
        isEasyFlightKeyPressed(player);

        if (keyJump.wasPressed() && fallFlyingEntity && elytraTime > randomNumber && config.getElytraCancel().getValue()) {
            stopFlying(client.player);
        }

        updateElytraFlyingTime(client);

        EasyFlightHandler.handleEasyFlight(client);

    }

    private void isEasyFlightKeyPressed(PlayerEntity player) {
        while (config.getEasyFlight().getValue() && KeyBinds.easyFlightToggleKey.wasPressed()) {
            easyFlightToggle = !easyFlightToggle;
            if (config.getEasyFlightMessage().getValue())
                player.sendMessage(ScreenTexts.composeToggleText(Text.translatable("message." + Constants.MOD_ID + ".toggleEasyFlight"), easyFlightToggle), true);
        }
    }

    private void toggleElytraIfKeyPressed(PlayerEntity player) {
        while (KeyBinds.elytraToggleKey.wasPressed() && config.getElytraLock().getValue()) {
            elytraToggle = !elytraToggle;
            if (config.getElytraLockMessage().getValue())
                player.sendMessage(ScreenTexts.composeToggleText(Text.translatable("message." + Constants.MOD_ID + ".toggleElytraLock"), elytraToggle), true);
        }
    }

    private void stopFlying(ClientPlayerEntity player) {
        player.stopFallFlying();
        player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
    }

    private void updateElytraFlyingTime(MinecraftClient client) {
        if (client.player == null) return;

        if (client.player.isFallFlying() && !client.options.jumpKey.isPressed()) {
            elytraTime = (elytraTime + 1) % 1000;
        } else {
            elytraTime = 0;
        }
    }
}
