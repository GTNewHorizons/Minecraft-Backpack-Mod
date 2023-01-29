package de.eydamos.backpack.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.config.GuiConfig;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;

public class GuiConfiguration extends GuiConfig {

    public GuiConfiguration(GuiScreen guiScreen) {
        super(
                guiScreen,
                new ConfigElement(ConfigurationBackpack.config.getCategory(Configuration.CATEGORY_GENERAL))
                        .getChildElements(),
                Constants.MOD_ID,
                false,
                false,
                I18n.format(Localizations.LABEL_BACKPACK_CONFIG));
    }
}
