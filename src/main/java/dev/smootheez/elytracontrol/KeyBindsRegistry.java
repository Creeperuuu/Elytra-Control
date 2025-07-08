package dev.smootheez.elytracontrol;

import com.mojang.blaze3d.platform.*;
import net.minecraft.client.*;

public class KeyBindsRegistry {
    private static final String KEYBIND_CATEGORY = "key.categories.elytracontrol";

    public static KeyMapping CONTROL_FLYING = new KeyMapping(
            "key.elytracontrol.controlFlying",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_SPACE,
            KEYBIND_CATEGORY
    );
}

