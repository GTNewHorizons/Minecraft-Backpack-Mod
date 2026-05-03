package de.eydamos.backpack.handler;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.gui.GuiAdvanced;
import de.eydamos.backpack.gui.GuiBackpack;
import de.eydamos.backpack.gui.tab.AbstractInventoryTab;
import de.eydamos.backpack.gui.tab.InventoryTabBackpack;
import de.eydamos.backpack.gui.tab.InventoryTabVanilla;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;

@SideOnly(Side.CLIENT)
public class GuiInventoryTabHandler {

    private static final InventoryTabVanilla TAB_VANILLA = new InventoryTabVanilla();
    private static final InventoryTabBackpack TAB_BACKPACK = new InventoryTabBackpack();
    private static final AbstractInventoryTab[] TABS = { TAB_VANILLA, TAB_BACKPACK };

    private static Method drawHoveringTextMethod;

    static {
        try {
            drawHoveringTextMethod = GuiScreen.class
                    .getDeclaredMethod("drawHoveringText", List.class, int.class, int.class, FontRenderer.class);
            drawHoveringTextMethod.setAccessible(true);
        } catch (NoSuchMethodException ignored) {}
    }

    @SubscribeEvent
    public void onPlayerJoinWorld(EntityJoinWorldEvent event) {
        if (!event.world.isRemote) return;
        if (!(event.entity instanceof net.minecraft.entity.player.EntityPlayer)) return;
        net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) event.entity;
        if (!player.equals(Minecraft.getMinecraft().thePlayer)) return;
        Backpack.packetHandler.networkWrapper
                .sendToServer(new MessagePersonalBackpack(player.getUniqueID().toString()));
    }

    @SubscribeEvent
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.gui instanceof GuiInventory) {
            int guiLeft = (event.gui.width - 176) / 2;
            int guiTop = (event.gui.height - 166) / 2;
            addTabs(event, guiLeft, guiTop, true, false);

        } else if (event.gui instanceof GuiAdvanced) {
            GuiAdvanced gui = (GuiAdvanced) event.gui;
            int guiLeft = (event.gui.width - gui.getWidth()) / 2;
            int guiTop = (event.gui.height - gui.getHeight()) / 2;
            boolean isBackpackGui = event.gui instanceof GuiBackpack;
            addTabs(event, guiLeft, guiTop, false, isBackpackGui);
        }
    }

    @SubscribeEvent
    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiInventory) && !(event.gui instanceof GuiAdvanced)) return;
        if (drawHoveringTextMethod == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        for (AbstractInventoryTab tab : TABS) {
            if (!tab.shouldAddToList()) continue;
            if (!tab.isHovering(event.mouseX, event.mouseY)) continue;
            List<String> tooltip = tab.getTooltip();
            if (tooltip == null) continue;
            try {
                drawHoveringTextMethod.invoke(event.gui, tooltip, event.mouseX, event.mouseY, mc.fontRenderer);
            } catch (Exception ignored) {}
            break;
        }
    }

    private void addTabs(GuiScreenEvent.InitGuiEvent.Post event, int guiLeft, int guiTop, boolean vanillaSelected,
            boolean backpackSelected) {
        int count = 0;
        for (AbstractInventoryTab tab : TABS) {
            if (!tab.shouldAddToList()) continue;

            tab.xPosition = guiLeft + count * 28;
            tab.yPosition = guiTop - 28;

            if (tab instanceof InventoryTabBackpack) {
                ((InventoryTabBackpack) tab).updateIcon();
                tab.enabled = !backpackSelected;
            } else {
                tab.enabled = !vanillaSelected;
            }

            event.buttonList.add(tab);
            count++;
        }
    }
}
