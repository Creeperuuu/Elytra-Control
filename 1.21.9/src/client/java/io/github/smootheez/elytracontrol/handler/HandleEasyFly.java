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

@Environment(EnvType.CLIENT)
public class HandleEasyFly implements ClientTickEvents.EndTick {
    private final ElytraControlConfig config = ConfigManager.getConfig(ElytraControlConfig.class);
    private ActionStep actionStep = ActionStep.START_MOMENTUM;
    private boolean startEasyFly = false;

    @Override
    public void onEndTick(Minecraft client) {
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null) return;

        if (canInitiateEasyFly(client, client.options, player) && hasFireworkInHand(player)) startEasyFly = true;

        startEasyFlyAction(player, gameMode);
    }

    private void startEasyFlyAction(LocalPlayer player, MultiPlayerGameMode gameMode) {
        if (startEasyFly)
            switch (actionStep) {
                case START_MOMENTUM:
                    handleMomentumAction(player);
                    break;
                case START_FLYING:
                    handleStartFlying(player);
                    break;
                case USE_FIREWORK:
                    handleUseFirework(player, gameMode);
                    break;
            }

    }

    private void handleUseFirework(LocalPlayer player, MultiPlayerGameMode gameMode) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting firework");
        for (InteractionHand hand : InteractionHand.values())
            if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem) {
                player.swing(hand);
                gameMode.useItem(player, hand);
                break;
            }
        startEasyFly = false;
        actionStep = ActionStep.START_MOMENTUM;
    }

    private void handleStartFlying(LocalPlayer player) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player flight");
        player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        actionStep = ActionStep.USE_FIREWORK;
    }

    private void handleMomentumAction(LocalPlayer player) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player momentum");
        Vec3 deltaMovement = player.getDeltaMovement();
        player.setDeltaMovement(deltaMovement.x, 0.15, deltaMovement.z);
        actionStep = ActionStep.START_FLYING;
    }

    private boolean canInitiateEasyFly(Minecraft minecraft, Options options, LocalPlayer player) {
        KeyMapping easyFly = KeyMappingRegistry.EASY_FLY;
        return (allowDefaultKey(options) || easyFly.isDown())
                && isCrosshairClear(minecraft)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
                && !player.swinging
                && !player.isFallFlying()
                && !player.isInWater()
                && !player.isUsingItem()
                && player.onGround()
                && !ElytraControlUtils.isShouldDisableElytra()
                && config.getEasyFly().getValue();
    }

    private boolean allowDefaultKey(Options options) {
        return options.keyUse.isDown() && config.getEasyFlyAllowDefaultKey().getValue();
    }

    private boolean hasFireworkInHand(LocalPlayer player) {
        for (InteractionHand hand : InteractionHand.values())
            if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem) return true;
        return false;
    }

    private boolean isCrosshairClear(Minecraft minecraft) {
        HitResult hitResult = minecraft.hitResult;
        return hitResult == null || hitResult.getType() == HitResult.Type.MISS;
    }

    enum ActionStep {
        START_MOMENTUM,
        START_FLYING,
        USE_FIREWORK
    }
}
