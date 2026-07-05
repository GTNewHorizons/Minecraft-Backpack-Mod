package de.eydamos.backpack.network.message;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.handler.EventHandlerClientOnly;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import io.netty.buffer.ByteBuf;

public class MessagePersonalBackpack implements IMessage, IMessageHandler<MessagePersonalBackpack, IMessage> {

    protected String playerUUID = "";
    protected int backpackDamage = -1;
    protected String backpackUUID = "";

    public MessagePersonalBackpack() {}

    public MessagePersonalBackpack(String UUID) {
        playerUUID = UUID;
    }

    public MessagePersonalBackpack(String UUID, int damage, String backpackUUID) {
        playerUUID = UUID;
        backpackDamage = damage;
        this.backpackUUID = backpackUUID;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        playerUUID = ByteBufUtils.readUTF8String(buffer);
        backpackDamage = buffer.readInt();
        backpackUUID = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, playerUUID);
        buffer.writeInt(backpackDamage);
        ByteBufUtils.writeUTF8String(buffer, backpackUUID);
    }

    @Override
    public IMessage onMessage(MessagePersonalBackpack message, MessageContext ctx) {
        IMessage returnMessage = null;
        if (BackpackUtil.isServerSide()) {
            EntityPlayer target = resolveOnlinePlayer(ctx, message.playerUUID);
            if (target == null) {
                return null;
            }
            String playerUUID = target.getUniqueID().toString();

            PlayerSave playerSave = new PlayerSave(playerUUID);
            ItemStack backpack = playerSave.getPersonalBackpack();
            if (backpack != null) {
                returnMessage = new MessagePersonalBackpack(
                        playerUUID,
                        backpack.getItemDamage(),
                        new BackpackSave(backpack).getUUID());
            } else {
                returnMessage = new MessagePersonalBackpack(playerUUID);
            }
        } else {
            // Client
            EventHandlerClientOnly.updateTag(message.playerUUID, message.backpackDamage, message.backpackUUID);
        }
        return returnMessage;
    }

    private EntityPlayer resolveOnlinePlayer(MessageContext ctx, String uuid) {
        try {
            return ctx.getServerHandler().playerEntity.worldObj.func_152378_a(UUID.fromString(uuid));
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
