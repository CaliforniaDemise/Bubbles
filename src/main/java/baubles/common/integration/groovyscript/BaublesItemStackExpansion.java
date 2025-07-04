package baubles.common.integration.groovyscript;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BaublesItemStackExpansion {

    @Nullable
    public static IBauble getBauble(ItemStack stack) {
        return BaublesApi.getBauble(stack);
    }

    @Nullable
    public static IBaubleType getBaubleType(ItemStack stack) {
        IBauble bauble = getBauble(stack);
        if (bauble != null) return bauble.getType(stack);
        return null;
    }
}
