package baubles.common.integration.crafttweaker;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CrafttweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@SuppressWarnings("unused")
@ZenExpansion("crafttweaker.item.IItemStack")
@ZenRegister
public class CTItemStackExpansion {

    @ZenMethod
    public static String getBaubleType(IItemStack item) {
        ItemStack stack = CrafttweakerMC.getItemStack(item);
        IBauble bauble = BaublesApi.getBauble(stack);
        if (bauble != null) return bauble.getType(stack).getRegistryName().toString();
        return null;
    }
}
