package de.eydamos.backpack.handler;

import java.util.UUID;

import de.eydamos.backpack.model.BackpackModelWorker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import de.eydamos.backpack.misc.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre;

public class EventHandlerClientOnly {

    BackpackModelWorker backRenderer = new BackpackModelWorker();

    @SubscribeEvent
    public void render(Pre event) {

        EntityPlayer entityPlayer = event.entityPlayer;
        NBTTagCompound playerData = entityPlayer.getEntityData();
        if(!playerData.hasKey(Constants.NBT.PERSONAL_BACKPACK_META)) {
            //Nope
            return;
        }

        int backMeta = playerData.getInteger(Constants.NBT.PERSONAL_BACKPACK_META);
        //Doing some render
        backRenderer.renderAt(entityPlayer,backMeta);


    }


    public static void updateTag(String uuid, int meta){

        World worldClient = Minecraft.getMinecraft().theWorld;

        if(worldClient!=null){
            //Try to find player
          EntityPlayer player = worldClient.func_152378_a(UUID.fromString(uuid));
          //No player
          if(player==null){
              return;
          }
          //Uptade tag
            player.getEntityData().setInteger(Constants.NBT.PERSONAL_BACKPACK_META , meta);

        }


    }



}
