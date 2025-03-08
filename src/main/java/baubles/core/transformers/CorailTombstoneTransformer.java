package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CorailTombstoneTransformer extends BaseTransformer {

    public static byte[] transformCompatibilityBaubles(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        cls.methods.removeIf(m -> m.name.equals("autoEquip"));
        { // autoEquip
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "autoEquip", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraftforge/items/ItemStackHandler;Ljava/util/List;Z)V", null, null);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 3);
            m.visitVarInsn(ILOAD, 4);
            m.visitMethodInsn(INVOKESTATIC, "baubles/core/transformers/CorailTombstoneTransformer", "$autoEquip", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraftforge/items/ItemStackHandler;Ljava/util/List;Z)V", false);
            m.visitInsn(RETURN);
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static void $autoEquip(EntityPlayer player, ItemStackHandler corailTombstone, List<Integer> ids, boolean isRespawn) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        IntList slots = new IntArrayList(8);
        List<ItemStack> list = new ArrayList<>(8);
        {
            Iterator<Integer> iterator = ids.iterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
                ItemStack stack = corailTombstone.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    IBauble bauble = BaublesApi.getBauble(stack);
                    if (bauble != null) {
                        list.add(stack);
                        slots.add(i);
                        iterator.remove();
                    }
                }
            }
        }
        Iterator<ItemStack> iterator = list.iterator();
        IntIterator intIterator = slots.iterator();
        while (iterator.hasNext()) {
            int a = intIterator.nextInt();
            ItemStack stack = iterator.next();
            IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(stack));
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack baubleStack = handler.getStackInSlot(i);
                if (!baubleStack.isEmpty()) continue;
                if (bauble.canEquip(stack, player) && bauble.canPutOnSlot(handler, i, stack)) {
                    handler.setStackInSlot(i, stack);
                    bauble.onEquipped(stack, player);
                    corailTombstone.setStackInSlot(a, ItemStack.EMPTY);
                    iterator.remove();
                    break;
                }
            }
        }
    }
}
