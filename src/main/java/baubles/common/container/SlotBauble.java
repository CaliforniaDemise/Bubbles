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
        this.slotIndex = itemHandler instanceof BaublesContainer ? ((BaublesContainer) itemHandler).getSlotByOffset(slot) : slot;
    }

    @Deprecated // Try not to use it in your mod
    @Override
    public IItemHandler getItemHandler() {
        return super.getItemHandler();
    }

    @Override
    public boolean isItemValid(@NotNull ItemStack stack) {
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        return bauble != null && baublesHandler.isItemValidForSlot(this.slotIndex, stack, player);
    }

    @Override
    public boolean canTakeStack(@NotNull EntityPlayer player) {
        ItemStack stack = getStack();
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
        if (getHasStack() && !ItemStack.areItemStacksEqual(stack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null).onUnequipped(getStack(), player);
        }
        ItemStack oldstack = getStack().copy();
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(this.slotIndex, stack);
        this.onSlotChanged();
        if (getHasStack() && !ItemStack.areItemStacksEqual(oldstack, getStack()) &&
                getStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            Objects.requireNonNull(getStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)).onEquipped(getStack(), player);
        }
    }

    @Override
    public int getSlotStackLimit() {
        return this.baublesHandler.getSlotLimit(this.slotIndex);
    }

    @Override
    public int getItemStackLimit(@NotNull ItemStack stack) {
        return this.getSlotStackLimit();
    }

    @Nullable
    @Override
    public String getSlotTexture() {
        ResourceLocation bg = this.baublesHandler.getSlotType(this.slotIndex).getBackgroundTexture();
        return bg == null ? null : bg.toString();
    }

    @NotNull
    @Override
    public ItemStack getStack() {
        return this.baublesHandler.getStackInSlot(this.slotIndex);
    }

    @NotNull
    @Override
    public ItemStack decrStackSize(int amount) {
        return this.baublesHandler.extractItem(this.slotIndex, amount, false);
    }
}
