package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class WizardryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/WizardryTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] basicClass) {
        switch (name) {
            case "com.teamwizardry.wizardry.api.item.BaublesSupport$ArmorAccessor": return transformBaublesSupport$ArmorAccessor(basicClass);
            case "com.teamwizardry.wizardry.api.item.BaublesSupport$StackAccessor": return transformBaublesSupport$StackAccessor(basicClass);
            default: return basicClass;
        }
    }

    private static byte[] transformBaublesSupport$StackAccessor(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("get")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "StackAccessor$isBauble", "(Lnet/minecraft/item/ItemStack;)Z", false));
                list.add(new InsnNode(IRETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformBaublesSupport$ArmorAccessor(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getBaublesOnly") || method.name.equals("getBaublesFallbackArmor")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKESTATIC && ((MethodInsnNode) node).name.equals("builder")) {
                        InsnList list = new InsnList();
                        LabelNode l_con = new LabelNode();
                        list.add(new InsnNode(ICONST_1));
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ArmorAccessor$getStacks", "(Lnet/minecraft/entity/EntityLivingBase;)Ljava/util/List;", false));
                        list.add(new InsnNode(ARETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
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

        public static List<ItemStack> ArmorAccessor$getStacks(EntityLivingBase entity) {
            ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(entity);
            if (handler != null) {
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) builder.add(stack);
                }
            }
            return builder.build();
        }

        public static boolean StackAccessor$isBauble(ItemStack stack) {
            return stack.getItem() instanceof IBauble || BaublesApi.getBauble(stack) != null;
        }
    }
}
