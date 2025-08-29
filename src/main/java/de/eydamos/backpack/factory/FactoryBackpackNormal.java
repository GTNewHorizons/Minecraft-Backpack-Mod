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
    public ContainerAdvanced getContainer(BackpackSave backpack, IInventory[] inventories, EntityPlayer player) {
        ContainerAdvanced container = (inventories[1] instanceof AbstractInventoryBackpack
                || inventories[1] instanceof InventoryEnderChest) ? new ContainerAdvanced(inventories, backpack)
                        : new ContainerAdvanced();

        int slotsPerRow = backpack.getSlotsPerRow();
        int totalSlots = inventories[1].getSizeInventory();
        int rows = (totalSlots + slotsPerRow - 1) / slotsPerRow;
        int maxWidth = slotsPerRow * SLOT;
        int x = X_SPACING;
        int y = 17;
        container.setWidth(maxWidth + 2 * X_SPACING);
        container.addBoundary(Boundaries.BACKPACK);

        // Backpack inventory
        for (int row = 0; row < rows; row++) {
            int cols = Math.min(slotsPerRow, totalSlots - row * slotsPerRow);
            for (int col = 0; col < cols; col++) {
                int index = row * slotsPerRow + col;
                container.addSlot(new SlotBackpack(inventories[1], index, x + col * SLOT, y));
            }
            y += SLOT;
        }

        container.addBoundary(Boundaries.BACKPACK_END);
        container.addBoundary(Boundaries.INVENTORY);

        y += 14; // space for label
        x = (container.getWidth() - 9 * SLOT) / 2;

        // Player inventory
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int index = col + row * 9 + 9;
                container.addSlot(new Slot(inventories[0], index, x + col * SLOT, y));
            }
            y += SLOT;
        }

        container.addBoundary(Boundaries.INVENTORY_END);
        container.addBoundary(Boundaries.HOTBAR);

        y += 6;

        // Hotbar
        for (int col = 0; col < 9; col++) {
            container.addSlot(new Slot(inventories[0], col, x + col * SLOT, y));
        }

        container.addBoundary(Boundaries.HOTBAR_END);

        container.setHeight(y + SLOT + 7);

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
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = (Slot) container.inventorySlots.get(i);
            guiSlot = new GuiSlot(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1);
            guiBackpack.addSubPart(guiSlot);
        }

        int inventorySpaceBefore = (int) Math.round(container.getWidth() / 2. - (SLOT * 9) / 2.);

        guiBackpack.addSubPart(new Label(X_SPACING, 6, 0x404040, inventories[1].getInventoryName()));
        guiBackpack.addSubPart(new Label(inventorySpaceBefore, textPositionY, 0x404040, "container.inventory"));

        return guiBackpack;
    }
}
