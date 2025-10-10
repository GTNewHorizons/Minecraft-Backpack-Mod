package de.eydamos.backpack.network.message;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import io.netty.buffer.ByteBuf;

/**
 * Request from the client to ask the server to send him the slot usage information about a backpack.
 */
public class MessageBackpackInfoRequest implements IMessage, IMessageHandler<MessageBackpackInfoRequest, IMessage> {

    private String backpackUUID;

    public MessageBackpackInfoRequest() {}

    public MessageBackpackInfoRequest(String uuid) {
        this.backpackUUID = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        this.backpackUUID = new String(buf.readBytes(length).array());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(backpackUUID.length());
        buf.writeBytes(backpackUUID.getBytes());
    }

    @Override
    public IMessage onMessage(MessageBackpackInfoRequest message, MessageContext ctx) {
        NBTTagCompound backpack = Backpack.saveFileHandler.loadBackpack(message.backpackUUID);

        BackpackSave backpackSave = new BackpackSave(backpack);

        int used = backpackSave.getInventory(Constants.NBT.INVENTORY_BACKPACK).tagCount();
        int total = backpackSave.getSize();

        return new MessageBackpackInfo(message.backpackUUID, used, total);
    }
}
