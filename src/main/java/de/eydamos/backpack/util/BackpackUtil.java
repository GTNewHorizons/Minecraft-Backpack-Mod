package de.eydamos.backpack.util;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import de.eydamos.backpack.inventory.InventoryPickup;
import de.eydamos.backpack.inventory.container.ContainerPickup;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;

public class BackpackUtil {

    public static void playOpenSound(Entity ent) {

        if (ConfigurationBackpack.PLAY_OPEN_SOUND) {
            String soundName = Constants.MOD_ID + ":OpenBackpack";
            float volume = 0.7f;
            float maxSpeed = 0.2f;
            float minSpeed = 0.9f;
            ent.worldObj.playSoundAtEntity(ent, soundName, volume, (float) ((Math.random() * maxSpeed) + minSpeed));
        }
    }

    public static boolean isEnderBackpack(ItemStack itemStack) {
        return itemStack != null && itemStack.getItemDamage() == ItemsBackpack.ENDERBACKPACK;
    }

    public static boolean isServerSide() {
        return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
    }

    public static boolean isServerSide(World world) {
        return !world.isRemote;
    }

    public static String getUUID(NBTTagCompound nbtTagCompound) {
        if (NBTUtil.hasTag(nbtTagCompound, Constants.NBT.UID)) {
            return NBTUtil.getString(nbtTagCompound, Constants.NBT.UID);
        }
        return null;
    }

    public static byte getType(ItemStack backpack) {
        if (backpack.getItem() == ItemsBackpack.backpack) {
            return 1;
        } else if (backpack.getItem() == ItemsBackpack.workbenchBackpack) {
            return 2;
        }
        return 0;
    }

    public static void pickupItem(EntityPlayer entityPlayer, ItemStack itemStack) {
        PlayerSave playerSave = new PlayerSave(entityPlayer);
        ItemStack backpack = playerSave.getPersonalBackpack();
        if (backpack != null) {
            InventoryPickup inventoryPickup = new InventoryPickup();
            inventoryPickup.setInventoryContent(backpack);

            ContainerPickup container = new ContainerPickup(
                    ItemBackpackBase.getInventory(backpack, entityPlayer),
                    new BackpackSave(backpack));
            boolean hasPickedUp = false;
            for (int i = 0; i < inventoryPickup.getSizeInventory(); i++) {
                ItemStack pickupItemStack = inventoryPickup.getStackInSlot(i);
                if (areStacksEqual(pickupItemStack, itemStack, true)) {
                    hasPickedUp = container.pickupItem(itemStack) || hasPickedUp;
                }
            }

            if (hasPickedUp) {
                container.onContainerClosed(entityPlayer);
            }
        }
    }

    public static boolean canStack(ItemStack stack1, ItemStack stack2) {
        return (stack1 == null) || (stack2 == null)
                || ((stack1.getItem() == stack2.getItem())
                        && (!stack1.getHasSubtypes() || stack1.getItemDamage() == stack2.getItemDamage())
                        && (ItemStack.areItemStackTagsEqual(stack1, stack2))
                        && (stack1.isStackable()));
    }

    /**
     * Checks if two items are equal based on item id and damage or if they have no subtypes if they have the same item
     * id.
     *
     * @param firstStack  The first ItemStack to check.
     * @param secondStack The second ItemStack to compare to.
     * @return True if both have the same item id and damage or true if both have no subtypes and have the same item id.
     *         False otherwise.
     */
    public static boolean areStacksEqual(ItemStack firstStack, ItemStack secondStack) {
        return areStacksEqual(firstStack, secondStack, false);
    }

    /**
     * Checks if two items are equal based on item id and damage or if they have no subtypes if they have the same item
     * id.
     *
     * @param firstStack       The first ItemStack to check.
     * @param secondStack      The second ItemStack to compare to.
     * @param useOreDictionary If true the method also checks if the ItemStacks have the same OreDictionary ID.
     * @return True if both have the same item id and damage or true if both have no subtypes and have the same item id.
     *         False otherwise.
     */
    public static boolean areStacksEqual(ItemStack firstStack, ItemStack secondStack, boolean useOreDictionary) {
        if (firstStack == null || secondStack == null) {
            return false;
        }
        // are items and damage the same
        if (firstStack.isItemEqual(secondStack)) {
            return true;
        }
        // if both have no subtype only check the item
        if (!firstStack.getHasSubtypes() && !secondStack.getHasSubtypes()) {
            if (firstStack.getItem() == secondStack.getItem()) {
                return true;
            }
        }
        return useOreDictionary && areStacksEqualByOD(firstStack, secondStack);
    }

    /**
     * Checks if two ItemStacks are equal based on the OreDictionary ID.
     *
     * @param firstStack  The first ItemStack.
     * @param secondStack The second ItemStack.
     * @return True if both ItemStacks have the same OreId, false otherwise or if one or both ItemStacks are null.
     */
    public static boolean areStacksEqualByOD(ItemStack firstStack, ItemStack secondStack) {
        if (firstStack == null || secondStack == null) {
            return false;
        }
        int[] oreIdFirst = OreDictionary.getOreIDs(firstStack);
        int[] oreIdSecond = OreDictionary.getOreIDs(secondStack);

        for (int a : oreIdFirst) {
            for (int b : oreIdSecond) {
                if (a == b) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Compares the UUID's of two ItemStacks.
     *
     * @param suspicious The ItemStack to check.
     * @param original   The original ItemStack to compare to
     * @return Returns true if both have the same UUID, false if one or both ItemStacks are null, one or both ItemStacks
     *         doesn't have the tag "backpack-UID" or if the UUID's are not equal.
     */
    public static boolean UUIDEquals(ItemStack suspicious, ItemStack original) {
        if (suspicious != null && original != null) {
            if (NBTItemStackUtil.hasTag(suspicious, Constants.NBT.UID)
                    && NBTItemStackUtil.hasTag(original, Constants.NBT.UID)) {
                String UIDsuspicious = NBTItemStackUtil.getString(suspicious, Constants.NBT.UID);
                String UIDoriginal = NBTItemStackUtil.getString(original, Constants.NBT.UID);
                return UUIDEquals(UIDsuspicious, UIDoriginal);
            }
        }
        return false;
    }

    /**
     * Compares the UUID's of two ItemStacks.
     *
     * @param suspicious The ItemStack to check.
     * @param original   The original UUID to compare to
     * @return Returns true if both have the same UUID, false if one or both ItemStacks are null, one or both ItemStacks
     *         doesn't have the tag "backpack-UID" or if the UUID's are not equal.
     */
    public static boolean UUIDEquals(ItemStack suspicious, String original) {
        if (suspicious != null && original != null) {
            if (NBTItemStackUtil.hasTag(suspicious, Constants.NBT.UID)) {
                String UIDsuspicious = NBTItemStackUtil.getString(suspicious, Constants.NBT.UID);
                return UUIDEquals(UIDsuspicious, original);
            }
        }
        return false;
    }

    /**
     * Compares the UUID's of two ItemStacks.
     *
     * @param suspicious The UUID to check.
     * @param original   The original UUID to compare to
     * @return Returns true if both have the same UUID, false if one or both ItemStacks are null, one or both ItemStacks
     *         doesn't have the tag "backpack-UID" or if the UUID's are not equal.
     */
    public static boolean UUIDEquals(String suspicious, String original) {
        if (suspicious != null && original != null) {
            try {
                UUID UIDsuspicious = UUID.fromString(suspicious);
                UUID UIDoriginal = UUID.fromString(original);
                return UIDsuspicious.equals(UIDoriginal);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return false;
    }
}
