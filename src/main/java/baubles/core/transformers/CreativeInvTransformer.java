package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class CreativeInvTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/CreativeInvTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.gui.inventory.GuiContainerCreative")) return transformGuiContainerCreative(basicClass);
        return basicClass;
    }

    private static byte[] transformGuiContainerCreative(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("handleMouseClick", "func_184098_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GOTO) {
                        i++;
                        if (i == 5) {
                            method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "$cleanup", "()V", false));
                            break;
                        }
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static void $cleanup() {
            PacketHandler.INSTANCE.sendToServer(new PacketCreativeClean());
        }
    }

    public static class PacketCreativeClean implements IMessage, IMessageHandler<PacketCreativeClean, IMessage> {
        @Override public void fromBytes(ByteBuf buf) {}
        @Override public void toBytes(ByteBuf buf) {}
        @Override
        public IMessage onMessage(PacketCreativeClean message, MessageContext ctx) {
            IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
            mainThread.addScheduledTask(() -> {
                IBaublesItemHandler handler = BaublesApi.getBaublesHandler(ctx.getServerHandler().player);
                for (int i = 0; i < handler.getSlots(); i++) {
                    handler.setStackInSlot(i, ItemStack.EMPTY);
                }
            });
            return null;
        }
    }
}
