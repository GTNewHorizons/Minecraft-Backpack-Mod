package de.eydamos.guiadvanced.misc;

import net.minecraft.client.Minecraft;

public interface AbstractGuiPart {

    int getWidth();

    void setWidth(int value);

    int getHeight();

    void setHeight(int value);

    void draw(Minecraft mc, int mouseX, int mouseY, float something);

    void setAbsolutePosition(int guiLeft, int guiTop);
}
