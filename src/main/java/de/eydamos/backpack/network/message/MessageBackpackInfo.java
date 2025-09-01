package de.eydamos.backpack.network.message;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.misc.BackpackUsageCache;
import io.netty.buffer.ByteBuf;

/**
 * Used to pass to the client the slot usage information for a specific backpack.
 */
public class MessageBackpackInfo implements IMessage, IMessageHandler<MessageBackpackInfo, IMessage> {

    private String backpackUUID;
    private int usedSlots;
    private int totalSlots;

    public MessageBackpackInfo() {}

    public MessageBackpackInfo(String uuid, int used, int total) {
        this.backpackUUID = uuid;
        this.usedSlots = used;
        this.totalSlots = total;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        this.backpackUUID = new String(buf.readBytes(length).array());
        this.usedSlots = buf.readInt();
        this.totalSlots = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(backpackUUID.length());
        buf.writeBytes(backpackUUID.getBytes());
        buf.writeInt(usedSlots);
        buf.writeInt(totalSlots);
    }

    @Override
    public IMessage onMessage(MessageBackpackInfo message, MessageContext ctx) {
        BackpackUsageCache.updateBackpackInfo(message.backpackUUID, message.usedSlots, message.totalSlots);
        return null;
    }

}
