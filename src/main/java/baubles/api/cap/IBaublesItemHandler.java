package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.IBaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

/**
 * An item handler specific for baubles
 * It will always be applied to entity, because I can't find any other reason for it to be not to
 * It also has additional methods for syncing and blocking events
 **/
public interface IBaublesItemHandler extends IItemHandlerModifiable {

    /**
     * Entity that has the baubles inventory
     * Can be used for checking stuff
     **/
    EntityLivingBase getEntity();

    IBaubleType getSlotType(int slotIndex);

    /**
     * {@link IItemHandler#isItemValid(int, ItemStack)} but with entity.
     *
     * @param slotIndex index of slot to put
     * @param stack     stack to try to put. It doesn't need to be {@link IBauble}
     * @param entity    entity that has inventory for baubles. It doesn't need to be {@link EntityPlayer}
     * @return true if given stack can be put to the specific slot
     **/
    boolean isItemValidForSlot(int slotIndex, ItemStack stack, EntityLivingBase entity);

    void onContentsChanged(int slotIndex);

    @Deprecated default void setPlayer(EntityLivingBase entity) {}

    @Deprecated default boolean isChanged(int slot) { return false; }
    @Deprecated default void setChanged(int slot, boolean changed) {}

    @Deprecated default boolean isEventBlocked() { return false; }
    @Deprecated default void setEventBlock(boolean blockEvents) {}
}