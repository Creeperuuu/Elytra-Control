package dev.smootheez.elytracontrol.helpers;

import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper {
    public static boolean isPlayerGliding(PlayerEntity player) {
        return player.isGliding();
    }
}
