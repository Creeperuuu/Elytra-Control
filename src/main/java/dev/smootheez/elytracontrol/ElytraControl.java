package dev.smootheez.elytracontrol;

import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class ElytraControl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Constants.LOGGER.info("Initializing " + Constants.MOD_ID + "...");
    }
}
