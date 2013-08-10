package backpack.item;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import backpack.Backpack;
import backpack.misc.ConfigurationBackpack;
import backpack.misc.Constants;
import backpack.model.ModelBackpack;
import backpack.util.IBackpack;
import backpack.util.IHasKeyBinding;
import backpack.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBackpack extends ItemArmor implements IBackpack, IHasKeyBinding, ISpecialArmor {
    protected Icon[] icons;
    protected ModelBiped backpackModel = null;

    /**
     * Creates an instance of the backpack item and sets some default values.
     * 
     * @param id
     *            The item id.
     */
    public ItemBackpack(int id) {
        super(id, ItemInfo.backpackMaterial, 0, 1);
        setMaxStackSize(1);
        setHasSubtypes(true);
        setUnlocalizedName(ItemInfo.UNLOCALIZED_NAME_BACKPACK);
        setCreativeTab(CreativeTabs.tabMisc);
        setFull3D();
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an
     * ItemStack so different stacks can have different names based on their
     * damage or NBT.
     */
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String name = super.getUnlocalizedName();

        int damage = itemStack.getItemDamage();
        if(damage >= 0 && damage < 17) {
            name += "." + ItemInfo.BACKPACK_COLORS[damage];
        }
        if(damage >= 32 && damage < 49) {
            name += ".big_" + ItemInfo.BACKPACK_COLORS[damage - 32];
        }
        if(damage == ItemInfo.ENDERBACKPACK) {
            name += "." + ItemInfo.BACKPACK_COLORS[16];
        }
        return name;
    }

    /**
     * Gets the icon from the registry.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        icons = new Icon[35];

        for(int i = 0; i < 35; ++i) {
            String name = "backpack:backpack";
            // colored backpacks + ender backpack 0-16
            if(i >= 0 && i < 17) {
                name += "_" + ItemInfo.BACKPACK_COLORS[i];
            }
            // normal backpack 17
            if(i == 17) {}
            // big colored backpack 18-34
            if(i > 17 && i < 34) {
                name += "_" + ItemInfo.BACKPACK_COLORS[i - 18] + "_big";
            }
            // big backpack 34
            if(i == 34) {
                name += "_big";
            }
            icons[i] = iconRegister.registerIcon(name);
        }
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
    public Icon getIconFromDamage(int damage) {
        if(damage >= 0 && damage < 16) {
            return icons[damage];
        }
        if(damage >= 32 && damage < 49) {
            return icons[damage - 14];
        }
        if(damage == ItemInfo.ENDERBACKPACK) {
            return icons[16];
        }
        return icons[17];
    }

    /**
     * Returns the sub items.
     * 
     * @param itemId
     *            the id of the item
     * @param tab
     *            A creative tab.
     * @param A
     *            List which stores the sub items.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(int itemId, CreativeTabs tab, List subItems) {
        for(int i = 0; i < 17; i++) {
            subItems.add(new ItemStack(itemId, 1, i));
        }
        for(int i = 32; i < 49; i++) {
            subItems.add(new ItemStack(itemId, 1, i));
        }
        if(itemId == Items.backpack.itemID) {
            subItems.add(new ItemStack(itemId, 1, ItemInfo.ENDERBACKPACK));
        }
    }

    /**
     * Callback for item usage. If the item does something special on right
     * clicking, he will have one of those. Return True if something happen and
     * false if it don't. This is for ITEMS, not BLOCKS
     * 
     * @param stack
     *            The itemstack which is used
     * @param player
     *            The player who used the item
     * @param worldObj
     *            The world in which the click has occured
     * @param x
     *            The x coord of the clicked block
     * @param y
     *            The y coord of the clicked block
     * @param z
     *            The z coord of the clicked block
     * @param side
     *            The side of the block that was clicked
     * @param hitX
     *            The x position on the block which got clicked
     * @param hitY
     *            The y position on the block which got clicked
     * @param hitz
     *            The z position on the block which got clicked
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World worldObj, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = worldObj.getBlockTileEntity(x, y, z);
        if(te != null && (te instanceof IInventory || te instanceof TileEntityEnderChest)) {
            if(te instanceof TileEntityEnderChest && stack.getItemDamage() == ItemInfo.ENDERBACKPACK) {
                return false;
            }
            player.openGui(Backpack.instance, Constants.GUI_ID_COMBINED, worldObj, x, y, z);
            return true;
        }
        return false;
    }

    /**
     * Handles what should be done on right clicking the item.
     * 
     * @param is
     *            The ItemStack which is right clicked.
     * @param world
     *            The world in which the player is.
     * @param player
     *            The player who right clicked the item.
     * @param Returns
     *            the ItemStack after the process.
     */
    @Override
    public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
        // if world.isRemote than we are on the client side
        if(world.isRemote) {
            // display rename GUI if player is sneaking
            if(player.isSneaking() && is.getItemDamage() != ItemInfo.ENDERBACKPACK) {
                player.openGui(Backpack.instance, Constants.GUI_ID_RENAME_BACKPACK, world, 0, 0, 0);
            }
            return is;
        }

        // when the player is not sneaking
        if(!player.isSneaking() && !ConfigurationBackpack.OPEN_ONLY_WEARED_BACKPACK) {
            player.openGui(Backpack.instance, Constants.GUI_ID_BACKPACK, world, 0, 0, 0);
        }
        return is;
    }

    @Override
    public void doKeyBindingAction(EntityPlayer player, ItemStack itemStack) {
        NBTUtil.setBoolean(itemStack, Constants.WEARED_BACKPACK_OPEN, true);
        player.openGui(Backpack.instance, Constants.GUI_ID_BACKPACK_WEARED, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
    }

    /**
     * Returns the item name to display in the tooltip.
     * 
     * @param itemstack
     *            The ItemStack to use for check.
     * @return The name of the backpack for the tooltip.
     */
    @Override
    public String getItemDisplayName(ItemStack itemstack) {
        // it ItemStack has a NBTTagCompound load name from inventory title.
        if(NBTUtil.hasTag(itemstack, "display")) {
            return NBTUtil.getCompoundTag(itemstack, "display").getString("Name");
        }
        // else if damage is between 0 and 15 return name from backpackNames
        // array
        int dmg = itemstack.getItemDamage();
        if(dmg >= 0 && dmg < 17) {
            return ItemInfo.NAME_BACKPACK[dmg];
        }
        if(dmg >= 32 && dmg < 49) {
            return "Big " + ItemInfo.NAME_BACKPACK[dmg - 32];
        }
        // else if damage is equal to ENDERBACKPACK then return backpackNames
        // index 16
        if(dmg == ItemInfo.ENDERBACKPACK) {
            return ItemInfo.NAME_BACKPACK[17];
        }

        // return index 0 of backpackNames array as fallback
        return ItemInfo.NAME_BACKPACK[16];
    }

    /**
     * Override ItemArmor implementation with default from Item so that the
     * correct color is rendered.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
        return 0xFFFFFF;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
        return "backpack:textures/model/backpack.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if(armorSlot == 1 && itemStack != null && itemStack.getItem() instanceof IBackpack) {
            if(backpackModel == null) {
                backpackModel = new ModelBackpack();
            }
            return backpackModel;
        }
        return null;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        return new ArmorProperties(0, damageReduceAmount / 25D, 80);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return damageReduceAmount;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
    }

    @Override
    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return false;
    }
}