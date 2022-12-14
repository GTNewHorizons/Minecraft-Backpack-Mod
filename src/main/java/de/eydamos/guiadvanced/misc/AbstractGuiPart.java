package de.eydamos.guiadvanced.misc;

import net.minecraft.client.Minecraft;

public interface AbstractGuiPart {
    public int getWidth();

    public void setWidth(int value);

    public int getHeight();

    public void setHeight(int value);

    public void draw(Minecraft mc, int mouseX, int mouseY, float something);

    public void setAbsolutePosition(int guiLeft, int guiTop);
}
