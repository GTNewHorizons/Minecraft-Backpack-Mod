package de.eydamos.backpack.gui.tab;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;

@SideOnly(Side.CLIENT)
public class InventoryTabBackpack extends AbstractInventoryTab {

    public InventoryTabBackpack() {
        super(0, 0, 0, new ItemStack(ItemsBackpack.backpack), 1);
    }

    @Override
    public void onTabClicked() {
        GuiHelper.sendOpenPersonalGui(Constants.Guis.OPEN_PERSONAL_BACKPACK);
    }

    @Override
    public boolean onShiftTabClicked() {
        GuiHelper.sendOpenPersonalGui(Constants.Guis.OPEN_PERSONAL_SLOT);
        return true;
    }

    @Override
    public boolean shouldAddToList() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return false;
        NBTTagCompound data = player.getEntityData();
        return data.hasKey(Constants.NBT.PERSONAL_BACKPACK_META)
                && data.getInteger(Constants.NBT.PERSONAL_BACKPACK_META) >= 0;
    }

    @Override
    public List<String> getTooltip() {
        return Arrays.asList(
                StatCollector.translateToLocal(Localizations.TAB_BACKPACK_TOOLTIP_OPEN),
                "§7" + StatCollector.translateToLocal(Localizations.TAB_BACKPACK_TOOLTIP_SHIFT));
    }

    public void updateIcon() {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return;
        int meta = player.getEntityData().getInteger(Constants.NBT.PERSONAL_BACKPACK_META);
        renderStack.setItemDamage(meta);
    }
}
