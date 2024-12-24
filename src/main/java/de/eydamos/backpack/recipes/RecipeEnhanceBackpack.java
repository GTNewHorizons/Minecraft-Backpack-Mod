package de.eydamos.backpack.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.item.ItemLeather;
import de.eydamos.backpack.item.ItemsBackpack;

public class RecipeEnhanceBackpack implements IRecipe {

    private ItemStack result;

    @Override
    public boolean matches(InventoryCrafting craftingGridInventory, World world) {
        result = null;
        ItemStack backpack = null;

        if (craftingGridInventory.getSizeInventory() < 9) {
            return false;
        }

        ItemStack slotStack;
        for (int i = 0; i < craftingGridInventory.getSizeInventory(); i++) {
            slotStack = craftingGridInventory.getStackInSlot(i);

            if (slotStack != null) {
                if (i == 4) {
                    if (!(slotStack.getItem() instanceof ItemBackpackBase)) {
                        return false;
                    }
                    if (slotStack.getItemDamage() / 100 > 1) {
                        return false;
                    }
                    backpack = slotStack;
                } else {
                    if (!(slotStack.getItem() instanceof ItemLeather)) {
                        return false;
                    }
                    if (slotStack.getItem() != ItemsBackpack.tannedLeather) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        if (backpack != null) {
            result = backpack.copy();
            result.setItemDamage(result.getItemDamage() + 100);
        }

        return result != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGridInventory) {
        return result.copy();
    }

    @Override
    public int getRecipeSize() {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }
}
