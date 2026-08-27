package de.eydamos.backpack.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.factory.FactoryBackpack;
import de.eydamos.backpack.gui.GuiBackpackRename;
import de.eydamos.backpack.item.ItemBackpackBase;
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

    // Cursor position captured when a tab is clicked, so opening the backpack GUI
    // through the network round-trip does not warp the cursor to the screen center.
    private static int savedCursorX = -1;
    private static int savedCursorY = -1;

    @SideOnly(Side.CLIENT)
    public static void saveCursorPosition() {
        savedCursorX = Mouse.getX();
        savedCursorY = Mouse.getY();
    }

    @SideOnly(Side.CLIENT)
    public static void restoreCursorPosition() {
        if (savedCursorX < 0) return;
        // Mouse.getY() measures from the bottom, but setCursorPosition places from the top here,
        // so the Y axis has to be flipped to land the cursor back where it was clicked.
        Mouse.setCursorPosition(savedCursorX, Display.getHeight() - savedCursorY);
        savedCursorX = -1;
        savedCursorY = -1;
    }

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

    /**
     * Opens a backpack from its ItemStack, building the save and the inventory itself. Use this instead of
     * {@link #displayBackpack}: it closes an already open container first, so the save is never read before that
     * container has flushed.
     * <p>
     * TODO Kept under a separate name because Backhand mixes into {@link #displayBackpack} by bare method name. Once
     * Backhand targets this method instead, fold the other overload into it, see
     * <a href="https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues/26484">modpack issue 26484</a>.
     *
     * @param backpack     ItemStack of the backpack to open.
     * @param entityPlayer Player to open it for.
     */
    public static void displayBackpackSelfSufficient(ItemStack backpack, EntityPlayerMP entityPlayer) {

        if (!isDimensionAllowed(entityPlayer)) return;

        // flush the open container first, or closing it later writes its stale contents back over the save
        prepare(entityPlayer);

        displayBackpack(
                new BackpackSave(backpack),
                ItemBackpackBase.getInventory(backpack, entityPlayer),
                entityPlayer);
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
