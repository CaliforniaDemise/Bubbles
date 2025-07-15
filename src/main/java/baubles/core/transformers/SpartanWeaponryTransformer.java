package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.oblivioussp.spartanweaponry.init.OreDictionarySW;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public class SpartanWeaponryTransformer extends BaseTransformer {

    private static final String HOOKS = "baubles/core/transformers/SpartanWeaponryTransformer$Hooks";

    public static byte[] transformQuiverHelper(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("findValidQuivers")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                LabelNode l_con_true = null;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (l_con_true == null && node.getOpcode() == IFEQ) {
                        l_con_true = ((JumpInsnNode) node).label;
                    }
                    if (node.getOpcode() == ALOAD) {
                        InsnList list = new InsnList();
                        list.add(new InsnNode(ICONST_1));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$getValidQuivers", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;)V", false));
                        list.add(new JumpInsnNode(GOTO, l_con_true));
                        list.add(l_con);
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("findFromBauble")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD) check = true;
                    else if (node.getOpcode() == ARETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$getFirstQuiver", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                    if (check) iterator.remove();
                }
            }
            else if (method.name.equals("isInBaublesSlot")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD) check = true;
                    else if (node.getOpcode() == IRETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$isFirstEqual", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                    if (check) iterator.remove();
                }
                break;
            }
        }
        return write(cls, 3);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static void $getValidQuivers(EntityPlayer player, List<ItemStack> quivers) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && OreDictionarySW.matches(OreDictionarySW.QUIVERS, stack)) quivers.add(stack);
            }
        }

        public static ItemStack $getFirstQuiver(EntityPlayer player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && OreDictionarySW.matches(OreDictionarySW.QUIVERS, stack)) return stack;
            }
            return ItemStack.EMPTY;
        }

        public static boolean $isFirstEqual(EntityPlayer player, ItemStack stack) {
            ItemStack s = $getFirstQuiver(player);
            if (s.isEmpty()) return false;
            return s.isItemEqual(stack);
        }
    }
}
