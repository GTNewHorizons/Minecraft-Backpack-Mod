package de.eydamos.backpack.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.inventory.container.ContainerAdvanced;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;

public class GuiWorkbenchBackpack extends GuiAdvanced {

    public GuiWorkbenchBackpack(ContainerAdvanced container) {
        super(container);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        super.drawScreen(mouseX, mouseY, par3);

        ArrayList<String> tooltip = new ArrayList<>();
        for (Object buttonObj : buttonList) {
            GuiButton button = (GuiButton) buttonObj;
            if (button.func_146115_a()) {
                if (button.id == 0) {
                    tooltip.add(I18n.format(Localizations.CLEAR_CRAFT_MATRIX));
                } else if (button.id == 1) {
                    tooltip.add(I18n.format(Localizations.SAVE_RECIPE));
                    tooltip.add(I18n.format(Localizations.CLICK_A_SLOT));
                }
            }
        }

        func_146283_a(tooltip, mouseX, mouseY);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        switch (guiButton.id) {
            case 0 -> GuiHelper.sendGuiCommand(Constants.GuiCommands.CLEAR);
            case 1 -> GuiHelper.sendGuiCommand(Constants.GuiCommands.SAVE);
        }
    }

    @Override
    protected void keyTyped(char charTyped, int keyCode) {
        super.keyTyped(charTyped, keyCode);

        switch (charTyped) {
            case 'c' -> GuiHelper.sendGuiCommand(Constants.GuiCommands.CLEAR);
            case 's' -> GuiHelper.sendGuiCommand(Constants.GuiCommands.SAVE);
        }
    }
}
