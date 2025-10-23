package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.elytracontrol.registry.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.network.protocol.game.*;

import java.util.*;

@Environment(EnvType.CLIENT)
public class HandleEndClientTick implements ClientTickEvents.EndTick {
    private int elytraTime = 0;
    private final Random random = new Random();

    @Override
    public void onEndTick(Minecraft client) {
        LocalPlayer clientPlayer = client.player;
        Options options = client.options;

        if (clientPlayer == null) return;

        var isPlayerFlying = clientPlayer.isFallFlying();
        int randomNumber = random.nextInt(3) + 1;

        defaultStopFlyingBehavior(options, randomNumber, isPlayerFlying, clientPlayer);
        customStopFlyingBehavior(clientPlayer);

        if (isPlayerFlying && !options.keyJump.isDown())
            elytraTime = (elytraTime + 1) % 1000;
        else elytraTime = 0;
    }

    private void customStopFlyingBehavior(LocalPlayer clientPlayer) {
        var startKey = KeyMappingRegistry.START_FLYING;
        var stopKey = KeyMappingRegistry.STOP_FLYING;

        if (startKey.same(stopKey)) handleSameKeyMode(clientPlayer, startKey);
        else handleSeparateKeyMode(clientPlayer, startKey, stopKey);
    }

    private void handleSameKeyMode(LocalPlayer player, KeyMapping key) {
        while (key.consumeClick()) {
            if (player.isFallFlying()) stopFallFlying(player);
            else sendStartFallFlyingPacket(player);
        }
    }

    private void handleSeparateKeyMode(LocalPlayer player, KeyMapping startKey, KeyMapping stopKey) {
        processStartFlying(player, startKey);
        processStopFlying(player, stopKey);
    }

    private void processStartFlying(LocalPlayer player, KeyMapping startKey) {
        while (startKey.consumeClick()) {
            if (!player.isFallFlying()) sendStartFallFlyingPacket(player);
        }
    }

    private void processStopFlying(LocalPlayer player, KeyMapping stopKey) {
        while (stopKey.consumeClick()) {
            if (player.isFallFlying()) stopFallFlying(player);
        }
    }

    private void defaultStopFlyingBehavior(Options options, int randomNumber, boolean isPlayerFlying, LocalPlayer clientPlayer) {
        if (options.keyJump.consumeClick()
                && elytraTime > randomNumber
                && isPlayerFlying
                && Boolean.TRUE.equals(ConfigManager.getConfig(ElytraControlConfig.class).getElytraControlDefault().getValue()))
            stopFallFlying(clientPlayer);
    }

    private static void stopFallFlying(LocalPlayer clientPlayer) {
        DebugMode.sendLoggerInfo("Stopping Elytra flight");
        clientPlayer.stopFallFlying();
        sendStartFallFlyingPacket(clientPlayer);
    }

    private static void sendStartFallFlyingPacket(LocalPlayer clientPlayer) {
        DebugMode.sendLoggerInfo("Sending start fall flying packet");
        clientPlayer.connection.send(new ServerboundPlayerCommandPacket(clientPlayer, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }
}
