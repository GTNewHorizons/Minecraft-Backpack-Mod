package de.eydamos.backpack.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.inventory.container.Boundaries;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.network.message.MessageRecipe;

public class OverlayHandlerBackpack implements IOverlayHandler {

    public static class SlotStack {

        protected final ItemStack itemStack;
        protected final int slotIndex;

        public SlotStack(ItemStack itemstack, int slot) {
            itemStack = itemstack;
            slotIndex = slot;
        }

        public ItemStack getStack() {
            return itemStack;
        }

        public int getSlot() {
            return slotIndex;
        }
    }

    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift) {
        if (!(gui.inventorySlots instanceof ContainerWorkbenchBackpack container)) {
            return;
        }

        container.clearCraftMatrix();

        int from = container.getBoundary(Boundaries.CRAFTING);
        int to = container.getBoundary(Boundaries.CRAFTING_END);
        int offsetX = container.isIntelligent() ? -17 : 5;
        int offsetY = 11;

        List<PositionedStack> ingredients = recipe.getIngredientStacks(recipeIndex);
        ArrayList<SlotStack> newRecipe = new ArrayList<>();

        for (PositionedStack positionedStack : ingredients) {
            for (int slotIndex = from; slotIndex < to; slotIndex++) {
                Slot slot = container.getSlot(slotIndex);
                if (slot.xDisplayPosition == positionedStack.relx + offsetX
                        && slot.yDisplayPosition == positionedStack.rely + offsetY) {
                    newRecipe.add(new SlotStack(positionedStack.item, slotIndex));
                }
            }
        }

        MessageRecipe message = new MessageRecipe(newRecipe);
        Backpack.packetHandler.networkWrapper.sendToServer(message);
    }
}
