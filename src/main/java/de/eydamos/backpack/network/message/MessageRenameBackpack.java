package de.eydamos.backpack.network.message;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.util.NBTItemStackUtil;
import io.netty.buffer.ByteBuf;

public class MessageRenameBackpack implements IMessage, IMessageHandler<MessageRenameBackpack, IMessage> {

    protected String name;

    public MessageRenameBackpack() {}

    public MessageRenameBackpack(String name) {
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        name = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, name);
    }

    @Override
    public IMessage onMessage(MessageRenameBackpack message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;
            setName(entityPlayer, message.name);
        }
        return null;
    }

    public void setName(EntityPlayer entityPlayer, String name) {
        if (entityPlayer.getCurrentEquippedItem() != null) {
            ItemStack itemStack = entityPlayer.getCurrentEquippedItem();

            if (itemStack.getItem() instanceof ItemBackpackBase) {
                NBTItemStackUtil.setString(itemStack, Constants.NBT.CUSTOM_NAME, name);
            }
        }
    }
}
