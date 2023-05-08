package de.eydamos.guiadvanced.misc;

public interface AbstractGui {

    int getWidth();

    void setWidth(int value);

    int getHeight();

    void setHeight(int value);

    void addSubPart(AbstractGuiPart newSubPart);

    void removeSubPart(AbstractGuiPart removeSubPart);

    void clearSubParts();
}
