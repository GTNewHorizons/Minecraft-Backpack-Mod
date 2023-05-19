package de.eydamos.backpack.inventory.container;

import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import de.eydamos.backpack.inventory.ISaveableInventory;
import de.eydamos.backpack.inventory.InventoryCraftingGrid;
import de.eydamos.backpack.inventory.InventoryRecipes;
import de.eydamos.backpack.inventory.slot.SlotPhantom;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

@ChestContainer
public class ContainerWorkbenchBackpack extends ContainerAdvanced {

    protected InventoryRecipes recipes;
    protected InventoryCraftingGrid craftingGrid;
    public IInventory craftResult = new InventoryCraftResult();
    protected World worldObj;
    protected boolean intelligent = false;
    protected boolean saveMode = false;

    public ContainerWorkbenchBackpack(InventoryCraftingGrid craftingMatrix) {
        craftingGrid = craftingMatrix;
        craftingGrid.setEventHandler(this);
    }

    /**
     *
     * @param inventories Array of inventories. Expected are: 0 PlayerInventory, 1 BackpackInventory, 2
     *                    InventoryCraftingGrid, 3 InventoryRecipes
     * @param save        the backpack save
     */
    public ContainerWorkbenchBackpack(IInventory[] inventories, BackpackSave save) {
        super(inventories, save);

        worldObj = ((InventoryPlayer) inventories[0]).player.worldObj;
        craftingGrid = (InventoryCraftingGrid) inventories[2];
        craftingGrid.setEventHandler(this);

        if (inventories[3] instanceof InventoryRecipes) {
            intelligent = true;
            recipes = (InventoryRecipes) inventories[3];
            recipes.setEventHandler(this);
        }

        onCraftMatrixChanged(craftingGrid);
    }

    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int modifier, EntityPlayer player) {
        Slot slot = slotIndex < 0 ? null : (Slot) inventorySlots.get(slotIndex);
        if (slot instanceof SlotPhantom) {
            if (slot.inventory == recipes) {
                if (BackpackUtil.isServerSide(player.worldObj)) {
                    if (saveMode) {
                        saveMode = false;
                        if (getSlot(0).getStack() != null) {
                            recipes.setInventorySlotContents(slot.getSlotIndex(), getSlot(0).getStack().copy());
                        }
                    } else {
                        recipes.loadRecipe(slot.getSlotIndex());
                    }
                }
            } else {
                slotPhantomClick(slot, mouseButton, modifier, player.inventory.getItemStack());
            }
            return null;
        }
        return super.slotClick(slotIndex, mouseButton, modifier, player);
    }

    /**
     * Sets the content of the crafting result slot based on the content of the crafting grid.
     *
     * @param inventory The inventory that has changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventory) {
        if (inventory == craftingGrid || inventory == craftResult) {
            craftResult.setInventorySlotContents(
                    0,
                    CraftingManager.getInstance().findMatchingRecipe(craftingGrid, worldObj));
        }
        detectAndSendChanges();
    }

    @Override
    public boolean func_94530_a(ItemStack itemStack, Slot slot) {
        return slot.inventory != craftResult && super.func_94530_a(itemStack, slot);
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        if (BackpackUtil.isServerSide(entityPlayer.worldObj) && backpackSave != null) {
            backpackSave.setManualSaving();
            if (inventory instanceof ISaveableInventory) {
                ((ISaveableInventory) inventory).writeToNBT(backpackSave);
            }
            if (craftingGrid != null) {
                ((ISaveableInventory) craftingGrid).writeToNBT(backpackSave);
            }
            if (recipes != null) {
                ((ISaveableInventory) recipes).writeToNBT(backpackSave);
            }
            backpackSave.save();

            new PlayerSave(entityPlayer).unsetPersonalBackpackOpen();
        }
        if (inventory != null) {
            inventory.closeInventory();
        }
        super.onContainerClosed(entityPlayer);
    }

    /**
     * Clears the craft matrix.
     */
    public void clearCraftMatrix() {
        for (int i = boundaries.get(Boundaries.CRAFTING); i < boundaries.get(Boundaries.CRAFTING_END); i++) {
            putStackInSlot(i, null);
        }
    }

    /**
     * Sets the save mode to true so a slot click in the recipe matrix will save the ItemStack from the result slot.
     */
    public void setSaveMode() {
        Slot slot = getSlot(0);
        if (slot.getHasStack()) {
            saveMode = true;
        }
    }

    public boolean isIntelligent() {
        return intelligent;
    }

    @ContainerSectionCallback
    public Map<ContainerSection, List<Slot>> getContainerSections() {
        return super.getContainerSections();
    }
}
