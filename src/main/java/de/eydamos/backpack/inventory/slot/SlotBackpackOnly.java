package de.eydamos.backpack.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.item.ItemBackpackBase;

public class SlotBackpackOnly extends Slot {

    public SlotBackpackOnly(IInventory inventory, int slotIndex, int posX, int posY) {
        super(inventory, slotIndex, posX, posY);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for backpacks.
     */
    @Override
    public boolean isItemValid(ItemStack is) {
        return is != null && is.getItem() instanceof ItemBackpackBase;
    }
}
