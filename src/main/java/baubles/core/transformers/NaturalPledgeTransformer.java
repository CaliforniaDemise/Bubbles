package baubles.core.transformers;

import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import com.wiresegal.naturalpledge.api.item.IPriestlyEmblem;
import com.wiresegal.naturalpledge.common.items.bauble.ItemDivineCloak;
import com.wiresegal.naturalpledge.common.items.bauble.ItemIronBelt;
import com.wiresegal.naturalpledge.common.items.travel.bauble.ItemToolbelt;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class NaturalPledgeTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/NaturalPledgeTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "com.wiresegal.naturalpledge.common.network.SetToolbeltItemClient":
            case "com.wiresegal.naturalpledge.common.network.SetToolbeltItemServer": return transformSetToolbeltItem(bytes);
            case "com.wiresegal.naturalpledge.common.network.BlinkMessage": return transformBlinkMessage(bytes);
            case "com.wiresegal.naturalpledge.common.items.bauble.ItemDivineCloak$Companion": return transformItemDivineCloak$Companion(bytes);
            case "com.wiresegal.naturalpledge.common.items.bauble.faith.ItemFaithBauble$Companion": return transformItemFaithBauble$Companion(bytes);
            case "com.wiresegal.naturalpledge.common.items.bauble.faith.PriestlyEmblemNjord": return transformPriestlyEmblemNjord(bytes);
            case "com.wiresegal.naturalpledge.common.items.travel.stones.ItemPolyStone$Companion": return transformItemPolyStone$Companion(bytes);
            case "com.wiresegal.naturalpledge.common.block.trap.BlockBaseTrap": return transformBlockBaseTrap(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformSetToolbeltItem(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("handle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getToolbelt", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformBlinkMessage(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("handle")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getDivineCloak", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemDivineCloak$Companion(byte[] bytes) {
        ClassNode cls = read(bytes);
        int count = 0;
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onFall") || method.name.equals("onDamage")) {
                ++count;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getDivineCloak", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                if (count == 2) break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemFaithBauble$Companion(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getEmblem")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getPriestlyEmblem", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformPriestlyEmblemNjord(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("floatInWater")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getIronBelt", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformItemPolyStone$Companion(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onPlayerTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ItemPolyStone$getAllSlots", "([ILnet/minecraftforge/event/entity/living/LivingEvent;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformBlockBaseTrap(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onEntityCollision")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        method.instructions.insertBefore(node, new MethodInsnNode(INVOKESTATIC, HOOK, "General$getDivineCloak", "(Lbaubles/api/cap/IBaublesItemHandler;I)I", false));
                        iterator.remove();
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static int General$getToolbelt(IBaublesItemHandler handler, int validSlot) {
            return CoreUtility.getSlot(handler, stack -> !stack.isEmpty() && stack.getItem() instanceof ItemToolbelt);
        }

        public static int General$getDivineCloak(IBaublesItemHandler handler, int validSlot) {
            return CoreUtility.getSlot(handler, stack -> !stack.isEmpty() && stack.getItem() instanceof ItemDivineCloak);
        }

        public static int General$getPriestlyEmblem(IBaublesItemHandler handler, int validSlot) {
            return CoreUtility.getSlot(handler, stack -> !stack.isEmpty() && stack.getItem() instanceof IPriestlyEmblem);
        }

        public static int General$getIronBelt(IBaublesItemHandler handler, int validSlot) {
            return CoreUtility.getSlot(handler, stack -> !stack.isEmpty() && stack.getItem() instanceof ItemIronBelt);
        }

        public static int[] ItemPolyStone$getAllSlots(int[] validSlots, LivingEvent event) {
            return CoreUtility.getSlotArray(event.getEntityLiving());
        }

        private Hooks() {}
    }

    private NaturalPledgeTransformer() {}
}
