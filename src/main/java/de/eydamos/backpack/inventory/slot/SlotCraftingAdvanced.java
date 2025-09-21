package de.eydamos.backpack.inventory.slot;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import cpw.mods.fml.common.FMLCommonHandler;
import de.eydamos.backpack.inventory.InventoryCraftingGrid;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.util.BackpackUtil;

public class SlotCraftingAdvanced extends SlotCrafting {

    protected final InventoryCraftingGrid craftingGrid;
    protected final IInventory backpackInventory;
    protected final ContainerWorkbenchBackpack eventHandler;
    protected final World worldObj;
    protected boolean realResult = false;

    public SlotCraftingAdvanced(EntityPlayer player, ContainerWorkbenchBackpack container, int slotIndex, int xPosition,
            int yPosition, IInventory[] inventories) {
        super(player, inventories[2], container.craftResult, slotIndex, xPosition, yPosition);
        craftingGrid = (InventoryCraftingGrid) inventories[2];
        backpackInventory = inventories[1];
        eventHandler = container;
        worldObj = player.worldObj;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack) {
        ArrayList<ItemStack> currentRecipe = getRecipeIngredients();

        FMLCommonHandler.instance().firePlayerCraftingEvent(entityPlayer, itemStack, craftingGrid);
        onCrafting(itemStack);

        for (ItemStack ingredient : currentRecipe) {
            // search for same item in backpack inventory and reduce ingredient amount
            for (int j = 0; j < backpackInventory.getSizeInventory(); j++) {
                ItemStack itemstack = backpackInventory.getStackInSlot(j);

                if (itemstack != null) {
                    if (BackpackUtil.areStacksEqual(ingredient, itemstack)) {
                        ingredient.stackSize -= backpackInventory.decrStackSize(j, ingredient.stackSize).stackSize;
                        if (itemstack.getItem().hasContainerItem(itemstack)) {
                            ItemStack containerItem = itemstack.getItem().getContainerItem(itemstack);

                            if (containerItem != null) {
                                if (containerItem.isItemStackDamageable()
                                        && containerItem.getItemDamage() > containerItem.getMaxDamage()) {
                                    MinecraftForge.EVENT_BUS
                                            .post(new PlayerDestroyItemEvent(entityPlayer, containerItem));
                                    containerItem = null;
                                }

                                if (containerItem != null) {
                                    if (backpackInventory.getStackInSlot(j) == null) {
                                        backpackInventory.setInventorySlotContents(j, containerItem);
                                    } else {
                                        if (!mergeItemStack(containerItem, backpackInventory)) {
                                            if (!entityPlayer.inventory.addItemStackToInventory(containerItem)) {
                                                entityPlayer.entityDropItem(containerItem, 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (ingredient.stackSize == 0) {
                            break;
                        }
                    }
                }
            }

            // if ingredient amount is not zero search for alternative item (ore dictionary)
            int slot = 0;
            while (ingredient.stackSize != 0) {
                slot = findAlternative(ingredient, slot);
                if (slot < 0) {
                    break;
                }
                ingredient.stackSize -= backpackInventory.decrStackSize(slot, ingredient.stackSize).stackSize;
            }
        }

        eventHandler.onCraftMatrixChanged(craftingGrid);
    }

    public ItemStack getCraftingResult() {
        craftingGrid.setUseInventoryMode(true);
        ItemStack result = CraftingManager.getInstance().findMatchingRecipe(craftingGrid, worldObj);
        craftingGrid.setUseInventoryMode(false);
        return result;
    }

    /**
     * Uses the oreDictionary to search for an alternative.
     *
     * @param original The original ItemStack.
     * @return The index of the slot which has the same item as in the original ItemStack based on the OreDictionary.
     */
    protected int findAlternative(ItemStack original) {
        return findAlternative(original, 0);
    }

    /**
     * Uses the oreDictionary to search for an alternative.
     *
     * @param original     The original ItemStack.
     * @param startingSlot The slot to start with the search.
     * @return The index of the slot which has the same item as in the original ItemStack based on the OreDictionary.
     */
    protected int findAlternative(ItemStack original, int startingSlot) {
        for (int i = startingSlot; i < backpackInventory.getSizeInventory(); i++) {
            ItemStack alternative = backpackInventory.getStackInSlot(i);
            if (alternative != null) {
                if (BackpackUtil.areStacksEqual(original, alternative, true)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        ArrayList<ItemStack> currentRecipe = getRecipeIngredients();

        Iterator<ItemStack> recipeIterator = currentRecipe.iterator();
        while (recipeIterator.hasNext()) {
            ItemStack recipeStack = recipeIterator.next();
            for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
                ItemStack backpackStack = backpackInventory.getStackInSlot(i);
                if (BackpackUtil.areStacksEqual(recipeStack, backpackStack, true)) {
                    recipeStack.stackSize -= backpackStack.stackSize;
                    if (recipeStack.stackSize <= 0) {
                        recipeIterator.remove();
                        break;
                    }
                }
            }
        }

        return currentRecipe.isEmpty();
    }

    /**
     * Returns an ArrayList with the ingredients of the recipe. Each ItemStack holds the amount of this item needed in
     * the recipe.
     *
     * @return An ArrayList with all the ingredients of the current recipe and the needed amount.
     */
    protected ArrayList<ItemStack> getRecipeIngredients() {
        ArrayList<ItemStack> currentRecipe = new ArrayList<>();
        boolean add;

        for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
            add = true;
            ItemStack recipeStack = craftingGrid.getStackInSlot(i);
            if (recipeStack != null) {
                for (ItemStack stack : currentRecipe) {
                    if (stack.isItemEqual(recipeStack)) {
                        stack.stackSize++;
                        add = false;
                        break;
                    }
                }
                if (add) {
                    ItemStack ingredient = recipeStack.copy();
                    ingredient.stackSize = 1;
                    currentRecipe.add(ingredient);
                }
            }
        }
        return currentRecipe;
    }

    /**
     * Slightly modified version of Container.mergeItemStack(...)
     *
     * @param sourceStack The stack that should be merged into the inventory.
     * @param inventory   The inventory where the stack should go.
     * @return True if the stack was put into the inventory completely, false otherwise.
     */
    protected boolean mergeItemStack(ItemStack sourceStack, IInventory inventory) {
        boolean result = false;
        int currentSlotIndex = 0;

        ItemStack slotStack;

        if (sourceStack.isStackable()) {
            while (sourceStack.stackSize > 0 && currentSlotIndex < inventory.getSizeInventory()) {
                slotStack = inventory.getStackInSlot(currentSlotIndex);

                if (slotStack != null && slotStack.getItem() == sourceStack.getItem()
                        && (!sourceStack.getHasSubtypes() || sourceStack.getItemDamage() == slotStack.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(sourceStack, slotStack)) {
                    int l = slotStack.stackSize + sourceStack.stackSize;

                    if (l <= sourceStack.getMaxStackSize()) {
                        sourceStack.stackSize = 0;
                        slotStack.stackSize = l;
                        inventory.markDirty();
                        result = true;
                    } else if (slotStack.stackSize < sourceStack.getMaxStackSize()) {
                        sourceStack.stackSize -= sourceStack.getMaxStackSize() - slotStack.stackSize;
                        slotStack.stackSize = sourceStack.getMaxStackSize();
                        inventory.markDirty();
                        result = true;
                    }
                }

                currentSlotIndex++;
            }
        }

        if (sourceStack.stackSize > 0) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                slotStack = inventory.getStackInSlot(i);

                if (slotStack == null) {
                    inventory.setInventorySlotContents(i, sourceStack.copy());
                    sourceStack.stackSize = 0;
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}
