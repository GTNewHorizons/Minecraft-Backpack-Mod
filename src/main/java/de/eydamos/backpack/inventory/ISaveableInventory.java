package de.eydamos.backpack.inventory;

import de.eydamos.backpack.saves.AbstractSave;

public interface ISaveableInventory<S extends AbstractSave> {

    void readFromNBT(S save);

    void writeToNBT(S save);
}
