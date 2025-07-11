package dev.smootheez.elytracontrol.registry;

import com.mojang.blaze3d.platform.*;
import net.minecraft.client.*;

public class KeyBindsRegistry {
    private static final String KEYBIND_CATEGORY = "key.categories.elytracontrol";

    public static final KeyMapping STOP_FLYING = new KeyMapping(
            "key.elytracontrol.stopFlying",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KEYBIND_CATEGORY
    );
    public static final KeyMapping START_FLYING = new KeyMapping(
            "key.elytracontrol.startFlying",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KEYBIND_CATEGORY
    );
    public static final KeyMapping DISABLE_FLYING_TOGGLE = new KeyMapping(
            "key.elytracontrol.disableFlyingToggle",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_V,
            KEYBIND_CATEGORY
    );
    public static final KeyMapping EASY_FLY_TOGGLE = new KeyMapping(
            "key.elytracontrol.easyFlyToggle",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KEYBIND_CATEGORY
    );
    public static final KeyMapping EASY_FLY = new KeyMapping(
            "key.elytracontrol.easyFly",
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            KEYBIND_CATEGORY
    );
}

