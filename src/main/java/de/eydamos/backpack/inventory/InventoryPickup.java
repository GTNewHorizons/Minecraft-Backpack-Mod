package de.eydamos.backpack.inventory;

import net.minecraft.item.ItemStack;

import de.eydamos.backpack.helper.InventoryHelper;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.BackpackSave;

public class InventoryPickup extends AbstractInventoryBackpack<BackpackSave> {

    protected boolean changeable;

    public InventoryPickup() {
        stackLimit = 1;
        defaultName = Localizations.INVENTORY_PICKUP;
        inventoryContent = new ItemStack[9];
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack newContent) {
        if (changeable) {
            super.setInventorySlotContents(slotIndex, newContent);

            if (eventHandler != null) {
                eventHandler.onCraftMatrixChanged(this);
            }
        }
    }

    @Override
    public void readFromNBT(BackpackSave backpackSave) {
        InventoryHelper.readInventory(backpackSave, Constants.NBT.INVENTORY_PICKUP_ITEMS, inventoryContent);
    }

    @Override
    public void writeToNBT(BackpackSave backpackSave) {
        if (isDirty) {
            InventoryHelper.writeInventory(backpackSave, Constants.NBT.INVENTORY_PICKUP_ITEMS, inventoryContent);

            isDirty = false;
        }
    }

    public void setInventoryContent(ItemStack backpack) {
        if (backpack == null) {
            inventoryContent = new ItemStack[9];
            changeable = false;
        } else {
            readFromNBT(new BackpackSave(backpack));
            changeable = true;
        }
    }
}
