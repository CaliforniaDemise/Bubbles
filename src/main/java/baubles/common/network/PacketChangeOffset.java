package baubles.common.network;

import baubles.api.cap.BaublesContainer;
import baubles.common.container.ContainerPlayerExpanded;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketChangeOffset implements IMessage {

    private int offsetChange;

    public PacketChangeOffset() {
    }

    public PacketChangeOffset(int offsetChange) {
        this.offsetChange = offsetChange;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        offsetChange = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(offsetChange);
    }

    public static class Handler implements IMessageHandler<PacketChangeOffset, IMessage> {
        @Override
        public IMessage onMessage(PacketChangeOffset message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                ContainerPlayerExpanded container = (ContainerPlayerExpanded) ctx.getServerHandler().player.openContainer;
                BaublesContainer baublesHandler = (BaublesContainer) container.baubles;
                baublesHandler.setOffset(baublesHandler.getSlotByOffset(message.offsetChange));
            });
            return null;
        }
    }
}
