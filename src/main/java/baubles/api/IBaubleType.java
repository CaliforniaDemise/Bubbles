package baubles.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IBaubleType {

    /**
     * Registry name of the bauble type.
     **/
    @NotNull
    ResourceLocation getRegistryName();

    int getOrder();

    /**
     * Translation key of the bauble type. A bit more extensible version of "baubles.AMULET"
     **/
    @NotNull
    String getTranslationKey();

    /**
     * Background icon for slots.
     * Use {@link TextureStitchEvent.Pre} for registering textures.
     * See {@link Slot#getBackgroundSprite()} for more information.
     */
    @Nullable
    @SideOnly(Side.CLIENT)
    ResourceLocation getBackgroundTexture();

    /**
     * Use this if you want to add type to the list of {@link Enchantment#getEntityEquipment(EntityLivingBase)}.
     * You should use {@link Item#canApplyAtEnchantingTable(ItemStack, Enchantment)} for specific items.
     **/
    default boolean canApplyEnchantment(Enchantment enchantment, ItemStack stack) {
        if (enchantment.type == null) return false;
        EnumEnchantmentType type = enchantment.type;
        return type == EnumEnchantmentType.ALL || type == EnumEnchantmentType.WEARABLE;
    }
}