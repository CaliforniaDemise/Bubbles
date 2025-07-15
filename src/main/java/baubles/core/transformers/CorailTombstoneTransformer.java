package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public class CorailTombstoneTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/CorailTombstoneTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("ovh.corail.tombstone.compatibility.CompatibilityBaubles")) return transformCompatibilityBaubles(basicClass);
        return basicClass;
    }

    private static byte[] transformCompatibilityBaubles(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.methods.removeIf(m -> m.name.equals("autoEquip"));
        { // autoEquip
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "autoEquip", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraftforge/items/ItemStackHandler;Ljava/util/List;Z)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitVarInsn(ILOAD, 4);
            m.visitMethodInsn(INVOKESTATIC, HOOK, "$autoEquip", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraftforge/items/ItemStackHandler;Ljava/util/List;Z)V", false);
            m.visitInsn(RETURN);
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static void $autoEquip(EntityPlayer player, ItemStackHandler corailTombstone, List<Integer> ids, boolean isRespawn) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            IntList intList = new IntArrayList(8);
            List<ItemStack> stackList = new ArrayList<>(8);
            {
                Iterator<Integer> iterator = ids.stream().sorted(Comparator.reverseOrder()).iterator();
                while (iterator.hasNext()) {
                    int i = iterator.next();
                    ItemStack stack = corailTombstone.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        IBauble bauble = BaublesApi.getBauble(stack);
                        if (bauble != null) {
                            intList.add(i);
                            stackList.add(stack);
                        }
                    }
                }
            }
            for (int a = 0; a < intList.size(); a++) {
                int slot = intList.getInt(a);
                ItemStack stack = stackList.get(a);
                IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(stack));
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack baubleStack = handler.getStackInSlot(i);
                    if (!baubleStack.isEmpty()) continue;
                    if (bauble.canEquip(stack, player) && bauble.canPutOnSlot(handler, i, handler.getSlotType(i), stack)) {
                        handler.setStackInSlot(i, stack);
                        bauble.onEquipped(stack, player);
                        corailTombstone.setStackInSlot(slot, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
    }
}
