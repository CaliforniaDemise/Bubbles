package baubles.core.transformers;

import baubles.core.CoreUtility;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class AstralSorceryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/AstralSorceryTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        if (name.equals("hellfirepvp.astralsorcery.common.util.BaublesHelper")) return transformBaublesHelper(bytes);
        return bytes;
    }

    private static byte[] transformBaublesHelper(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getWornBaublesForType")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "BaublesHelper$getSlotArray", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static int[] BaublesHelper$getSlotArray(int[] validSlots, EntityPlayer player) {
            return CoreUtility.getSlotArray(player);
        }

        private Hooks() {}
    }

    private AstralSorceryTransformer() {}
}
