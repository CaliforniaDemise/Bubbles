package baubles.core.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

public class WearableBackpacksTransformer extends BaseTransformer {

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.mcft.copy.backpacks.api.BackpackHelper")) return transformBackpackHelper(basicClass);
        return basicClass;
    }

    private static byte[] transformBackpackHelper(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setBackpackBaubleSlotItemStack")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == CHECKCAST) {
                        iterator.remove();
                    } else if (node.getOpcode() == INVOKEVIRTUAL) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "setStackInSlot", "(ILnet/minecraft/item/ItemStack;)V", true));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }
}
