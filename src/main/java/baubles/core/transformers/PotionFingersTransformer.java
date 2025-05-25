package baubles.core.transformers;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;

/**
 * Fuck you Vazkii!!
 **/
public class PotionFingersTransformer extends BaseTransformer {

    public static byte[] transformItemRing(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        boolean wornTickExists = false;
        Iterator<MethodNode> iterator = cls.methods.iterator();
        while (iterator.hasNext()) {
            MethodNode method = iterator.next();
            if (method.name.equals("updatePotionStatus") || method.name.equals("onUnequipped") || method.name.equals("onEquipped"))
                iterator.remove();
            else if (method.name.equals("onWornTick")) wornTickExists = true;
        }
        cls.methods.removeIf(m -> m.name.equals("updatePotionStatus") || m.name.equals("onUnequipped") || m.name.equals("onEquipped"));
        { // onEquipped
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onEquipped", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
            m.visitInsn(ICONST_1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
            m.visitInsn(RETURN);
        }
        { // onUnequipped
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onUnequipped", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ALOAD, 1);
            m.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
            m.visitInsn(ICONST_1);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
            m.visitInsn(RETURN);
        }
        if (!wornTickExists) {
            { // onWornTick
                MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "onWornTick", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;)V", null, null);
                m.visitVarInsn(ALOAD, 0);
                m.visitVarInsn(ALOAD, 2);
                m.visitVarInsn(ALOAD, 1);
                m.visitMethodInsn(INVOKESTATIC, cls.name, "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
                m.visitInsn(ICONST_0);
                m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
                m.visitInsn(RETURN);
            }
        }
        { // updatePotionStatus
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", null, null);
            m.visitVarInsn(ALOAD, 2);
            Label l_con_pnull = new Label();
            m.visitJumpInsn(IFNONNULL, l_con_pnull);
            m.visitInsn(RETURN);
            m.visitLabel(l_con_pnull);
            m.visitFrame(F_SAME, 0, null, 0, null);

            String entityLivingBase = "net/minecraft/entity/EntityLivingBase";
            m.visitVarInsn(ALOAD, 1);
            m.visitFieldInsn(GETFIELD, entityLivingBase, getName("world", "field_70170_p"), "Lnet/minecraft/world/World;");
            m.visitFieldInsn(GETFIELD, "net/minecraft/world/World", getName("isRemote", "field_72995_K"), "Z");
            Label l_con_remote = new Label();
            m.visitJumpInsn(IFNE, l_con_remote);

            m.visitVarInsn(ILOAD, 3);
            Label l_con_unequip = new Label();
            m.visitJumpInsn(IFNE, l_con_unequip);

            m.visitInsn(ICONST_M1);
            m.visitVarInsn(ISTORE, 4);

            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(INSTANCEOF, "net/minecraft/entity/player/EntityPlayer");
            Label l_con_playercheck = new Label();
            m.visitJumpInsn(IFEQ, l_con_playercheck);
            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
            m.visitMethodInsn(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/player/EntityPlayer;)Lbaubles/api/cap/IBaublesItemHandler;", false);
            m.visitVarInsn(ASTORE, 5);

            m.visitInsn(ICONST_0);
            m.visitVarInsn(ISTORE, 6);

            Label l_loop = new Label();
            Label l_loop_continue = new Label();
            m.visitLabel(l_loop);
            m.visitFrame(F_APPEND, 3, new Object[]{INTEGER, "baubles/api/cap/IBaublesItemHandler", INTEGER}, 0, null);
            m.visitVarInsn(ILOAD, 6);
            m.visitVarInsn(ALOAD, 5);
            m.visitMethodInsn(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getSlots", "()I", true);
            m.visitJumpInsn(IF_ICMPEQ, l_loop_continue);
            m.visitVarInsn(ALOAD, 5);
            m.visitVarInsn(ILOAD, 6);
            m.visitMethodInsn(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getStackInSlot", "(I)Lnet/minecraft/item/ItemStack;", true);
            m.visitVarInsn(ASTORE, 7);
            m.visitVarInsn(ALOAD, 7);
            m.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", getName("getItem", ""), "()Lnet/minecraft/item/Item;", false);
            m.visitTypeInsn(INSTANCEOF, "vazkii/potionfingers/ItemRing");
            Label l_con_instance = new Label();
            m.visitJumpInsn(IFEQ, l_con_instance);
            m.visitVarInsn(ALOAD, 7);
            m.visitMethodInsn(INVOKESTATIC, "vazkii/potionfingers/ItemRing", "getPotion", "(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/potion/Potion;", false);
            m.visitVarInsn(ALOAD, 2);
            m.visitJumpInsn(IF_ACMPNE, l_con_instance);
            m.visitIincInsn(4, 1);
            m.visitLabel(l_con_instance);
            m.visitFrame(F_APPEND, 1, new Object[]{"net/minecraft/item/ItemStack"}, 0, null);
            m.visitIincInsn(6, 1);
            m.visitJumpInsn(GOTO, l_loop);
            m.visitLabel(l_loop_continue);
            m.visitFrame(F_CHOP, 3, null, 0, null);

            m.visitLabel(l_con_playercheck);
            m.visitFrame(F_SAME, 0, null, 0, null);

            m.visitVarInsn(ALOAD, 1);
            m.visitTypeInsn(NEW, "net/minecraft/potion/PotionEffect");
            m.visitInsn(DUP);
            m.visitVarInsn(ALOAD, 2);
            m.visitFieldInsn(GETSTATIC, "java/lang/Integer", "MAX_VALUE", "I");
            m.visitVarInsn(ILOAD, 4);
            m.visitInsn(ICONST_1);
            m.visitInsn(ICONST_0);
            m.visitMethodInsn(INVOKESPECIAL, "net/minecraft/potion/PotionEffect", "<init>", "(Lnet/minecraft/potion/Potion;IIZZ)V", false);
            m.visitMethodInsn(INVOKEVIRTUAL, entityLivingBase, getName("addPotionEffect", "func_70690_d"), "(Lnet/minecraft/potion/PotionEffect;)V", false);
            m.visitInsn(RETURN);
            m.visitLabel(l_con_unequip);
            m.visitFrame(F_CHOP, 1, null, 0, null);

            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitMethodInsn(INVOKEVIRTUAL, entityLivingBase, getName("removePotionEffect", "func_184589_d"), "(Lnet/minecraft/potion/Potion;)V", false);
            m.visitLabel(l_con_remote);
            m.visitFrame(F_SAME, 0, null, 0, null);
            m.visitInsn(RETURN);
        }
        { // Fallback for the Pansmith's fork
            MethodVisitor m = cls.visitMethod(ACC_PUBLIC, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Lnet/minecraft/item/ItemStack;Z)V", null, null);
            m.visitVarInsn(ALOAD, 0);
            m.visitVarInsn(ALOAD, 1);
            m.visitVarInsn(ALOAD, 2);
            m.visitVarInsn(ILOAD, 4);
            m.visitMethodInsn(INVOKEVIRTUAL, cls.name, "updatePotionStatus", "(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/Potion;Z)V", false);
            m.visitInsn(RETURN);
        }
        return write(cls);
    }
}