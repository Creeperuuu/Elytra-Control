package io.github.smootheez.elytracontrol.config;

import io.github.smootheez.elytracontrol.config.option.*;
import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.api.*;
import io.github.smootheez.smoothiezapi.config.option.*;

@Config(name = Constants.MOD_ID, autoGui = true)
public class ElytraControlConfig implements ConfigApi {
    private final BooleanOption debugMode = new BooleanOption("debug_mode", false);
    private final BooleanOption elytraControlAllowDefaultKey = new BooleanOption("elytra_control_allow_default_key", true);

    private final BooleanOption disableElytra = new BooleanOption("disable_elytra", false);
    private final BooleanOption disableElytraNotification = new BooleanOption("disable_elytra_notification", true);

    private final EnumOption<LockIconMode> lockIconMode = new EnumOption<>("lock_icon_mode", LockIconMode.ICON_TEXT);
    private final EnumOption<OverlayPosition> overlayPosition = new EnumOption<>("overlay_position", OverlayPosition.TOP_LEFT);

    private final BooleanOption easyFly = new BooleanOption("easy_fly", false);
    private final BooleanOption easyFlyAllowDefaultKey = new BooleanOption("easy_fly_allow_default_key", true);

    private final DoubleOption upwardVelocity = new DoubleOption("upward_velocity", 0.15, 0.12, 0.42);

    public BooleanOption getDebugMode() {
        return debugMode;
    }

    public BooleanOption getElytraControlAllowDefaultKey() {
        return elytraControlAllowDefaultKey;
    }

    public BooleanOption getDisableElytra() {
        return disableElytra;
    }

    public BooleanOption getDisableElytraNotification() {
        return disableElytraNotification;
    }

    public EnumOption<LockIconMode> getLockIconMode() {
        return lockIconMode;
    }

    public EnumOption<OverlayPosition> getOverlayPosition() {
        return overlayPosition;
    }

    public BooleanOption getEasyFly() {
        return easyFly;
    }

    public BooleanOption getEasyFlyAllowDefaultKey() {
        return easyFlyAllowDefaultKey;
    }

    public DoubleOption getUpwardVelocity() {
        return upwardVelocity;
    }
}
