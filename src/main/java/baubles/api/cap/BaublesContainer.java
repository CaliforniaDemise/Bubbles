package baubles.api.cap;

import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.common.Config;
import baubles.common.init.BaubleTypes;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Callable;

// TODO Move offset to container? It's used nowhere except SlotBauble which is bound to Container
/**
 * Default implementation of {@link IBaublesItemHandler}
 **/
public class BaublesContainer implements PlayerBaubleHandler, INBTSerializable<NBTTagCompound> {

    private final SlotMap slots;

    private int offset = 0; // Can't be higher than getSlots()

    /**
     * Entity which has the baubles inventory
     **/
    private final EntityLivingBase player;

    /**
     * Items to drop when slots get updated
     **/
    private ItemStack[] itemsToPuke = null;

    /**
     * Only for internal use. Do not use it anywhere else.
     * Used for factory parameter of {@link CapabilityManager#register(Class, Capability.IStorage, Callable)}
     **/
    public BaublesContainer() {
        this(null);
    }

    public BaublesContainer(EntityLivingBase player) {
        this.slots = new SlotMap();
        this.slots.init(getDefaultSlotTypes());
        this.player = player;
    }

    @Override
    public EntityLivingBase getEntity() {
        return this.player;
    }

    @Override
    public IBaubleType getSlotType(int slotIndex) {
        return this.slots.getSlotType(slotIndex);
    }

    public int getOffset() {
        return this.offset;
    }

    @Override
    public int getSlotByOffset(int slotIndex) {
        if (slotIndex < 0) slotIndex += this.getSlots();
        return (this.offset + slotIndex) % this.getSlots();
    }

    @Override
    public void setOffset(int offset) {
        if (this.getSlots() < 9) return;
        this.offset = offset;
    }

    @Override
    public void resetOffset() {
        this.offset = 0;
    }

