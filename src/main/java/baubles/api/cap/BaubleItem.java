package baubles.api.cap;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A basic example for a bauble item.
 * You're free to use this as a template.
 * Never assume Item is IBauble. Get IBauble from {@link BaublesCapabilities#CAPABILITY_ITEM_BAUBLE} or use {@link BaublesApi#getBauble(ItemStack)}.
 **/
public class BaubleItem extends Item implements IBauble {
    private final BaubleType baubleType;

    public BaubleItem(BaubleType type) {
        baubleType = type;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BaubleType getBaubleType(@NotNull ItemStack itemstack) {
        return baubleType;
    }

    @NotNull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        boolean check = false;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(playerIn);
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.isItemValidForSlot(i, stack, playerIn)) {
                check = true;
                if (!worldIn.isRemote) {
                    handler.setStackInSlot(i, stack.copy());
                    stack.shrink(1);
                }
                break;
            }
        }
        this.onEquipped(stack, playerIn);
        if (check) return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        else return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
