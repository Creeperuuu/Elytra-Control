package dev.smootheez.elytracontrol.config;

import dev.smootheez.elytracontrol.Constants;
import dev.smootheez.elytracontrol.config.option.LockIconMode;
import dev.smootheez.scl.annotation.Config;
import dev.smootheez.scl.api.ConfigProvider;
import dev.smootheez.scl.config.ConfigOption;

@Config(Constants.MOD_ID)
public class ElytraControlConfig implements ConfigProvider {
    public static final ConfigOption<Boolean> elytraLock = ConfigOption.create("elytraLock", true);
    public static final ConfigOption<Boolean> elytraCancel = ConfigOption.create("elytraCancel", true);
    public static final ConfigOption<Boolean> easyFlight = ConfigOption.create("easyFlight", false);
    public static final ConfigOption<Boolean> easyFlightMessage = ConfigOption.create("easyFlightMessage", true);
    public static final ConfigOption<Boolean> elytraLockMessage = ConfigOption.create("elytraLockMessage", true);

    public static final ConfigOption<LockIconMode> lockIconMode = ConfigOption.create("iconLockMode", LockIconMode.ICON_TEXT);
}
