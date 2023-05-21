package de.eydamos.backpack.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.misc.Constants;
import io.netty.buffer.ByteBuf;

public class MessageGuiCommand implements IMessage, IMessageHandler<MessageGuiCommand, IMessage> {

    protected byte command;

    public MessageGuiCommand() {}

    public MessageGuiCommand(byte guiCommand) {
        command = guiCommand;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        command = buffer.readByte();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeByte(command);
    }

    @Override
    public IMessage onMessage(MessageGuiCommand message, MessageContext ctx) {
        EntityPlayerMP entityPlayer = ctx.getServerHandler().playerEntity;

        Container container = entityPlayer.openContainer;

        switch (message.command) {
            case Constants.GuiCommands.CLEAR -> {
                if (container instanceof ContainerWorkbenchBackpack) {
                    ((ContainerWorkbenchBackpack) container).clearCraftMatrix();
                }
            }
            case Constants.GuiCommands.SAVE -> {
                if (container instanceof ContainerWorkbenchBackpack) {
                    ((ContainerWorkbenchBackpack) container).setSaveMode();
                }
            }
        }

        return null;
    }
}
