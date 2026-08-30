package io.github.smootheez.elytracontrol.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

/**
 * Client-side helper for swapping the item in the chest armor slot with an
 * Elytra or a chestplate from the player's inventory.
 *
 * The swap is performed through normal inventory click packets, so no
 * server-side mod is required.
 */
public final class ElytraArmorSwap {
    private static final int CHEST_MENU_SLOT = 6;

    private ElytraArmorSwap() {}

    /**
     * Swaps Elytra <-> chestplate using the player's normal inventory menu.
     *
     * @return true when a swap was initiated, false when no suitable item
     *         was available.
     */
    public static boolean toggle(Minecraft client, LocalPlayer player) {
        if (client.gameMode == null || player.isSpectator()) return false;

        ItemStack equipped = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean wearingElytra = equipped.getItem() instanceof ElytraItem;
        boolean wearingChestplate = isChestplate(equipped);

        if (!wearingElytra && !wearingChestplate) return false;

        int inventorySlot = findSwapItem(player, wearingElytra);
        if (inventorySlot < 0) return false;

        // Three normal pickup clicks reproduce the same operation as
        // manually swapping an item between the chest slot and inventory.
        int syncId = player.inventoryMenu.containerId;
        client.gameMode.handleInventoryMouseClick(syncId, CHEST_MENU_SLOT, 0, ClickType.PICKUP, player);
        client.gameMode.handleInventoryMouseClick(syncId, inventorySlot, 0, ClickType.PICKUP, player);
        client.gameMode.handleInventoryMouseClick(syncId, CHEST_MENU_SLOT, 0, ClickType.PICKUP, player);

        return true;
    }

    private static int findSwapItem(LocalPlayer player, boolean needChestplate) {
        var inventory = player.getInventory();

        // Only search the normal inventory (0-35). Armor/offhand slots are
        // deliberately excluded because they are represented separately in
        // the player inventory menu.
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (needChestplate) {
                if (isChestplate(stack)) return slot + 9;
            } else if (stack.getItem() instanceof ElytraItem) {
                return slot + 9;
            }
        }

        return -1;
    }

    private static boolean isChestplate(ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armorItem)) return false;
        return armorItem.getType() == ArmorItem.Type.CHESTPLATE;
    }
}
