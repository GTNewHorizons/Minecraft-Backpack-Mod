package de.eydamos.backpack.helper;

import java.util.Arrays;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;

public class InventoryHelper {

    /**
     * This method will read the ItemStacks from the given ItemStack's NBTCompound with the given key and load them into
     * the given inventory.
     *
     * @param backpackSave The BackpackSave with the NBTCompound to read from.
     * @param name         The name of the key in the NBTCompound.
     * @param inventory    The inventory to fill with the ItemStacks.
     */
    public static void readInventory(BackpackSave backpackSave, String name, ItemStack[] inventory) {
        readInventory(backpackSave, name, inventory, true);
    }

    /**
     * This method will read the ItemStacks from the given ItemStack's NBTCompound with the given key and load them into
     * the given inventory.
     *
     * @param backpackSave   The BackpackSave with the NBTCompound to read from.
     * @param inventoryName  The name of the key in the NBTCompound.
     * @param inventory      The inventory to fill with the ItemStacks.
     * @param clearInventory If the inventory should be cleared before reading the content.
     */
    public static void readInventory(BackpackSave backpackSave, String inventoryName, ItemStack[] inventory,
            boolean clearInventory) {
        if (clearInventory) {
            Arrays.fill(inventory, null);
        }

        NBTTagList inventoryList = backpackSave.getInventory(inventoryName);
        for (int i = 0; i < inventoryList.tagCount(); i++) {
            NBTTagCompound slotEntry = inventoryList.getCompoundTagAt(i);
            int slot = slotEntry.getByte(Constants.NBT.SLOT) & 0xff;

            if (slot < inventory.length) {
                inventory[slot] = ItemStack.loadItemStackFromNBT(slotEntry);
            }
        }
    }

    /**
     * Will save the ItemStacks from the given Inventory in the NBTCompound of the given ItemStack under the given key.
     *
     * @param backpackSave  The backpackSave to write on.
     * @param inventoryName The name of the key in the NBTCompound.
     * @param inventory     The inventory to read from.
     */
    public static void writeInventory(BackpackSave backpackSave, String inventoryName, ItemStack[] inventory) {
        NBTTagList inventoryList = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null) {
                NBTTagCompound slotEntry = new NBTTagCompound();
                slotEntry.setByte(Constants.NBT.SLOT, (byte) i);
                inventory[i].writeToNBT(slotEntry);
                inventoryList.appendTag(slotEntry);
            }
        }

        backpackSave.setInventory(inventoryName, inventoryList);
    }
}
