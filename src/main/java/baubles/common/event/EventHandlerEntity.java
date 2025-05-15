package baubles.common.event;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.BaublesContainerProvider;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Baubles;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketSync;
import cofh.core.enchantment.EnchantmentSoulbound;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

@SuppressWarnings("unused") // gets used by Forge event handler
public class EventHandlerEntity {

    @SubscribeEvent
    public void cloneCapabilitiesEvent(PlayerEvent.Clone event) {
        try {
            BaublesContainer bco = (BaublesContainer) BaublesApi.getBaublesHandler(event.getOriginal());
            NBTTagCompound nbt = bco.serializeNBT();
            BaublesContainer bcn = (BaublesContainer) BaublesApi.getBaublesHandler(event.getEntityPlayer());
            bcn.deserializeNBT(nbt);
        } catch (Exception e) {
            Baubles.log.error("Could not clone player [" + event.getOriginal().getName() + "] baubles when changing dimensions");
        }
    }

    // TODO Support for entities other than player.
    @SubscribeEvent
    public void attachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(Baubles.MODID, "container"),
                    new BaublesContainerProvider(new BaublesContainer((EntityLivingBase) event.getObject())));
        }
    }

    @SubscribeEvent
    public void playerJoin(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            this.syncSlots(player, Collections.singletonList(player));
            BaublesContainer container = (BaublesContainer) BaublesApi.getBaublesHandler(player);
        }
    }

    @SubscribeEvent
    public void onStartTracking(PlayerEvent.StartTracking event) {
        Entity target = event.getTarget();
        if (target instanceof EntityPlayerMP) {
            this.syncSlots((EntityPlayer) target, Collections.singletonList(event.getEntityPlayer()));
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            BaublesContainer container = (BaublesContainer) baubles;
            container.update(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(stack));
                bauble.onWornTick(stack, player);
                if (event.side == Side.SERVER && event.player.world.getWorldTime() % 10 == 0 && bauble.willAutoSync(stack, player)) {
                    WorldServer world = (WorldServer) event.player.world;
                    MinecraftServer server = world.getMinecraftServer();
                    if (server != null) {
                        Set<?> receivers = world.getEntityTracker().getTrackingPlayers(player);
                        PacketSync sync = new PacketSync(player, i, stack);
                        for (Object o : receivers) {
                            EntityPlayerMP receiver = (EntityPlayerMP) o;
                            PacketHandler.INSTANCE.sendTo(sync, receiver);
                        }
                    }
                }
            }
        }
    }

    private void syncSlots(EntityPlayer player, Collection<? extends EntityPlayer> receivers) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); i++) {
            syncSlot(player, i, baubles.getStackInSlot(i), receivers);
        }
    }

    private void syncSlot(EntityPlayer player, int slot, ItemStack stack, Collection<? extends EntityPlayer> receivers) {
        PacketSync pkt = new PacketSync(player, slot, stack);
        for (EntityPlayer receiver : receivers) {
            PacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP) receiver);
        }
    }

    @SubscribeEvent
    public void playerDeath(PlayerDropsEvent event) {
        if (event.getEntity() instanceof EntityPlayer
                && !event.getEntity().world.isRemote
                && !event.getEntity().world.getGameRules().getBoolean("keepInventory")) {
            dropItemsAt(event.getEntityPlayer(), event.getDrops(), event.getEntityPlayer());
        }
    }

    @GameRegistry.ObjectHolder("cofhcore:soulbound") public static Enchantment COFH_SOULBOUND = null;
    @GameRegistry.ObjectHolder("tombstone:soulbound") public static Enchantment TOMBSTONE_SOULBOUND = null;

    public void dropItemsAt(EntityPlayer player, List<EntityItem> drops, Entity e) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        player.captureDrops = true;
        for (int i = 0; i < baubles.getSlots(); ++i) {
            if (!baubles.getStackInSlot(i).isEmpty()) {
                ItemStack stack = baubles.getStackInSlot(i);
                IBauble bauble = Objects.requireNonNull(stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null));
                IBauble.DeathResult result = bauble.onDeath(i, stack, player);
                switch (result) {
                    case CUSTOM:
                    case ALWAYS_KEEP:
                        break;
                    case ALWAYS_DROP: {
                        baubles.setStackInSlot(i, ItemStack.EMPTY);
                        player.dropItem(stack, true, false);
                        break;
                    }
                    case DESTROY:
                        baubles.setStackInSlot(i, ItemStack.EMPTY);
                        break;
                    case DEFAULT: {
                        boolean vanishingCheck = EnchantmentHelper.hasVanishingCurse(stack);
                        if (!this.isFakePlayer(player)) {
                            if (EnchantmentHelper.getEnchantmentLevel(TOMBSTONE_SOULBOUND, stack) != 0) break;
                            if (this.handleCofhSouldbound(stack)) break;
                            if (!EnchantmentHelper.hasVanishingCurse(stack)) player.dropItem(stack, true, false);
                            baubles.setStackInSlot(i, ItemStack.EMPTY);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        player.captureDrops = false;
    }

    private boolean hasAnySoulbound(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(COFH_SOULBOUND, stack) > 0 || EnchantmentHelper.getEnchantmentLevel(TOMBSTONE_SOULBOUND, stack) > 0;
    }

    private boolean handleCofhSouldbound(ItemStack stack) {
        if (COFH_SOULBOUND != null) {
            int level = EnchantmentHelper.getEnchantmentLevel(COFH_SOULBOUND, stack);
            if (level > 1) {
                if (EnchantmentSoulbound.permanent) {
                    ItemHelper.removeEnchantment(stack, COFH_SOULBOUND);
                    ItemHelper.addEnchantment(stack, COFH_SOULBOUND, level - 1);
                } else if (MathHelper.RANDOM.nextInt(level + 1) == 0) {
                    ItemHelper.removeEnchantment(stack, COFH_SOULBOUND);
                    ItemHelper.addEnchantment(stack, COFH_SOULBOUND, level - 1);
                }
                return true;
            }
        }
        return false;
    }

    private boolean isFakePlayer(EntityPlayer player) {
        return player instanceof FakePlayer;
    }
}
