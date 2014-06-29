package de.eydamos.backpack.inventory.container;

import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import de.eydamos.backpack.inventory.AbstractInventoryBackpack;
import de.eydamos.backpack.inventory.ISaveableInventory;
import de.eydamos.backpack.inventory.InventoryPickup;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;

@ChestContainer
public class ContainerPersonalSlot extends ContainerAdvanced {
    protected InventoryPickup inventoryPickup;

    public ContainerPersonalSlot(AbstractInventoryBackpack slotInventory, InventoryPickup pickupInventory) {
        super(slotInventory);
        slotInventory.setEventHandler(this);
        inventoryPickup = pickupInventory;
        inventoryPickup.setEventHandler(this);
        inventoryPickup.openInventory();

        onCraftMatrixChanged(inventory);
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public void onCraftMatrixChanged(IInventory changedInventory) {
        if(changedInventory == inventory) {
            inventoryPickup.setInventoryContent(inventory.getStackInSlot(0));
        } else if(changedInventory == inventoryPickup) {
            inventoryPickup.writeToNBT(new BackpackSave(inventory.getStackInSlot(0)));
        }
        super.onCraftMatrixChanged(changedInventory);
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        if(BackpackUtil.isServerSide(entityPlayer.worldObj)) {
            if(inventory instanceof ISaveableInventory) {
                ((ISaveableInventory) inventory).writeToNBT(new PlayerSave(entityPlayer));
            }
        }
        inventory.closeInventory();
        inventoryPickup.closeInventory();
    }

    public IInventory getInventoryPickup() {
        return inventoryPickup;
    }
}
