package de.eydamos.backpack.inventory;

import net.minecraft.item.ItemStack;

import de.eydamos.backpack.helper.InventoryHelper;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;

public class InventoryBackpack extends AbstractInventoryBackpack<BackpackSave> {

    public InventoryBackpack(String nameDefault, String nameCustom) {
        defaultName = nameDefault;
        customName = nameCustom;
    }

    /**
     * Read name, size and content of inventory from NBTTagCompound.
     *
     * @param backpackSave the backpack save
     */
    @Override
    public void readFromNBT(BackpackSave backpackSave) {
        int size = backpackSave.getSize();
        inventoryContent = new ItemStack[size];

        InventoryHelper.readInventory(backpackSave, Constants.NBT.INVENTORY_BACKPACK, inventoryContent);
    }

    /**
     * If inventory isDirty (something has changed) write name, site and content of inventory to NBTTagCompound.
     *
     * @param backpackSave the backpack save
     */
    @Override
    public void writeToNBT(BackpackSave backpackSave) {
        if (isDirty) {
            backpackSave.setSize(getSizeInventory());

            InventoryHelper.writeInventory(backpackSave, Constants.NBT.INVENTORY_BACKPACK, inventoryContent);

            isDirty = false;
        }
    }
}
