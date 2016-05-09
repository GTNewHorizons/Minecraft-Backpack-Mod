package de.eydamos.backpack.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBackpack extends ItemBackpackBase {
    protected IIcon[] icons;

    /**
     * Creates an instance of the backpack item and sets some default values.
     * 
     * @param id
     *            The item id.
     */
    public ItemBackpack() {
        super();
        setUnlocalizedName(ItemsBackpack.UNLOCALIZED_NAME_BACKPACK);
    }

    /**
     * Gets the icon from the registry.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        icons = new IIcon[52];

        String name;
        for(int tier = 0; tier < 3; tier++) {
            for(int meta = 0; meta < 17; meta++) {
                name = "backpack:backpack";
                name += (meta == 0 ? "" : '_') + ItemsBackpack.BACKPACK_COLORS[meta];
                name += (tier == 0 ? "" : '_') + ItemsBackpack.BACKPACK_TIERS[tier];
                icons[tier * 17 + meta] = iconRegister.registerIcon(name);
            }
        }
        icons[51] = iconRegister.registerIcon("backpack:backpack_ender");
    }

    /**
     * Returns the icon index based on the item damage.
     * 
     * @param damage
     *            The damage to check for.
     * @return The icon index.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int tier = damage / 100;
        int meta = damage % 100;
        if(tier > 2) {
            return icons[51];
        }
        return icons[tier * 17 + meta];
    }
}
