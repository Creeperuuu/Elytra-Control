package io.github.smootheez.elytracontrol.registry;

import com.mojang.blaze3d.platform.*;
import io.github.smootheez.elytracontrol.util.*;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.keybinding.v1.*;
import net.minecraft.client.*;
import net.minecraft.resources.*;

@Environment(EnvType.CLIENT)
public final class KeyMappingRegistry {
    private KeyMappingRegistry() {}

    private static final KeyMapping.Category ELYTRA_CONTROL_CATEGORY = KeyMapping.Category.register(
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "elytracontrol")
    );

    public static final KeyMapping STOP_FLYING = new KeyMapping(
            "key." + Constants.MOD_ID + ".stop_flying",
            InputConstants.UNKNOWN.getValue(),
            ELYTRA_CONTROL_CATEGORY
    );

    public static final KeyMapping START_FLYING = new KeyMapping(
            "key." + Constants.MOD_ID + ".start_flying",
            InputConstants.UNKNOWN.getValue(),
            ELYTRA_CONTROL_CATEGORY
    );

    public static final KeyMapping DISABLE_ELYTRA = new KeyMapping(
            "key." + Constants.MOD_ID + ".disable_elytra",
            InputConstants.KEY_V,
            ELYTRA_CONTROL_CATEGORY
    );

    public static final KeyMapping EASY_FLY = new KeyMapping(
            "key." + Constants.MOD_ID + ".easy_fly",
            InputConstants.UNKNOWN.getValue(),
            ELYTRA_CONTROL_CATEGORY
    );

    public static void registerKeyMappings() {
        Constants.LOGGER.info("Registering Key Mappings for " + Constants.MOD_NAME + "(" + Constants.MOD_ID + ")...");
        KeyBindingHelper.registerKeyBinding(STOP_FLYING);
        KeyBindingHelper.registerKeyBinding(START_FLYING);
        KeyBindingHelper.registerKeyBinding(DISABLE_ELYTRA);
        KeyBindingHelper.registerKeyBinding(EASY_FLY);
    }
}
