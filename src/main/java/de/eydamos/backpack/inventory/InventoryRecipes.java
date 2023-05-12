package de.eydamos.backpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.helper.InventoryHelper;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.BackpackSave;

public class InventoryRecipes extends InventoryBasic {

    protected IInventory craftingGrid;
    protected ItemStack[][] recipesIngredients = new ItemStack[9][9];

    public InventoryRecipes(IInventory craftingMatrix) {
        super(Localizations.INVENTORY_RECIPES, false, 9);
        craftingGrid = craftingMatrix;
    }

    @Override
    public void setInventorySlotContents(int slotIndex, ItemStack newContent) {
        super.setInventorySlotContents(slotIndex, newContent);
        for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
            ItemStack itemStack = craftingGrid.getStackInSlot(i);
            recipesIngredients[slotIndex][i] = itemStack == null ? null : itemStack.copy();
        }
    }

    @Override
    public void readFromNBT(BackpackSave backpackSave) {
        InventoryHelper.readInventory(backpackSave, Constants.NBT.INVENTORY_RECIPES, inventoryContent);
        for (int i = 0; i < recipesIngredients.length; i++) {
            InventoryHelper.readInventory(backpackSave, Constants.NBT.INVENTORY_RECIPE + i, recipesIngredients[i]);
        }
    }

    @Override
    public void writeToNBT(BackpackSave backpackSave) {
        InventoryHelper.writeInventory(backpackSave, Constants.NBT.INVENTORY_RECIPES, inventoryContent);
        for (int i = 0; i < recipesIngredients.length; i++) {
            InventoryHelper.writeInventory(backpackSave, Constants.NBT.INVENTORY_RECIPE + i, recipesIngredients[i]);
        }
    }

    public void loadRecipe(int slotIndex) {
        for (int i = 0; i < recipesIngredients[slotIndex].length; i++) {
            craftingGrid.setInventorySlotContents(i, recipesIngredients[slotIndex][i]);
        }
    }
}
