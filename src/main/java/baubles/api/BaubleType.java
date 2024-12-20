package baubles.api;

import baubles.common.Baubles;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Default bauble types
 **/
public enum BaubleType implements IBaubleType {

    AMULET("amulet"),
    RING("ring"),
    BELT("belt"),
    TRINKET("trinket"),
    HEAD("head"),
    BODY("body"),
    CHARM("charm");

    private static final Map<ResourceLocation, IBaubleType> TYPES = new HashMap<>();

    final ResourceLocation name;
    final String translationKey, backgroundTexture;
    final IntList validSlots = new IntArrayList(1);

    BaubleType(String name, int... validSlots) {
        this.name = new ResourceLocation(Baubles.MODID, name);
        this.translationKey = "baubles.type." + name;
        this.backgroundTexture = "baubles:gui/slots/" + name;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        return translationKey;
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

    public void addSlot(int slot) {
        validSlots.add(slot);
    }

    // Bauble Type Map TODO this sucks
    public static Map<ResourceLocation, IBaubleType> getTypes() {
        return TYPES;
    }

    public static IBaubleType register(IBaubleType type) {
        TYPES.put(type.getRegistryName(), type);
        return type;
    }

    @Nullable
    public static IBaubleType getType(ResourceLocation location) {
        return TYPES.get(location);
    }

    public static IBaubleType getOrCreateType(ResourceLocation location) {
        IBaubleType baubleType = TYPES.get(location);
        if (baubleType == null) baubleType = putType(location);
        return baubleType;
    }

    private static IBaubleType putType(ResourceLocation location) {
        IBaubleType type = new BaubleTypeImpl(location);
        TYPES.put(location, type);
        return type;
    }

    static {
        for (BaubleType type : BaubleType.values()) {
            TYPES.put(type.name, type);
        }
    }

    // Deprecated
    @Deprecated
    public boolean hasSlot(int slot) {
        switch (slot) {
            default: return false;
            case 0: return this == AMULET || this == TRINKET;
            case 1: case 2: return this == RING || this == TRINKET;
            case 3: return this == BELT || this == TRINKET;
            case 4: return this == HEAD || this == TRINKET;
            case 5: return this == BODY || this == TRINKET;
            case 6: return this == CHARM || this == TRINKET;
        }
    }

    @Deprecated
    public int[] getValidSlots() {
        int[] array;
        if (this == RING) {
            array = new int[2];
            array[1] = -1;
        }
        else array = new int[1];
        array[0] = -1;
        return validSlots.toArray(array);
    }
}
