package de.eydamos.guiadvanced.form;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;

import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.Rectangle;
import de.eydamos.guiadvanced.util.RenderHelper.BackgroundRepeat;

public class Button extends GuiButton implements AbstractGuiPart {

    protected int relativePositionX;
    protected int relativePositionY;

    public Button(int buttonId, int posX, int posY, int width, int height, String text) {
        super(buttonId, posX, posY, width, height, text);
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
        // unneeded GuiScreen calls drawButton directly so we overwrite this method
    }

    @Override
    public void setAbsolutePosition(int guiLeft, int guiTop) {
        xPosition = guiLeft + relativePositionX;
        yPosition = guiTop + relativePositionY;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            field_146123_n = mouseX >= xPosition && mouseY >= yPosition
                    && mouseX < xPosition + width
                    && mouseY < yPosition + height;
            int offset = getHoverState(field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            Rectangle rectangle = new Rectangle(2, 2);
            rectangle.setBackground(buttonTextures);
            rectangle.setBackgroundSize(2, 2);
            rectangle.startDrawing();
            // draw upper left corner
            rectangle.setBackgroundPosition(0, 46 + offset * 20);
            rectangle.addBoxVertices(xPosition, yPosition);
            // draw upper right corner
            rectangle.setBackgroundPosition(198, 46 + offset * 20);
            rectangle.addBoxVertices(xPosition + width - 2, yPosition);
            // draw lower left corner
            rectangle.setBackgroundPosition(0, 64 + offset * 20);
            rectangle.addBoxVertices(xPosition, yPosition + height - 2);
            // draw lower right corner
            rectangle.setBackgroundPosition(198, 64 + offset * 20);
            rectangle.addBoxVertices(xPosition + width - 2, yPosition + height - 2);

            // borders top/bottom
            rectangle.setWidth(width - 4);
            rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT_X);
            // draw top border
            rectangle.setBackgroundPosition(2, 46 + offset * 20);
            rectangle.addBoxVertices(xPosition + 2, yPosition);
            // draw bottom border
            rectangle.setBackgroundPosition(2, 64 + offset * 20);
            rectangle.addBoxVertices(xPosition + 2, yPosition + height - 2);

            // borders left/right
            rectangle.setWidth(2);
            rectangle.setHeight(height - 4);
            rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT_Y);
            // draw left border
            rectangle.setBackgroundPosition(0, 48 + offset * 20);
            rectangle.addBoxVertices(xPosition, yPosition + 2);
            // draw right border
            rectangle.setBackgroundPosition(198, 48 + offset * 20);
            rectangle.addBoxVertices(xPosition + width - 2, yPosition + 2);

            // draw background
            rectangle.setWidth(width - 4);
            rectangle.setHeight(height - 4);
            rectangle.setBackgroundSize(18, 18);
            rectangle.setBackgroundRepeat(BackgroundRepeat.REPEAT);
            rectangle.setBackgroundPosition(2, 48 + offset * 20);
            rectangle.addBoxVertices(xPosition + 2, yPosition + 2);
            rectangle.performDrawing();

            mouseDragged(mc, mouseX, mouseY);
            int l = 14737632;

            if (packedFGColour != 0) {
                l = packedFGColour;
            } else if (!enabled) {
                l = 10526880;
            } else if (field_146123_n) {
                l = 16777120;
            }

            drawCenteredString(fontrenderer, displayString, xPosition + width / 2 + 1, yPosition + (height - 8) / 2, l);
        }
    }
}
