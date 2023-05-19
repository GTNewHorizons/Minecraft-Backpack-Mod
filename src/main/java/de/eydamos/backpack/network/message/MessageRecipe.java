package de.eydamos.backpack.network.message;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.nei.OverlayHandlerBackpack.SlotStack;
import io.netty.buffer.ByteBuf;

public class MessageRecipe implements IMessage, IMessageHandler<MessageRecipe, IMessage> {

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

        if (container instanceof ContainerWorkbenchBackpack) {
            ((ContainerWorkbenchBackpack) container).clearCraftMatrix();

            for (SlotStack slotStack : message.recipeList) {
                container.putStackInSlot(slotStack.getSlot(), slotStack.getStack());
            }
        }

        return null;
    }
}
