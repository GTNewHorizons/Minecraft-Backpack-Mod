package de.eydamos.backpack.item;

import de.eydamos.backpack.misc.Localizations;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabBackpacks extends CreativeTabs {
    protected ItemStack iconItemStack = null;

    public TabBackpacks() {
        super(Localizations.TAB_BACKPACKS);
    }

    @Override
    public Item getTabIconItem() {
        return ItemsBackpack.backpack;
    }
}
