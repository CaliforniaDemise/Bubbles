package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import com.oblivioussp.spartanweaponry.init.OreDictionarySW;
import com.oblivioussp.spartanweaponry.item.ItemQuiverBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.List;

public final class SpartanWeaponryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/SpartanWeaponryTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (_name) {
            case "com.oblivioussp.spartanweaponry.client.gui.GuiHandler": return transformGuiHandler(bytes);
            case "com.oblivioussp.spartanweaponry.network.PacketKeyHandle": return transformPacketKeyHandle(bytes);
            case "com.oblivioussp.spartanweaponry.util.QuiverHelper": return transformQuiverHelper(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformGuiHandler(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                    ++count;
                    if (count == 2) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getQuiverSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    private static byte[] transformPacketKeyHandle(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("handle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot") && node.getPrevious().getOpcode() == ICONST_5) {
                        ++count;
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getQuiverSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        if (count == 2) break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformQuiverHelper(byte[] bytes) {
        ClassNode cls = read(bytes);
        label:
        for (MethodNode method : cls.methods) {
            switch (method.name) {
                case "findValidQuivers": {
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
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getValidQuivers", "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;)V", false));
                            list.add(new JumpInsnNode(GOTO, l_con_true));
                            list.add(l_con);
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                    break;
                }
                case "findFromBauble": {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    boolean check = false;
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == ALOAD) check = true;
                        else if (node.getOpcode() == ARETURN) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getFirstQuiver", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", false));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                        if (check) iterator.remove();
                    }
                    break;
                }
                case "isInBaublesSlot": {
                    Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                    boolean check = false;
                    while (iterator.hasNext()) {
                        AbstractInsnNode node = iterator.next();
                        if (node.getOpcode() == ALOAD) check = true;
                        else if (node.getOpcode() == IRETURN) {
                            InsnList list = new InsnList();
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new VarInsnNode(ALOAD, 1));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$isFirstEqual", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z", false));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                        if (check) iterator.remove();
                    }
                    break label;
                }
            }
        }
        return write(cls, 3);
    }

    @SuppressWarnings("unused")
    public static class Hooks {

        public static int General$getQuiverSlot(int slot, EntityPlayer player) {
            return CoreUtility.getSlot(player, stack -> !stack.isEmpty() && stack.getItem() instanceof ItemQuiverBase);
        }

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

        private Hooks() {}
    }

    private SpartanWeaponryTransformer() {}
}
