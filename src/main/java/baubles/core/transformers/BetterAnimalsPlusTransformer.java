package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import its_meow.betteranimalsplus.common.item.ItemCape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class BetterAnimalsPlusTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/BetterAnimalsPlusTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "its_meow.betteranimalsplus.common.item.ItemBearCapeBaubles":
            case "its_meow.betteranimalsplus.common.item.ItemWolfCapeBaubles": return transformCapes(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformCapes(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("isValidArmor")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("ordinal")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 3));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ItemCape$getIndex", "(ILnet/minecraft/entity/Entity;)I", false));
                        method.instructions.insert(node, list);
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

        public static int ItemCape$getIndex(int ordinal, Entity entity) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) entity);
            return CoreUtility.getSlot(entity, stack -> stack.getItem() instanceof ItemCape);
        }

        private Hooks() {}
    }

    private BetterAnimalsPlusTransformer() {}
}