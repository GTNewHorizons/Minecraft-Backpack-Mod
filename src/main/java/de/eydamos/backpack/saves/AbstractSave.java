package de.eydamos.backpack.saves;

import net.minecraft.nbt.NBTTagCompound;

public abstract class AbstractSave {

    protected String UID;
    protected NBTTagCompound nbtTagCompound;
    protected boolean manualSaving = false;

    public AbstractSave(NBTTagCompound data) {
        nbtTagCompound = data;

        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
    }

    public AbstractSave(String UUID) {
        load(UUID);
    }

    public void setManualSaving() {
        manualSaving = true;
    }

    public abstract byte getType();

    public abstract void setType(byte value);

    public abstract void save();

    protected abstract void load(String UUID);
}
