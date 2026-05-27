package baubles.core.transformers;

import artifacts.common.init.ModItems;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBaubleType;
import baubles.api.cap.IBaublesItemHandler;
import com.artur114.armoredarms.core.api.EnumHandSideAA;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public final class ArmoredArmsTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ArmoredArmsTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        if (name.equals("com.artur114.armoredarms.client.integration.artifacts.layer.ArmRenderLayerArtifacts")) {
            return transformArmRenderLayerArtifacts(bytes);
        }
        return bytes;
    }

    private static byte[] transformArmRenderLayerArtifacts(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTextures")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ArmRenderLayerArtifacts$getRingSlots", "([ILnet/minecraft/entity/player/EntityPlayer;Lcom/artur114/armoredarms/core/api/EnumHandSideAA;)[I", false));
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

        public static int[] ArmRenderLayerArtifacts$getRingSlots(int[] validSlots, EntityPlayer player, EnumHandSideAA side) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int count = 0;
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                IBaubleType type = handler.getSlotType(i);
                if (!stack.isEmpty() && isGlove(stack.getItem())) {
                    if (type != BaubleType.RING) {
                        return new int[] { i, i };
                    }
                    else {
                        boolean check = (side.ordinal() == 1 && (count & 1) == 1) || (side.ordinal() != 1 && (count & 1) == 0);
                        if (check) {
                            int[] out = new int[2];
                            out[count & 1] = i;
                            return out;
                        }
                    }
                }
                if (type == BaubleType.RING) ++count;
            }
            return new int[] { -1, -1 };
        }

        private static boolean isGlove(Item item) {
            return item == ModItems.POWER_GLOVE || item == ModItems.FERAL_CLAWS || item == ModItems.MECHANICAL_GLOVE || item == ModItems.FIRE_GAUNTLET || item == ModItems.POCKET_PISTON;
        }

        private Hooks() {}
    }

    private ArmoredArmsTransformer() {}
}
