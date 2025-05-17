package baubles.api;

import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.event.EventHandlerItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A capability that defines an item that can be put into bauble slots.
 * If you want to define a bauble item you may implement this on items class or inject it with {@link AttachCapabilitiesEvent<Item>}
 * If you want to get IBauble from an ItemStack, use {@link BaublesApi#getBauble(ItemStack)} or get from {@link BaublesCapabilities#CAPABILITY_ITEM_BAUBLE} if getBauble is insufficient.
 * DO NOT CHECK IF ITEM IS INSTANCE OF THIS. It's only for convenience.
 * @see EventHandlerItem#itemCapabilityAttach(AttachCapabilitiesEvent)
 *
 * @author Azanor
 */
public interface IBauble {

    /**
     * This method return the type of bauble this is.
     * Type is used to determine the slots it can go into.
     */
    @NotNull
    default IBaubleType getType(@NotNull ItemStack itemStack) {
        return getBaubleType(itemStack);
    }

    /**
     * @deprecated
     * @see IBauble#getType(ItemStack)}
     */
    @Deprecated
    default BaubleType getBaubleType(@NotNull ItemStack itemstack) {
        return BaubleType.TRINKET;
    }

    /**
     * Check that happens before putting the item to an empty slot
     * @see IBauble#canEquip(ItemStack, EntityLivingBase) for entity based check.
     **/
    default boolean canPutOnSlot(IBaublesItemHandler handler, int slotIndex, IBaubleType slotType, ItemStack stack) {
        return this.getType(stack) == BaubleType.TRINKET || slotType == BaubleType.TRINKET || this.getType(stack) == slotType;
    }

    /**
     * This method is called once per tick if the bauble is being worn by a player
     */
    default void onWornTick(ItemStack itemstack, EntityLivingBase player) {
    }

    /**
     * This method is called when the bauble is equipped by a player
     */
    default void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    /**
     * This method is called when the bauble is unequipped by a player
     */
    default void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
    }

    /**
     * can this bauble be placed in a bauble slot.
     */
    default boolean canEquip(ItemStack itemstack, @Nullable EntityLivingBase entity) {
        return true;
    }

    /**
     * Can this bauble be removed from a bauble slot.
     * If item has Curse of Binding it will not be equippable.
     */
    default boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    /**
     * Will bauble automatically sync to client if a change is detected in its NBT or damage values?
     * Default is off, so override and set to true if you want to auto sync.
     * This sync is not instant, but occurs every 10 ticks (.5 seconds).
     */
    default boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return false;
    }

    /**
     * Runs when an entity that has this item dies.
     *
     * @param slotIndex The slot index item is in
     * @param stack     The stack in question
     * @param living    The entity that has died
     * @return Way item drop should be handled when entity dies.
     */
    default DeathResult onDeath(int slotIndex, ItemStack stack, EntityLivingBase living) {
        return DeathResult.DEFAULT;
    }

    /**
     * Enums to define how item dropping should be handled on entity death.
     */
    enum DeathResult {
        DEFAULT,
        ALWAYS_KEEP,
        ALWAYS_DROP,
        DESTROY,
        CUSTOM
    }
}
