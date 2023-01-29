package de.eydamos.backpack.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiWorkbenchBackpack;
import de.eydamos.backpack.inventory.AbstractInventoryBackpack;
import de.eydamos.backpack.inventory.InventoryCraftingGrid;
import de.eydamos.backpack.inventory.InventoryRecipes;
import de.eydamos.backpack.inventory.container.Boundaries;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.inventory.container.ContainerWorkbenchBackpack;
import de.eydamos.backpack.inventory.slot.SlotBackpack;
import de.eydamos.backpack.inventory.slot.SlotCraftingAdvanced;
import de.eydamos.backpack.inventory.slot.SlotPhantom;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.guiadvanced.form.Button;
import de.eydamos.guiadvanced.form.Label;
import de.eydamos.guiadvanced.subpart.GuiSlot;
import de.eydamos.guiadvanced.subpart.Icon;
import de.eydamos.guiadvanced.util.Alignment;

public class FactoryWorkbenchBackpack extends AbstractFactory<BackpackSave> {

    protected final int BIG_X_SPACING = 30;

    @Override
    public ContainerAdvanced getContainer(BackpackSave backpack, IInventory[] inventories, EntityPlayer entityPlayer) {
        IInventory[] containerInventories = new IInventory[4];

        InventoryCraftingGrid craftingGrid = new InventoryCraftingGrid(inventories[1]);
        InventoryRecipes recipes = null;

        if (backpack.isIntelligent()) {
            recipes = new InventoryRecipes(craftingGrid);
        }

        containerInventories[0] = inventories[0];
        containerInventories[1] = inventories[1];
        containerInventories[2] = craftingGrid;
        containerInventories[3] = recipes;

        ContainerWorkbenchBackpack container;
        if (inventories[1] instanceof AbstractInventoryBackpack) {
            container = new ContainerWorkbenchBackpack(containerInventories, backpack);
        } else {
            container = new ContainerWorkbenchBackpack(craftingGrid);
        }

        // set container width (needed for gui)
        container.setWidth(9 * SLOT + 2 * X_SPACING);

        int xSpacing;
        int x;
        if (backpack.isIntelligent()) {
            xSpacing = X_SPACING;
            x = xSpacing + 72;
        } else {
            xSpacing = BIG_X_SPACING;
            x = xSpacing + 95;
        }
        int y = 17;

        container.addBoundary(Boundaries.EXTRA);

        // result slot
        container.addSlot(new SlotCraftingAdvanced(entityPlayer, container, 0, x, y + 18, containerInventories));

        x = xSpacing;

        container.addBoundary(Boundaries.EXTRA_END);
        container.addBoundary(Boundaries.CRAFTING);

        // crafting grid
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                container.addSlot(new SlotPhantom(craftingGrid, col + row * 3, x, y));
                x += SLOT;
            }
            y += SLOT;
            x = xSpacing;
        }

        container.addBoundary(Boundaries.CRAFTING_END);

        if (backpack.isIntelligent()) {
            // recipes
            y = 17;
            x += 108;
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    container.addSlot(new SlotPhantom(recipes, col + row * 3, x, y));
                    x += SLOT;
                }
                y += SLOT;
                x = xSpacing + 108;
            }
        }

        x = X_SPACING;
        y += 5;

        container.addBoundary(Boundaries.BACKPACK);

        int remainingSlots = inventories[1].getSizeInventory();
        // backpack inventory
        for (int row = 0; row < inventories[1].getSizeInventory() / backpack.getSlotsPerRow(); row++) {
            int cols = remainingSlots - 9 >= 9 ? 9 : remainingSlots;
            remainingSlots -= cols;
            if (cols * SLOT < 9 * SLOT /* && !hasScrollbar */) {
                x += (int) Math.round(9 * SLOT / 2. - cols * SLOT / 2.) + 1;
            }
            for (int col = 0; col < cols; ++col) {
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
    public GuiContainer getGuiContainer(BackpackSave backpack, IInventory[] inventories, EntityPlayer entityPlayer) {
        ContainerAdvanced container = getContainer(backpack, inventories, entityPlayer);
        GuiWorkbenchBackpack guiBackpack = new GuiWorkbenchBackpack(container);

        GuiSlot guiSlot;
        for (int i = 0; i < container.inventorySlots.size(); i++) {
            Slot slot = (Slot) container.inventorySlots.get(i);
            if (i == 0) {
                guiSlot = new GuiSlot(slot.xDisplayPosition - 5, slot.yDisplayPosition - 5, 26);
            } else {
                guiSlot = new GuiSlot(slot.xDisplayPosition - 1, slot.yDisplayPosition - 1);
            }
            guiBackpack.addSubPart(guiSlot);
        }

        if (!backpack.isIntelligent()) {
            // arrow
            guiBackpack.addSubPart(new Icon(0, 238, 90, 35, 22, 15));
            // clear button
            Button btn_clear = new Button(0, 88, 16, 11, 11, "c");
            guiBackpack.addSubPart(btn_clear);
        } else {
            Button btn_clear = new Button(0, 66, 16, 11, 11, "c");
            Button btn_save = new Button(1, 81, 16, 11, 11, "s");
            guiBackpack.addSubPart(btn_clear);
            guiBackpack.addSubPart(btn_save);
        }

        int textPositionX = 28;
        Alignment alignment = Alignment.LEFT;
        if (backpack.isIntelligent()) {
            textPositionX = guiBackpack.getWidth() / 2;
            alignment = Alignment.CENTER;
        }
        int textPositionY = 6;

        int slotsPerRow = backpack.getSlotsPerRow();
        int inventoryRows = (int) Math.ceil(inventories[1].getSizeInventory() / (float) slotsPerRow);
        int textPositionY2 = textPositionY + inventoryRows * SLOT + 3 * SLOT + 19;

        guiBackpack.addSubPart(new Label(textPositionX, textPositionY, 0x404040, "container.crafting", alignment));
        guiBackpack.addSubPart(new Label(X_SPACING, textPositionY2, 0x404040, "container.inventory"));

        return guiBackpack;
    }
}
