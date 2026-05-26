package baubles.core.transformers;


import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class PlethoraTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/PlethoraTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "org.squiddev.plethora.gameplay.neural.ItemNeuralInterface": return transformItemNeuralInterface(bytes);
            case "org.squiddev.plethora.gameplay.neural.NeuralHelpers": return transformNeuralHelpers(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformItemNeuralInterface(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("onItemRightClick", "func_77659_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getAllSlots", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            } else if (method.name.equals("onWornTick")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new TypeInsnNode(NEW, "org/squiddev/plethora/utils/TinySlot$BaublesSlot"));
                list.add(new InsnNode(DUP));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ItemNeuralInterface$getBaublesHandler", "(Lnet/minecraft/entity/EntityLivingBase;)Lbaubles/api/cap/IBaublesItemHandler;", false));
                list.add(new InsnNode(ICONST_M1));
                list.add(new MethodInsnNode(INVOKESPECIAL, "org/squiddev/plethora/utils/TinySlot$BaublesSlot", "<init>", "(Lnet/minecraft/item/ItemStack;Lbaubles/api/cap/IBaublesItemHandler;I)V", false));
                list.add(new InsnNode(ICONST_1));
                list.add(new MethodInsnNode(INVOKESTATIC, "org/squiddev/plethora/gameplay/neural/ItemNeuralInterface", "onUpdate", "(Lnet/minecraft/item/ItemStack;Lorg/squiddev/plethora/utils/TinySlot;Lnet/minecraft/entity/player/EntityPlayer;Z)V", false));
                list.add(new InsnNode(RETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformNeuralHelpers(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getBauble")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getAllSlots", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static int[] $getAllSlots(int[] validSlots, EntityPlayer player) {
            return CoreUtility.getSlotArray(player);
        }

        public static IBaublesItemHandler ItemNeuralInterface$getBaublesHandler(EntityLivingBase entity) {
            return BaublesApi.getBaublesHandler(entity);

        }
    }
}
