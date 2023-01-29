package de.eydamos.backpack.inventory;

import de.eydamos.backpack.saves.AbstractSave;

public interface ISaveableInventory<S extends AbstractSave> {

    public void readFromNBT(S save);

    public void writeToNBT(S save);
}
