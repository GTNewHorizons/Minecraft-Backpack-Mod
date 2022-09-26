package de.eydamos.guiadvanced.misc;

public interface AbstractGui {
    public int getWidth();

    public void setWidth(int value);

    public int getHeight();

    public void setHeight(int value);

    public void addSubPart(AbstractGuiPart newSubPart);

    public void removeSubPart(AbstractGuiPart removeSubPart);

    public void clearSubParts();
}
