package de.eydamos.backpack.inventory;

import net.minecraft.item.ItemStack;

import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.PlayerSave;

public class InventoryBackpackSlot extends AbstractInventoryBackpack<PlayerSave> {

    public InventoryBackpackSlot() {
        stackLimit = 1;
        defaultName = Localizations.INVENTORY_PERSONAL;
        inventoryContent = new ItemStack[1];
    }

    public InventoryBackpackSlot(PlayerSave playerSave) {
        this();
        if (playerSave != null) {
            readFromNBT(playerSave);
        }
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack newContent) {
        super.setInventorySlotContents(slotIndex, newContent);

        if (eventHandler != null) {
            eventHandler.onCraftMatrixChanged(this);
        }
    }

    @Override
    public void readFromNBT(PlayerSave playerSave) {
        inventoryContent[0] = playerSave.getPersonalBackpack();
    }

    @Override
    public void writeToNBT(PlayerSave playerSave) {
        if (isDirty) {
            playerSave.setPersonalBackpack(inventoryContent[0]);

            isDirty = false;
        }
    }
}
