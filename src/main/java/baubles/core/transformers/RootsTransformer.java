package baubles.core.transformers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RootsTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/RootsTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("epicsquid.roots.integration.baubles.pouch.BaublePowderInventoryUtil")) {
            return transformBaublePowderInventoryUtil(basicClass);
        }
        return basicClass;
    }

    private static byte[] transformBaublePowderInventoryUtil(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getPouch")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$slotArray", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insertBefore(node, list);
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    public static class Hooks {

        public static int[] $slotArray(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int[] out = new int[handler.getSlots()];
            for (int i = 0; i < handler.getSlots(); i++) {
                out[i] = i;
            }
            return out;
        }
    }
}
