package crafttweaker.api.minecraft;

import crafttweaker.api.entity.IEntityLivingBase;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CrafttweakerMC {
    public static ItemStack getItemStack(IItemStack stack) { return ItemStack.EMPTY; }
    public static IItemStack getIItemStack(ItemStack item) { return null; }
    public static EntityLivingBase getEntityLivingBase(IEntityLivingBase entityLivingBase) { return null; }
    public static EntityPlayer getPlayer(IPlayer player) { return null; }
}
