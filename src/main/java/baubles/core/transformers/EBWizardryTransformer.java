package baubles.core.transformers;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.EntityPlayer;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public class EBWizardryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/EBWizardryTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("electroblob.wizardry.integration.baubles.WizardryBaublesIntegration")) return transformWizardryBaublesIntegration(basicClass);
        return basicClass;
    }

    private static byte[] transformWizardryBaublesIntegration(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getEquippedArtefacts")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                AbstractInsnNode node = iterator.next();
                if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                    InsnList list = new InsnList();
                    list.add(new VarInsnNode(ALOAD, 0));
                    list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$getTypeSlots", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)[I", false));
                    method.instructions.insertBefore(node, list);
                    iterator.remove();
                    break;
                }
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static int[] $getTypeSlots(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            IntList list = new IntArrayList();
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) == type) {
                    list.add(i);
                }
            }
            return list.toArray(new int[0]);
        }
    }
}
