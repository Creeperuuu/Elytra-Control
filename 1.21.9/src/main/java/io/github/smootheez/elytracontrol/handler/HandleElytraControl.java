package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.elytracontrol.registry.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.player.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.*;

import java.util.*;

/**
 * This class handles all client-side Elytra control logic.
 * It listens for client tick events (per frame update) and manages:
 * - Enabling/disabling Elytra flight
 * - Custom keybinding behavior for start/stop flying
 * - Flight timing and random-based stop control
 * - Optional Elytra disable toggle notifications
 * <p>
 * Environment: CLIENT only.
 */
@Environment(EnvType.CLIENT)
public class HandleElytraControl implements ClientTickEvents.EndTick {

    private static final ElytraControlConfig CONFIG = ConfigManager.getConfig(ElytraControlConfig.class);
    private int elytraTime = 0;
    private final Random random = new Random();

    @Override
    public void onEndTick(Minecraft client) {
        final LocalPlayer clientPlayer = client.player;
        final Options options = client.options;

        if (clientPlayer == null) return;

        var isPlayerFlying = clientPlayer.isFallFlying();

        disableElytra(clientPlayer, isPlayerFlying);
        defaultStopFlyingBehavior(options, isPlayerFlying, clientPlayer);
        customStopFlyingBehavior(client, clientPlayer, isPlayerFlying);
        countElytraTime(isPlayerFlying, options);
    }

    /**
     * Toggles Elytra disabling when the assigned key is pressed.
     */
    private static void disableElytra(LocalPlayer clientPlayer, boolean isFallFlying) {
        while (KeyMappingRegistry.DISABLE_ELYTRA.consumeClick()) {
            var shouldDisableElytra = !ElytraControlUtils.isShouldDisableElytra();
            ElytraControlUtils.setShouldDisableElytra(shouldDisableElytra);

            if (Boolean.TRUE.equals(CONFIG.getDisableElytraNotification().getValue())) {
                stopFallFlying(clientPlayer, isFallFlying);

                clientPlayer.displayClientMessage(CommonComponents.optionStatus(
                        Component.translatable("notification." + Constants.MOD_ID + ".disable_elytra"), shouldDisableElytra), true);
            }
        }
    }

    private void countElytraTime(boolean isPlayerFlying, Options options) {
        if (isPlayerFlying && !options.keyJump.isDown())
            elytraTime = (elytraTime + 1) % 1000;
        else elytraTime = 0;
    }

    /**
     * The Stop Flying key now acts as an Elytra/chestplate toggle:
     *
     * Elytra equipped + flying:
     *   stop local flight -> swap Elytra for chestplate
     *
     * Chestplate equipped + not flying:
     *   swap chestplate for Elytra -> request Elytra flight
     *
     * This keeps the whole operation client-side and uses normal Minecraft
     * inventory click packets for the equipment swap.
     */
    private void customStopFlyingBehavior(Minecraft client, LocalPlayer player, boolean isFallFlying) {
        var startKey = KeyMappingRegistry.START_FLYING;
        var stopKey = KeyMappingRegistry.STOP_FLYING;

        if (startKey.same(stopKey)) {
            while (startKey.consumeClick())
                toggleFlightWithArmor(client, player, isFallFlying);
            return;
        }

        processStartFlying(player, startKey);

        while (stopKey.consumeClick())
            toggleFlightWithArmor(client, player, player.isFallFlying());
    }

    /**
     * Starts Elytra flight when the Start Flying key is pressed.
     */
    private void processStartFlying(LocalPlayer player, KeyMapping startKey) {
        while (startKey.consumeClick()) {
            if (!player.isFallFlying())
                sendStartFallFlyingPacket(player);
        }
    }

    /**
     * Default Elytra stop behavior triggered by the jump key.
     * This remains the normal stop behavior; only the Elytra Control
     * Stop Flying key performs the armor swap.
     */
    private void defaultStopFlyingBehavior(Options options, boolean isPlayerFlying, LocalPlayer clientPlayer) {
        int randomNumber = random.nextInt(3) + 1;
        if (options.keyJump.consumeClick()
                && elytraTime > randomNumber
                && Boolean.TRUE.equals(CONFIG.getElytraControlAllowDefaultKey().getValue()))
            stopFallFlying(clientPlayer, isPlayerFlying);
    }

    private static void toggleFlightWithArmor(Minecraft client, LocalPlayer player, boolean isFallFlying) {
        if (isFallFlying) {
            DebugMode.sendLoggerInfo("Stopping Elytra flight and swapping to chestplate");

            // Stop the client-side fall-flying state first. Removing the Elytra
            // through the inventory transaction then keeps the server in sync.
            player.stopFallFlying();

            if (!ElytraArmorSwap.toggle(client, player))
                DebugMode.sendLoggerInfo("No chestplate available for Elytra swap");
            return;
        }

        DebugMode.sendLoggerInfo("Swapping to Elytra and starting flight");

        if (ElytraArmorSwap.toggle(client, player)) {
            sendStartFallFlyingPacket(player);
        } else {
            DebugMode.sendLoggerInfo("No Elytra available for Elytra swap");
        }
    }

    /**
     * Normal stop behavior used by the jump key and the Disable Elytra option.
     */
    private static void stopFallFlying(LocalPlayer clientPlayer, boolean isFallFlying) {
        if (!isFallFlying) return;

        DebugMode.sendLoggerInfo("Stopping Elytra flight");
        clientPlayer.stopFallFlying();
    }

    /**
     * Sends the normal client-to-server command used to initiate Elytra flight.
     */
    private static void sendStartFallFlyingPacket(LocalPlayer clientPlayer) {
        DebugMode.sendLoggerInfo("Sending start fall flying packet");
        clientPlayer.connection.send(new ServerboundPlayerCommandPacket(
                clientPlayer, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }
}
