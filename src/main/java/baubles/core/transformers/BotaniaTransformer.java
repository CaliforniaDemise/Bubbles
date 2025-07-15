package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.*;
import vazkii.botania.common.item.equipment.bauble.ItemTravelBelt;

import java.util.Iterator;

public class BotaniaTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/BotaniaTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            case "vazkii.botania.common.item.equipment.bauble.ItemDivaCharm": return BotaniaTransformer.transformItemDivaCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTiara": return BotaniaTransformer.transformItemTiara(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemGoddessCharm": return BotaniaTransformer.transformItemGoddessCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemHolyCloak": return BotaniaTransformer.transformItemHolyCloak(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemMonocle": return BotaniaTransformer.transformItemMonocle(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTravelBelt": return BotaniaTransformer.transformItemTravelBelt(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemWaterRing": return BotaniaTransformer.transformItemWaterRing(basicClass);
            default: return basicClass;
        }
    }

    private static byte[] transformItemDivaCharm(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("lambda$onEntityDamaged$0")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemTiara(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("updatePlayerFlyStatus")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ICONST_4) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        node = node.getNext();
                        method.instructions.remove(node.getPrevious());
                        node = node.getNext();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKEVIRTUAL, "net/minecraft/item/ItemStack", "isEmpty", "()Z", false));
                        LabelNode l_con = new LabelNode();
                        list.add(new JumpInsnNode(IFEQ, l_con));
                        list.add(new LabelNode());
                        list.add(new InsnNode(RETURN));
                        list.add(l_con);
                        list.add(new FrameNode(F_SAME, 0, null, 0, null));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
            } else if (method.name.equals("shouldPlayerHaveFlight")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ICONST_4) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemGoddessCharm(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onExplosion")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 5));
                        list.add(new FieldInsnNode(GETSTATIC, "vazkii/botania/common/item/ModItems", "goddessCharm", "Lnet/minecraft/item/Item;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemHolyCloak(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onPlayerDamage")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "isBaubleEquipped", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemMonocle(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("hasMonocle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == BIPUSH) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, "baubles/api/BaublesApi", "getBaublesHandler", "(Lnet/minecraft/entity/player/EntityPlayer;)Lbaubles/api/cap/IBaublesItemHandler;", false));
                        list.add(new MethodInsnNode(INVOKEINTERFACE, "baubles/api/cap/IBaublesItemHandler", "getSlots", "()I", true));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemTravelBelt(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("updatePlayerStepStatus")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getTravelBeltSlot", "(Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            } else if (method.name.equals("onPlayerJump")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getTravelBeltSlot", "(Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            } else if (method.name.equals("shouldPlayerHaveStepup")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE) {
                        method.instructions.remove(node.getPrevious());
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getTravelBeltSlot", "(Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemWaterRing(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onWornTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                boolean remove = false;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INSTANCEOF) {
                        remove = true;
                    }
                    if (remove) {
                        method.instructions.remove(node.getPrevious());
                        if (node.getOpcode() == RETURN) {
                            method.instructions.remove(node.getNext().getNext().getNext());
                            method.instructions.remove(node.getNext().getNext());
                            method.instructions.remove(node.getNext());
                            method.instructions.remove(node);
                            break;
                        }
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static int $getTravelBeltSlot(EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof ItemTravelBelt) return i;
            }
            return -1;
        }
    }
}
