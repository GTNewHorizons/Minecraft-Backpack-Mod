package de.eydamos.backpack.network.message;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
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
        backpackUUID = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, backpackUUID);
    }

    @Override
    public IMessage onMessage(MessageBackpackInfoRequest message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (!isValidUUID(message.backpackUUID) || !canRequestBackpackInfo(player, message.backpackUUID)) {
            return null;
        }

        NBTTagCompound backpack = Backpack.saveFileHandler.loadBackpack(message.backpackUUID);

        BackpackSave backpackSave = new BackpackSave(backpack);

        int used = backpackSave.getInventory(Constants.NBT.INVENTORY_BACKPACK).tagCount();
        int total = backpackSave.getSize();

        return new MessageBackpackInfo(message.backpackUUID, used, total);
    }

    private boolean canRequestBackpackInfo(EntityPlayerMP player, String uuid) {
        for (Object slotObject : player.openContainer.inventorySlots) {
            Slot slot = (Slot) slotObject;
            if (slot != null && BackpackUtil.UUIDEquals(slot.getStack(), uuid)) {
                return true;
            }
        }

        ItemStack personalBackpack = new PlayerSave(player).getPersonalBackpack();
        if (BackpackUtil.UUIDEquals(personalBackpack, uuid)) return true;

        if (player.openContainer instanceof ContainerAdvanced container) {
            BackpackSave openBackpack = container.getBackpackSave();
            return openBackpack != null && BackpackUtil.UUIDEquals(openBackpack.getUUID(), uuid);
        }

        return false;
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
