package de.eydamos.backpack.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiBackpack;
import de.eydamos.backpack.inventory.AbstractInventoryBackpack;
import de.eydamos.backpack.inventory.InventoryBackpackSlot;
import de.eydamos.backpack.inventory.InventoryBasic;
import de.eydamos.backpack.inventory.InventoryPickup;
import de.eydamos.backpack.inventory.container.Boundaries;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.inventory.container.ContainerPersonalSlot;
import de.eydamos.backpack.inventory.slot.SlotBackpackOnly;
import de.eydamos.backpack.inventory.slot.SlotPhantom;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.guiadvanced.form.Label;
import de.eydamos.guiadvanced.subpart.GuiSlot;

public class FactoryPersonalSlot extends AbstractFactory<PlayerSave> {

    @Override
    public ContainerAdvanced getContainer(PlayerSave player, IInventory[] inventories, EntityPlayer entityPlayer) {
        AbstractInventoryBackpack inventorySlot;

        if (BackpackUtil.isServerSide()) {
            inventorySlot = new InventoryBackpackSlot(player);
        } else {
            inventorySlot = new InventoryBasic(Localizations.INVENTORY_PERSONAL, false, 1);
        }
        InventoryPickup inventoryPickup = new InventoryPickup();
        ContainerAdvanced container = new ContainerPersonalSlot(inventorySlot, inventoryPickup);

        int maxWidth = 160;

        // set container width (needed for gui)
        container.setWidth(maxWidth + 2 * X_SPACING);

        int x = (int) Math.round(maxWidth / 2. - SLOT / 2.) + 1;
        int y = 17; // initial space for label

        container.addBoundary(Boundaries.BACKPACK);

        // backpack slot
        container.addSlot(new SlotBackpackOnly(inventorySlot, 0, x, y));

        container.addBoundary(Boundaries.BACKPACK_END);

        x = X_SPACING;
        y += 15 + SLOT;
        // pickup inventory
        for (int i = 0; i < inventoryPickup.getSizeInventory(); i++) {
            container.addSlot(new SlotPhantom(inventoryPickup, i, x, y));
            x += SLOT;
        }

        container.addBoundary(Boundaries.INVENTORY);

        x = X_SPACING;
        y += 24;
        // player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                container.addSlot(new Slot(inventories[0], col + row * 9 + 9, x, y));
                x += SLOT;
            }
            y += SLOT;
            x = X_SPACING;
        }

        container.addBoundary(Boundaries.INVENTORY_END);
        container.addBoundary(Boundaries.HOTBAR);

        y += 6;

        // hotbar
        for (int col = 0; col < 9; col++) {
            container.addSlot(new Slot(inventories[0], col, x, y));
            x += SLOT;
        }

        container.addBoundary(Boundaries.HOTBAR_END);

        y += SLOT;
        y += 7;

        // set container height (needed for gui)
        container.setHeight(y);

        return container;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer getGuiContainer(PlayerSave player, IInventory[] inventories, EntityPlayer entityPlayer) {
        ContainerPersonalSlot container = (ContainerPersonalSlot) getContainer(player, inventories, entityPlayer);
        GuiBackpack guiBackpack = new GuiBackpack(container);

        GuiSlot guiSlot;
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = (Slot) container.inventorySlots.get(i);
            guiSlot = new GuiSlot(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1);
            guiBackpack.addSubPart(guiSlot);
        }

        guiBackpack.addSubPart(new Label(X_SPACING, 6, 0x404040, container.getInventoryToSave().getInventoryName()));
        guiBackpack.addSubPart(new Label(X_SPACING, 38, 0x404040, container.getInventoryPickup().getInventoryName()));

        return guiBackpack;
    }
}
