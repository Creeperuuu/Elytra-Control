package dev.smootheez.elytracontrol;

import dev.smootheez.elytracontrol.config.*;
import dev.smootheez.elytracontrol.event.*;
import dev.smootheez.elytracontrol.gui.*;
import dev.smootheez.elytracontrol.registry.*;
import dev.smootheez.scl.registry.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.*;
import net.fabricmc.fabric.api.client.rendering.v1.hud.*;
import net.minecraft.util.*;


@Environment(EnvType.CLIENT)
public class ElytraControl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Initializing " + Constants.MOD_ID + "...");
        ConfigRegistry.registerConfig(ElytraControlConfig.class);

        KeyBinds.registerKeyBinds();

        HudElementRegistry.addFirst(Identifier.of(Constants.MOD_ID), new ElytraControlHud());
        ClientTickEvents.END_CLIENT_TICK.register(new EndTickEvent());
    }
}
