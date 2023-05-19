package de.eydamos.guiadvanced.form;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import de.eydamos.guiadvanced.misc.AbstractGuiPart;

public class Textbox extends GuiTextField implements AbstractGuiPart {

    protected final int relativePositionX;
    protected final int relativePositionY;

    public Textbox(FontRenderer fontRenderer, int posX, int posY, int width, int height) {
        super(fontRenderer, posX, posY, width, height);
        relativePositionX = posX;
        relativePositionY = posY;
    }

    @Override
    public void setWidth(int value) {
        ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, this, value, "width", "h");
    }

    @Override
    public int getHeight() {
        return ObfuscationReflectionHelper.getPrivateValue(GuiTextField.class, this, "height", "i");
    }

    @Override
    public void setHeight(int value) {
        ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, this, value, "height", "i");
    }

    @Override
    public void draw(Minecraft mc, int mouseX, int mouseY, float something) {
        drawTextBox();
    }

    @Override
    public void setAbsolutePosition(int guiLeft, int guiTop) {
        ObfuscationReflectionHelper
                .setPrivateValue(GuiTextField.class, this, guiLeft + relativePositionX, "xPosition", "f");
        ObfuscationReflectionHelper
                .setPrivateValue(GuiTextField.class, this, guiTop + relativePositionY, "yPosition", "g");
    }
}
