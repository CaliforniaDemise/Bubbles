package baubles.core.transformers;

import baubles.core.CoreUtility;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class RootsTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/RootsTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "epicsquid.roots.integration.baubles.pouch.BaublePowderInventoryUtil": return transformBaublePowderInventoryUtil(bytes);
            case "epicsquid.roots.integration.baubles.pouch.PouchEquipHandler": return transformPouchEquipHandler(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformBaublePowderInventoryUtil(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getPouch")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$slotArray", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformPouchEquipHandler(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("tryEquipPouch")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$slotArray", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        iterator.remove();
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

        public static int[] $slotArray(int[] validSlots, EntityPlayer player) {
            return CoreUtility.getSlotArray(player);
        }

        private Hooks() {}
    }

    private RootsTransformer() {}
}
