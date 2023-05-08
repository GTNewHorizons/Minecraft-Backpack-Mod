package de.eydamos.guiadvanced;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import de.eydamos.guiadvanced.misc.AbstractGui;
import de.eydamos.guiadvanced.misc.AbstractGuiPart;
import de.eydamos.guiadvanced.util.RenderHelper;

public class ContainerWindow extends GuiContainer implements AbstractGui {

    protected final ArrayList<AbstractGuiPart> subParts = new ArrayList<AbstractGuiPart>();

    public ContainerWindow(Container container) {
        super(container);
    }

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
        super.initGui();

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

    /* ========== overrides from GuiContainer ========== */
    @Override
    protected void drawGuiContainerBackgroundLayer(float something, int mouseX, int mouseY) {
        // draw background
        RenderHelper.drawOuterCornerTopLeft(guiLeft, guiTop);
        RenderHelper.drawBorderTop(guiLeft + 4, guiTop, xSize - 8, 4);
        RenderHelper.drawOuterCornerTopRight(guiLeft + xSize - 4, guiTop);
        RenderHelper.drawBorderLeft(guiLeft, guiTop + 4, 4, ySize - 8);
        RenderHelper.drawBackground(guiLeft + 4, guiTop + 4, xSize - 8, ySize - 8);
        RenderHelper.drawBorderRight(guiLeft + xSize - 4, guiTop + 4, 4, ySize - 8);
        RenderHelper.drawOuterCornerBottomLeft(guiLeft, guiTop + ySize - 4);
        RenderHelper.drawBorderBottom(guiLeft + 4, guiTop + ySize - 4, xSize - 8, 4);
        RenderHelper.drawOuterCornerBottomRight(guiLeft + xSize - 4, guiTop + ySize - 4);

        // draw subparts
        for (AbstractGuiPart guiPart : subParts) {
            guiPart.draw(mc, mouseX, mouseY, something);
        }
    }
}
