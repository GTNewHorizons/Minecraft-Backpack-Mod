package de.eydamos.backpack.factory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiAdvanced;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.saves.AbstractSave;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class FactoryBackpack {
    public static <S extends AbstractSave> ContainerAdvanced getContainer(
            S save, IInventory[] inventories, EntityPlayer entityPlayer) {
        byte type = save.getType();
        if (type == 1) {
            return new FactoryBackpackNormal().getContainer((BackpackSave) save, inventories, entityPlayer);
        } else if (type == 2) {
            return new FactoryWorkbenchBackpack().getContainer((BackpackSave) save, inventories, entityPlayer);
        } else if (type == -1) {
            return new FactoryPersonalSlot().getContainer((PlayerSave) save, inventories, entityPlayer);
        }
        return new ContainerAdvanced();
    }

    @SideOnly(Side.CLIENT)
    public static <S extends AbstractSave> GuiContainer getGuiContainer(
            AbstractSave save, IInventory[] inventories, EntityPlayer entityPlayer) {
        byte type = save.getType();
        if (type == 1) {
            return new FactoryBackpackNormal().getGuiContainer((BackpackSave) save, inventories, entityPlayer);
        } else if (type == 2) {
            return new FactoryWorkbenchBackpack().getGuiContainer((BackpackSave) save, inventories, entityPlayer);
        } else if (type == -1) {
            return new FactoryPersonalSlot().getGuiContainer((PlayerSave) save, inventories, entityPlayer);
        }
        return new GuiAdvanced(getContainer(save, inventories, entityPlayer));
    }
}
