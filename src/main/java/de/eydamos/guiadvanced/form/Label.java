package de.eydamos.guiadvanced.form;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.Alignment;

public class Label implements AbstractGuiPart {

    protected int xPosition;
    protected int yPosition;
    protected int color;
    protected int relativePositionX;
    protected int relativePositionY;
    protected String text;
    protected Alignment textAlignment = Alignment.LEFT;

    public Label(int posX, int posY, int color, String text) {
        relativePositionX = posX;
        relativePositionY = posY;
        this.color = color;
        this.text = text;
    }

    public Label(int posX, int posY, int color, String text, Alignment alignment) {
        this(posX, posY, color, text);
        textAlignment = alignment;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public void setWidth(int value) {}

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setHeight(int value) {}

    public int getColor() {
        return color;
    }

    public void setColor(int value) {
        color = value;
    }

    public Alignment getAlignment() {
        return textAlignment;
    }

    public void setAlignment(Alignment value) {
        textAlignment = value;
    }

    @Override
    public void draw(Minecraft mc, int mouseX, int mouseY, float something) {
        String label = I18n.format(text);
        int offset = 0;
        switch (textAlignment) {
            case RIGHT -> offset -= mc.fontRenderer.getStringWidth(label);
            case CENTER -> offset -= mc.fontRenderer.getStringWidth(label) / 2;
            default -> {}
        }
        mc.fontRenderer.drawString(label, xPosition + offset, yPosition, color);
    }

    @Override
    public void setAbsolutePosition(int guiLeft, int guiTop) {
        xPosition = guiLeft + relativePositionX;
        yPosition = guiTop + relativePositionY;
    }
}
