package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class WearableBackpacksTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/WearableBackpacksTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        if (name.equals("net.mcft.copy.backpacks.api.BackpackHelper")) {
            return transformBackpackHelper(bytes);
        }
        return bytes;
    }

    private static byte[] transformBackpackHelper(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getBackpackBaubleSlotItemStack")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETSTATIC, "net/mcft/copy/backpacks/BackpacksContent", "BACKPACK", "Lnet/mcft/copy/backpacks/item/ItemBackpack;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "BackpackHelper$getSlotIndex", "(ILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("setBackpackBaubleSlotItemStack")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "BackpackHelper$setStack", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)V", false));
                list.add(new InsnNode(RETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static int BackpackHelper$getSlotIndex(int validSlot, EntityPlayer player, Item item) {
            return CoreUtility.getSlot(player, stack -> !stack.isEmpty() && stack.getItem() == item);
        }

        public static void BackpackHelper$setStack(EntityPlayer player, ItemStack stack) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack s = handler.getStackInSlot(i);
                if (s.isEmpty() && handler.isItemValidForSlot(i, stack, player)) {
                    handler.setStackInSlot(i, stack);
                    break;
                }
            }
        }

        private Hooks() {}
    }

    private WearableBackpacksTransformer() {}
}
