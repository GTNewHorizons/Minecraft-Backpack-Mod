package de.eydamos.backpack.inventory.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.inventory.ISaveableInventory;
import de.eydamos.backpack.inventory.slot.SlotCraftingAdvanced;
import de.eydamos.backpack.inventory.slot.SlotPhantom;
import de.eydamos.backpack.saves.BackpackSave;
import de.eydamos.backpack.saves.PlayerSave;
import de.eydamos.backpack.util.BackpackUtil;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;

@ChestContainer
public class ContainerAdvanced extends Container {

    protected int width;
    protected int height;
    protected IInventory inventory;
    protected BackpackSave backpackSave;
    protected final Map<Boundaries, Integer> boundaries = new HashMap<>();

    public ContainerAdvanced() {}

    public ContainerAdvanced(IInventory inventoryToSave) {
        inventory = inventoryToSave;
        inventory.openInventory();
    }

    public ContainerAdvanced(IInventory[] inventories, BackpackSave save) {
        this(inventories.length == 1 ? inventories[0] : inventories[1]);
        backpackSave = save;
        for (IInventory inventory : inventories) {
            if (inventory instanceof ISaveableInventory) {
                ((ISaveableInventory) inventory).readFromNBT(backpackSave);
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer) {
        if (inventory instanceof InventoryEnderChest) {
            return true;
        }

        PlayerSave playerSave = new PlayerSave(entityPlayer);
        String UUID = null;
        if (!Objects.equals(playerSave.getPersonalBackpackOpen(), "")) {
            UUID = playerSave.getPersonalBackpackOpen();
        } else if (entityPlayer.getCurrentEquippedItem() != null) {
            UUID = BackpackSave.getUUID(entityPlayer.getCurrentEquippedItem());
        }
        if (UUID == null || backpackSave == null) {
            return false;
        }
        return BackpackUtil.UUIDEquals(UUID, backpackSave.getUUID());
    }

    @Override
    public void onContainerClosed(EntityPlayer entityPlayer) {
        if (BackpackUtil.isServerSide(entityPlayer.worldObj) && backpackSave != null) {
            if (inventory instanceof ISaveableInventory) {
                ((ISaveableInventory) inventory).writeToNBT(backpackSave);
            }

            new PlayerSave(entityPlayer).unsetPersonalBackpackOpen();
        }
        if (inventory != null) {
            inventory.closeInventory();
        }

        if (backpackSave != null) {
            Backpack.proxy.invalidateBackpackCache(backpackSave.getUUID());
        }

        super.onContainerClosed(entityPlayer);
    }

    /**
     * Called when a player shift-clicks on a slot.
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer entityPlayer, int slotPos) {
        ItemStack returnStack = null;
        Slot slot = (Slot) inventorySlots.get(slotPos);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack;
            if (slot instanceof SlotCraftingAdvanced) {
                itemStack = ((SlotCraftingAdvanced) slot).getCraftingResult();
                if (itemStack == null) {
                    return null;
                }
            } else {
                itemStack = slot.getStack();
            }
            returnStack = itemStack.copy();

            if (boundaries.containsKey(Boundaries.EXTRA) && slotPos >= boundaries.get(Boundaries.EXTRA)
                    && slotPos < boundaries.get(Boundaries.EXTRA_END)) {
                if (!mergeItemStack(
                        itemStack,
                        boundaries.get(Boundaries.BACKPACK),
                        boundaries.get(Boundaries.BACKPACK_END),
                        false)) { // to backpack
                    if (!mergeItemStack(
                            itemStack,
                            boundaries.get(Boundaries.HOTBAR),
                            boundaries.get(Boundaries.HOTBAR_END),
                            true)) { // to hotbar
                        if (!mergeItemStack(
                                itemStack,
                                boundaries.get(Boundaries.INVENTORY),
                                boundaries.get(Boundaries.INVENTORY_END),
                                false)) { // to inventory
                            return null;
                        }
                    }
                }

                slot.onSlotChange(itemStack, returnStack);
            } else if (slotPos >= boundaries.get(Boundaries.BACKPACK)
                    && slotPos < boundaries.get(Boundaries.BACKPACK_END)) { // from backpack
                        if (!mergeItemStack(
                                itemStack,
                                boundaries.get(Boundaries.HOTBAR),
                                boundaries.get(Boundaries.HOTBAR_END),
                                true)) { // to hotbar
                            if (!mergeItemStack(
                                    itemStack,
                                    boundaries.get(Boundaries.INVENTORY),
                                    boundaries.get(Boundaries.INVENTORY_END),
                                    false)) { // to inventory
                                return null;
                            }
                        }
                    } else
                if (slotPos >= boundaries.get(Boundaries.INVENTORY)
                        && slotPos < boundaries.get(Boundaries.INVENTORY_END)) { // from inventory
                            if (!mergeItemStack(
                                    itemStack,
                                    boundaries.get(Boundaries.BACKPACK),
                                    boundaries.get(Boundaries.BACKPACK_END),
                                    false)) { // to backpack
                                if (!mergeItemStack(
                                        itemStack,
                                        boundaries.get(Boundaries.HOTBAR),
                                        boundaries.get(Boundaries.HOTBAR_END),
                                        true)) { // to hotbar
                                    return null;
                                }
                            }
                        } else
                    if (slotPos >= boundaries.get(Boundaries.HOTBAR)
                            && slotPos < boundaries.get(Boundaries.HOTBAR_END)) { // from hotbar
                                if (!mergeItemStack(
                                        itemStack,
                                        boundaries.get(Boundaries.BACKPACK),
                                        boundaries.get(Boundaries.BACKPACK_END),
                                        false)) { // to backpack
                                    if (!mergeItemStack(
                                            itemStack,
                                            boundaries.get(Boundaries.INVENTORY),
                                            boundaries.get(Boundaries.INVENTORY_END),
                                            false)) { // to inventory
                                        return null;
                                    }
                                }
                            } else {
                                return null;
                            }

            if (itemStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemStack.stackSize == returnStack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(entityPlayer, itemStack);
        }

        return returnStack;
    }

    /**
     * Copy of mergeItemStack from class Container, which will ignore disabled slots.
     */
    @Override
    protected boolean mergeItemStack(ItemStack sourceStack, int firstSlot, int lastSlot, boolean backwards) {
        boolean result = false;
        int currentSlotIndex = firstSlot;

        if (backwards) {
            currentSlotIndex = lastSlot - 1;
        }

        Slot slot;
        ItemStack slotStack;

        if (sourceStack.isStackable()) {
            while (sourceStack.stackSize > 0
                    && (!backwards && currentSlotIndex < lastSlot || backwards && currentSlotIndex >= firstSlot)) {
                slot = (Slot) inventorySlots.get(currentSlotIndex);
                if (
                /* !(slot instanceof SlotScrolling && ((SlotScrolling) slot).isDisabled()) && */ slot
                        .isItemValid(sourceStack)) {
                    slotStack = slot.getStack();

                    if (slotStack != null && slotStack.getItem() == sourceStack.getItem()
                            && (!sourceStack.getHasSubtypes()
                                    || sourceStack.getItemDamage() == slotStack.getItemDamage())
                            && ItemStack.areItemStackTagsEqual(sourceStack, slotStack)) {
                        int l = slotStack.stackSize + sourceStack.stackSize;

                        if (l <= sourceStack.getMaxStackSize()) {
                            sourceStack.stackSize = 0;
                            slotStack.stackSize = l;
                            slot.onSlotChanged();
                            result = true;
                        } else if (slotStack.stackSize < sourceStack.getMaxStackSize()) {
                            sourceStack.stackSize -= sourceStack.getMaxStackSize() - slotStack.stackSize;
                            slotStack.stackSize = sourceStack.getMaxStackSize();
                            slot.onSlotChanged();
                            result = true;
                        }
                    }
                }

                if (backwards) {
                    --currentSlotIndex;
                } else {
                    ++currentSlotIndex;
                }
            }
        }

        if (sourceStack.stackSize > 0) {
            if (backwards) {
                currentSlotIndex = lastSlot - 1;
            } else {
                currentSlotIndex = firstSlot;
            }

            while (!backwards && currentSlotIndex < lastSlot || backwards && currentSlotIndex >= firstSlot) {
                slot = (Slot) inventorySlots.get(currentSlotIndex);
                if (
                /* !(slot instanceof SlotScrolling && ((SlotScrolling) slot).isDisabled()) && */ slot
                        .isItemValid(sourceStack)) {
                    slotStack = slot.getStack();

                    if (slotStack == null) {
                        result = true;

                        slot.putStack(sourceStack.copy());
                        slot.onSlotChanged();

                        sourceStack.stackSize -= slot.getStack().stackSize;

                        if (sourceStack.stackSize == 0) {
                            break;
                        }
                    }
                }

                if (backwards) {
                    --currentSlotIndex;
                } else {
                    ++currentSlotIndex;
                }
            }
        }

        return result;
    }

    @Override
    public ItemStack slotClick(int slotIndex, int mouseButton, int modifier, EntityPlayer entityPlayer) {
        Slot slot = slotIndex < 0 || slotIndex >= inventorySlots.size() ? null : (Slot) inventorySlots.get(slotIndex);
        if (slot instanceof SlotPhantom) {
            slotPhantomClick(slot, mouseButton, modifier, entityPlayer.inventory.getItemStack());
            return null;
        }
        return super.slotClick(slotIndex, mouseButton, modifier, entityPlayer);
    }

    /**
     * Handles clicking on a phantom slot.
     *
     * @param slot        The slot that has been clicked.
     * @param mouseButton The mouse button identifier: 0: left click 1: right click & left click during drag and drop 2:
     *                    middle click (scrollwheel)
     * @param modifier    The mouse modifier: 0: normal click 3: drag and drop middle click 5: drag and drop left or
     *                    right click
     * @param stackHeld   The stack that the player holds on his mouse.
     */
    protected void slotPhantomClick(Slot slot, int mouseButton, int modifier, ItemStack stackHeld) {
        if (((SlotPhantom) slot).canChangeStack()) {
            if (mouseButton == 2) {
                slot.putStack(null);
            } else {
                ItemStack phantomStack = null;

                if (stackHeld != null) {
                    phantomStack = stackHeld.copy();
                    phantomStack.stackSize = 1;
                }

                slot.putStack(phantomStack);
            }
            slot.onSlotChanged();
        }
    }

    public void addSlot(Slot slot) {
        addSlotToContainer(slot);
    }

    public void addBoundary(Boundaries boundary) {
        boundaries.put(boundary, inventorySlots.size());
    }

    public int getBoundary(Boundaries boundary) {
        if (boundaries.containsKey(boundary)) {
            return boundaries.get(boundary);
        }
        return -1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int value) {
        width = value;
    }

    public void setHeight(int value) {
        height = value;
    }

    public IInventory getInventoryToSave() {
        return inventory;
    }

    @ContainerSectionCallback
    public Map<ContainerSection, List<Slot>> getContainerSections() {
        Map<ContainerSection, List<Slot>> slotRefs = new HashMap<>();

        if (boundaries.containsKey(Boundaries.CRAFTING)) {
            slotRefs.put(ContainerSection.CRAFTING_OUT, inventorySlots.subList(0, 1));
            slotRefs.put(
                    ContainerSection.CRAFTING_IN_PERSISTENT,
                    inventorySlots.subList(getBoundary(Boundaries.CRAFTING), getBoundary(Boundaries.CRAFTING_END)));
        }
        slotRefs.put(
                ContainerSection.INVENTORY,
                inventorySlots.subList(getBoundary(Boundaries.INVENTORY), getBoundary(Boundaries.HOTBAR_END)));
        slotRefs.put(
                ContainerSection.INVENTORY_NOT_HOTBAR,
                inventorySlots.subList(getBoundary(Boundaries.INVENTORY), getBoundary(Boundaries.INVENTORY_END)));
        slotRefs.put(
                ContainerSection.INVENTORY_HOTBAR,
                inventorySlots.subList(getBoundary(Boundaries.HOTBAR), getBoundary(Boundaries.HOTBAR_END)));
        slotRefs.put(
                ContainerSection.CHEST,
                inventorySlots.subList(getBoundary(Boundaries.BACKPACK), getBoundary(Boundaries.BACKPACK_END)));
        return slotRefs;
    }
}
