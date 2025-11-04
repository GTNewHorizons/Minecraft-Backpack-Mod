package de.eydamos.guiadvanced;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import de.eydamos.guiadvanced.misc.AbstractGui;
import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.Rectangle;
import de.eydamos.guiadvanced.util.RenderHelper;

public class Window extends GuiScreen implements AbstractGui {

    protected final ArrayList<AbstractGuiPart> subParts = new ArrayList<>();
    protected int xSize = 0;
    protected int ySize = 0;
    protected int guiLeft = 0;
    protected int guiTop = 0;

    @Override
    public int getWidth() {
        return xSize;
    }

    @Override
    public void setWidth(int value) {
        xSize = value;
    }

    @Override
    public int getHeight() {
        return ySize;
    }

    @Override
    public void setHeight(int value) {
        ySize = value;
    }

    @Override
    public void initGui() {
        guiLeft = (width - xSize) / 2;
        guiTop = (height - ySize) / 2;

        for (AbstractGuiPart guiPart : subParts) {
            guiPart.setAbsolutePosition(guiLeft, guiTop);
            if (guiPart instanceof GuiButton) {
                buttonList.add(guiPart);
            }
        }
    }

    @Override
    public void addSubPart(AbstractGuiPart newSubPart) {
        subParts.add(newSubPart);
    }

    @Override
    public void removeSubPart(AbstractGuiPart removeSubPart) {
        subParts.remove(removeSubPart);
    }

    @Override
    public void clearSubParts() {
        subParts.clear();
    }

    /* ========== overrides from GuiScreen ========== */
    @Override
    public void drawScreen(int mouseX, int mouseY, float something) {
        // draw background
        Rectangle rect = new Rectangle();
        rect.startDrawing();
        RenderHelper.drawOuterCornerTopLeft(rect, guiLeft, guiTop);
        RenderHelper.drawBorderTop(rect, guiLeft + 4, guiTop, xSize - 8, 4);
        RenderHelper.drawOuterCornerTopRight(rect, guiLeft + xSize - 4, guiTop);
        RenderHelper.drawBorderLeft(rect, guiLeft, guiTop + 4, 4, ySize - 8);
        RenderHelper.drawBackground(rect, guiLeft + 4, guiTop + 4, xSize - 8, ySize - 8);
        RenderHelper.drawBorderRight(rect, guiLeft + xSize - 4, guiTop + 4, 4, ySize - 8);
        RenderHelper.drawOuterCornerBottomLeft(rect, guiLeft, guiTop + ySize - 4);
        RenderHelper.drawBorderBottom(rect, guiLeft + 4, guiTop + ySize - 4, xSize - 8, 4);
        RenderHelper.drawOuterCornerBottomRight(rect, guiLeft + xSize - 4, guiTop + ySize - 4);
        rect.performDrawing();

        // draw subparts
        for (AbstractGuiPart guiPart : subParts) {
            guiPart.draw(mc, mouseX, mouseY, something);
        }

        super.drawScreen(mouseX, mouseY, something);
    }
}
