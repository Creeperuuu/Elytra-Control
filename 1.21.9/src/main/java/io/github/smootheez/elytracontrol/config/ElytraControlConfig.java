package io.github.smootheez.elytracontrol.config;

import io.github.smootheez.elytracontrol.util.*;
import io.github.smootheez.smoothiezapi.api.*;
import io.github.smootheez.smoothiezapi.config.option.*;

@Config(name = Constants.MOD_ID, autoGui = true)
public class ElytraControlConfig implements ConfigApi {
    private final BooleanOption debugMode = new BooleanOption("debug_mode", false);
    private final BooleanOption elytraControlDefault = new BooleanOption("elytra_control_default", true);
    private final BooleanOption disableElytra = new BooleanOption("disable_elytra", false);

    public BooleanOption getDebugMode() {
        return debugMode;
    }

    public BooleanOption getDisableElytra() {
        return disableElytra;
    }

    public BooleanOption getElytraControlDefault() {
        return elytraControlDefault;
    }
}
