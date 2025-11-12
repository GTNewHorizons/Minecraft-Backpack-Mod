package de.eydamos.guiadvanced.subpart;

import net.minecraft.client.Minecraft;

import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.Rectangle;
import de.eydamos.guiadvanced.util.RenderHelper.BackgroundRepeat;

public class GuiSlot implements AbstractGuiPart {

    protected int xPosition;
    protected int yPosition;
    protected int relativePositionX;
    protected int relativePositionY;
    protected int width;
    protected int height;

    public GuiSlot(int posX, int posY) {
        this(posX, posY, 18, 18);
    }

    public GuiSlot(int posX, int posY, int widthHeight) {
        this(posX, posY, widthHeight, widthHeight);
    }

    public GuiSlot(int posX, int posY, int width, int height) {
        setWidth(width);
        setHeight(height);
        relativePositionX = posX;
        relativePositionY = posY;
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
        Rectangle rectangle = new Rectangle(1, 1);
        rectangle.startDrawing();
        // draw upper left corner
        rectangle.setBackgroundPosition(201, 0);
        rectangle.addBoxVertices(xPosition, yPosition);
        // draw upper right corner
        rectangle.setBackgroundPosition(218, 0);
        rectangle.addBoxVertices(xPosition + width - 1, yPosition);
        // draw lower left corner
        rectangle.setBackgroundPosition(201, 17);
        rectangle.addBoxVertices(xPosition, yPosition + height - 1);
        // draw lower right corner
        rectangle.setBackgroundPosition(218, 17);
        rectangle.addBoxVertices(xPosition + width - 1, yPosition + height - 1);

        // borders top/bottom
        rectangle.setWidth(width - 2);
        rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
        // draw top border
        rectangle.setBackgroundPosition(202, 0);
        rectangle.addBoxVertices(xPosition + 1, yPosition);
        // draw bottom border
        rectangle.setBackgroundPosition(202, 17);
        rectangle.addBoxVertices(xPosition + 1, yPosition + height - 1);

        // borders left/right
        rectangle.setWidth(1);
        rectangle.setHeight(height - 2);
        rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
        // draw left border
        rectangle.setBackgroundPosition(201, 1);
        rectangle.addBoxVertices(xPosition, yPosition + 1);
        // draw right border
        rectangle.setBackgroundPosition(218, 1);
        rectangle.addBoxVertices(xPosition + width - 1, yPosition + 1);

        // draw background
        rectangle.setWidth(width - 2);
        rectangle.setHeight(height - 2);
        rectangle.setBackgroundSize(14, 14);
        rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT);
        rectangle.setBackgroundPosition(202, 1);
        rectangle.addBoxVertices(xPosition + 1, yPosition + 1);
        rectangle.performDrawing();
    }

    @Override
    public void setAbsolutePosition(int guiLeft, int guiTop) {
        xPosition = guiLeft + relativePositionX;
        yPosition = guiTop + relativePositionY;
    }
}
