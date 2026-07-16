package de.eydamos.backpack.network.message;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.inventory.container.Boundaries;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.inventory.slot.SlotPhantom;
import de.eydamos.backpack.nei.OverlayHandlerBackpack.SlotStack;
import io.netty.buffer.ByteBuf;

public class MessageRecipe implements IMessage, IMessageHandler<MessageRecipe, IMessage> {

    private static final int MAX_RECIPE_SLOTS = 9;

    protected ArrayList<SlotStack> recipeList;

    public MessageRecipe() {
        recipeList = new ArrayList<>();
    }

    public MessageRecipe(ArrayList<SlotStack> recipe) {
        recipeList = recipe;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        int max = buffer.readInt();
        if (max < 0 || max > MAX_RECIPE_SLOTS) {
            return;
        }
        for (int i = 0; i < max; i++) {
            recipeList.add(new SlotStack(ByteBufUtils.readItemStack(buffer), buffer.readInt()));
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(recipeList.size());
        for (SlotStack slotStack : recipeList) {
            ByteBufUtils.writeItemStack(buffer, slotStack.getStack());
            buffer.writeInt(slotStack.getSlot());
        }
    }

    @Override
    public IMessage onMessage(MessageRecipe message, MessageContext ctx) {
        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;

        Container container = entityPlayer.openContainer;

        if (container instanceof ContainerWorkbenchBackpack workbench) {
            int from = workbench.getBoundary(Boundaries.CRAFTING);
            int to = workbench.getBoundary(Boundaries.CRAFTING_END);
            if (from < 0 || to < from) {
                return null;
            }

            workbench.clearCraftMatrix();

            for (SlotStack slotStack : message.recipeList) {
                int slotIndex = slotStack.getSlot();
                if (slotIndex < from || slotIndex >= to || slotIndex >= workbench.inventorySlots.size()) {
                    continue;
                }
                Slot slot = workbench.getSlot(slotIndex);
                if (!(slot instanceof SlotPhantom)) {
                    continue;
                }
                ItemStack stack = slotStack.getStack();
                if (stack != null) {
                    stack = stack.copy();
                    stack.stackSize = 1;
                }
                workbench.putStackInSlot(slotIndex, stack);
            }
        }

        return null;
    }
}
