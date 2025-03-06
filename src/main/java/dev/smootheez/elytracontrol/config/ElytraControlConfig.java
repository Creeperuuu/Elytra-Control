package dev.smootheez.elytracontrol.config;

import dev.smootheez.elytracontrol.Constants;
import dev.smootheez.elytracontrol.config.option.LockIconMode;
import dev.smootheez.scl.annotation.Config;
import dev.smootheez.scl.api.ConfigProvider;
import dev.smootheez.scl.config.ConfigOption;

@Config(Constants.MOD_ID)
public class ElytraControlConfig implements ConfigProvider {
    private static final ElytraControlConfig INSTANCE = new ElytraControlConfig();

    private final ConfigOption<Boolean> elytraLock = ConfigOption.create("elytraLock", true);
    private final ConfigOption<Boolean> elytraCancel = ConfigOption.create("elytraCancel", true);
    private final ConfigOption<Boolean> easyFlight = ConfigOption.create("easyFlight", false);
    private final ConfigOption<Boolean> easyFlightMessage = ConfigOption.create("easyFlightMessage", true);
    private final ConfigOption<Boolean> elytraLockMessage = ConfigOption.create("elytraLockMessage", true);

    private final ConfigOption<LockIconMode> lockIconMode = ConfigOption.create("iconLockMode", LockIconMode.ICON_TEXT);

    public static ElytraControlConfig getInstance() {
        return INSTANCE;
    }

    public ConfigOption<LockIconMode> getLockIconMode() {
        return lockIconMode;
    }

    public ConfigOption<Boolean> getElytraLock() {
        return elytraLock;
    }

    public ConfigOption<Boolean> getElytraCancel() {
        return elytraCancel;
    }

    public ConfigOption<Boolean> getEasyFlight() {
        return easyFlight;
    }

    public ConfigOption<Boolean> getEasyFlightMessage() {
        return easyFlightMessage;
    }

    public ConfigOption<Boolean> getElytraLockMessage() {
        return elytraLockMessage;
    }
}
