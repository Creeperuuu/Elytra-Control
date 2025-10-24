package io.github.smootheez.elytracontrol.handler;

import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.minecraft.client.*;

@Environment(EnvType.CLIENT)
public class HandleEasyFly implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(Minecraft client) {
    }
}
