package de.eydamos.backpack.recipes;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import de.eydamos.backpack.item.ItemBackpack;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.util.BackpackUtil;

public class RecipeRecolorBackpack implements IRecipe {

    private final ArrayList<Item> allowedDyes = new ArrayList<>();
    private ItemStack result;

    public RecipeRecolorBackpack() {
        allowedDyes.add(Items.dye);
        allowedDyes.add(Items.leather);
        allowedDyes.add(ItemsBackpack.tannedLeather);
    }

    @Override
    public boolean matches(InventoryCrafting craftingGridInventory, World world) {
        result = null;
        ItemStack backpack = null;
        ItemStack dye = null;

        ItemStack slotStack;
        for (int i = 0; i < craftingGridInventory.getSizeInventory(); i++) {
            slotStack = craftingGridInventory.getStackInSlot(i);

            if (slotStack != null) {
                if (slotStack.getItem() instanceof ItemBackpack) {
                    if (BackpackUtil.isEnderBackpack(slotStack) || backpack != null) {
                        return false;
                    }
                    backpack = slotStack;
                } else if (allowedDyes.contains(slotStack.getItem())) {
                    if (dye != null) {
                        return false;
                    }
                    dye = slotStack;
                } else {
                    return false;
                }
            }
        }

        if (backpack != null && dye != null) {
            int tier = backpack.getItemDamage() / 100;
            if (tier != 0 && dye.getItem() == Items.leather) {
                return false;
            } else if (tier != 1 && tier != 2 && dye.getItem() == ItemsBackpack.tannedLeather) {
                return false;
            }

            int damage = (dye.getItem() instanceof ItemDye ? dye.getItemDamage() + 1 : 0) + tier * 100;

            result = backpack.copy();
            result.setItemDamage(damage);
        }

        return result != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftingGridInventory) {
        return result.copy();
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return result;
    }
}
