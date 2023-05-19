package de.eydamos.backpack.network.message;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.handler.EventHandlerClientOnly;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import io.netty.buffer.ByteBuf;

public class MessagePersonalBackpack implements IMessage, IMessageHandler<MessagePersonalBackpack, IMessage> {

    protected String playerUUID = "";
    protected int backpackDamage = -1;

    public MessagePersonalBackpack() {}

    public MessagePersonalBackpack(String UUID) {
        playerUUID = UUID;
    }

    public MessagePersonalBackpack(String UUID, int damage) {
        playerUUID = UUID;
        backpackDamage = damage;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        playerUUID = ByteBufUtils.readUTF8String(buffer);
        backpackDamage = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, playerUUID);
        buffer.writeInt(backpackDamage);
    }

    @Override
    public IMessage onMessage(MessagePersonalBackpack message, MessageContext ctx) {
        IMessage returnMessage = null;
        if (BackpackUtil.isServerSide()) {

            PlayerSave playerSave = new PlayerSave(message.playerUUID);
            ItemStack backpack = playerSave.getPersonalBackpack();
            if (backpack != null) {
                returnMessage = new MessagePersonalBackpack(message.playerUUID, backpack.getItemDamage());
            } else {
                returnMessage = new MessagePersonalBackpack(message.playerUUID);
            }
        } else {
            // Client
            EventHandlerClientOnly.updateTag(message.playerUUID, message.backpackDamage);
        }
        return returnMessage;
    }
}
