package dev.smootheez.elytracontrol.handler;

import dev.smootheez.elytracontrol.registry.*;
import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.network.protocol.game.*;

import java.util.*;

public class MinecraftClientHandler {
    private static int elytraTime = 0;
    private final Minecraft client;

    public MinecraftClientHandler(Minecraft client) {
        this.client = client;
    }

    public void handleKeyBinds() {
        var player = client.player;
        if (player == null) return;

        while (KeyBindsRegistry.STOP_FLYING.consumeClick()) {
            if (!player.isFallFlying()) continue;
            player.stopFallFlying();
            sendStartFlyingPacket(player);
        }
        while (KeyBindsRegistry.START_FLYING.consumeClick()) {
            if (player.isFallFlying()) continue;
            sendStartFlyingPacket(player);
        }
    }

    public void onEndClientTick() {
        var player = client.player;
        if (player == null) return;

        Random random = new Random();
        int randomNumber = random.nextInt(3) + 1;

        if (client.options.keyJump.consumeClick() && player.isFallFlying() && elytraTime > randomNumber && KeyBindsRegistry.STOP_FLYING.isUnbound()) {
            player.stopFallFlying();
            sendStartFlyingPacket(player);
        }

        if (player.isFallFlying() && !client.options.keyJump.isDown())
            elytraTime = (elytraTime + 1) % 1000;
        else elytraTime = 0;
    }

    private void sendStartFlyingPacket(LocalPlayer player) {
        player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }
}
