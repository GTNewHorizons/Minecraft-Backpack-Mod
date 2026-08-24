package de.eydamos.backpack.saves;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.backpack.util.NBTUtil;

public class PlayerSave extends AbstractSave {

    protected byte type;

    public PlayerSave(String uuid) {
        super(uuid);
    }

    public PlayerSave(NBTTagCompound data) {
        super(data);
    }

    public PlayerSave(EntityPlayer entityPlayer) {
        this(new NBTTagCompound());
        load(entityPlayer.getUniqueID().toString());
    }

    public String getPersonalBackpackOpen() {
        return NBTUtil.getString(nbtTagCompound, Constants.NBT.PERSONAL_BACKPACK_OPEN);
    }

    public void setPersonalBackpackOpen(String backpackUUID) {
        NBTUtil.setString(nbtTagCompound, Constants.NBT.PERSONAL_BACKPACK_OPEN, backpackUUID);

        if (!manualSaving) {
            save();
        }
    }

    public void unsetPersonalBackpackOpen() {
        NBTUtil.removeTag(nbtTagCompound, Constants.NBT.PERSONAL_BACKPACK_OPEN);

        if (!manualSaving) {
            save();
        }
    }

    public boolean hasPersonalBackpack() {
        return NBTUtil.hasTag(nbtTagCompound, Constants.NBT.INVENTORY_PERSONAL_BACKPACK);
    }

    public ItemStack getPersonalBackpack() {
        if (NBTUtil.hasTag(nbtTagCompound, Constants.NBT.INVENTORY_PERSONAL_BACKPACK)) {
            return ItemStack.loadItemStackFromNBT(
                    NBTUtil.getCompoundTag(nbtTagCompound, Constants.NBT.INVENTORY_PERSONAL_BACKPACK));
        }

        return null;
    }

    public void setPersonalBackpack(ItemStack itemStack) {
        if (itemStack == null) {
            NBTUtil.removeTag(nbtTagCompound, Constants.NBT.INVENTORY_PERSONAL_BACKPACK);
        } else {
            NBTTagCompound backpack = new NBTTagCompound();
            itemStack.writeToNBT(backpack);
            NBTUtil.setCompoundTag(nbtTagCompound, Constants.NBT.INVENTORY_PERSONAL_BACKPACK, backpack);
        }

        if (!manualSaving) {
            save();
        }
    }

    @Override
    public byte getType() {
        return type;
    }

    @Override
    public void setType(byte value) {
        type = value;
    }

    @Override
    public void save() {
        if (UID != null && BackpackUtil.isServerSide()) {
            Backpack.saveFileHandler.savePlayer(nbtTagCompound, UID);
        }

        manualSaving = false;
    }

    @Override
    protected void load(String UUID) {
        if (UUID != null && BackpackUtil.isServerSide()) {
            UID = UUID;
            nbtTagCompound = Backpack.saveFileHandler.loadPlayer(UID);
        }
    }
}
