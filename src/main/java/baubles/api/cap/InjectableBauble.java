package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.common.integration.ModCompatibility;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of {@link IBauble} to inject to items that normally aren't baubles.
 * It's encouraged to implement your own {@link IBauble} classes.
 * Used in {@link ModCompatibility#getBaubleToInject(ItemStack)}
 **/
public class InjectableBauble implements IBauble {

    private final IBaubleType type;

    private final int value;

    public static final int INVENTORY = 1;
    public static final int ARMOR = 2;
    public static final int PASSIVE = 4;

    public InjectableBauble(IBaubleType type, int value) {
        this.type = type;
        this.value = value;
    }

    @NotNull
    @Override
    public IBaubleType getType(@NotNull ItemStack itemStack) {
        return this.type;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        Item item = itemstack.getItem();
        if ((this.value & INVENTORY) != 0) item.onUpdate(itemstack, player.world, player, 0, (this.value & PASSIVE) != 0);
        if ((this.value & ARMOR) != 0 && player instanceof EntityPlayer)
            itemstack.getItem().onArmorTick(player.world, (EntityPlayer) player, itemstack);
    }
}
