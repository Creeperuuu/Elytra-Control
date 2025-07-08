package dev.smootheez.elytracontrol.registry;

import com.mojang.blaze3d.platform.*;
import net.minecraft.client.*;

public class KeyBindsRegistry {
    private static final String KEYBIND_CATEGORY = "key.categories.elytracontrol";

    public static final KeyMapping STOP_FLYING = new KeyMapping(
            "key.elytracontrol.stopFlying",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_SPACE,
            KEYBIND_CATEGORY
    );
    public static final KeyMapping START_FLYING = new KeyMapping(
            "key.elytracontrol.startFlying",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_SPACE,
            KEYBIND_CATEGORY
    );

}

