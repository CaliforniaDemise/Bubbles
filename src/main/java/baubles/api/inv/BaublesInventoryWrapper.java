package baubles.api.inv;

import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.jetbrains.annotations.NotNull;

// TODO Remove it once sure.
public class BaublesInventoryWrapper implements IInventory {
    final IBaublesItemHandler handler;
    final EntityPlayer player;

    public BaublesInventoryWrapper(IBaublesItemHandler handler) {
        super();
        this.handler = handler;
        this.player = null;
    }

    public BaublesInventoryWrapper(IBaublesItemHandler handler, EntityPlayer player) {
        super();
        this.handler = handler;
        this.player = player;
    }

   @NotNull
    @Override
    public String getName() {
        return "BaublesInventory";
    }

   @NotNull
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentString(this.getName());
    }

    @Override
    public int getSizeInventory() {
        return handler.getSlots();
    }

   @NotNull
    @Override
    public ItemStack getStackInSlot(int index) {
        return handler.getStackInSlot(index);
    }

   @NotNull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return handler.extractItem(index, count, false);
    }

   @NotNull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack out = this.getStackInSlot(index);
        handler.setStackInSlot(index, ItemStack.EMPTY);
        return out;
    }

    @Override
    public void setInventorySlotContents(int index, @NotNull ItemStack stack) {
        handler.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(@NotNull EntityPlayer player) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int index, @NotNull ItemStack stack) {
        return handler.isItemValidForSlot(index, stack, player);
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public void markDirty() {
    }

    @Override
    public void openInventory(@NotNull EntityPlayer player) {
    }

    @Override
    public void closeInventory(@NotNull EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }
}
