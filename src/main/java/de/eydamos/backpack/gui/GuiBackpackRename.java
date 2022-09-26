package de.eydamos.backpack.gui;

import cpw.mods.fml.client.FMLClientHandler;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.guiadvanced.Window;
import de.eydamos.guiadvanced.form.Button;
import de.eydamos.guiadvanced.form.Label;
import de.eydamos.guiadvanced.form.Textbox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiBackpackRename extends Window {
    private String TITLE = I18n.format(Localizations.INVENTORY_RENAME);
    private String NEW_NAME = I18n.format(Localizations.LABEL_NEW_NAME);

    protected Textbox txt_backpackName;
    protected Button btn_ok, btn_cancel;

    public GuiBackpackRename() {
        setWidth(240);
        setHeight(100);

        fontRendererObj = FMLClientHandler.instance().getClient().fontRenderer;

        // create button for ok and disable it at the beginning
        btn_ok = new Button(0, xSize - 100, 70, 60, 20, I18n.format(Localizations.BUTTON_OK));
        btn_ok.enabled = false;

        // create button for cancel
        btn_cancel = new Button(1, 40, 70, 60, 20, I18n.format(Localizations.BUTTON_CANCEL));

        // add buttons to supParts
        addSubPart(btn_ok);
        addSubPart(btn_cancel);

        // create "Rename your Backpack" label at the top in the middle
        int posX = xSize / 2 - fontRendererObj.getStringWidth(TITLE) / 2;
        addSubPart(new Label(posX, 10, 0x000000, TITLE));

        // create "New name:" label at the left site above the GuiTextField
        addSubPart(new Label(20, 30, 0x404040, NEW_NAME));

        // create text field
        txt_backpackName = new Textbox(fontRendererObj, 20, 40, 200, 20);
        txt_backpackName.setFocused(true);
        txt_backpackName.setMaxStringLength(32);
        addSubPart(txt_backpackName);
    }

    @Override
    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        txt_backpackName.updateCursorCounter();
    }

    /**
     * Fired when a control is clicked. This is the equivalent of
     * ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        // if button is disabled ignore click
        if (!guibutton.enabled) {
            return;
        }

        // id 0 = ok; id 1 = cancel
        switch (guibutton.id) {
            case 0:
                String name = txt_backpackName.getText().trim();

                GuiHelper.renameBackpack(name);
            case 1:
                // remove the GUI
                mc.setIngameFocus();
                break;
            default:
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char c, int i) {
        // add char to GuiTextField
        txt_backpackName.textboxKeyTyped(c, i);
        // enable ok button when GuiTextField content is greater than 0 chars
        ((GuiButton) buttonList.get(0)).enabled =
                txt_backpackName.getText().trim().length() > 0;
        // perform click event on ok button when Enter is pressed
        if (c == '\n' || c == '\r') {
            actionPerformed((GuiButton) buttonList.get(0));
        }
        // perform click event on cancel button when Esc is pressed
        if (Integer.valueOf(c) == 27) {
            actionPerformed((GuiButton) buttonList.get(1));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int i, int j, int k) {
        super.mouseClicked(i, j, k);
        // move cursor to clicked position in GuiTextField
        txt_backpackName.mouseClicked(i, j, k);
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float something) {
        // draw transparent background
        drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, something);
    }
}
