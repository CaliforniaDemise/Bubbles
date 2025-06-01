package baubles.common.network;

import baubles.api.BaublesApi;
import baubles.api.cap.BaublesContainer;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.util.concurrent.ListenableFutureTask;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sync slots while loading the gui.
 **/
public class PacketSyncSlots implements IMessage, IMessageHandler<PacketSyncSlots, IMessage> {

    private NBTTagCompound compound;

    public PacketSyncSlots() {}
    public PacketSyncSlots(NBTTagCompound compound) { this.compound = compound; }

    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if (tag == null) return;
        this.compound = tag;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, this.compound);
    }

    @Override
    public IMessage onMessage(PacketSyncSlots message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(new SyncTask(message));
        return null;
    }

    @SideOnly(Side.CLIENT)
    private static class SyncTask implements Runnable {

        private final PacketSyncSlots message;

        public SyncTask(PacketSyncSlots message) {
            this.message = message;
        }

        @Override
        public void run() {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(Minecraft.getMinecraft().player);
            ((BaublesContainer) handler).deserializeNBT(message.compound);
        }
    }
}
