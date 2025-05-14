package baubles.common.integration.groovyscript;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class BaublesEntityLivingBaseExpansion {

    public static IBaublesItemHandler getBaublesHandler(EntityLivingBase entity) {
        return BaublesApi.getBaublesHandler(entity);
    }

    public static ItemStack getBaublesItem(EntityLivingBase entity, int slotIndex) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
        if (handler != null) return handler.getStackInSlot(slotIndex);
        return ItemStack.EMPTY;
    }

    public static boolean setBaublesItem(EntityLivingBase entity, int slotIndex, ItemStack stack) {
        IBauble bauble = BaublesApi.getBauble(stack);
        if (bauble == null) return false;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
        if (handler != null) {
            if (bauble.canEquip(stack, entity) && bauble.canPutOnSlot(handler, slotIndex, handler.getSlotType(slotIndex), stack)) {
                handler.setStackInSlot(slotIndex, stack);
                return true;
            }
        }
        return false;
    }

    public static ItemStack insertBaublesItem(EntityLivingBase entity, ItemStack stack) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack s = handler.insertItem(i, stack, false);
                if (s != stack) return s;
            }
        }
        return stack;
    }
}
