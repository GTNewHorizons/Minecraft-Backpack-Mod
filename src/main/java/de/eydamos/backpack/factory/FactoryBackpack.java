package de.eydamos.backpack.factory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.gui.GuiAdvanced;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.saves.AbstractSave;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;

public class FactoryBackpack {

    public static <S extends AbstractSave> ContainerAdvanced getContainer(S save, IInventory[] inventories,
            EntityPlayer entityPlayer) {
        byte type = save.getType();
        return switch (type) {
            case 1 -> new FactoryBackpackNormal().getContainer((BackpackSave) save, inventories, entityPlayer);
            case 2 -> new FactoryWorkbenchBackpack().getContainer((BackpackSave) save, inventories, entityPlayer);
            case -1 -> new FactoryPersonalSlot().getContainer((PlayerSave) save, inventories, entityPlayer);
            default -> new ContainerAdvanced();
        };
    }

    @SideOnly(Side.CLIENT)
    public static <S extends AbstractSave> GuiContainer getGuiContainer(AbstractSave save, IInventory[] inventories,
            EntityPlayer entityPlayer) {
        byte type = save.getType();
        return switch (type) {
            case 1 -> new FactoryBackpackNormal().getGuiContainer((BackpackSave) save, inventories, entityPlayer);
            case 2 -> new FactoryWorkbenchBackpack().getGuiContainer((BackpackSave) save, inventories, entityPlayer);
            case -1 -> new FactoryPersonalSlot().getGuiContainer((PlayerSave) save, inventories, entityPlayer);
            default -> new GuiAdvanced(getContainer(save, inventories, entityPlayer));
        };
    }
}
