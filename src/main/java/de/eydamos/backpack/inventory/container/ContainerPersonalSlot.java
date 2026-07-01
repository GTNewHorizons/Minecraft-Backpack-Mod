package de.eydamos.backpack.inventory.container;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.inventory.AbstractInventoryBackpack;
import de.eydamos.backpack.inventory.ISaveableInventory;
import de.eydamos.backpack.inventory.InventoryPickup;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

@ChestContainer
public class ContainerPersonalSlot extends ContainerAdvanced {

    protected final InventoryPickup inventoryPickup;

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
        if (changedInventory == inventory) {
            inventoryPickup.setInventoryContent(inventory.getStackInSlot(0));
        } else if (changedInventory == inventoryPickup) {
            inventoryPickup.writeToNBT(new BackpackSave(inventory.getStackInSlot(0)));
        }
        super.onCraftMatrixChanged(changedInventory);
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        if (BackpackUtil.isServerSide(entityPlayer.worldObj)) {
            if (inventory instanceof ISaveableInventory) {
                PlayerSave playerSave = new PlayerSave(entityPlayer);
                ((ISaveableInventory) inventory).writeToNBT(playerSave);
                // Push the new worn backpack state so the inventory tab appears/disappears
                // right away instead of waiting for the periodic client sync.
                ItemStack backpack = playerSave.getPersonalBackpack();
                int damage = backpack != null ? backpack.getItemDamage() : -1;
                Backpack.packetHandler.networkWrapper.sendTo(
                        new MessagePersonalBackpack(entityPlayer.getUniqueID().toString(), damage),
                        (EntityPlayerMP) entityPlayer);
            }
        }
        inventory.closeInventory();
        inventoryPickup.closeInventory();
    }

    public IInventory getInventoryPickup() {
        return inventoryPickup;
    }

    @ContainerSectionCallback
    public Map<ContainerSection, List<Slot>> getContainerSections() {
        return super.getContainerSections();
    }
}
