package baubles.core;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class CoreUtility {

    public static int[] getSlotArray(Entity entity) {
        return getSlotArray((EntityPlayer) entity);
    }

    public static int[] getSlotArray(EntityPlayer player) {
        return getSlotArray(BaublesApi.getBaublesHandler(player));
    }

    public static int[] getSlotArray(IBaublesItemHandler handler) {
        return ((BaublesContainer) handler).getAllSlots();
    }

    public static int getSlot(Entity entity, Predicate<ItemStack> predicate) {
        return getSlot((EntityPlayer) entity, predicate);
    }

    public static int getSlot(EntityPlayer player, Predicate<ItemStack> predicate) {
        return getSlot(BaublesApi.getBaublesHandler(player), predicate);
    }

    public static int getSlot(IBaublesItemHandler handler, Predicate<ItemStack> predicate) {
        for (int i = 0; i < handler.getSlots(); ++i) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && predicate.test(stack)) return i;
        }
        return -1;
    }
}
