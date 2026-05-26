package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableSet;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class EBWizardryTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/EBWizardryTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] basicClass) {
        if (name.equals("electroblob.wizardry.integration.baubles.WizardryBaublesIntegration")) return transformWizardryBaublesIntegration(basicClass);
        return basicClass;
    }

    private static byte[] transformWizardryBaublesIntegration(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getEquippedArtefacts")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "WBI$getArtefacts", "(Lnet/minecraft/entity/player/EntityPlayer;[Lelectroblob/wizardry/item/ItemArtefact$Type)Ljava/util/List;", false));
                list.add(new InsnNode(ARETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static List<ItemArtefact> WBI$getArtefacts(EntityPlayer player, ItemArtefact.Type... types) {
            List<ItemArtefact> artefacts = new ArrayList<>();
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            long set = getTypeSet(types);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemArtefact) {
                    ItemArtefact artefact = (ItemArtefact) stack.getItem();
                    if (check(set, artefact.getType())) artefacts.add(artefact);
                }
            }
            return artefacts;
        }

        private static long getTypeSet(ItemArtefact.Type... types) {
            long out = 0L;
            for (ItemArtefact.Type type : types) {
                out |= 1 << type.ordinal();
            }
            return out;
        }

        private static boolean check(long set, ItemArtefact.Type type) {
            return (set & (1 << type.ordinal())) != 0;
        }
    }
}
