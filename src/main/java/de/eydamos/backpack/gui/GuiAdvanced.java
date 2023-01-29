package de.eydamos.backpack.gui;

import de.eydamos.backpack.handler.KeyInputHandler;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.guiadvanced.ContainerWindow;

public class GuiAdvanced extends ContainerWindow {

    public GuiAdvanced(ContainerAdvanced container) {
        super(container);
        setWidth(container.getWidth());
        setHeight(container.getHeight());
    }

    @Override
    protected void keyTyped(char charTyped, int keyCode) {
        super.keyTyped(charTyped, keyCode);

        if (KeyInputHandler.personalBackpack.getKeyCode() == keyCode) {
            mc.thePlayer.closeScreen();
        }
    }
}
