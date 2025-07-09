package dev.smootheez.elytracontrol.mixin;

import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(KeyMapping.class)
public abstract class KeyMappingMixin {

    @Shadow @Final private static Map<String, Integer> CATEGORY_SORT_ORDER;

    @Inject(method = "<clinit>", at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/KeyMapping;CATEGORY_SORT_ORDER:Ljava/util/Map;",
            shift = At.Shift.AFTER
    ))
    private static void addCustomCategory(CallbackInfo info) {
        CATEGORY_SORT_ORDER.put("key.categories.elytracontrol", CATEGORY_SORT_ORDER.size());
    }
}
