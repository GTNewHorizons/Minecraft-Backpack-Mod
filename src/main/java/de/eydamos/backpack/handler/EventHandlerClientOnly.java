package de.eydamos.backpack.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public class EventHandlerClientOnly {
    public static HashMap<String, Integer> backpackDamage = new HashMap<String, Integer>();

    @SubscribeEvent
    public void render(Pre event) {
        EntityPlayer entityPlayer = event.entityPlayer;
        String UUID = entityPlayer.getUniqueID().toString();

        Backpack.packetHandler.networkWrapper.sendToServer(new MessagePersonalBackpack(UUID));

        if(backpackDamage.containsKey(UUID) && backpackDamage.get(UUID) != -1) {
            GL11.glPushMatrix();

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            Minecraft.getMinecraft().renderEngine.bindTexture(Constants.modelTexture);

            Constants.model.render(entityPlayer, 0F, 0F, 0F, 0F, 0F, 0.0625F);

            GL11.glPopMatrix();
        }
    }

    @SubscribeEvent
    public void onConfigurationChanged(ConfigChangedEvent event) {
        ConfigurationBackpack.loadConfiguration();
    }

}
