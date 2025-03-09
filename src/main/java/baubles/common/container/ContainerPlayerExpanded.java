package baubles.common.container;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerPlayerExpanded extends Container {

    private static final EntityEquipmentSlot[] equipmentSlots = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    public final InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public final InventoryCraftResult craftResult = new InventoryCraftResult();
    private final EntityPlayer thePlayer;
    public BaublesContainer baubles;

    public ContainerPlayerExpanded(InventoryPlayer playerInv, EntityPlayer player) {
        this.thePlayer = player;
        baubles = (BaublesContainer) BaublesApi.getBaublesHandler(player);
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 154, 28));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }

        for (int k = 0; k < 4; k++) {
            final EntityEquipmentSlot slot = equipmentSlots[k];
            this.addSlotToContainer(new Slot(playerInv, 36 + (3 - k), 8, 8 + k * 18) {
                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public boolean isItemValid(@NotNull ItemStack stack) {
                    return stack.getItem().isValidArmor(stack, slot, player);
                }

                @Override
                public boolean canTakeStack(@NotNull EntityPlayer playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @Override
                public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[slot.getIndex()];
                }
            });
        }

        for (int i = 0; i < Math.min(8, Objects.requireNonNull(baubles).getSlots()); i++) {
            this.addSlotToContainer(new SlotBauble(player, baubles, i, -22, 6 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInv, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }

        this.addSlotToContainer(new Slot(playerInv, 40, 77, 62) {
            @Override
            public boolean isItemValid(@NotNull ItemStack stack) {
                return super.isItemValid(stack);
            }

            @Override
            public String getSlotTexture() {
                return "minecraft:items/empty_armor_slot_shield";
            }
        });

        this.onCraftMatrixChanged(this.craftMatrix);
    }

    @Override
    public void onCraftMatrixChanged(@NotNull IInventory par1IInventory) {
        this.slotChangedCraftingGrid(this.thePlayer.getEntityWorld(), this.thePlayer, this.craftMatrix, this.craftResult);
    }

    @Override
    public void onContainerClosed(@NotNull EntityPlayer player) {
        super.onContainerClosed(player);
        this.baubles.resetOffset();
        this.craftResult.clear();
        if (!player.world.isRemote) {
            this.clearContainer(player, player.world, this.craftMatrix);
        }
    }

    @Override
    public boolean canInteractWith(@NotNull EntityPlayer par1EntityPlayer) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack transferStackInSlot(@NotNull EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
            int slotShift = Math.min(8, baubles.getSlots());
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= 1 && index < 5) {
                if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 5 && index < 9) {
                if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 9 && index < 9 + slotShift) { // baubles -> inv
                if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get(8 - entityequipmentslot.getIndex()).getHasStack()) { // inv -> armor
                int i = 8 - entityequipmentslot.getIndex();
                if (!this.mergeItemStack(itemstack1, i, i + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // inv -> offhand
            else if (entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get(45 + slotShift).getHasStack()) {
                if (!this.mergeItemStack(itemstack1, 45 + slotShift, 46 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // inv -> bauble
            else if (itemstack1.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null) && !(slot instanceof SlotBauble)) {
                IBauble bauble = Objects.requireNonNull(itemstack1.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null));
                if (playerIn.isCreative() || bauble.canEquip(itemstack1, playerIn)) {
                    BaublesContainer container = baubles;
                    boolean check = true;
                    for (int i = 0; i < this.baubles.getSlots(); i++) {
                        if (container.isItemValidForSlot(i, itemstack1, playerIn)) {
                            if (!mergeBauble(itemstack1, i)) check = false;
                            else {
                                bauble.onEquipped(baubles.getStackInSlot(i), playerIn);
                                break;
                            }
                        }
                    }
                    if (!check) return ItemStack.EMPTY;
                }
            } else if (index >= 9 + slotShift && index < 36 + slotShift) {
                if (!this.mergeItemStack(itemstack1, 36 + slotShift, 45 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 36 + slotShift && index < 45 + slotShift) {
                if (!this.mergeItemStack(itemstack1, 9 + slotShift, 36 + slotShift, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 9 + slotShift, 45 + slotShift, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.putStack(itemstack1);
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty() && slot instanceof SlotBauble) {
                IBauble cap = itemstack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                if (cap != null) cap.onUnequipped(itemstack, playerIn);
            }
            ItemStack itemstack2 = slot.onTake(playerIn, itemstack1);
            if (index == 0) {
                playerIn.dropItem(itemstack2, false);
            }
        }

        return itemstack;
    }

    @Override
    public boolean canMergeSlot(@NotNull ItemStack stack, Slot slot) {
        return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
    }

    private boolean mergeBauble(ItemStack stack, int slotIndex) {
        BaublesContainer container = baubles;
        boolean flag = false;
        if (!stack.isEmpty()) {
            ItemStack itemstack = container.getStackInSlot(slotIndex);
            IBauble bauble = BaublesApi.getBauble(stack);
            if (bauble == null) return false;
            if (stack.isStackable() && !itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ItemStack.areItemStackTagsEqual(stack, itemstack)) {
                int j = itemstack.getCount() + stack.getCount();
                int maxSize = Math.min(container.getSlotLimit(slotIndex), stack.getMaxStackSize());
                if (j <= maxSize) {
                    stack.setCount(0);
                    itemstack.setCount(j);
                    flag = true;
                } else if (itemstack.getCount() < maxSize) {
                    stack.shrink(maxSize - itemstack.getCount());
                    itemstack.setCount(maxSize);
                    flag = true;
                }
                container.setOffset(slotIndex);
            }
            else if (itemstack.isEmpty() && bauble.canPutOnSlot(container, slotIndex, stack)) {
                if (stack.getCount() > container.getSlotLimit(slotIndex))
                    container.setStackInSlot(slotIndex, stack.splitStack(container.getSlotLimit(slotIndex)));
                else container.setStackInSlot(slotIndex, stack.splitStack(stack.getCount()));
                container.setOffset(slotIndex);
                flag = true;
            }
        }
        return flag;
    }
}
