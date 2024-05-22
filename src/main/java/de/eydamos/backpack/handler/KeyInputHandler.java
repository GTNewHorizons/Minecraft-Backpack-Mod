package de.eydamos.backpack.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.eydamos.backpack.helper.GuiHelper;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;

@SideOnly(Side.CLIENT)
public class KeyInputHandler {

    public static KeyBinding personalBackpack = new KeyBinding(
            Localizations.KEY_PERSONAL,
            Keyboard.KEY_NONE,
            Localizations.KEY_CATEGORY);

    @SubscribeEvent
    public void handleKeyInputEvent(KeyInputEvent event) {
        onInput();
    }

    @SubscribeEvent
    public void handleMouseInputEvent(MouseInputEvent event) {
        onInput();
    }

    public void onInput() {
        if (personalBackpack.isPressed()) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            // If we are not in a GUI of any kind
            if (mc.inGameHasFocus) {
                // get the current player which has pressed the key
                EntityPlayer player = mc.thePlayer;
                if (player != null) {
                    // if player is sneaking open the slot gui else open the backpack
                    if (player.isSneaking()) {
                        GuiHelper.sendOpenPersonalGui(Constants.Guis.OPEN_PERSONAL_SLOT);
                    } else {
                        GuiHelper.sendOpenPersonalGui(Constants.Guis.OPEN_PERSONAL_BACKPACK);
                    }
                }
            }
        }
    }
}
