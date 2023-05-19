package de.eydamos.backpack.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.factory.FactoryBackpack;
import de.eydamos.backpack.gui.GuiBackpackRename;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.network.message.MessageGuiCommand;
import de.eydamos.backpack.network.message.MessageOpenBackpack;
import de.eydamos.backpack.network.message.MessageOpenGui;
import de.eydamos.backpack.network.message.MessageOpenPersonalSlot;
import de.eydamos.backpack.network.message.MessageRenameBackpack;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;

public class GuiHelper {

    @SideOnly(Side.CLIENT)
    public static void displayRenameGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiBackpackRename());
    }

    public static void displayBackpack(BackpackSave backpackSave, IInventory inventory, EntityPlayerMP entityPlayer) {

        if (!isDimensionAllowed(entityPlayer)) return;

        prepare(entityPlayer);

        MessageOpenBackpack message = new MessageOpenBackpack(backpackSave, inventory, entityPlayer.currentWindowId);
        Backpack.packetHandler.networkWrapper.sendTo(message, entityPlayer);

        Container container = FactoryBackpack
                .getContainer(backpackSave, new IInventory[] { entityPlayer.inventory, inventory }, entityPlayer);
        openContainer(container, entityPlayer);

        BackpackUtil.playOpenSound(entityPlayer);
    }

    public static void displayPersonalSlot(EntityPlayerMP entityPlayer) {

        if (!isDimensionAllowed(entityPlayer)) return;

        PlayerSave playerSave = new PlayerSave(entityPlayer);
        playerSave.setType((byte) -1);

        prepare(entityPlayer);

        MessageOpenPersonalSlot message = new MessageOpenPersonalSlot(entityPlayer.currentWindowId);
        Backpack.packetHandler.networkWrapper.sendTo(message, entityPlayer);

        Container container = FactoryBackpack
                .getContainer(playerSave, new IInventory[] { entityPlayer.inventory }, entityPlayer);
        openContainer(container, entityPlayer);
    }

    private static boolean isDimensionAllowed(EntityPlayerMP entityPlayer) {
        int currentDimID = (entityPlayer.worldObj.provider.dimensionId);
        for (String id : ConfigurationBackpack.FORBIDDEN_DIMENSIONS) {
            if (id.equals(Integer.toString(currentDimID))) return false;
        }
        return true;
    }

    @SideOnly(Side.CLIENT)
    public static void sendOpenPersonalGui(byte gui) {
        MessageOpenGui message = new MessageOpenGui(gui);
        Backpack.packetHandler.networkWrapper.sendToServer(message);
    }

    @SideOnly(Side.CLIENT)
    public static void sendGuiCommand(byte command) {
        MessageGuiCommand message = new MessageGuiCommand(command);
        Backpack.packetHandler.networkWrapper.sendToServer(message);
    }

    @SideOnly(Side.CLIENT)
    public static void renameBackpack(String name) {
        MessageRenameBackpack message = new MessageRenameBackpack(name);
        // send new name to server
        Backpack.packetHandler.networkWrapper.sendToServer(message);
        // save the name on client
        message.setName(Minecraft.getMinecraft().thePlayer, name);
    }

    protected static void prepare(EntityPlayerMP entityPlayer) {
        if (entityPlayer.openContainer != entityPlayer.inventoryContainer) {
            entityPlayer.closeScreen();
        }

        entityPlayer.getNextWindowId();
    }

    protected static void openContainer(Container container, EntityPlayerMP entityPlayer) {
        entityPlayer.openContainer = container;
        entityPlayer.openContainer.windowId = entityPlayer.currentWindowId;
        entityPlayer.openContainer.addCraftingToCrafters(entityPlayer);
    }
}
