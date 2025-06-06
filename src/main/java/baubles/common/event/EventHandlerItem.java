package baubles.common.event;

import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.InjectableBauble;
import baubles.common.Baubles;
import baubles.common.integration.ModCompatibility;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("unused") // gets used by Forge event handler
public class EventHandlerItem {

    private static final ResourceLocation capabilityResourceLocation = new ResourceLocation(Baubles.MODID, "bauble_cap");

    /**
     * Handles backwards compatibility with items that implement IBauble instead of exposing it as a capability.
     * This adds a IBauble capability wrapper for all items, if the item:
     * - does implement the IBauble interface
     * - does not already have the capability
     * - did not get the capability by another event handler earlier in the chain
     */
    @SuppressWarnings("all")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void itemCapabilityAttach(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.isEmpty()) return;
        InjectableBauble bauble = ModCompatibility.getBaubleToInject(stack);
        if (bauble != null) {
            event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {
                @Override
                public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                    return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
                }

                @Override
                public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                    return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast(bauble) : null;
                }
            });
            return;
        }

        if (!(stack.getItem() instanceof IBauble) || stack.hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) return;
        event.addCapability(capabilityResourceLocation, new ICapabilityProvider() {

            @Override
            public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE;
            }

            @Override
            public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
                return capability == BaublesCapabilities.CAPABILITY_ITEM_BAUBLE ? BaublesCapabilities.CAPABILITY_ITEM_BAUBLE.cast((IBauble) stack.getItem()) : null;
            }
        });
    }

    @SubscribeEvent
    public void remapEntries(RegistryEvent.MissingMappings<Item> event) {
        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getAllMappings()) {
            ResourceLocation key = mapping.key;
            if (key.getNamespace().equals(ModCompatibility.ACTUALLY_ADDITIONS) && key.getPath().endsWith("_bauble")) {
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(key.getNamespace(), key.getPath().substring(0, key.getPath().length() - 7)));
                if (item != null && item != Items.AIR) {
                    mapping.remap(item);
                }
            }
        }
    }
}
