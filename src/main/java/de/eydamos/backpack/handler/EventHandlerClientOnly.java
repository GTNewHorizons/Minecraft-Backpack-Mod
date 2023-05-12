package de.eydamos.backpack.handler;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.model.BackpackModelWorker;

public class EventHandlerClientOnly {

    final BackpackModelWorker backRenderer = new BackpackModelWorker();

    @SubscribeEvent
    public void render(Pre event) {

        EntityPlayer entityPlayer = event.entityPlayer;
        NBTTagCompound playerData = entityPlayer.getEntityData();
        if (!playerData.hasKey(Constants.NBT.PERSONAL_BACKPACK_META)) {
            // Nope
            return;
        }

        int backMeta = playerData.getInteger(Constants.NBT.PERSONAL_BACKPACK_META);
        // Doing some render
        backRenderer.renderAt(entityPlayer, backMeta);
    }

    public static void updateTag(String uuid, int meta) {

        World worldClient = Minecraft.getMinecraft().theWorld;

        if (worldClient != null) {
            // Try to find player
            EntityPlayer player = worldClient.func_152378_a(UUID.fromString(uuid));
            // No player
            if (player == null) {
                return;
            }
            // Update tag
            player.getEntityData().setInteger(Constants.NBT.PERSONAL_BACKPACK_META, meta);
        }
    }
}
