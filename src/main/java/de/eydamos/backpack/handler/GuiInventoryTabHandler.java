package de.eydamos.backpack.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraftforge.client.event.GuiScreenEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiAdvanced;
import de.eydamos.backpack.gui.GuiButtonInventoryTab;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.Constants;

@SideOnly(Side.CLIENT)
public class GuiInventoryTabHandler {

    private static final int TAB_ID_BACKPACK = 0xBAC4;
    private static final int TAB_ID_INVENTORY = 0xBAC5;

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        NBTTagCompound playerData = player.getEntityData();
        if (!playerData.hasKey(Constants.NBT.PERSONAL_BACKPACK_META)) return;
        int meta = playerData.getInteger(Constants.NBT.PERSONAL_BACKPACK_META);
        if (meta < 0) return;

        ItemStack backpackStack = new ItemStack(ItemsBackpack.backpack, 1, meta);
        ItemStack inventoryStack = new ItemStack(Blocks.crafting_table);

        if (event.gui instanceof GuiInventory) {
            int guiLeft = (event.gui.width - 176) / 2;
            int guiTop = (event.gui.height - 166) / 2;

            // Vanilla inventory tab — selected (current), not clickable
            GuiButtonInventoryTab inventoryTab = new GuiButtonInventoryTab(
                    TAB_ID_INVENTORY, guiLeft, guiTop - 28, inventoryStack);
            inventoryTab.enabled = false;
            event.buttonList.add(inventoryTab);

            // Backpack tab — clickable
            event.buttonList.add(
                    new GuiButtonInventoryTab(TAB_ID_BACKPACK, guiLeft + 28, guiTop - 28, backpackStack));

        } else if (event.gui instanceof GuiAdvanced) {
            GuiAdvanced guiAdvanced = (GuiAdvanced) event.gui;
            int guiLeft = (event.gui.width - guiAdvanced.getWidth()) / 2;
            int guiTop = (event.gui.height - guiAdvanced.getHeight()) / 2;

            // Vanilla inventory tab — clickable
            event.buttonList.add(
                    new GuiButtonInventoryTab(TAB_ID_INVENTORY, guiLeft, guiTop - 28, inventoryStack));

            // Backpack tab — selected (current), not clickable
            GuiButtonInventoryTab backpackTab = new GuiButtonInventoryTab(
                    TAB_ID_BACKPACK, guiLeft + 28, guiTop - 28, backpackStack);
            backpackTab.enabled = false;
            event.buttonList.add(backpackTab);
        }
    }

    @SubscribeEvent
    public void onActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.gui instanceof GuiInventory && event.button.id == TAB_ID_BACKPACK) {
            GuiHelper.sendOpenPersonalGui(Constants.Guis.OPEN_PERSONAL_BACKPACK);

        } else if (event.gui instanceof GuiAdvanced && event.button.id == TAB_ID_INVENTORY) {
            mc.thePlayer.sendQueue.addToSendQueue(
                    new C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId));
            mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
        }
    }
}