    @Override
    public void onContentsChanged(int slot) {
        if (!this.player.world.isRemote) {
            WorldServer world = (WorldServer) this.player.world;
            MinecraftServer server = world.getMinecraftServer();
            if (server != null) {
                Set<?> receivers = world.getEntityTracker().getTrackingPlayers(player);
                PacketSync sync = new PacketSync(player, slot, this.getStackInSlot(slot));
                for (Object o : receivers) {
                    EntityPlayerMP receiver = (EntityPlayerMP) o;
                    PacketHandler.INSTANCE.sendTo(sync, receiver);
                }
                if (this.player instanceof EntityPlayer) PacketHandler.INSTANCE.sendTo(sync, (EntityPlayerMP) player);
            }
        }
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack, EntityLivingBase entity) {
        if (stack == null || stack.isEmpty()) return false;
        IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
        if (bauble != null) return bauble.canEquip(stack, entity) && bauble.canPutOnSlot(this, slot, this.getSlotType(slot), stack);
        return false;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty() || this.isItemValidForSlot(slot, stack, this.player)) {
            this.setStack(slot, stack);
            this.onContentsChanged(slot);
        }
    }

    @Override
    public int getSlots() {
        return this.slots.getSlotAmount();
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        slot = this.validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;
        ItemStack stack = this.getStack(slot);
        if (stack == null) stack = ItemStack.EMPTY;
        return stack;
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!this.isItemValidForSlot(slot, stack, this.player)) return stack;
        if (stack.isEmpty()) return ItemStack.EMPTY;

        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;

        ItemStack existing = getStack(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing)) return stack;
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;
        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty())
                setStack(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }

            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        slot = validateSlotIndex(slot);
        if (slot == -1) return ItemStack.EMPTY;

        ItemStack existing = this.getStack(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setStack(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                this.setStack(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    public void pukeItems(World world, double x, double y, double z) {
        if (this.itemsToPuke != null) {
            for (ItemStack stack : this.itemsToPuke) {
                EntityItem eItem = new EntityItem(world, x, y, z, stack);
                world.spawnEntity(eItem);
            }
            this.itemsToPuke = null;
        }
    }

    public void pukeItems(Entity entity) {
        this.pukeItems(entity.world, entity.posX, entity.posY, entity.posZ);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList typeList = new NBTTagList();
        for (Pair<IBaubleType, ItemStack[]> pair : this.slots.list) {
            ItemStack[] items = pair.getValue();
            NBTTagCompound typeTag = new NBTTagCompound();
            NBTTagList itemsList = new NBTTagList();
            for (int a = 0; a < items.length; a++) {
                ItemStack stack = items[a];
                if (stack == null || stack.isEmpty()) continue;
                NBTTagCompound stackTag = new NBTTagCompound();
                stackTag.setInteger("Slot", a);
                stack.writeToNBT(stackTag);
                itemsList.appendTag(stackTag);
            }
            typeTag.setString("Name", pair.getKey().getRegistryName().toString());
            typeTag.setInteger("Count", items.length);
            typeTag.setTag("Items", itemsList);
            typeList.appendTag(typeTag);
        }
        compound.setTag("Types", typeList);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.slots.list.clear();
        this.slots.slotAmount = 0;
        if (nbt.hasKey("Items", Constants.NBT.TAG_COMPOUND)) this.deserializeNBTOld(nbt);
        else {
            NBTTagList typeList = nbt.getTagList("Types", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < typeList.tagCount(); i++) {
                NBTTagCompound typeTag = typeList.getCompoundTagAt(i);
                IBaubleType type = BaubleTypes.get(new ResourceLocation(typeTag.getString("Name")));
                int slotCount = typeTag.getInteger("Count");
                NBTTagList itemsList = typeTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
                ItemStack[] stacks = new ItemStack[slotCount];
                for (int a = 0; a < itemsList.tagCount(); a++) {
                    NBTTagCompound stackTag = itemsList.getCompoundTagAt(a);
                    int slot = stackTag.getInteger("Slot");
                    ItemStack stack = new ItemStack(stackTag);
                    stacks[slot] = stack;
                }
                this.slots.list.add(Pair.of(type, stacks));
                this.slots.slotAmount += slotCount;
                this.slots.list.sort(Comparator.comparingInt(p -> p.getKey().getOrder()));
            }
        }
    }

    private void deserializeNBTOld(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
//        List<ItemStack> itemsToPuke = new ArrayList<>();
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound stackTag = list.getCompoundTagAt(i);
            int slot = stackTag.getInteger("Slot");
            if (!stackTag.hasKey("id", 8)) continue;
            ItemStack stack = new ItemStack(stackTag);
            this.slots.putItem(slot, stack);
//            IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(stack));
//            if (slot < getSlots()) {
//                if (bauble.canPutOnSlot(this, slot, this.getSlotType(slot), stack)) this.slots.putItem(slot, stack);
//                else itemsToPuke.add(stack);
//            } else itemsToPuke.add(new ItemStack(stackTag));
        }
//        if (!itemsToPuke.isEmpty()) this.itemsToPuke = itemsToPuke.toArray(new ItemStack[0]);
    }

    @Deprecated @Override public boolean isChanged(int slot) { return false; }
    @Deprecated @Override public void setChanged(int slot, boolean change) {}

    private static IBaubleType[] getDefaultSlotTypes() {
        return Config.getSlotTypes();
    }

    /**
     * Use {@link BaublesContainer#getStackInSlot(int)}
     **/
    private ItemStack getStack(int slot) {
        if (slot == -1) return ItemStack.EMPTY;
        ItemStack stack = this.slots.getStack(slot);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    /**
     * Use {@link BaublesContainer#setStackInSlot(int, ItemStack)}
     **/
    private void setStack(int slot, ItemStack stack) {
        this.slots.putItem(slot, stack);
    }

    private int validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.slots.getSlotAmount()) {
            return -1;
        }
        return slot;
    }

    // TODO Handle item dropping when slot amount is decreased
    private static class SlotMap {

        private final List<Pair<IBaubleType, ItemStack[]>> list;

        private int slotAmount = 0;

        protected SlotMap() {
            this.list = new ArrayList<>();
        }

        private void init(IBaubleType[] types) {
            Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
            for (IBaubleType type : types) {
                if (type == null) continue;
                map.put(type, map.getInt(type) + 1);
            }
            map.object2IntEntrySet().forEach(entry -> {
                this.slotAmount += entry.getIntValue();
                ItemStack[] stacks = new ItemStack[entry.getIntValue()];
                Arrays.fill(stacks, ItemStack.EMPTY);
                this.list.add(Pair.of(entry.getKey(), stacks));
            });
            this.list.sort(Comparator.comparingInt(p -> p.getKey().getOrder()));
        }

        protected int getSlotAmount() {
            return this.slotAmount;
        }

        protected void setTypeAmount(IBaubleType type, int slotAmount) {
            Iterator<Pair<IBaubleType, ItemStack[]>> iterator = this.list.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Pair<IBaubleType, ItemStack[]> pair = iterator.next();
                if (pair.getKey() == type) {
                    if (pair.getValue().length == slotAmount) return;
                    ItemStack[] items = new ItemStack[slotAmount];
                    System.arraycopy(pair.getValue(), 0, items, 0, Math.min(slotAmount, pair.getValue().length));
                    this.list.set(i, Pair.of(type, items));
                    this.slotAmount += slotAmount - pair.getValue().length;
                    return;
                }
                i++;
            }
            this.slotAmount += slotAmount;
            ItemStack[] items = new ItemStack[slotAmount];
            Arrays.fill(items, ItemStack.EMPTY);
            this.list.add(Pair.of(type, items));
            this.list.sort(Comparator.comparingInt(p -> p.getKey().getOrder()));
        }

        protected void putItem(int slotIndex, ItemStack stack) {
            if (stack == null) stack = ItemStack.EMPTY;
            int slot = 0;
            for (Pair<IBaubleType, ItemStack[]> pair : this.list) {
                int newSlot = slot + pair.getValue().length;
                if (slotIndex < newSlot) {
                    int typeIndex = slotIndex - slot;
                    pair.getValue()[typeIndex] = stack;
                    return;
                } else slot = newSlot;
            }
        }

        protected ItemStack getStack(int slotIndex) {
            int slot = 0;
            for (Pair<IBaubleType, ItemStack[]> pair : this.list) {
                int newSlot = slot + pair.getValue().length;
                if (slotIndex < newSlot) {
                    int typeIndex = slotIndex - slot;
                    return pair.getValue()[typeIndex];
                } else slot = newSlot;
            }
            return ItemStack.EMPTY;
        }

        private IBaubleType getSlotType(int slotIndex) {
            int slot = 0;
            for (Pair<IBaubleType, ItemStack[]> pair : this.list) {
                slot += pair.getValue().length;
                if (slotIndex < slot) return pair.getKey();
            }
            throw new IndexOutOfBoundsException("Amount of slots are less than " + slotIndex);
        }
    }
}