package de.eydamos.backpack.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.factory.FactoryBackpack;
import de.eydamos.backpack.inventory.ISaveableInventory;
import de.eydamos.backpack.inventory.InventoryBasic;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.util.NBTUtil;
import io.netty.buffer.ByteBuf;

public class MessageOpenBackpack implements IMessage, IMessageHandler<MessageOpenBackpack, IMessage> {

    protected String uuid;
    protected byte type;
    protected String name;
    protected boolean customName;
    protected int size;
    protected int windowId = 0;
    protected boolean intelligent = false;

    public MessageOpenBackpack() {}

    public MessageOpenBackpack(BackpackSave backpack, IInventory inventory, int windowID) {
        if (inventory instanceof ISaveableInventory<?>) {
            ((ISaveableInventory) inventory).readFromNBT(backpack);
        }
        uuid = backpack.getUUID();
        type = backpack.getType();
        name = inventory.getInventoryName();
        customName = inventory.hasCustomInventoryName();
        size = inventory.getSizeInventory();
        windowId = windowID;
        intelligent = backpack.isIntelligent();
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        windowId = buffer.readInt();
        uuid = ByteBufUtils.readUTF8String(buffer);
        type = buffer.readByte();
        name = ByteBufUtils.readUTF8String(buffer);
        customName = buffer.readBoolean();
        size = buffer.readInt();
        intelligent = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(windowId);
        ByteBufUtils.writeUTF8String(buffer, uuid);
        buffer.writeByte(type);
        ByteBufUtils.writeUTF8String(buffer, name);
        buffer.writeBoolean(customName);
        buffer.writeInt(size);
        buffer.writeBoolean(intelligent);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageOpenBackpack message, MessageContext ctx) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;

        IInventory backpackInventory = new InventoryBasic(message.name, message.customName, message.size);

        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        NBTUtil.setString(nbtTagCompound, Constants.NBT.UID, message.uuid);
        NBTUtil.setByte(nbtTagCompound, Constants.NBT.TYPE, message.type);
        NBTUtil.setInteger(nbtTagCompound, Constants.NBT.SIZE, message.size);
        NBTUtil.setBoolean(nbtTagCompound, Constants.NBT.INTELLIGENT, message.intelligent);

        BackpackSave backpackSave = new BackpackSave(nbtTagCompound);

        Minecraft.getMinecraft().displayGuiScreen(
                FactoryBackpack.getGuiContainer(
                        backpackSave,
                        new IInventory[] { entityPlayer.inventory, backpackInventory },
                        entityPlayer));
        entityPlayer.openContainer.windowId = message.windowId;

        return null;
    }
}
