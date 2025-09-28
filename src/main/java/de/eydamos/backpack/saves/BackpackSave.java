package de.eydamos.backpack.saves;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.backpack.util.NBTItemStackUtil;
import de.eydamos.backpack.util.NBTUtil;

public class BackpackSave extends AbstractSave {

    public BackpackSave(String uuid) {
        super(uuid);
    }

    public BackpackSave(NBTTagCompound data) {
        super(data);
        if (NBTUtil.hasTag(nbtTagCompound, Constants.NBT.UID)) {
            UID = NBTUtil.getString(nbtTagCompound, Constants.NBT.UID);
        }
    }

    public BackpackSave(ItemStack backpack) {
        this(backpack, false);
    }

    public BackpackSave(ItemStack backpack, boolean force) {
        super(new NBTTagCompound());
        if (!NBTItemStackUtil.hasTag(backpack, Constants.NBT.UID)) {
            initialize(backpack);
        } else {
            if (backpack.getItem() instanceof ItemBackpackBase) {
                load(NBTItemStackUtil.getString(backpack, Constants.NBT.UID));
                if (force) {
                    initialize(backpack);
                }
            }
        }
    }

    public boolean isUninitialized() {
        return nbtTagCompound.hasNoTags();
    }

    public void initialize(ItemStack backpack) {
        if (backpack.getItem() instanceof ItemBackpackBase && BackpackUtil.isServerSide()) {
            NBTItemStackUtil
                    .setString(backpack, Constants.NBT.NAME, backpack.getItem().getUnlocalizedName(backpack) + ".name");
            if (!NBTItemStackUtil.hasTag(backpack, Constants.NBT.UID)) {
                UID = UUID.randomUUID().toString();
                NBTItemStackUtil.setString(backpack, Constants.NBT.UID, UID);
            }

            int size = getSize(backpack);

            setManualSaving();

            setSize(size);
            setType(BackpackUtil.getType(backpack));
            if (!NBTUtil.hasTag(nbtTagCompound, Constants.NBT.INVENTORIES)) {
                NBTUtil.setCompoundTag(nbtTagCompound, Constants.NBT.INVENTORIES, new NBTTagCompound());
            }

            save();
        }
    }

    private static int getSize(ItemStack backpack) {
        int damage = backpack.getItemDamage();
        int tier = damage / 100;
        int meta = damage % 100;

        // Only accept valid tiers (0, 1, 2); fallback to 0 if invalid
        if (tier >= 3) tier = 0;

        // Ender backpack
        if (meta == 99) {
            return 27;
        }

        // Workbench variants
        if (meta == 17) {
            if (tier == 2) return 18;
            if (tier == 0) return 9;
        }

        // Standard backpacks
        if (meta < 17) {
            switch (tier) {
                case 2:
                    return ConfigurationBackpack.BACKPACK_SLOTS_L;
                case 1:
                    return ConfigurationBackpack.BACKPACK_SLOTS_M;
                case 0:
                    return ConfigurationBackpack.BACKPACK_SLOTS_S;
            }
        }

        return 0;
    }

    public String getUUID() {
        return UID;
    }

    public static String getUUID(ItemStack backpack) {
        if (NBTItemStackUtil.hasTag(backpack, Constants.NBT.UID)) {
            return NBTItemStackUtil.getString(backpack, Constants.NBT.UID);
        } else {
            return new BackpackSave(backpack).getUUID();
        }
    }

    public boolean isIntelligent() {
        return NBTUtil.getBoolean(nbtTagCompound, Constants.NBT.INTELLIGENT);
    }

    public void setIntelligent() {
        NBTUtil.setBoolean(nbtTagCompound, Constants.NBT.INTELLIGENT, true);

        if (!manualSaving) {
            save();
        }
    }

    public int getSize() {
        return NBTUtil.getInteger(nbtTagCompound, Constants.NBT.SIZE);
    }

    public void setSize(int size) {
        NBTUtil.setInteger(nbtTagCompound, Constants.NBT.SIZE, size);

        if (!manualSaving) {
            save();
        }
    }

    public int getSlotsPerRow() {
        int size = getSize();

        // 7 rows of slots is the most that can fit in max MC gui scale,
        // which is 63 total slots in 7 rows of 9 slots each.
        // Anything above 63 needs more than the vanilla 9 slots per row.
        if (size <= 63) {
            return 9;
        }

        final int MIN_ROWS = 3; // 3 * 19 = 57, 57 < 63
        final int MAX_ROWS = 7;
        final int MIN_COLS = 9;
        final int MAX_COLS = 19;
        // Search for the perfect rectangular fit
        for (int rows = MAX_ROWS; rows >= MIN_ROWS; rows--) {
            if (size % rows != 0) continue;
            int columns = size / rows;
            if (columns >= MIN_COLS && columns <= MAX_COLS) {
                return columns;
            }
        }

        // Search for the next best fit
        for (int columns = MIN_COLS; columns <= MAX_COLS; columns++) {
            int rows = (size + columns - 1) / columns;
            if (rows <= MAX_ROWS) {
                return columns;
            }
        }

        throw new IllegalArgumentException("Inventory too large to fit in a 7x19 grid (" + size + " slots)");
    }

    @Override
    public byte getType() {
        return NBTUtil.getByte(nbtTagCompound, Constants.NBT.TYPE);
    }

    @Override
    public void setType(byte type) {
        NBTUtil.setByte(nbtTagCompound, Constants.NBT.TYPE, type);

        if (!manualSaving) {
            save();
        }
    }

    public NBTTagList getInventory(String inventory) {
        NBTTagCompound inventories = NBTUtil.getCompoundTag(nbtTagCompound, Constants.NBT.INVENTORIES);
        return NBTUtil.getTagList(inventories, inventory, NBT.TAG_COMPOUND);
    }

    public void setInventory(String inventoryName, NBTTagList inventory) {
        NBTTagCompound inventories = NBTUtil.getCompoundTag(nbtTagCompound, Constants.NBT.INVENTORIES);
        NBTUtil.setTagList(inventories, inventoryName, inventory);

        if (!manualSaving) {
            save();
        }
    }

    @Override
    public void save() {
        if (UID != null && BackpackUtil.isServerSide()) {
            Backpack.saveFileHandler.saveBackpack(nbtTagCompound, UID);
        }

        manualSaving = false;
    }

    @Override
    protected void load(String UUID) {
        if (UUID != null && BackpackUtil.isServerSide()) {
            UID = UUID;
            nbtTagCompound = Backpack.saveFileHandler.loadBackpack(UID);
        }
    }
}
