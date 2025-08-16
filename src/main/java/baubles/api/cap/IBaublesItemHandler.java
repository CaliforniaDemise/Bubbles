package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.IBaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    /**
     * Gets the bauble type of the slot based on slots index.
     **/
    IBaubleType getSlotType(int slotIndex);

    @NotNull
    List<ItemStack> getTypeStacks(IBaubleType type);

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

    // TODO Remove it once sure.
    @Deprecated boolean isChanged(int slot);
    @Deprecated void setChanged(int slot, boolean changed);
}