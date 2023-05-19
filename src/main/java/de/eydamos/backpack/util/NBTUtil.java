package de.eydamos.backpack.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTUtil {

    /**
     * Initializes the {@link NBTTagCompound} if it is null.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to initialize.
     */
    private static NBTTagCompound initNBTTagCompound(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            return new NBTTagCompound();
        }
        return nbtTagCompound;
    }

    /**
     * Checks if the {@link NBTTagCompound} has a given tag.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag which should be checked.
     * @return True if the {@link NBTTagCompound} has the tag otherwise false.
     */
    public static boolean hasTag(NBTTagCompound nbtTagCompound, String tagName) {
        if (nbtTagCompound != null) {
            return nbtTagCompound.hasKey(tagName);
        }
        return false;
    }

    /**
     * Removes the given tag from the {@link NBTTagCompound} .
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be removed.
     */
    public static void removeTag(NBTTagCompound nbtTagCompound, String tagName) {
        if (nbtTagCompound != null) {
            nbtTagCompound.removeTag(tagName);
        }
    }

    /**
     * Gets a String value with the given tag from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a String or an empty String if the {@link NBTTagCompound} has no such key.
     */
    public static String getString(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getString(tagName);
        }

        return "";
    }

    /**
     * Sets the given String value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is
     * null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a String.
     */
    public static void setString(NBTTagCompound nbtTagCompound, String tagName, String tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setString(tagName, tagValue);
    }

    /**
     * Gets a Boolean value with the given tag from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Boolean or false if the {@link NBTTagCompound} has no such key.
     */
    public static Boolean getBoolean(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getBoolean(tagName);
        }

        return false;
    }

    /**
     * Sets the given boolean value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is
     * null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a boolean.
     */
    public static void setBoolean(NBTTagCompound nbtTagCompound, String tagName, boolean tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setBoolean(tagName, tagValue);
    }

    /**
     * Gets a Byte value with the given tag from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Byte or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Byte getByte(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getByte(tagName);
        }

        return (byte) 0;
    }

    /**
     * Sets the given byte value for the given tag on the {@link NBTTagCompound} . If the {@link NBTTagCompound} is null
     * it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a byte.
     */
    public static void setByte(NBTTagCompound nbtTagCompound, String tagName, byte tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setByte(tagName, tagValue);
    }

    /**
     * Gets a Short value with the given tag from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Short or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Short getShort(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getShort(tagName);
        }

        return (short) 0;
    }

    /**
     * Sets the given short value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is null
     * it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a short.
     */
    public static void setShort(NBTTagCompound nbtTagCompound, String tagName, short tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setShort(tagName, tagValue);
    }

    /**
     * Gets an Integer value with the given tag from the {@link NBTTagCompound} of the given nbtTagCompound.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as an Integer or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Integer getInteger(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getInteger(tagName);
        }

        return 0;
    }

    /**
     * Sets the given integer value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is
     * null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as an integer.
     */
    public static void setInteger(NBTTagCompound nbtTagCompound, String tagName, int tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setInteger(tagName, tagValue);
    }

    /**
     * Gets a Long value with the given tag from the {@link NBTTagCompound} of the given nbtTagCompound.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Long or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Long getLong(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getLong(tagName);
        }

        return (long) 0;
    }

    /**
     * Sets the given long value for the given tag on the {@link NBTTagCompound} . If the {@link NBTTagCompound} is null
     * it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a long.
     */
    public static void setLong(NBTTagCompound nbtTagCompound, String tagName, long tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setLong(tagName, tagValue);
    }

    /**
     * Gets a Float value with the given tag from the {@link NBTTagCompound} of the given nbtTagCompound.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Float or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Float getFloat(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getFloat(tagName);
        }

        return (float) 0;
    }

    /**
     * Sets the given float value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is null
     * it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a float.
     */
    public static void setFloat(NBTTagCompound nbtTagCompound, String tagName, float tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setFloat(tagName, tagValue);
    }

    /**
     * Gets a Double value with the given tag from the {@link NBTTagCompound} of the given nbtTagCompound.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a Double or 0 if the {@link NBTTagCompound} has no such key.
     */
    public static Double getDouble(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getDouble(tagName);
        }

        return (double) 0;
    }

    /**
     * Sets the given double value for the given tag on the {@link NBTTagCompound}. If the {@link NBTTagCompound} is
     * null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a double.
     */
    public static void setDouble(NBTTagCompound nbtTagCompound, String tagName, double tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setDouble(tagName, tagValue);
    }

    /**
     * Gets a {@link NBTTagCompound} with the given tag name from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a {@link NBTTagCompound} or an empty {@link NBTTagCompound} if the
     *         {@link NBTTagCompound} has no such key.
     */
    public static NBTTagCompound getCompoundTag(NBTTagCompound nbtTagCompound, String tagName) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getCompoundTag(tagName);
        }

        return new NBTTagCompound();
    }

    /**
     * Sets the given {@link NBTTagCompound} with the given tag name on the {@link NBTTagCompound}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a {@link NBTTagCompound}.
     */
    public static void setCompoundTag(NBTTagCompound nbtTagCompound, String tagName, NBTTagCompound tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setTag(tagName, tagValue);
    }

    /**
     * Gets a {@link NBTTagList} with the given tag name from the {@link NBTTagCompound}.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to check.
     * @param tagName        The name of the tag for which the value should be determined.
     * @return The value of the given tag as a {@link NBTTagList} or an empty {@link NBTTagList} if the
     *         {@link NBTTagCompound} has no such key.
     */
    public static NBTTagList getTagList(NBTTagCompound nbtTagCompound, String tagName, int tagType) {
        if (hasTag(nbtTagCompound, tagName)) {
            return nbtTagCompound.getTagList(tagName, tagType);
        }
        return new NBTTagList();
    }

    /**
     * Sets the given {@link NBTTagCompound} with the given tag name on the {@link NBTTagCompound}. If the
     * {@link NBTTagCompound} is null it will be initialized.
     *
     * @param nbtTagCompound The {@link NBTTagCompound} to change.
     * @param tagName        The name of the tag which should be set.
     * @param tagValue       The value which should be set to the given tag as a {@link NBTTagCompound}.
     */
    public static void setTagList(NBTTagCompound nbtTagCompound, String tagName, NBTTagList tagValue) {
        nbtTagCompound = initNBTTagCompound(nbtTagCompound);
        nbtTagCompound.setTag(tagName, tagValue);
    }
}
