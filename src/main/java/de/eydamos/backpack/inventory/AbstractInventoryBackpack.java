package de.eydamos.backpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.saves.AbstractSave;

public abstract class AbstractInventoryBackpack<S extends AbstractSave> implements IInventory, ISaveableInventory<S> {

    protected ItemStack[] inventoryContent;
    protected String defaultName;
    protected String customName;
    protected int stackLimit = 64;
    protected boolean isDirty = false;
    protected ContainerAdvanced eventHandler;

    @Override
    public int getSizeInventory() {
        if (null == inventoryContent) {
            return 0;
        }
        return inventoryContent.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (null != inventoryContent) {
            return inventoryContent[slot];
        }
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack itemstack = null;

        if (getStackInSlot(slot) != null) {
            if (getStackInSlot(slot).stackSize <= amount) {
                itemstack = getStackInSlot(slot);
                setInventorySlotContents(slot, null);
            } else {
                itemstack = getStackInSlot(slot).splitStack(amount);

                if (getStackInSlot(slot).stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }

        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack itemstack = null;

        if (getStackInSlot(slot) != null) {
            itemstack = getStackInSlot(slot);
            inventoryContent[slot] = null;
        }

        return itemstack;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack newContent) {
        if (null != inventoryContent && inventoryContent.length > slotIndex) {
            inventoryContent[slotIndex] = newContent;

            if (newContent != null && newContent.stackSize > getInventoryStackLimit()) {
                newContent.stackSize = getInventoryStackLimit();
            }

            markDirty();
        }
    }

    @Override
    public String getInventoryName() {
        return hasCustomInventoryName() ? customName : defaultName;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return customName != null && customName.length() > 0;
    }

    @Override
    public int getInventoryStackLimit() {
        return stackLimit;
    }

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return true;
    }

    /**
     * Set the custom name of the backpack.
     *
     * @param name a custom name
     */
    public void setCustomName(String name) {
        customName = name;
        markDirty();
    }

    public void setEventHandler(ContainerAdvanced container) {
        eventHandler = container;
    }
}
