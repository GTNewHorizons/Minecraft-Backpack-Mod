package de.eydamos.backpack.handler;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.backpack.util.NBTItemStackUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.world.WorldEvent;

public class EventHandlerBackpack {
    @SubscribeEvent
    public void serverTick(PlayerTickEvent event) {
        if(ConfigurationBackpack.MAX_BACKPACK_AMOUNT > 0) {
            if(event.side == Side.SERVER) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;

                int counter = 0;
                if(new PlayerSave(player).hasPersonalBackpack()) {
                    counter++;
                }

                ItemStack[] inventory = player.inventory.mainInventory;
                for(int i = 0; i < inventory.length; i++) {
                    if(inventory[i] != null && inventory[i].getItem() instanceof ItemBackpackBase) {
                        counter++;
                        if(counter > ConfigurationBackpack.MAX_BACKPACK_AMOUNT) {
                            player.entityDropItem(inventory[i].copy(), 0);
                            inventory[i] = null;
                        }
                    }
                }

                counter -= ConfigurationBackpack.MAX_BACKPACK_AMOUNT;
                if(counter > 0) {
                    IChatComponent message = new ChatComponentText("[Backpacks] ");
                    message.appendSibling(new ChatComponentTranslation(Localizations.MESSAGE_ALLOWED_BACKPACKS, ConfigurationBackpack.MAX_BACKPACK_AMOUNT));
                    player.addChatMessage(message);
                    message = new ChatComponentText("[Backpacks] ").appendSibling(new ChatComponentTranslation(Localizations.MESSAGE_DROPPED_BACKPACKS, counter));
                    player.addChatMessage(message);
                }
            }
        }
    }

    @SubscribeEvent
    public void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equalsIgnoreCase(Constants.MOD_ID)) {
        	ConfigurationBackpack.loadConfiguration();
        }
    }
    
    @SubscribeEvent
    public void playerPickup(EntityItemPickupEvent event) {
        BackpackUtil.pickupItem(event.entityPlayer, event.item.getEntityItem());
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event) {
        Backpack.saveFileHandler.init();
    }

    @SubscribeEvent
    public void playerDies(PlayerDropsEvent event) {
        EntityPlayer entityPlayer = event.entityPlayer;

        PlayerSave playerSave = new PlayerSave(entityPlayer);
        ItemStack backpack = playerSave.getPersonalBackpack();
        if(backpack != null) {
            event.drops.add(new EntityItem(entityPlayer.worldObj, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ, backpack));
            playerSave.setPersonalBackpack(null);
        }
    }

    @SubscribeEvent
    public void itemCrafted(ItemCraftedEvent event) {
        ItemStack craftedItem = event.crafting;
        if(craftedItem != null && craftedItem.getItem() == ItemsBackpack.workbenchBackpack) {
            if(NBTItemStackUtil.hasTag(craftedItem, Constants.NBT.INTELLIGENT)) {
                BackpackSave backpackSave = new BackpackSave(craftedItem);
                backpackSave.setIntelligent();
                NBTItemStackUtil.removeTag(craftedItem, Constants.NBT.INTELLIGENT);
            }
        }
    }
}
