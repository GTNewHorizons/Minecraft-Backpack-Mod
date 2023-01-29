package de.eydamos.backpack.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.saves.AbstractSave;

abstract class AbstractFactory<S extends AbstractSave> {

    protected final int X_SPACING = 8;
    protected final int SLOT = 18;

    public abstract ContainerAdvanced getContainer(S nbtTagCompound, IInventory[] inventories,
            EntityPlayer entityPlayer);

    public abstract GuiContainer getGuiContainer(S nbtTagCompound, IInventory[] inventories, EntityPlayer entityPlayer);
}
