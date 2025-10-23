package io.github.smootheez.elytracontrol;

import io.github.smootheez.elytracontrol.handler.*;
import io.github.smootheez.elytracontrol.registry.*;
import io.github.smootheez.elytracontrol.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;

@Environment(EnvType.CLIENT)
public class ElytraControlClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Initializing Client " + Constants.MOD_NAME + "(" + Constants.MOD_ID + ")...");
        KeyMappingRegistry.registerKeyMappings();
        ClientTickEvents.END_CLIENT_TICK.register(new HandleEndClientTick());
    }
}
