package io.github.smootheez.elytracontrol.util;

import io.github.smootheez.elytracontrol.config.*;
import io.github.smootheez.smoothiezapi.config.*;

public final class DebugMode {
    private DebugMode() {}

    public static void sendLoggerInfo(String message) {
        if (Boolean.TRUE.equals(ConfigManager.getConfig(ElytraControlConfig.class).getDebugMode().getValue())) {
            Constants.LOGGER.info(message);
        }
    }
}
