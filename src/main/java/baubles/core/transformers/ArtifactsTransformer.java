package baubles.core.transformers;

import artifacts.common.init.ModItems;
import artifacts.common.item.BaubleAmulet;
import artifacts.common.item.BaubleBottledCloud;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Lovely lovely.
 * Love from Croatia 🇭🇷♥
 **/
public class ArtifactsTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ArtifactsTransformer$Hooks";

    public static byte[] transform(String name, String transformedName, byte[] basicClass, boolean isRLArtifact) {
       switch (transformedName) {
           case "artifacts.client.model.layer.LayerAmulet": return ArtifactsTransformer.transformLayerAmulet(basicClass, isRLArtifact);
           case "artifacts.client.model.layer.LayerBelt": return ArtifactsTransformer.transformLayerBelt(basicClass, isRLArtifact);
           case "artifacts.client.model.layer.LayerCloak": return ArtifactsTransformer.transformLayerCloak(basicClass, isRLArtifact);
           case "artifacts.client.model.layer.LayerDrinkingHat": return ArtifactsTransformer.transformLayerDrinkingHat(basicClass, isRLArtifact);
           case "artifacts.client.model.layer.LayerGloves": return ArtifactsTransformer.transformLayerGloves(basicClass);
           case "artifacts.client.model.layer.LayerNightVisionGoggles": return ArtifactsTransformer.transformLayerNightVisionGoggles(basicClass, isRLArtifact);
           case "artifacts.client.model.layer.LayerSnorkel": return ArtifactsTransformer.transformLayerSnorkel(basicClass, isRLArtifact);
           case "artifacts.common.item.AttributeModifierBauble": return ArtifactsTransformer.transformAttributeModifierBauble(basicClass);
           case "artifacts.common.item.BaubleAmulet": return ArtifactsTransformer.transformBaubleAmulet(basicClass, isRLArtifact);
           case "artifacts.common.item.BaubleBottledCloud": return ArtifactsTransformer.transformBaubleBottledCloud(basicClass); // Non-RL
           default: return basicClass;
       }
    }

    public static boolean checkArtifacts(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        return cls.visibleAnnotations.get(0).values.get(3).equals("RLArtifacts");
    }

    private static byte[] transformAttributeModifierBauble(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("applyModifiers")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$slotArray", "(Lnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insertBefore(node, list);
                        method.instructions.remove(node.getNext());
                        method.instructions.remove(node);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerAmulet(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTexturesGetModel")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerAmulet$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerBelt(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTexturesGetModel")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerBelt$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerCloak(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderChest")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerCloak$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerDrinkingHat(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerDrinkingHat$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerGloves(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("setTextures")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerGloves$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/EnumHandSide;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerNightVisionGoggles(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerNightVisionGoggles$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    private static byte[] transformLayerSnorkel(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("renderLayer")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL) {
                        do {
                            iterator.remove();
                            node = iterator.next();
                        }
                        while (node.getOpcode() != INVOKEINTERFACE);
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "LayerSnorkel$getRenderStack", "(Lbaubles/api/BaubleType;Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    /**
     * MODDERS LOOK AT API PACKAGE {@link baubles.api.BaublesApi#isBaubleEquipped(EntityPlayer, Item)} CHALLENGE
     * FUCKING IMPOSSIBLE
     **/
    private static byte[] transformBaubleAmulet(byte[] basicClass, boolean isRLArtifact) {
        if (!isRLArtifact) return basicClass;
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onLivingDeath")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals("AMULET")) {
                        for (int i = 0; i < 2; i++) {
                            iterator.remove();
                            node = iterator.next();
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "$slotArray", "(Lnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    /**
     * MODDERS LOOK AT API PACKAGE {@link baubles.api.BaublesApi#isBaubleEquipped(EntityPlayer, Item)} CHALLENGE
     * FUCKING IMPOSSIBLE
     **/
    private static byte[] transformBaubleBottledCloud(byte[] basicClass) {
        ClassNode cls = read(basicClass);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onClientTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).name.equals("BELT")) {
                        for (int i = 0; i < 4; i++) {
                            iterator.remove();
                            node = iterator.next();
                        }
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "BaubleBottledCloud$getSlot", "(Lnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    @SuppressWarnings("unused")
    public static class Hooks {
        public static int[] $slotArray(EntityPlayer player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            int[] out = new int[handler.getSlots()];
            for (int i = 0; i < handler.getSlots(); i++) {
                out[i] = i;
            }
            return out;
        }

        public static int BaubleBottledCloud$getSlot(EntityPlayer player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() instanceof BaubleBottledCloud) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerAmulet$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.getItem() instanceof BaubleAmulet) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerBelt$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                Item item = baubles.getStackInSlot(i).getItem();
                if(item == ModItems.BOTTLED_CLOUD) return i;
                else if(item == ModItems.BOTTLED_FART) return i;
                else if(item == ModItems.ANTIDOTE_VESSEL) return i;
                else if(item == ModItems.BUBBLE_WRAP) return i;
                else if(item == ModItems.OBSIDIAN_SKULL) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerCloak$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.getItem() == ModItems.STAR_CLOAK) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerDrinkingHat$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.getItem() == ModItems.DRINKING_HAT) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerGloves$getRenderStack(BaubleType type, EntityPlayer player, EnumHandSide hand) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            int a = -1;
            int oldA;
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                Item item = baubles.getStackInSlot(i).getItem();
                if (item == ModItems.POWER_GLOVE || item == ModItems.FERAL_CLAWS || item == ModItems.MECHANICAL_GLOVE || item == ModItems.FIRE_GAUNTLET || item == ModItems.POCKET_PISTON) {
                    oldA = a;
                    a = i;
                    if (hand == EnumHandSide.LEFT && oldA == -1) return a;
                    if (hand == EnumHandSide.RIGHT && oldA != -1) return a;
                }
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerNightVisionGoggles$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.getItem() == ModItems.NIGHT_VISION_GOGGLES) return i;
            }
            return -1;
        }

        @SideOnly(Side.CLIENT)
        public static int LayerSnorkel$getRenderStack(BaubleType type, EntityPlayer player) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < baubles.getSlots(); i++) {
                if (baubles.getSlotType(i) != type) continue;
                ItemStack stack = baubles.getStackInSlot(i);
                if (stack.getItem() == ModItems.SNORKEL) return i;
            }
            return -1;
        }
    }
}
