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

    /** Configuration for Elytra Control settings loaded from config manager. */
    private static final ElytraControlConfig CONFIG = ConfigManager.getConfig(ElytraControlConfig.class);

    /** Timer counter for Elytra flight duration. */
    private int elytraTime = 0;

    /** Random generator for probabilistic behaviors (e.g., flight interruption). */
    private final Random random = new Random();

    /**
     * Called every client tick (after normal game tick).
     * Handles Elytra flight logic, input detection, and configuration checks.
     */
    @Override
    public void onEndTick(Minecraft client) {
        final LocalPlayer clientPlayer = client.player;
        final Options options = client.options;

        // Exit early if no player is present (e.g., at main menu)
        if (clientPlayer == null) return;

        // Check if player is currently flying with Elytra
        var isPlayerFlying = clientPlayer.isFallFlying();

        // Handle toggle to disable Elytra flight via keybind
        disableElytra(clientPlayer, isPlayerFlying);

        // Apply default stop-flying behavior (based on jump key + random chance)
        defaultStopFlyingBehavior(options, isPlayerFlying, clientPlayer);

        // Apply custom keybind start/stop logic for Elytra flight
        customStopFlyingBehavior(clientPlayer, isPlayerFlying);

        // Count Elytra flight time for timing-based features
        countElytraTime(isPlayerFlying, options);
    }

    /**
     * Toggles Elytra disabling when the assigned key is pressed.
     * Displays notification (if enabled) and stops flight if necessary.
     */
    private static void disableElytra(LocalPlayer clientPlayer, boolean isFallFlying) {
        // Check if the "Disable Elytra" keybind was pressed
        while (KeyMappingRegistry.DISABLE_ELYTRA.consumeClick()) {
            // Toggle disable flag
            var shouldDisableElytra = !ElytraControlUtils.isShouldDisableElytra();
            ElytraControlUtils.setShouldDisableElytra(shouldDisableElytra);

            // If notification config is enabled, show message on screen
            if (Boolean.TRUE.equals(CONFIG.getDisableElytraNotification().getValue())) {
                // Stop flight if currently flying
                stopFallFlying(clientPlayer, isFallFlying);

                // Display toggle status (on/off) message
                clientPlayer.displayClientMessage(CommonComponents.optionStatus(
                        Component.translatable("notification." + Constants.MOD_ID + ".disable_elytra"), shouldDisableElytra), true);
            }
        }
    }

    /**
     * Tracks how long the player has been flying with Elytra.
     * Resets when flight stops or when jump key is pressed.
     */
    private void countElytraTime(boolean isPlayerFlying, Options options) {
        if (isPlayerFlying && !options.keyJump.isDown())
            elytraTime = (elytraTime + 1) % 1000; // keep counter bounded
        else elytraTime = 0; // reset if not flying or jumping
    }

    /**
     * Handles custom flight start/stop keybind logic.
     * Supports two modes:
     * - Same key for start/stop
     * - Separate keys for start and stop
     */
    private void customStopFlyingBehavior(LocalPlayer clientPlayer, boolean isFallFlying) {
        var startKey = KeyMappingRegistry.START_FLYING;
        var stopKey = KeyMappingRegistry.STOP_FLYING;

        // Check if both actions share the same key
        if (startKey.same(stopKey)) handleSameKeyMode(clientPlayer, startKey, isFallFlying);
        else handleSeparateKeyMode(clientPlayer, startKey, stopKey, isFallFlying);
    }

    /**
     * Handles the case where the same key toggles both start and stop flight.
     * Each press toggles Elytra flight state.
     */
    private void handleSameKeyMode(LocalPlayer player, KeyMapping key, boolean isFallFlying) {
        while (key.consumeClick()) {
            stopFallFlying(player, isFallFlying); // Stop flight if active
            sendStartFallFlyingPacket(player);    // Then send start packet (for restart)
        }
    }

    /**
     * Handles mode where start/stop keys are distinct.
     */
    private void handleSeparateKeyMode(LocalPlayer player, KeyMapping startKey, KeyMapping stopKey, boolean isFallFlying) {
        processStartFlying(player, startKey);
        processStopFlying(player, stopKey, isFallFlying);
    }

    /**
     * Starts Elytra flight when the start key is pressed.
     * Only triggers if the player is not already flying.
     */
    private void processStartFlying(LocalPlayer player, KeyMapping startKey) {
        while (startKey.consumeClick())
            if (!player.isFallFlying())
                sendStartFallFlyingPacket(player);
    }

    /**
     * Stops Elytra flight when the stop key is pressed.
     */
    private void processStopFlying(LocalPlayer player, KeyMapping stopKey, boolean isFallFlying) {
        while (stopKey.consumeClick())
            stopFallFlying(player, isFallFlying);
    }

    /**
     * Default Elytra stop behavior triggered by jump key.
     * Adds a random delay to avoid instant stop, depending on config.
     */
    private void defaultStopFlyingBehavior(Options options, boolean isPlayerFlying, LocalPlayer clientPlayer) {
        int randomNumber = random.nextInt(3) + 1; // random 1–3
        if (options.keyJump.consumeClick()
                && elytraTime > randomNumber
                && Boolean.TRUE.equals(CONFIG.getElytraControlAllowDefaultKey().getValue()))
            stopFallFlying(clientPlayer, isPlayerFlying);
    }

    /**
     * Stops Elytra flight safely and sends packet to update server-side state.
     */
    private static void stopFallFlying(LocalPlayer clientPlayer, boolean isFallFlying) {
        if (!isFallFlying) return; // no action if not flying

        DebugMode.sendLoggerInfo("Stopping Elytra flight");

        // Stop the local flight state
        clientPlayer.stopFallFlying();

        // Send packet to sync with server (prevents desync)
        sendStartFallFlyingPacket(clientPlayer);
    }

    /**
     * Sends a packet to the server to start Elytra flight.
     * Used for sync and initiating flight from client-side.
     */
    private static void sendStartFallFlyingPacket(LocalPlayer clientPlayer) {
        DebugMode.sendLoggerInfo("Sending start fall flying packet");
        clientPlayer.connection.send(new ServerboundPlayerCommandPacket(
                clientPlayer, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
    }
}

