package dev.smootheez.elytracontrol.config;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.scl.api.*;
import dev.smootheez.scl.config.*;

@Config(name = Constants.MOD_ID, gui = true)
public class ElytraControlConfig {
    public static final ConfigOption<Boolean> DEFAULT_ELTRA_CONTROL = ConfigOption.create("defaultElytraControl", true);
    public static final ConfigOption<Boolean> ALLOW_FLYING = ConfigOption.create("allowFlying", true);
    public static final ConfigOption<Boolean> DISABLE_NOTIFICATION = ConfigOption.create("disableNotification", true);
    public static final ConfigOption<Boolean> EASY_FLY = ConfigOption.create("easyFly", false);
}
