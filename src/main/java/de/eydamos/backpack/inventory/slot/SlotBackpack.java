package de.eydamos.backpack.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameData;
import de.eydamos.backpack.item.ItemBackpackBase;
import de.eydamos.backpack.misc.ConfigurationBackpack;

// TODO rework mechanic there are no IDs any more
public class SlotBackpack extends Slot {
    public SlotBackpack(IInventory inventory, int slotIndex, int xPos, int yPos) {
        super(inventory, slotIndex, xPos, yPos);
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * backpacks and disallowed items.
     */
    @Override
    public boolean isItemValid(ItemStack is) {
        // check for backpack
        if(is != null && is.getItem() instanceof ItemBackpackBase) {
            return false;
        }
        
        String[] forbiddenItemClasses = {
            // Adventure Backpack 2
            "com.darkona.adventurebackpack.item.ItemAdventureBackpack",
            // Jabba Dolly
            "mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover",   
            "mcp.mobius.betterbarrels.common.items.dolly.ItemDiamondMover",
            // Forestry Backpacks, includes Railcraft and MagicBees addons
            "forestry.storage.items.ItemBackpack",
            "forestry.storage.items.ItemBackpackNaturalist"
            // Backpack Mod
            //"de.eydamos.backpack.item.ItemBackpack",
            //"de.eydamos.backpack.item.ItemWorkbenchBackpack"
            };
        
        Item item = is.getItem();
        
        for (String itemClass : forbiddenItemClasses) {
        	if (item.getClass().getName() == (itemClass)) return false;	
        }

        // check for disallowedItems
        String[] disallowedItems = ConfigurationBackpack.DISALLOW_ITEMS.split(",");
        for(String disallowedItem : disallowedItems) {
            Object[] disallowedData = getDisallowedData(disallowedItem);
            // if Integer check id
            if(disallowedData[0] instanceof Item) {
                if((Item) disallowedData[0] == is.getItem()) {
                    // if disallwedData has 2 values check item damage
                    if(disallowedData.length == 2) {
                        if((Integer) disallowedData[1] == is.getItemDamage()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                int[] ids = OreDictionary.getOreIDs(is);
                for(int id : ids) {
                // if not an integer it is a string so check for ore dictionary name
                    if(id == OreDictionary.getOreID((String)disallowedData[0])) {
                        return false;
                    }
                }
            }
        }
        return super.isItemValid(is);
    }

    private Object[] getDisallowedData(String disallowedItem) {
        String[] disallowedWithMeta = disallowedItem.split(":");
        Object[] result = new Object[disallowedWithMeta.length];
        result[0] = GameData.getItemRegistry().getObject(disallowedWithMeta[0]);
        if(result.length == 2 && disallowedWithMeta[1].matches("^-?\\d+$")) {
            result[1] = Integer.valueOf(disallowedWithMeta[1]);
        }
        return result;
    }
}
