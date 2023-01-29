package de.eydamos.backpack.network.message;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.factory.FactoryBackpack;
import de.eydamos.backpack.saves.PlayerSave;
import io.netty.buffer.ByteBuf;

public class MessageOpenPersonalSlot implements IMessage, IMessageHandler<MessageOpenPersonalSlot, IMessage> {

    protected int windowId = 0;

    public MessageOpenPersonalSlot() {}

    public MessageOpenPersonalSlot(int windowId) {
        this.windowId = windowId;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        windowId = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(windowId);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IMessage onMessage(MessageOpenPersonalSlot message, MessageContext ctx) {
        EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;

        PlayerSave playerSave = new PlayerSave(entityPlayer);
        playerSave.setType((byte) -1);

        Minecraft.getMinecraft().displayGuiScreen(
                FactoryBackpack.getGuiContainer(playerSave, new IInventory[] { entityPlayer.inventory }, entityPlayer));
        entityPlayer.openContainer.windowId = message.windowId;

        return null;
    }
}
