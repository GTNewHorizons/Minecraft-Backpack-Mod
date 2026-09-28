package de.eydamos.backpack.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import io.netty.buffer.ByteBuf;

public class MessageOpenGui implements IMessage, IMessageHandler<MessageOpenGui, IMessage> {

    protected byte guiToOpen;

    public MessageOpenGui() {}

    public MessageOpenGui(byte toOpen) {
        guiToOpen = toOpen;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        guiToOpen = buffer.readByte();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(guiToOpen);
    }

    @Override
    public IMessage onMessage(MessageOpenGui message, MessageContext ctx) {
        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;
        switch (message.guiToOpen) {
            case Constants.Guis.OPEN_PERSONAL_BACKPACK -> {
                PlayerSave playerSave = new PlayerSave(entityPlayer);
                ItemStack backpack = playerSave.getPersonalBackpack();
                if (backpack != null) {
                    GuiHelper.displayBackpackSelfSufficient(backpack, entityPlayer);
                    // Set the open flag after displaying: closing a previously open backpack
                    // clears it, so setting it earlier would be undone and the GUI would close.
                    // Skip it when opening was refused, or the next backpack would fail canInteractWith.
                    String uuid = BackpackSave.getUUID(backpack);
                    if (entityPlayer.openContainer instanceof ContainerAdvanced container
                            && container.getBackpackSave() != null
                            && BackpackUtil.UUIDEquals(container.getBackpackSave().getUUID(), uuid)) {
                        new PlayerSave(entityPlayer).setPersonalBackpackOpen(uuid);
                    }
                }
            }
            case Constants.Guis.OPEN_PERSONAL_SLOT -> GuiHelper.displayPersonalSlot(entityPlayer);
        }
        return null;
    }
}
