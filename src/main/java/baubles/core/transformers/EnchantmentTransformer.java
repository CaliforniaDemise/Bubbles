package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.List;


public class EnchantmentTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/EnchantmentTransformer$Hooks";

    /**
     * Transforms {@link Enchantment#getEntityEquipment(EntityLivingBase)} for making enchantments in baubles work.
     **/
    public static byte[] transformEnchantment(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("getEntityEquipment", "func_185260_a"))) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != ARETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "Enchantment$getEntityEquipment_tooLazy", "(Ljava/util/List;Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/entity/EntityLivingBase;)Ljava/util/List;", false));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    /**
     * Transforms {@link Item#canApplyAtEnchantingTable(ItemStack, Enchantment)} for making enchantments appliable for baubles.
     **/
    public static byte[] transformItem(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("canApplyAtEnchantingTable")) {
                AbstractInsnNode node = method.instructions.getLast();
                if (node.getOpcode() != IRETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                LabelNode l_con_enchtype = new LabelNode();
                list.add(new JumpInsnNode(IFEQ, l_con_enchtype));
                list.add(new InsnNode(ICONST_1));
                list.add(new InsnNode(IRETURN));
                list.add(l_con_enchtype);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new FieldInsnNode(GETSTATIC, "baubles/api/cap/BaublesCapabilities", "CAPABILITY_ITEM_BAUBLE", "Lnet/minecraftforge/common/capabilities/Capability;"));
                list.add(new InsnNode(ACONST_NULL));
                list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "getCapability", "(Lnet/minecraftforge/common/capabilities/Capability;Lnet/minecraft/util/EnumFacing;)Ljava/lang/Object;", false));
                list.add(new TypeInsnNode(CHECKCAST, "baubles/api/IBauble"));
                list.add(new VarInsnNode(ASTORE, 3));
                list.add(new VarInsnNode(ALOAD, 3));
                LabelNode l_con_null = new LabelNode();
                list.add(new JumpInsnNode(IFNONNULL, l_con_null));
                list.add(new InsnNode(ICONST_0));
                list.add(new InsnNode(IRETURN));
                list.add(l_con_null);
                list.add(new FrameNode(F_APPEND, 1, new Object[]{"baubles/api/IBauble"}, 0, null));
                list.add(new VarInsnNode(ALOAD, 3));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/IBauble", "getType", "(Lnet/minecraft/item/ItemStack;)Lbaubles/api/IBaubleType;", true));
                list.add(new VarInsnNode(ALOAD, 2));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/IBaubleType", "canApplyEnchantment", "(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)Z", true));
                method.instructions.insertBefore(node, list);
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static List<ItemStack> Enchantment$getEntityEquipment_tooLazy(List<ItemStack> list, Enchantment enchantment, EntityLivingBase entity) {
            if (!(entity instanceof EntityPlayer)) return list;
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                IBauble bauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                if (bauble == null) continue;
                if (bauble.getType(stack).canApplyEnchantment(enchantment, stack)) list.add(stack);
            }
            return list;
        }
    }
}
