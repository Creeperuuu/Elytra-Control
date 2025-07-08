package dev.smootheez.elytracontrol.config;

import dev.smootheez.elytracontrol.*;
import dev.smootheez.elytracontrol.config.option.*;
import dev.smootheez.scl.api.*;
import dev.smootheez.scl.config.*;

@Config(name = Constants.MOD_ID, gui = true)
public class ElytraControlConfig {
    public static final ConfigOption<Boolean> elytraLock = ConfigOption.create("elytraLock", true);
    public static final ConfigOption<Boolean> elytraCancel = ConfigOption.create("elytraCancel", true);
    public static final ConfigOption<Boolean> easyFlight = ConfigOption.create("easyFlight", false);
    public static final ConfigOption<Boolean> easyFlightMessage = ConfigOption.create("easyFlightMessage", true);
    public static final ConfigOption<Boolean> elytraLockMessage = ConfigOption.create("elytraLockMessage", true);

    public static final ConfigOption<LockIconMode> lockIconMode = ConfigOption.create("iconLockMode", LockIconMode.ICON_TEXT);
}
