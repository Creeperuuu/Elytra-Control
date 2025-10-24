package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.elytracontrol.registry.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.player.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;

/**
 * Client-side handler that automates Elytra takeoff using fireworks.
 * <p>
 * When triggered (by key input or default interaction), this class performs a
 * three-step sequence to make Elytra flight easier:
 * <ol>
 *     <li>Apply a small upward momentum to the player.</li>
 *     <li>Start Elytra flight.</li>
 *     <li>Automatically use a firework rocket to boost the player.</li>
 * </ol>
 * The automation runs only when certain conditions are met (player is on ground,
 * Elytra equipped, not in water, etc.).
 */
@Environment(EnvType.CLIENT)
public class HandleEasyFly implements ClientTickEvents.EndTick {

    /** Access to the mod configuration for user settings and toggles. */
    private final ElytraControlConfig config = ConfigManager.getConfig(ElytraControlConfig.class);

    /** Tracks which step of the Easy Fly process is currently active. */
    private ActionStep actionStep = ActionStep.START_MOMENTUM;

    /** Flag to indicate if Easy Fly sequence should begin this tick. */
    private boolean startEasyFly = false;

    /**
     * Called at the end of each client tick.
     * <p>
     * Checks if Easy Fly can be initiated and, if so, executes the sequence.
     *
     * @param client The Minecraft client instance.
     */
    @Override
    public void onEndTick(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;

        // Skip if player or game mode is not yet initialized
        if (player == null || gameMode == null) return;

        // Check if player meets all conditions to start Easy Fly
        if (canInitiateEasyFly(client, client.options, player) && hasFireworkInHand(player))
            startEasyFly = true;

        // Run the Easy Fly sequence if it was initiated
        startEasyFlyAction(player, gameMode);
    }

    /**
     * Executes the Easy Fly action sequence in multiple stages:
     * <ul>
     *     <li>{@link ActionStep#START_MOMENTUM} — apply upward velocity</li>
     *     <li>{@link ActionStep#START_FLYING} — trigger Elytra flight packet</li>
     *     <li>{@link ActionStep#USE_FIREWORK} — use firework rocket to boost</li>
     * </ul>
     *
     * @param player   The local player.
     * @param gameMode The current multiplayer game mode instance.
     */
    private void startEasyFlyAction(LocalPlayer player, MultiPlayerGameMode gameMode) {
        if (startEasyFly)
            switch (actionStep) {
                case START_MOMENTUM -> handleMomentumAction(player);
                case START_FLYING -> handleStartFlying(player);
                case USE_FIREWORK -> handleUseFirework(player, gameMode);
            }
    }

    /**
     * Final step — triggers the player to use a firework rocket,
     * giving them Elytra boost.
     *
     * @param player   The local player.
     * @param gameMode The multiplayer game mode instance.
     */
    private void handleUseFirework(LocalPlayer player, MultiPlayerGameMode gameMode) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting firework");

        // Loop through both hands and use the first firework found
        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem) {
                player.swing(hand);                    // Play hand swing animation
                gameMode.useItem(player, hand);        // Trigger item use on server
                break;
            }
        }

        // Reset sequence after using the firework
        startEasyFly = false;
        actionStep = ActionStep.START_MOMENTUM;
    }

    /**
     * Second step — sends a network packet to start Elytra flight.
     *
     * @param player The local player.
     */
    private void handleStartFlying(LocalPlayer player) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player flight");

        // Send packet to initiate Elytra flight server-side
        player.connection.send(new ServerboundPlayerCommandPacket(
                player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));

        // Move to next step (use firework)
        actionStep = ActionStep.USE_FIREWORK;
    }

    /**
     * First step — gives the player a slight upward motion to help
     * trigger flight conditions.
     *
     * @param player The local player.
     */
    private void handleMomentumAction(LocalPlayer player) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player momentum");

        // Add a small vertical lift to simulate jump/boost
        Vec3 deltaMovement = player.getDeltaMovement();
        player.setDeltaMovement(deltaMovement.x, 0.15, deltaMovement.z);

        // Proceed to Elytra flight start
        actionStep = ActionStep.START_FLYING;
    }

    /**
     * Determines whether the player can start Easy Fly mode.
     * Checks multiple conditions such as Elytra equipped,
     * player not flying, not in water, and clear crosshair.
     *
     * @param minecraft Minecraft instance.
     * @param options   Client options for input keys.
     * @param player    The local player.
     * @return {@code true} if all Easy Fly conditions are met.
     */
    private boolean canInitiateEasyFly(Minecraft minecraft, Options options, LocalPlayer player) {
        KeyMapping easyFly = KeyMappingRegistry.EASY_FLY;
        return (allowDefaultKey(options) || easyFly.isDown()) // Check activation key
                && isCrosshairClear(minecraft)                 // Ensure player not targeting a block/entity
                && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA) // Elytra equipped
                && !player.swinging                            // Not attacking
                && !player.isFallFlying()                      // Not already flying
                && !player.isInWater()                         // Not underwater
                && !player.isUsingItem()                       // Not using other items
                && player.onGround()                           // Must be standing on ground
                && !ElytraControlUtils.isShouldDisableElytra()  // Elytra control not disabled
                && config.getEasyFly().getValue();              // Feature enabled in config
    }

    /**
     * Allows the use of the default "use item" key (right-click)
     * to initiate Easy Fly if enabled in configuration.
     *
     * @param options Client key mappings.
     * @return {@code true} if the default key is pressed and allowed.
     */
    private boolean allowDefaultKey(Options options) {
        return options.keyUse.isDown() && config.getEasyFlyAllowDefaultKey().getValue();
    }

    /**
     * Checks if the player is holding a firework rocket in either hand.
     * Required for the Easy Fly boost phase.
     *
     * @param player The local player.
     * @return {@code true} if a firework rocket is found.
     */
    private boolean hasFireworkInHand(LocalPlayer player) {
        for (InteractionHand hand : InteractionHand.values())
            if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem)
                return true;
        return false;
    }

    /**
     * Checks whether the player's crosshair is not pointing at any block
     * or entity (i.e., the target is empty space).
     *
     * @param minecraft Minecraft instance.
     * @return {@code true} if crosshair target is clear.
     */
    private boolean isCrosshairClear(Minecraft minecraft) {
        HitResult hitResult = minecraft.hitResult;
        return hitResult == null || hitResult.getType() == HitResult.Type.MISS;
    }

    /**
     * Enumeration representing each stage of the Easy Fly process.
     */
    enum ActionStep {
        /** Initial upward push for takeoff momentum. */
        START_MOMENTUM,

        /** Sends packet to initiate Elytra flight. */
        START_FLYING,

        /** Uses firework rocket to boost flight. */
        USE_FIREWORK
    }
}

