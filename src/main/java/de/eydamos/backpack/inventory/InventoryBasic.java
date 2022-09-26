package de.eydamos.backpack.inventory;

import de.eydamos.backpack.saves.BackpackSave;
import net.minecraft.item.ItemStack;

public class InventoryBasic extends AbstractInventoryBackpack<BackpackSave> {

    public InventoryBasic(String name, boolean translated, int slots) {
        if (translated) {
            customName = name;
        } else {
            defaultName = name;
        }
        inventoryContent = new ItemStack[slots];
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack newContent) {
        super.setInventorySlotContents(slotIndex, newContent);

        if (eventHandler != null) {
            eventHandler.onCraftMatrixChanged(this);
        }
    }

    @Override
    public void readFromNBT(BackpackSave backpackSave) {}

    @Override
    public void writeToNBT(BackpackSave backpackSave) {}
}
