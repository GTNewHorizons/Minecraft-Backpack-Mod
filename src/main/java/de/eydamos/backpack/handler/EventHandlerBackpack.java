package de.eydamos.backpack.handler;

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

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.misc.Localizations;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import de.eydamos.backpack.util.EnchUtils;
import de.eydamos.backpack.util.NBTItemStackUtil;

public class EventHandlerBackpack {

    @SubscribeEvent
    public void serverTick(PlayerTickEvent event) {

        // On client
        if (ConfigurationBackpack.RENDER_BACKPACK_MODEL) {
            if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
                int ticks = event.player.ticksExisted;
                // Update players bags 3 sec
                if (ticks % (3 * 20) == 0) {
                    String pUid = event.player.getUniqueID().toString();
                    Backpack.packetHandler.networkWrapper.sendToServer(new MessagePersonalBackpack(pUid));
                }
            }
        }

        if (ConfigurationBackpack.MAX_BACKPACK_AMOUNT > 0) {
            if (event.side == Side.SERVER) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;

                int counter = 0;
                if (new PlayerSave(player).hasPersonalBackpack()) {
                    counter++;
                }

                ItemStack[] inventory = player.inventory.mainInventory;
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i] != null && inventory[i].getItem() instanceof ItemBackpackBase) {
                        counter++;
                        if (counter > ConfigurationBackpack.MAX_BACKPACK_AMOUNT) {
                            player.entityDropItem(inventory[i].copy(), 0);
                            inventory[i] = null;
                        }
                    }
                }

                counter -= ConfigurationBackpack.MAX_BACKPACK_AMOUNT;
                if (counter > 0) {
                    IChatComponent message = new ChatComponentText("[Backpacks] ");
                    message.appendSibling(
                            new ChatComponentTranslation(
                                    Localizations.MESSAGE_ALLOWED_BACKPACKS,
                                    ConfigurationBackpack.MAX_BACKPACK_AMOUNT));
                    player.addChatMessage(message);
                    message = new ChatComponentText("[Backpacks] ").appendSibling(
                            new ChatComponentTranslation(Localizations.MESSAGE_DROPPED_BACKPACKS, counter));
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
        if (!event.world.isRemote && event.world.provider.dimensionId == 0) {
            Backpack.saveFileHandler.init();
        }
    }

    @SubscribeEvent
    public void playerDies(PlayerDropsEvent event) {
        EntityPlayer entityPlayer = event.entityPlayer;

        PlayerSave playerSave = new PlayerSave(entityPlayer);
        ItemStack backpack = playerSave.getPersonalBackpack();
        if (backpack != null) {
            if (EnchUtils.isSoulBound(backpack)
                    || entityPlayer.getEntityWorld().getGameRules().getGameRuleBooleanValue("keepInventory")) {
                // Do nothing, let the playerSave object continue to contain the player's backpack.
            } else {
                event.drops.add(
                        new EntityItem(
                                entityPlayer.worldObj,
                                entityPlayer.posX,
                                entityPlayer.posY,
                                entityPlayer.posZ,
                                backpack));
                playerSave.setPersonalBackpack(null);
            }
        }
    }

    @SubscribeEvent
    public void itemCrafted(ItemCraftedEvent event) {
        ItemStack craftedItem = event.crafting;
        if (craftedItem != null && craftedItem.getItem() == ItemsBackpack.workbenchBackpack) {
            if (NBTItemStackUtil.hasTag(craftedItem, Constants.NBT.INTELLIGENT)) {
                BackpackSave backpackSave = new BackpackSave(craftedItem);
                backpackSave.setIntelligent();
                NBTItemStackUtil.removeTag(craftedItem, Constants.NBT.INTELLIGENT);
            }
        }
    }
}
