package baubles.api;

import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface should be extended by items that can be worn in bauble slots
 *
 * @author Azanor
 */
public interface IBauble {

    /**
     * This method return the type of bauble this is.
     * Type is used to determine the slots it can go into.
     */
    @NotNull
    default IBaubleType getType(ItemStack itemStack) {
        return getBaubleType(itemStack);
    }

    /**
     * This method return the type of bauble this is.
     * Type is used to determine the slots it can go into.
     *
     * @deprecated prefer calling {@link IBauble#getType(ItemStack)} wherever possible
     */
    @Deprecated
    default BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.TRINKET;
    }

    default boolean canPutOnSlot(IBaublesItemHandler handler, int slotIndex, ItemStack stack) {
        return this.getType(stack) == BaubleType.TRINKET || handler.getSlotType(slotIndex) == BaubleType.TRINKET || this.getType(stack) == handler.getSlotType(slotIndex);
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

    default boolean shouldRender(ItemStack stack, EntityPlayer player) {
        return this instanceof IRenderBauble || stack.getItem() instanceof IRenderBauble;
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
    default DropResult onDeath(int slotIndex, ItemStack stack, EntityLivingBase living) {
        return DropResult.DEFAULT;
    }

    /**
     * Enums to define how item dropping should be handled on entity death.
     */
    enum DropResult {
        DEFAULT,
        ALWAYS_KEEP,
        ALWAYS_DROP,
        DESTROY,
        CUSTOM
    }
}
