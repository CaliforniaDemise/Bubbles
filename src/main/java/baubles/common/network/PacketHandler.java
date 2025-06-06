package baubles.common.network;

import baubles.common.Baubles;
import baubles.core.transformers.CreativeInvTransformer.PacketCreativeClean;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Baubles.MODID.toLowerCase());

    public static void init() {
        INSTANCE.registerMessage(PacketOpenBaublesInventory.class, PacketOpenBaublesInventory.class, 0, Side.SERVER);
        INSTANCE.registerMessage(PacketOpenNormalInventory.class, PacketOpenNormalInventory.class, 1, Side.SERVER);
        INSTANCE.registerMessage(PacketChangeOffset.class, PacketChangeOffset.class, 2, Side.SERVER);
        INSTANCE.registerMessage(PacketSync.class, PacketSync.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(PacketCreativeClean.class, PacketCreativeClean.class, 4, Side.SERVER);
        INSTANCE.registerMessage(PacketSyncSlots.class, PacketSyncSlots.class, 5, Side.CLIENT);
    }
}
