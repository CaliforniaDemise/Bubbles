package baubles.core.transformers;

import baubles.core.CoreUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.objectweb.asm.tree.*;
import org.zeith.thaumicadditions.items.baubles.ItemBeltMeteor;
import org.zeith.thaumicadditions.items.baubles.ItemBeltStriding;
import org.zeith.thaumicadditions.items.baubles.ItemBeltTraveler;

import java.util.Iterator;

public final class ThaumicAdditionsTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ThaumicAdditionsTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "org.zeith.thaumicadditions.events.LivingEventsTAR": return transformLivingEventsTAR(bytes);
            case "org.zeith.thaumicadditions.items.baubles.ItemBeltStriding": return transformItemBeltStriding(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformLivingEventsTAR(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node.getOpcode() == ICONST_3) {
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LivingEventsTAR$getSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                    method.instructions.insert(node, list);
                    break;
                }
            }
        }
        return write(cls);
    }

    private static byte[] transformItemBeltStriding(byte[] bytes) {
        ClassNode cls = read(bytes);
        int count = 0;
        for (MethodNode method : cls.methods) {
            if (method.name.equals("playerJumps") || method.name.equals("playerFalls")) {
                ++count;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ItemBeltStriding$getAllSlots", "([Inet/minecraftforge/event/entity/living/LivingEvent;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                if (count == 2) break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static int LivingEventsTAR$getSlot(int slotIndex, EntityPlayer player) {
            return CoreUtility.getSlot(player, stack -> stack.getItem() instanceof ItemBeltTraveler || stack.getItem() instanceof ItemBeltMeteor || stack.getItem() instanceof ItemBeltStriding);
        }

        public static int[] ItemBeltStriding$getAllSlots(int[] validSlots, LivingEvent event) {
            return CoreUtility.getSlotArray(event.getEntityLiving());
        }
    }
}
