package baubles.common.container;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SlotBauble extends SlotItemHandler {

    private final int slotIndex;

    private final IBaublesItemHandler baublesHandler;
    private final EntityPlayer player;

    public SlotBauble(EntityPlayer player, IBaublesItemHandler itemHandler, int slot, int par4, int par5) {
        super(itemHandler, slot, par4, par5);
        this.baublesHandler = itemHandler;
        this.player = player;
        this.slotIndex = slot;
    }

    public int getRealSlotIndex() {
        if (this.baublesHandler instanceof BaublesContainer) return ((BaublesContainer) this.baublesHandler).getSlotByOffset(this.slotIndex);
        return this.slotIndex;
    }

    @Deprecated // Try not to use it in your mod
    @Override
    public IItemHandler getItemHandler() {
        return super.getItemHandler();
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble != null && baublesHandler.isItemValidForSlot(this.getRealSlotIndex(), stack, player);
    }

    @Override
    public boolean canTakeStack(@NotNull EntityPlayer player) {
        ItemStack stack = this.getStack();
        if (stack.isEmpty()) return false;
        int binding = EnchantmentHelper.getEnchantmentLevel(Enchantments.BINDING_CURSE, stack);
        if (!player.isCreative() && binding > 0) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble == null || bauble.canUnequip(stack, player);
    }

    @NotNull
    @Override
    public ItemStack onTake(@NotNull EntityPlayer playerIn, @NotNull ItemStack stack) {
        if (!stack.isEmpty()) {
            IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            if (bauble != null) bauble.onUnequipped(stack, playerIn);
        }
        return super.onTake(playerIn, stack);
    }

    @Override
    public void putStack(@NotNull ItemStack stack) {
        if (this.getHasStack() && !ItemStack.areItemStacksEqual(stack, this.getStack()) &&
                this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(this.getStack(), player);
        }
        ItemStack oldstack = this.getStack().copy();
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(this.getRealSlotIndex(), stack);
        this.onSlotChanged();
        if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack, this.getStack()) &&
                this.getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            Objects.requireNonNull(this.getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)).onEquipped(this.getStack(), player);
        }
    }

    @Override
    public int getSlotStackLimit() {
        return this.baublesHandler.getSlotLimit(this.getRealSlotIndex());
    }

    @Override
    public int getItemStackLimit(@NotNull ItemStack stack) {
        return this.getSlotStackLimit();
    }

    @Nullable
    @Override
    public String getSlotTexture() {
        ResourceLocation bg = this.baublesHandler.getSlotType(this.getRealSlotIndex()).getBackgroundTexture();
        return bg == null ? null : bg.toString();
    }

    @NotNull
    @Override
    public ItemStack getStack() {
        return this.baublesHandler.getStackInSlot(this.getRealSlotIndex());
    }

    @NotNull
    @Override
    public ItemStack decrStackSize(int amount) {
        return this.baublesHandler.extractItem(this.getRealSlotIndex(), amount, false);
    }
}
