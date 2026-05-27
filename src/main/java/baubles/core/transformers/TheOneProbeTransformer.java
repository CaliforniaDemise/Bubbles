package baubles.core.transformers;

import baubles.api.BaublesApi;
import mcjty.theoneprobe.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class TheOneProbeTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/TheOneProbeTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        if (name.equals("mcjty.theoneprobe.compat.BaubleTools")) {
            return transformBaubleTools(bytes);
        }
        return bytes;
    }

    private static byte[] transformBaubleTools(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("hasProbeGoggle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ICONST_4) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "BaubleTools$getGoggleSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
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
    public static final class Hooks {

        public static int BaubleTools$getGoggleSlot(int slot, EntityPlayer player) {
            return BaublesApi.isBaubleEquipped(player, ModItems.probeGoggles);
        }

        private Hooks() {}
    }

    private TheOneProbeTransformer() {}
}
