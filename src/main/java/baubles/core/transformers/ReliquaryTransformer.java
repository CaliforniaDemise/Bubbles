package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.common.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

public class ReliquaryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ReliquaryTransformer$Hooks";

    public static byte[] transformItemAngelHeartVial(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("decreaseAngelHeartByOne")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETSTATIC, "xreliquary/init/ModItems", "angelheartVial", "Lxreliquary/items/ItemAngelheartVial;"));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$decreaseAngelHeartByOne", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)V", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    public static byte[] transformItemPhoenixDown(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("revertPhoenixDownToAngelicFeather")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETSTATIC, "xreliquary/init/ModItems", "phoenixDown", "Lxreliquary/items/ItemPhoenixDown;"));
                list.add(new FieldInsnNode(GETSTATIC, "xreliquary/init/ModItems", "angelicFeather", "Lxreliquary/items/ItemAngelicFeather;"));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$revertPhoenixDownToAngelicFeather", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;Lnet/minecraft/item/Item;)V", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static void $revertPhoenixDownToAngelicFeather(EntityPlayer player, Item phoenixDown, Item angelicFeather) {
            if (!Config.compat_reliquary) return;
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() == phoenixDown) {
                    handler.setStackInSlot(i, new ItemStack(angelicFeather));
                    handler.extractItem(i, 1, false);
                    return;
                }
            }
        }

        public static void $decreaseAngelHeartByOne(EntityPlayer player, Item angelHeart) {
            if (!Config.compat_reliquary) return;
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() == angelHeart) {
                    handler.extractItem(i, 1, false);
                    return;
                }
            }
        }
    }
}
