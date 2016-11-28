package de.eydamos.backpack.inventory.slot;

import cpw.mods.fml.common.registry.GameData;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// TODO rework mechanic there are no IDs any more
public class SlotBackpack extends Slot {
    public SlotBackpack(IInventory inventory, int slotIndex, int xPos, int yPos) {
        super(inventory, slotIndex, xPos, yPos);
    }

    private static final String[] FORBIDDEN_CLASSES =
    {
        // Adventure Backpack 2
        "com.darkona.adventurebackpack.item.ItemAdventureBackpack",
        // Backpack Mod
        "de.eydamos.backpack.item.ItemBackpack",
        "de.eydamos.backpack.item.ItemWorkbenchBackpack",
        // Blue Power Canvas Bags
        "com.bluepowermod.item.ItemCanvasBag",
        // Extra Utilities Golden Bag of Holding
        "com.rwtema.extrautils.item.ItemGoldenBag",
        // Forestry Backpacks +addons
        "forestry.storage.items.ItemBackpack",
        "forestry.storage.items.ItemBackpackNaturalist",
        // Jabba Dolly
        "mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover",
        "mcp.mobius.betterbarrels.common.items.dolly.ItemDiamondMover",
        // Project Red Exploration Backpacks
        "mrtjp.projectred.exploration.ItemBackpack"
    };

    /**
     * Check if the stack is a valid item for this slot. Always true beside for
     * backpacks and disallowed items.
     */
    @Override
    public boolean isItemValid(ItemStack is) {
        
    	if(is == null) return false;
    	
    	Item itemCurrent = is.getItem();
    	
    	// check for forbiddenItemClasses
    	for (String itemClass : FORBIDDEN_CLASSES) {
    	    if (itemCurrent.getClass().getName().equals(itemClass)) return false;	
    	}

    	// check for disallowedItems
    	for (String itemDisallowed : ConfigurationBackpack.DISALLOW_ITEM_IDS) {
    	    if (Item.itemRegistry.getNameForObject(itemCurrent).equals(itemDisallowed)) return false;
    	}
    		
    	    /*String[] disallowedItems = ConfigurationBackpack.DISALLOW_ITEMS.split(",");
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
    	    }*/
    		
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
