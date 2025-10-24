package io.github.smootheez.elytracontrol.util;

public final class ElytraControlUtils {
    private ElytraControlUtils() {}

    private static boolean shouldDisableElytra = false;

    public static boolean isShouldDisableElytra() {
        return shouldDisableElytra;
    }

    public static void setShouldDisableElytra(boolean shouldDisableElytra) {
        ElytraControlUtils.shouldDisableElytra = shouldDisableElytra;
    }
}
