package de.eydamos.backpack.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.gui.GuiAdvanced;
import de.eydamos.backpack.gui.GuiBackpack;
import de.eydamos.backpack.gui.GuiPersonalSlot;
import de.eydamos.backpack.gui.tab.AbstractInventoryTab;
import de.eydamos.backpack.gui.tab.InventoryTabBackpack;
import de.eydamos.backpack.gui.tab.InventoryTabVanilla;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;
import tconstruct.client.tabs.AbstractTab;
import tconstruct.client.tabs.TabRegistry;

@SideOnly(Side.CLIENT)
public class GuiInventoryTabHandler {

    private static final InventoryTabVanilla TAB_VANILLA = new InventoryTabVanilla();
    private static final InventoryTabBackpack TAB_BACKPACK = new InventoryTabBackpack();
    private static final AbstractInventoryTab[] TABS_ALL = { TAB_VANILLA, TAB_BACKPACK };
    private static final AbstractInventoryTab[] TABS_BACKPACK_ONLY = { TAB_BACKPACK };

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
        if (event.gui instanceof GuiPersonalSlot) {
            return;
        } else if (event.gui instanceof GuiAdvanced) {
            GuiAdvanced gui = (GuiAdvanced) event.gui;
            int guiLeft = (event.gui.width - gui.getWidth()) / 2;
            int guiTop = (event.gui.height - gui.getHeight()) / 2;
            boolean isBackpackGui = event.gui instanceof GuiBackpack;
            if (Loader.isModLoaded("TConstruct")) {
                // AbstractTab.class won't match any concrete tab, so all TConstruct tabs are enabled
                TabRegistry.updateTabValues(guiLeft, guiTop, AbstractTab.class);
                TabRegistry.addTabsToList(event.buttonList);
                addTabs(event, guiLeft, guiTop, TABS_BACKPACK_ONLY, isBackpackGui);
            } else {
                addTabs(event, guiLeft, guiTop, TABS_ALL, isBackpackGui);
            }
        } else {
            // Handles GuiInventory and other mod GUIs (TConstruct, Galacticraft, etc.)
            // that already have inventory tabs — detect their tab row and append ours.
            addBackpackTabIfTabsPresent(event);
        }
    }

    private void addBackpackTabIfTabsPresent(GuiScreenEvent.InitGuiEvent.Post event) {
        int guiLeft = (event.gui.width - 176) / 2;
        if (Loader.isModLoaded("TConstruct")) {
            addBackpackTabAfterTConstructTabs(event, guiLeft);
        } else if (event.gui instanceof GuiInventory) {
            int guiTop = (event.gui.height - 166) / 2;
            // Add vanilla tab (selected/disabled) + backpack tab — mirrors TConstruct behaviour
            addTabs(event, guiLeft, guiTop, TABS_ALL, false);
        }
    }

    // Separate method so AbstractTab is only resolved by the JVM when TConstruct is actually loaded
    private void addBackpackTabAfterTConstructTabs(GuiScreenEvent.InitGuiEvent.Post event, int guiLeft) {
        int tabY = Integer.MIN_VALUE;
        for (Object obj : event.buttonList) {
            if (obj instanceof AbstractTab) {
                tabY = Math.max(tabY, ((GuiButton) obj).yPosition);
            }
        }
        if (tabY == Integer.MIN_VALUE) return;
        addTabs(event, guiLeft, tabY + 28, TABS_BACKPACK_ONLY, false);
    }

    private void addTabs(GuiScreenEvent.InitGuiEvent.Post event, int guiLeft, int guiTop, AbstractInventoryTab[] tabs,
            boolean backpackSelected) {
        int nextId = 0;
        int nextTabX = guiLeft;
        for (Object obj : event.buttonList) {
            if (!(obj instanceof GuiButton)) continue;
            GuiButton btn = (GuiButton) obj;
            nextId = Math.max(nextId, btn.id + 1);
            // Track the rightmost existing tab to place ours after it (handles potion offset too)
            if (btn.yPosition == guiTop - 28) {
                nextTabX = Math.max(nextTabX, btn.xPosition + 28);
            }
        }

        boolean hasExistingTabs = nextTabX > guiLeft;

        int count = 0;
        for (AbstractInventoryTab tab : tabs) {
            if (!tab.shouldAddToList()) continue;

            tab.id = nextId + count;
            tab.xPosition = nextTabX + count * 28;
            tab.yPosition = guiTop - 28;
            tab.setXOffset((hasExistingTabs || count > 0) ? 1 : 0);

            if (tab instanceof InventoryTabBackpack) {
                ((InventoryTabBackpack) tab).updateIcon();
                tab.enabled = !backpackSelected;
            } else {
                tab.enabled = backpackSelected;
            }

            event.buttonList.add(tab);
            count++;
        }
    }
}
