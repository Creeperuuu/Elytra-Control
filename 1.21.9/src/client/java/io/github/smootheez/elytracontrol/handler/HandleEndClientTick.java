package io.github.smootheez.elytracontrol.handler;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.smoothiezapi.config.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;
import net.minecraft.client.player.*;

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

        // TODO: Fix this logic, somehow it didnt behave as expected
        if (options.keyJump.consumeClick()
                && elytraTime > randomNumber
                && isPlayerFlying
                && Boolean.TRUE.equals(ConfigManager.getConfig(ElytraControlConfig.class).getElytraControlDefault().getValue())) {
            clientPlayer.stopFallFlying();
        }

        if (isPlayerFlying && !options.keyJump.isDown())
            elytraTime = (elytraTime + 1) % 1000;
        else elytraTime = 0;
    }
}
