package de.eydamos.backpack.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemsBackpack {

    public static Item backpack;
    public static Item boundLeather;
    public static Item tannedLeather;
    public static Item workbenchBackpack;
    public static CreativeTabs tabBackpacks = new TabBackpacks();

    public static final String UNLOCALIZED_NAME_BACKPACK = "backpack";
    public static final String UNLOCALIZED_NAME_BACKPACK_WORKBENCH = "workbenchbackpack";
    public static final String UNLOCALIZED_NAME_BOUND_LEATHER = "boundLeather";
    public static final String UNLOCALIZED_NAME_TANNED_LEATHER = "tannedLeather";

    public static final int ENDERBACKPACK = 31999;
    public static final String[] BACKPACK_TIERS = { "", "medium", "big" };
    public static final String[] BACKPACK_COLORS = { "", "black", "red", "green", "brown", "blue", "purple", "cyan",
            "lightGray", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white", "ender" };

    public static void initItems() {
        backpack = new ItemBackpack();
        workbenchBackpack = new ItemWorkbenchBackpack();
        boundLeather = new ItemLeather();
        tannedLeather = new ItemLeather();

        GameRegistry.registerItem(backpack, "backpack");
        GameRegistry.registerItem(workbenchBackpack, "workbenchbackpack");
        GameRegistry.registerItem(boundLeather, "boundLeather");
        GameRegistry.registerItem(tannedLeather, "tannedLeather");
    }
}
