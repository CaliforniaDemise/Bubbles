package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.inv.BaublesInventoryWrapper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author Azanor
 */
public class BaublesApi {

    /**
     * Easy method for reaching to grow, shrink and set slot amount methods.
     **/
    @NotNull
    public static BaublesContainer getBaublesContainer(EntityPlayer player) {
        return (BaublesContainer) getBaublesHandler(player);
    }

    /**
     * Retrieves the baubles item handler capability handler for the supplied player
     */
    @NotNull
    public static IBaublesItemHandler getBaublesHandler(EntityPlayer player) {
        return Objects.requireNonNull(getBaublesHandler((EntityLivingBase) player));
    }

    @Nullable
    public static IBaublesItemHandler getBaublesHandler(EntityLivingBase living) {
        if (living == null) return null;
        return living.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null);
    }

    /**
     * Retrieves the baubles capability handler wrapped as a IInventory for the supplied player
     */
    @Deprecated
    public static IInventory getBaubles(EntityPlayer player) {
        IBaublesItemHandler handler = Objects.requireNonNull(player.getCapability(BaublesCapabilities.CAPABILITY_BAUBLES, null));
        return new BaublesInventoryWrapper(handler, player);
    }

    @Nullable
    public static IBauble getBauble(ItemStack stack) {
        return stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
    }

    /**
     * Returns if the passed in item is equipped in a bauble slot. Will return the first slot found
     *
     * @return -1 if not found and slot number if it is found
     */
    public static int isBaubleEquipped(EntityPlayer player, Item bauble) {
        IBaublesItemHandler handler = getBaublesHandler(player);
        for (int a = 0; a < handler.getSlots(); a++) {
            if (!handler.getStackInSlot(a).isEmpty() && handler.getStackInSlot(a).getItem() == bauble) return a;
        }
        return -1;
    }
}
