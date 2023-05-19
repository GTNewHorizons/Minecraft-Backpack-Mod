package de.eydamos.backpack.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTItemStackUtil {

    /**
     * Initializes the {@link NBTTagCompound} for the given {@link ItemStack} if it is null.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     */
    private static void initNBTTagCompound(ItemStack itemStack) {
        if (itemStack.stackTagCompound == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
    }

    /**
     * Checks if the {@link NBTTagCompound} of the given {@link ItemStack} has a given tag.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be checked.
     * @return True if the {@link NBTTagCompound} has the tag otherwise false.
     */
    public static boolean hasTag(ItemStack itemStack, String tagName) {
        if (itemStack.stackTagCompound != null) {
            return itemStack.stackTagCompound.hasKey(tagName);
        }
        return false;
    }

    /**
     * Removes the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be removed.
     */
    public static void removeTag(ItemStack itemStack, String tagName) {
        if (itemStack.stackTagCompound != null) {
            itemStack.stackTagCompound.removeTag(tagName);
        }
    }

    /**
     * Gets a String value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a String or an empty String if the NBT Tag Compound has no such key.
     */
    public static String getString(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getString(tagName);
        }

        return "";
    }

    /**
     * Sets the given String value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If
     * the {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a String.
     */
    public static void setString(ItemStack itemStack, String tagName, String tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setString(tagName, tagValue);
    }

    /**
     * Gets a Boolean value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Boolean or false if the NBT Tag Compound has no such key.
     */
    public static Boolean getBoolean(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getBoolean(tagName);
        }

        return false;
    }

    /**
     * Sets the given boolean value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If
     * the {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a boolean.
     */
    public static void setBoolean(ItemStack itemStack, String tagName, boolean tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setBoolean(tagName, tagValue);
    }

    /**
     * Gets a Byte value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Byte or 0 if the NBT Tag Compound has no such key.
     */
    public static Byte getByte(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getByte(tagName);
        }

        return (byte) 0;
    }

    /**
     * Sets the given byte value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a byte.
     */
    public static void setByte(ItemStack itemStack, String tagName, byte tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setByte(tagName, tagValue);
    }

    /**
     * Gets a Short value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Short or 0 if the NBT Tag Compound has no such key.
     */
    public static Short getShort(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getShort(tagName);
        }

        return (short) 0;
    }

    /**
     * Sets the given short value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a short.
     */
    public static void setShort(ItemStack itemStack, String tagName, short tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setShort(tagName, tagValue);
    }

    /**
     * Gets an Integer value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as an Integer or 0 if the NBT Tag Compound has no such key.
     */
    public static Integer getInteger(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getInteger(tagName);
        }

        return 0;
    }

    /**
     * Sets the given integer value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If
     * the {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as an integer.
     */
    public static void setInteger(ItemStack itemStack, String tagName, int tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setInteger(tagName, tagValue);
    }

    /**
     * Gets a Long value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Long or 0 if the NBT Tag Compound has no such key.
     */
    public static Long getLong(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getLong(tagName);
        }

        return (long) 0;
    }

    /**
     * Sets the given long value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a long.
     */
    public static void setLong(ItemStack itemStack, String tagName, long tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setLong(tagName, tagValue);
    }

    /**
     * Gets a Float value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Float or 0 if the NBT Tag Compound has no such key.
     */
    public static Float getFloat(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getFloat(tagName);
        }

        return (float) 0;
    }

    /**
     * Sets the given float value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a float.
     */
    public static void setFloat(ItemStack itemStack, String tagName, float tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setFloat(tagName, tagValue);
    }

    /**
     * Gets a Double value of the given tag from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Double or 0 if the NBT Tag Compound has no such key.
     */
    public static Double getDouble(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getDouble(tagName);
        }

        return (double) 0;
    }

    /**
     * Sets the given double value for the given tag on the {@link NBTTagCompound} of the given {@link ItemStack}. If
     * the {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a double.
     */
    public static void setDouble(ItemStack itemStack, String tagName, double tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setDouble(tagName, tagValue);
    }

    /**
     * Gets a {@link NBTTagCompound} with the given tag name from the {@link NBTTagCompound} of the given
     * {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a {@link NBTTagCompound}.
     */
    public static NBTTagCompound getCompoundTag(ItemStack itemStack, String tagName) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getCompoundTag(tagName);
        }

        return new NBTTagCompound();
    }

    /**
     * Sets the given {@link NBTTagCompound} with the given tag name on the {@link NBTTagCompound} of the given
     * {@link ItemStack}. If the {@link NBTTagCompound} is null it will be initialized.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag which should be set.
     * @param tagValue  The value which should be set to the given tag as a {@link NBTTagCompound}.
     */
    public static void setCompoundTag(ItemStack itemStack, String tagName, NBTTagCompound tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setTag(tagName, tagValue);
    }

    /**
     * Gets a {@link NBTTagList} with the given tag name from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     * @return The value of the given tag as a {@link NBTTagCompound}.
     */
    public static NBTTagList getTagList(ItemStack itemStack, String tagName, int tagType) {
        if (hasTag(itemStack, tagName)) {
            return itemStack.stackTagCompound.getTagList(tagName, tagType);
        }
        return new NBTTagList();
    }

    /**
     * Sets a {@link NBTTagList} with the given tag name from the {@link NBTTagCompound} of the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} which holds the {@link NBTTagCompound}.
     * @param tagName   The name of the tag for which the value should be determined.
     */
    public static void setTagList(ItemStack itemStack, String tagName, NBTTagList tagValue) {
        initNBTTagCompound(itemStack);
        itemStack.stackTagCompound.setTag(tagName, tagValue);
    }
}
