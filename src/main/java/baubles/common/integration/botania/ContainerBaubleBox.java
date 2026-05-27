package baubles.common.integration.botania;

import baubles.api.IBauble;
import baubles.api.gui.ContainerBaubles;
import baubles.common.container.SlotBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import vazkii.botania.api.mana.IManaItem;
import vazkii.botania.client.gui.SlotLocked;

import java.util.Objects;

public class ContainerBaubleBox extends vazkii.botania.client.gui.box.ContainerBaubleBox implements ContainerBaubles {

    private int offset = 0;

    public ContainerBaubleBox(EntityPlayer player, vazkii.botania.client.gui.box.InventoryBaubleBox boxInv) {
        super(player, boxInv);
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();
        final IInventory playerInv = player.inventory;

        for (int i = 0; i < Math.min(8, Objects.requireNonNull(baubles).getSlots()); i++) {
            this.addSlotToContainer(new SlotBauble(player, baubles, i, -22, 6 + (i * 18)));
        }

        for(int i = 0; i < 4; ++i)
            for(int j = 0; j < 6; ++j) {
                int k = j + i * 6;
                addSlotToContainer(new SlotItemHandler(boxInv, k, 62 + j * 18, 8 + i * 18));
            }

        for(int i = 0; i < 3; ++i)
            for(int j = 0; j < 9; ++j)
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        for(int i = 0; i < 9; ++i) {
            if(playerInv.getStackInSlot(i) == InventoryBaubleBoxAccess.get(boxInv).getBox())
                addSlotToContainer(new SlotLocked(playerInv, i, 8 + i * 18, 142));
            else addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slotIndex);

        if(slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            Item item = itemstack1.getItem();

            int boxStart = Math.min(8, baubles.getSlots());
            int boxEnd = boxStart + 24;
            int invEnd = boxEnd + 36;

            if(slotIndex < boxEnd) {
                if(!mergeItemStack(itemstack1, boxEnd, invEnd, true))
                    return ItemStack.EMPTY;
            } else {
                if(!itemstack1.isEmpty()
                        && (item instanceof IBauble || item instanceof IManaItem || RODS.contains(item.getRegistryName()))
                        && !mergeItemStack(itemstack1, boxStart, boxEnd, false))
                    return ItemStack.EMPTY;
            }

            if(itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if(itemstack1.getCount() == itemstack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void setOffset(int offset) {
        if (this.baubles.getSlots() < 9) return;
        this.offset = offset;
    }

    @Override
    public int getSlotByOffset(int slotIndex) {
        if (slotIndex < 0) slotIndex += this.baubles.getSlots();
        return (this.offset + slotIndex) % this.baubles.getSlots();
    }

    @Override
    public void resetOffset() {
        this.offset = 0;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.resetOffset();
    }
}
