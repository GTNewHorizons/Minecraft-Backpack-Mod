package de.eydamos.backpack.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.inventory.InventoryBackpack;
import de.eydamos.backpack.misc.BackpackUsageCache;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.backpack.util.EnchUtils;
import de.eydamos.backpack.util.NBTItemStackUtil;

public class ItemBackpackBase extends Item {

    public ItemBackpackBase() {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(ItemsBackpack.tabBackpacks);
    }

    /**
     * Returns the sub items.
     *
     * @param item     the id of the item
     * @param tab      a creative tab.
     * @param subItems a list that stores the sub items.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List subItems) {
        if (item == ItemsBackpack.backpack) {
            for (int tier = 0; tier < 3; tier++) {
                for (int i = 0; i < 17; i++) {
                    subItems.add(new ItemStack(item, 1, tier * 100 + i));
                }
            }
            subItems.add(new ItemStack(item, 1, ItemsBackpack.ENDERBACKPACK));
        } else if (item == ItemsBackpack.workbenchBackpack) {
            subItems.add(new ItemStack(item, 1, 17));
            subItems.add(new ItemStack(item, 1, 217));
        }
    }

    /**
     * Callback for item usage. If the item does something special on right-clicking, he will have one of those. Return
     * True if something happen and false if it doesn't. This is for ITEMS, not BLOCKS
     *
     * @param stack    The ItemStack which is used
     * @param player   The player who used the item
     * @param worldObj The world in which the click has occurred
     * @param x        The x coord of the clicked block
     * @param y        The y coord of the clicked block
     * @param z        The z coord of the clicked block
     * @param side     The side of the block that was clicked
     * @param hitX     The x position on the block which got clicked
     * @param hitY     The y position on the block which got clicked
     * @param hitZ     The z position on the block which got clicked
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World worldObj, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        /*
         * TileEntity te = worldObj.getTileEntity(x, y, z); if(te != null && (te instanceof IInventory || te instanceof
         * TileEntityEnderChest)) { boolean openGui = false; if(te instanceof TileEntityChest) { openGui = true; } if(te
         * instanceof TileEntityEnderChest && !BackpackUtil.isEnderBackpack(stack)) { openGui = true; } if(te instanceof
         * TileEntityFurnace) { openGui = true; } if(te instanceof TileEntityHopper) { openGui = true; } if(te
         * instanceof TileEntityBrewingStand) { openGui = true; } if(te instanceof TileEntityDispenser) { openGui =
         * true; } if(te instanceof TileEntityDropper) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityDirtChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityCopperChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityIronChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntitySilverChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityGoldChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityDiamondChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityCrystalChest")) { openGui = true; }
         * if(te.getClass().getSimpleName().equals("TileEntityObsidianChest")) { openGui = true; } if(openGui) {
         * //player.openGui(Backpack.instance, Constants.GUI_ID_COMBINED, worldObj, x, y, z); return true; } }
         */
        return false;
    }

    /**
     * Handles what should be done on right-clicking the item.
     *
     * @param itemStack    The ItemStack which is right-clicked.
     * @param world        The world in which the player is.
     * @param entityPlayer The player who right-clicked the item.
     * @return the ItemStack after the process.
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (null == itemStack.getTagCompound()) {
            onCreated(itemStack, world, entityPlayer);
        }
        if (!BackpackUtil.isServerSide(world)) {
            // display rename GUI if player is sneaking
            if (entityPlayer.isSneaking() && !BackpackUtil.isEnderBackpack(itemStack)) {
                GuiHelper.displayRenameGui();
            }
            return itemStack;
        }

        // when the player is not sneaking
        if (!entityPlayer.isSneaking() && !ConfigurationBackpack.OPEN_ONLY_PERSONAL_BACKPACK) {
            GuiHelper.displayBackpack(
                    new BackpackSave(itemStack),
                    getInventory(itemStack, entityPlayer),
                    (EntityPlayerMP) entityPlayer);
        }
        return itemStack;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        String name = super.getUnlocalizedName();

        int damage = itemStack.getItemDamage();
        int tier = damage / 100 < 3 ? damage / 100 : 0;
        int meta = damage % 100;
        name += (tier == 0 ? "" : '.') + ItemsBackpack.BACKPACK_TIERS[tier];
        if (meta > 0 && meta < 17) { // add color
            name += (tier == 0 ? '.' : '_') + ItemsBackpack.BACKPACK_COLORS[damage % 100];
        }
        if (meta == 99) { // ender backpack
            name += (tier == 0 ? '.' : '_') + ItemsBackpack.BACKPACK_COLORS[17];
        }
        return name;
    }

    /**
     * Returns the item name to display in the tooltip.
     *
     * @param itemStack The ItemStack to use for check.
     * @return The name of the backpack for the tooltip.
     */
    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        // it ItemStack has a NBTTagCompound load name from it.
        if (NBTItemStackUtil.hasTag(itemStack, Constants.NBT.CUSTOM_NAME)) {
            return NBTItemStackUtil.getString(itemStack, Constants.NBT.CUSTOM_NAME);
        }
        return StatCollector.translateToLocal(getUnlocalizedName(itemStack) + ".name");
    }

    @Override
    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return EnchUtils.isSoulBook(itemstack2);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        new BackpackSave(itemStack, true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List information,
            boolean advancedTooltip) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            if (itemStack.getItemDamage() != ItemsBackpack.ENDERBACKPACK) {
                // TODO BackpackUtil.getTier()
                information.add(
                        EnumChatFormatting.YELLOW + StatCollector.translateToLocal(Localizations.TIER)
                                + " "
                                + (itemStack.getItemDamage() / 100 + 1));

                if (itemStack.stackTagCompound == null) return;

                String uuid = itemStack.stackTagCompound.getString(Constants.NBT.UID);
                if (uuid == null) return;

                BackpackUsageCache.BackpackSlotUsageInfo info = BackpackUsageCache.getBackpackInfo(uuid);

                if (info != null) {
                    String slotsText = StatCollector.translateToLocal(Localizations.SLOTS_USED);
                    information.add(info.usedSlots + "/" + info.totalSlots + ' ' + slotsText);
                } else {
                    BackpackUsageCache.requestBackpackInfo(uuid);
                }
            }
        } else {
            information.add(StatCollector.translateToLocal(Localizations.MORE_INFORMATION));
        }
    }

    public static IInventory getInventory(ItemStack itemStack, EntityPlayer entityPlayer) {
        if (BackpackUtil.isEnderBackpack(itemStack)) {
            return entityPlayer.getInventoryEnderChest();
        }

        String defaultName = NBTItemStackUtil.getString(itemStack, Constants.NBT.NAME);
        String customName = NBTItemStackUtil.getString(itemStack, Constants.NBT.CUSTOM_NAME);

        return new InventoryBackpack(defaultName, customName);
    }
}
