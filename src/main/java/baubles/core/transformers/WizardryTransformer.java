package baubles.core.transformers;

import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public class WizardryTransformer extends BaseTransformer {

    private static final String HOOKS = "baubles/core/transformers/WizardryTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("com.teamwizardry.wizardry.api.item.BaublesSupport$ArmorAccessor")) return BaublesSupport$ArmorAccessor(basicClass);
        return basicClass;
    }

    private static byte[] BaublesSupport$ArmorAccessor(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getBaublesFallbackArmor")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                LabelNode l_con = new LabelNode();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ASTORE && ((VarInsnNode) node).var == 3) {
                        check = true;
                        InsnList list = new InsnList();
                        list.add(new InsnNode(ICONST_1));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$addStacks", "(Lbaubles/api/cap/IBaublesItemHandler;Lcom/google/common/collect/ImmutableList$Builder;)Ljava/util/List;", false));
                        list.add(new InsnNode(ARETURN));
                        method.instructions.insert(node, list);
                    }
                    else if (check && node.getOpcode() == ARETURN) {
                        while (node.getOpcode() != ALOAD) node = node.getPrevious();
                        InsnList list = new InsnList();
                        list.add(l_con);
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("getBaublesOnly")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                LabelNode l_con = new LabelNode();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ASTORE && ((VarInsnNode) node).var == 3) {
                        check = true;
                        InsnList list = new InsnList();
                        list.add(new InsnNode(ICONST_1));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$addStacks", "(Lbaubles/api/cap/IBaublesItemHandler;Lcom/google/common/collect/ImmutableList$Builder;)Ljava/util/List;", false));
                        list.add(new InsnNode(ARETURN));
                        method.instructions.insert(node, list);
                    }
                    else if (check && node.getOpcode() == ARETURN) {
                        while (node.getOpcode() != ALOAD) node = node.getPrevious();
                        InsnList list = new InsnList();
                        list.add(l_con);
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls, 3);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static List<ItemStack> $addStacks(IBaublesItemHandler handler, ImmutableList.Builder<ItemStack> list) {
            for (int i = 0; i < handler.getSlots(); i++) {
                list.add(handler.getStackInSlot(i));
            }
            return list.build();
        }
    }
}
