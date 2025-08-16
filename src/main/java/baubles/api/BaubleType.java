package baubles.api;

import baubles.common.Baubles;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Default bauble types
 **/
public enum BaubleType implements IBaubleType, Comparable<BaubleType> {

    AMULET("amulet"),
    RING("ring"),
    BELT("belt"),
    TRINKET("trinket"),
    HEAD("head"),
    BODY("body"),
    CHARM("charm");

    final ResourceLocation name;
    final String translationKey;
    final ResourceLocation backgroundTexture;

    BaubleType(String name, int... validSlots) {
        this.name = new ResourceLocation(Baubles.MODID, name);
        this.translationKey = "baubles.type." + name;
        this.backgroundTexture = new ResourceLocation(Baubles.MODID, "gui/slots/" + name);
    }

    @NotNull
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Override
    public int getOrder() {
        return 100 + (this.ordinal() * 100);
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return this.backgroundTexture;
    }

    @Override
    public boolean canApplyEnchantment(Enchantment enchantment, ItemStack stack) {
        if (enchantment.type == null) return false;
        if (IBaubleType.super.canApplyEnchantment(enchantment, stack)) return true;
        switch (enchantment.type) {
            case ARMOR_HEAD: if (this == HEAD) return true;
            case ARMOR_CHEST: if (this == AMULET || this == BODY) return true;
            case ARMOR_LEGS: if (this == BELT) return true;
            case BREAKABLE: return stack.isItemStackDamageable();
        }
        return false;
    }

    // Deprecated
    @Deprecated
    public boolean hasSlot(int slot) {
        switch (slot) {
            case 0: return this == AMULET || this == TRINKET;
            case 1:
            case 2: return this == RING || this == TRINKET;
            case 3: return this == BELT || this == TRINKET;
            case 4: return this == HEAD || this == TRINKET;
            case 5: return this == BODY || this == TRINKET;
            case 6: return this == CHARM || this == TRINKET;
            default: return false;
        }
    }

    @Deprecated
    public int[] getValidSlots() {
        return new int[] { -1, -1 };
    }
}
