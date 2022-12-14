package de.eydamos.backpack.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotPhantom extends Slot {

    public SlotPhantom(IInventory inventory, int slotIndex, int posX, int posY) {
        super(inventory, slotIndex, posX, posY);
    }

    /**
     * If the stack in this slot can be changed.
     *
     * @return True if the slot can be changed, false otherwise.
     */
    public boolean canChangeStack() {
        return true;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }
}
