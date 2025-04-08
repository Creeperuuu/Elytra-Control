package dev.smootheez.elytracontrol.handler;

import dev.smootheez.elytracontrol.config.ElytraControlConfig;
import dev.smootheez.elytracontrol.event.EndTickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;

@Environment(EnvType.CLIENT)
public class EasyFlightHandler {

    private static final int FIRST_JUMP_TICKS = 1;
    private static final int SECOND_JUMP_DELAY_TICKS = 1;
    private static final int SECOND_JUMP_TICKS = 1;
    private static final int USE_KEY_DELAY_TICKS = 1;
    private static final int USE_KEY_TICKS = 1;

    private static boolean isFlightSequenceActive = false;
    private static int currentTick = 0;
    private static boolean wasUseKeyPreviouslyPressed = false;

    public static void handleEasyFlight(MinecraftClient client) {
        if (client.player == null) return;

        boolean isUseKeyCurrentlyPressed = client.options.useKey.isPressed();

        if (!isFlightSequenceActive) {
            if (isUseKeyCurrentlyPressed && !wasUseKeyPreviouslyPressed && canInitiateFlight(client)) {
                startFlightSequence(client);
            }
        } else {
            continueFlightSequence(client);
        }

        wasUseKeyPreviouslyPressed = isUseKeyCurrentlyPressed;
    }

    private static boolean canInitiateFlight(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return false;

        boolean isWearingElytra = client.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA;
        return EndTickEvent.elytraToggle && ElytraControlConfig.easyFlight.getValue()
                && !client.player.isFallFlying() && client.player.isOnGround()
                && isHoldingFirework(client.player) && isWearingElytra
                && isCrosshairClear(client) && !client.interactionManager.getCurrentGameMode().isCreative()
                && EndTickEvent.easyFlightToggle && !client.player.isTouchingWater();
    }

    private static void startFlightSequence(MinecraftClient client) {
        isFlightSequenceActive = true;
        currentTick = 0;
        client.options.useKey.setPressed(false);
    }

    private static void continueFlightSequence(MinecraftClient client) {
        currentTick++;
        int totalSequenceTicks = calculateTotalSequenceTicks();

        if (isFirstJumpPhase()) {
            pressJumpKey(client);
        } else if (isSecondJumpPhase()) {
            pressJumpKey(client);
        } else if (isUseKeyPhase(totalSequenceTicks)) {
            pressUseKey(client);
        } else if (isSequenceComplete(totalSequenceTicks)) {
            resetFlightSequence(client);
        } else {
            releaseKeys(client);
        }
    }

    private static int calculateTotalSequenceTicks() {
        return FIRST_JUMP_TICKS + SECOND_JUMP_DELAY_TICKS + SECOND_JUMP_TICKS + USE_KEY_DELAY_TICKS + USE_KEY_TICKS;
    }

    private static boolean isFirstJumpPhase() {
        return currentTick <= FIRST_JUMP_TICKS;
    }

    private static boolean isSecondJumpPhase() {
        return currentTick == FIRST_JUMP_TICKS + SECOND_JUMP_DELAY_TICKS + SECOND_JUMP_TICKS;
    }

    private static boolean isUseKeyPhase(int totalSequenceTicks) {
        return currentTick == totalSequenceTicks - USE_KEY_TICKS;
    }

    private static boolean isSequenceComplete(int totalSequenceTicks) {
        return currentTick > totalSequenceTicks;
    }

    private static void pressJumpKey(MinecraftClient client) {
        client.options.jumpKey.setPressed(true);
    }

    private static void pressUseKey(MinecraftClient client) {
        client.options.useKey.setPressed(true);
    }

    private static void releaseKeys(MinecraftClient client) {
        client.options.jumpKey.setPressed(false);
        client.options.useKey.setPressed(false);
    }

    private static void resetFlightSequence(MinecraftClient client) {
        isFlightSequenceActive = false;
        releaseKeys(client);
    }

    private static boolean isHoldingFirework(ClientPlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        return mainHand.getItem() instanceof FireworkRocketItem || offHand.getItem() instanceof FireworkRocketItem;
    }

    private static boolean isCrosshairClear(MinecraftClient client) {
        HitResult hitResult = client.crosshairTarget;

        if (client.player == null) return false;

        ItemStack itemStack = client.player.getStackInHand(client.player.getActiveHand());

        ActionResult actionResult = itemStack.use(client.world, client.player, client.player.getActiveHand()).getResult();

        boolean isUsingItem = actionResult.isAccepted();
        boolean isSwingingHand = client.player.handSwinging;

        return hitResult == null || hitResult.getType() == HitResult.Type.MISS && !isUsingItem && !isSwingingHand;
    }
}