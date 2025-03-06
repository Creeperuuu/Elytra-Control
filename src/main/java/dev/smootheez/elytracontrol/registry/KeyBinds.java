package dev.smootheez.elytracontrol.registry;

import dev.smootheez.elytracontrol.Constants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class KeyBinds {
    public static final KeyBinding elytraToggleKey = new KeyBinding(
            "key." + Constants.MOD_ID + ".elytraToggle",
            InputUtil.GLFW_KEY_V,
            "key.category.elytracontrol"
    );

    public static final KeyBinding easyFlightToggleKey = new KeyBinding(
            "key." + Constants.MOD_ID + ".easyFlightToggle",
            InputUtil.UNKNOWN_KEY.getCode(),
            "key.category.elytracontrol"
    );

    public static void registerKeyBinds() {
        KeyBindingHelper.registerKeyBinding(elytraToggleKey);
        KeyBindingHelper.registerKeyBinding(easyFlightToggleKey);
    }
}
