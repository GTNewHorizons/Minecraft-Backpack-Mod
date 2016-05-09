package de.eydamos.backpack.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.Slot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiBackpack;
import de.eydamos.backpack.inventory.AbstractInventoryBackpack;
import de.eydamos.backpack.inventory.container.Boundaries;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.inventory.slot.SlotBackpack;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.guiadvanced.form.Label;
import de.eydamos.guiadvanced.subpart.GuiSlot;

public class FactoryBackpackNormal extends AbstractFactory<BackpackSave> {

    @Override
    public ContainerAdvanced getContainer(BackpackSave backpack, IInventory[] inventories, EntityPlayer entityPlayer) {
        ContainerAdvanced container;
        if(inventories[1] instanceof AbstractInventoryBackpack || inventories[1] instanceof InventoryEnderChest) {
            container = new ContainerAdvanced(inventories, backpack);
        } else {
            container = new ContainerAdvanced();
        }

        int slotsPerRow = backpack.getSlotsPerRow();
        int inventoryRows = (int) Math.ceil(inventories[1].getSizeInventory() / (float) slotsPerRow);
        int maxWidth = (slotsPerRow < 9 ? 9 : slotsPerRow) * SLOT;

        // set container width (needed for gui)
        container.setWidth(maxWidth + 2 * X_SPACING);

        int x = X_SPACING;
        int y = 17; // initial space for label

        container.addBoundary(Boundaries.BACKPACK);

        int remainingSlots = inventories[1].getSizeInventory();
        // backpack inventory
        for(int row = 0; row < inventoryRows; row++) {
            int cols = remainingSlots - slotsPerRow >= slotsPerRow ? slotsPerRow : remainingSlots;
            remainingSlots -= cols;
            if(cols * SLOT < maxWidth/* && !hasScrollbar */) {
                x += (int) Math.round(maxWidth / 2. - cols * SLOT / 2.) + 1;
            }
            for(int col = 0; col < cols; ++col) {
                container.addSlot(new SlotBackpack(inventories[1], col + row * 9, x, y));
                x += SLOT;
            }
            y += SLOT;
            x = X_SPACING;
        }

        container.addBoundary(Boundaries.BACKPACK_END);
        container.addBoundary(Boundaries.INVENTORY);

        y += 14; // space for label

        // player inventory
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 9; col++) {
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
        for(int col = 0; col < 9; col++) {
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
    public GuiContainer getGuiContainer(BackpackSave backpack, IInventory[] inventories, EntityPlayer entityPlayer) {
        ContainerAdvanced container = getContainer(backpack, inventories, entityPlayer);
        GuiBackpack guiBackpack = new GuiBackpack(container);

        int slotsPerRow = backpack.getSlotsPerRow();
        int inventoryRows = (int) Math.ceil(inventories[1].getSizeInventory() / (float) slotsPerRow);
        int textPositionY = 17 + inventoryRows * SLOT + 2;

        GuiSlot guiSlot;
        for(int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = (Slot) container.inventorySlots.get(i);
            guiSlot = new GuiSlot(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1);
            guiBackpack.addSubPart(guiSlot);
        }

        guiBackpack.addSubPart(new Label(X_SPACING, 6, 0x404040, inventories[1].getInventoryName()));
        guiBackpack.addSubPart(new Label(X_SPACING, textPositionY, 0x404040, "container.inventory"));

        return guiBackpack;
    }

}
