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
        Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
        {
            Iterator<Integer> iterator = ids.stream().sorted(Comparator.reverseOrder()).iterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
                ItemStack stack = corailTombstone.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    IBauble bauble = BaublesApi.getBauble(stack);
                    if (bauble != null) {
                        map.put(i, stack);
                    }
                }
            }
        }
        for (Int2ObjectMap.Entry<ItemStack> entry : map.int2ObjectEntrySet()) {
            IBauble bauble = Objects.requireNonNull(BaublesApi.getBauble(entry.getValue()));
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack baubleStack = handler.getStackInSlot(i);
                if (!baubleStack.isEmpty()) continue;
                if (bauble.canEquip(entry.getValue(), player) && bauble.canPutOnSlot(handler, i, entry.getValue())) {
                    handler.setStackInSlot(i, entry.getValue());
                    bauble.onEquipped(entry.getValue(), player);
                    corailTombstone.setStackInSlot(entry.getIntKey(), ItemStack.EMPTY);
                    break;
                }
            }
        }
    }
}
