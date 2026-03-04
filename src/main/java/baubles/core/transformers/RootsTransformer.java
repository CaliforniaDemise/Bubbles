package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class RootsTransformer extends BaseTransformer {

    private static final String HOOKS = "baubles/core/transformers/RootsTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("epicsquid.roots.integration.baubles.pouch.BaublePowderInventoryUtil"))
            return transformBaublePowderInventoryUtil(basicClass);
        return basicClass;
    }

    // Replace getPouch() body to check all bauble slots, not just BaubleType.BELT.getValidSlots()
    // which returns [-1, -1] in Bubbles (deprecated) and thus never finds anything.
    private static byte[] transformBaublePowderInventoryUtil(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getPouch")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean check = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (!check && node.getOpcode() == ALOAD) check = true;
                    else if (node.getOpcode() == ARETURN) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOKS, "$getPouch", "(Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;", false));
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
        private static Class<?> itemPouchClass = null;
        private static boolean pouchClassLookupDone = false;

        private static Class<?> getItemPouchClass() {
            if (!pouchClassLookupDone) {
                pouchClassLookupDone = true;
                try {
                    itemPouchClass = Class.forName("epicsquid.roots.item.ItemPouch");
                } catch (ClassNotFoundException ignored) {
                }
            }
            return itemPouchClass;
        }

        public static ItemStack $getPouch(EntityPlayer player) {
            Class<?> pouchClass = getItemPouchClass();
            if (pouchClass == null) return ItemStack.EMPTY;
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (pouchClass.isInstance(stack.getItem())) return stack;
            }
            return ItemStack.EMPTY;
        }
    }
}
