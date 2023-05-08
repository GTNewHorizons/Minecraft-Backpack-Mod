package de.eydamos.backpack.inventory;

import java.util.Arrays;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import de.eydamos.backpack.helper.InventoryHelper;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.util.BackpackUtil;

public class InventoryCraftingGrid extends InventoryCrafting implements ISaveableInventory<BackpackSave> {

    protected AbstractInventoryBackpack backpackInventory = null;
    protected boolean useInventoryMode = false;
    protected boolean isDirty = false;
    protected int[] mapping;

    public InventoryCraftingGrid(IInventory inventoryBackpack) {
        super(null, 3, 3);
        if (inventoryBackpack instanceof AbstractInventoryBackpack<?>) {
            backpackInventory = (AbstractInventoryBackpack) inventoryBackpack;
        }

        mapping = new int[getSizeInventory()];
        Arrays.fill(mapping, -1);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack returnStack = super.decrStackSize(slot, amount);
        markDirty();
        return returnStack;
    }

    @Override
    public void setInventorySlotContents(int slotPosition, ItemStack newItemStack) {
        if (useInventoryMode) {
            int correspondingSlot = findCorrespondingSlot(slotPosition);
            // if there is a corresponding slot set the item
            if (correspondingSlot != -1) {
                backpackInventory.setInventorySlotContents(correspondingSlot, newItemStack);
                return;
            }
        }
        super.setInventorySlotContents(slotPosition, newItemStack);
        markDirty();
    }

    @Override
    public void markDirty() {
        isDirty = true;
    }

    @Override
    public ItemStack getStackInSlot(int slotPosition) {
        if (useInventoryMode) {
            int correspondingSlot = findCorrespondingSlot(slotPosition);
            if (correspondingSlot == -1) {
                return null;
            } else {
                return backpackInventory.getStackInSlot(correspondingSlot);
            }
        }
        return super.getStackInSlot(slotPosition);
    }

    @Override
    public void readFromNBT(BackpackSave backpackSave) {
        InventoryHelper.readInventory(backpackSave, Constants.NBT.INVENTORY_CRAFTING_GRID, getStackList());
    }

    @Override
    public void writeToNBT(BackpackSave backpackSave) {
        if (isDirty) {
            InventoryHelper.writeInventory(backpackSave, Constants.NBT.INVENTORY_CRAFTING_GRID, getStackList());

            isDirty = false;
        }
    }

    protected ItemStack[] getStackList() {
        return ObfuscationReflectionHelper.getPrivateValue(InventoryCrafting.class, this, "stackList", "a");
    }

    public void setEventHandler(Container container) {
        ObfuscationReflectionHelper.setPrivateValue(InventoryCrafting.class, this, container, "eventHandler", "c");
    }

    /**
     * Sets if the craftMatrix should use the backpacks inventory to provide it's content.
     *
     * @param value The new value for the mode.
     */
    public void setUseInventoryMode(boolean value) {
        useInventoryMode = value;
        if (!value) {
            Arrays.fill(mapping, -1);
        }
    }

    /**
     * Will try to find a slot with the same ItemStack as the given slot from the craft matrix.
     *
     * @param recipeSlotPosition The index of the slot in the craft matrix.
     * @return The slot number with the same content or -1 if nothing was found.
     */
    protected int findCorrespondingSlot(int recipeSlotPosition) {
        if (mapping[recipeSlotPosition] != -1) {
            return mapping[recipeSlotPosition];
        }
        ItemStack craftingGridStack = super.getStackInSlot(recipeSlotPosition);
        for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
            ItemStack inventoryStack = backpackInventory.getStackInSlot(i);
            if (BackpackUtil.areStacksEqual(craftingGridStack, inventoryStack)) {
                mapping[recipeSlotPosition] = i;
                return i;
            }
        }
        for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
            ItemStack inventoryStack = backpackInventory.getStackInSlot(i);
            if (BackpackUtil.areStacksEqualByOD(craftingGridStack, inventoryStack)) {
                mapping[recipeSlotPosition] = i;
                return i;
            }
        }
        return -1;
    }
}
