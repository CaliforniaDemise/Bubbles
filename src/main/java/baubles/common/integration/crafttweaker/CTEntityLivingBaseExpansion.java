package baubles.common.integration.crafttweaker;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CrafttweakerMC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@SuppressWarnings("unused")
@ZenExpansion("crafttweaker.api.entity.IEntityLivingBase")
@ZenRegister
public class CTEntityLivingBaseExpansion {

    @ZenMethod
    public static IItemStack getBaublesItem(IEntityLivingBase entity, int slotIndex) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(CrafttweakerMC.getEntityLivingBase(entity));
        if (handler != null) return CrafttweakerMC.getIItemStack(handler.getStackInSlot(slotIndex));
        return null;
    }

    @ZenMethod
    public static boolean setBaublesItem(IEntityLivingBase entity, int slotIndex, IItemStack stack) {
        ItemStack itemStack = CrafttweakerMC.getItemStack(stack);
        EntityLivingBase livingBase = CrafttweakerMC.getEntityLivingBase(entity);
        IBauble bauble = BaublesApi.getBauble(itemStack);
        if (bauble == null) return false;
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(livingBase);
        if (handler != null) {
            if (bauble.canEquip(itemStack, livingBase) && bauble.canPutOnSlot(handler, slotIndex, handler.getSlotType(slotIndex), itemStack)) {
                handler.setStackInSlot(slotIndex, itemStack);
                return true;
            }
        }
        return false;
    }

    @ZenMethod
    public static IItemStack insertBaublesItem(IEntityLivingBase entity, IItemStack stack) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(CrafttweakerMC.getEntityLivingBase(entity));
        ItemStack itemStack = CrafttweakerMC.getItemStack(stack);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack s = handler.insertItem(i, itemStack, false);
                if (s != itemStack) return CrafttweakerMC.getIItemStack(s);
            }
        }
        return stack;
    }
}
