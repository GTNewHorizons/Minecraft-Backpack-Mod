package de.eydamos.backpack.gui.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class InventoryTabVanilla extends AbstractInventoryTab {

    public InventoryTabVanilla() {
        super(0, 0, 0, new ItemStack(Blocks.crafting_table), 0);
    }

    @Override
    public void onTabClicked() {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.openContainer.windowId));
        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
    }

    @Override
    public boolean shouldAddToList() {
        return true;
    }

}
