package de.eydamos.backpack.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.inventory.slot.SlotBackpack;
import de.eydamos.backpack.saves.BackpackSave;

public class ContainerPickup extends ContainerAdvanced {

    public ContainerPickup(IInventory inventoryToSave, BackpackSave save) {
        super(new IInventory[] { inventoryToSave }, save);

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            addSlotToContainer(new SlotBackpack(inventory, i, 0, 0));
        }
    }

    public boolean pickupItem(ItemStack itemStack) {
        if (mergeItemStack(itemStack, 0, inventorySlots.size(), false)) {
            inventory.markDirty();
            return true;
        }

        return false;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return true;
    }
}
