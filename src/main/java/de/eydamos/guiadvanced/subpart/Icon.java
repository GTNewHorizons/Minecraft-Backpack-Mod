package de.eydamos.guiadvanced.subpart;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.Rectangle;

public class Icon implements AbstractGuiPart {

    protected int xPosition;
    protected int yPosition;
    protected int uPosition;
    protected int vPosition;
    protected int relativePositionX;
    protected int relativePositionY;
    protected int width;
    protected int height;
    protected ResourceLocation image;

    public Icon(int posX, int posY, int widthHeight) {
        this(null, 0, 0, posX, posY, widthHeight, widthHeight);
    }

    public Icon(int posX, int posY, int width, int height) {
        this(null, 0, 0, posX, posY, width, height);
    }

    public Icon(int u, int v, int posX, int posY, int width, int height) {
        this(null, u, v, posX, posY, width, height);
    }

    public Icon(ResourceLocation graphic, int posX, int posY, int width, int height) {
        this(graphic, 0, 0, posX, posY, width, height);
    }

    public Icon(ResourceLocation graphic, int u, int v, int posX, int posY, int width, int height) {
        setWidth(width);
        setHeight(height);
        relativePositionX = posX;
        relativePositionY = posY;
        image = graphic;
        uPosition = u;
        vPosition = v;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int value) {
        width = value;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int value) {
        height = value;
    }

    @Override
    public void draw(Minecraft mc, int mouseX, int mouseY, float something) {
        Rectangle icon = new Rectangle(width, height);
        icon.startDrawing();
        if (image != null) {
            icon.setBackground(image);
        }
        icon.setBackgroundPosition(uPosition, vPosition);
        icon.setBackgroundSize(width, height);
        icon.addBoxVertices(xPosition, yPosition);
        icon.performDrawing();
    }

    @Override
    public void setAbsolutePosition(int guiLeft, int guiTop) {
        xPosition = guiLeft + relativePositionX;
        yPosition = guiTop + relativePositionY;
    }
}
