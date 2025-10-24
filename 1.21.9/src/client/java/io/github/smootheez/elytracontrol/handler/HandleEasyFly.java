package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
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
        if (player == null) return;

        if (canInitiateEasyFly(client)) {
            for (InteractionHand hand : InteractionHand.values())
                if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem) {
                    startEasyFly = true;
                    break;
                }
        }

        if (startEasyFly) {
            switch (actionStep) {
                case START_MOMENTUM:
                    handleMomentumAction(client);
                    break;
                case START_FLYING:
                    handleStartFlying(client);
                    break;
                case USE_FIREWORK:
                    handleUseFirework(client);
                    break;
            }
        }
    }

    private void handleUseFirework(Minecraft client) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting firework");
        LocalPlayer player = client.player;
        MultiPlayerGameMode gameMode = client.gameMode;
        if (player == null || gameMode == null) return;
        for (InteractionHand hand : InteractionHand.values()) {
            if (player.getItemInHand(hand).getItem() instanceof FireworkRocketItem) {
                player.swing(hand);
                gameMode.useItem(player, hand);
                break;
            }
        }
        startEasyFly = false;
        actionStep = ActionStep.START_MOMENTUM;
    }

    private void handleStartFlying(Minecraft client) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player flight");
        LocalPlayer player = client.player;
        if (player == null) return;
        player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        actionStep = ActionStep.USE_FIREWORK;
    }

    private void handleMomentumAction(Minecraft client) {
        DebugMode.sendLoggerInfo("[Handle Easy Fly] Starting player momentum");
        LocalPlayer player = client.player;
        if (player == null) return;
        Vec3 deltaMovement = player.getDeltaMovement();
        player.setDeltaMovement(deltaMovement.x, 0.15, deltaMovement.z);
        actionStep = ActionStep.START_FLYING;
    }

    private boolean canInitiateEasyFly(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        Options options = minecraft.options;

        return player != null
                && options.keyUse.isDown()
                && isCrosshairClear(minecraft)
                && player.getItemBySlot(EquipmentSlot.CHEST).is(Items.ELYTRA)
                && !player.swinging
                && !player.isFallFlying()
                && !player.isInWater()
                && !player.isUsingItem()
                && player.onGround()
                && !HandleElytraControl.isShouldDisableElytra();
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
