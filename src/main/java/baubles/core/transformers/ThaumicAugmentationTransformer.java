package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.objectweb.asm.tree.*;
import thecodex6824.thaumicaugmentation.api.TAItems;
import thecodex6824.thaumicaugmentation.api.augment.CapabilityAugment;
import thecodex6824.thaumicaugmentation.api.augment.CapabilityAugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.IAugment;
import thecodex6824.thaumicaugmentation.api.augment.IAugmentableItem;
import thecodex6824.thaumicaugmentation.api.augment.builder.IThaumostaticHarnessAugment;
import thecodex6824.thaumicaugmentation.common.item.trait.IElytraCompat;

import java.util.Iterator;

// TODO Handle it without overwriting - TAHooksClient, TAHooksCommon
public final class ThaumicAugmentationTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ThaumicAugmentationTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "thecodex6824.thaumicaugmentation.client.event.ClientEventHandler": return transformClientEventHandler(bytes);
            case "thecodex6824.thaumicaugmentation.client.internal.TAHooksClient": return transformTAHooksClient(bytes);
            case "thecodex6824.thaumicaugmentation.client.renderer.layer.RenderLayerHarness": return transformRenderLayerHarness(bytes);
            case "thecodex6824.thaumicaugmentation.common.event.PlayerEventHandler": return transformPlayerEventHandler(bytes);
            case "thecodex6824.thaumicaugmentation.common.internal.TAHooksCommon": return transformTAHooksCommon(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformClientEventHandler(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onClientTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                int count = 0;
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        ++count;
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getAllSlots", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        if (count == 2) break;
                    }
                }
            }
            else if (method.name.equals("onFlightChange")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSlots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getAllSlots", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformTAHooksClient(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("checkPlayerSprintState")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new VarInsnNode(ILOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "TAHooksClient$checkPlayerSprintState", "(Lnet/minecraft/entity/player/EntityPlayer;Z)Z", false));
                list.add(new InsnNode(IRETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("checkElytra")) {
                int count = 0;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ALOAD && ((VarInsnNode) node).var == 2) {
                        ++count;
                        if (count == 2) {
                            InsnList list = new InsnList();
                            LabelNode l_con = new LabelNode();
                            list.add(new InsnNode(ICONST_1));
                            list.add(new JumpInsnNode(IFEQ, l_con));
                            list.add(new VarInsnNode(ALOAD, 0));
                            list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "TAHooksClient$checkElytraBaubles", "(Lnet/minecraft/client/entity/EntityPlayerSP;)V", false));
                            list.add(new InsnNode(RETURN));
                            list.add(l_con);
                            list.add(new FrameNode(F_SAME, 0, null, 0, null));
                            method.instructions.insertBefore(node, list);
                            break;
                        }
                    }
                }
            }
            else if (method.name.equals("shouldRenderCape")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "TAHooksClient$shouldRenderCape$getElytraItem", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformRenderLayerHarness(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("doRenderLayer", "func_177141_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "RenderLayerHarness$getHarnessSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformPlayerEventHandler(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("playerCanBoost")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("getValidSots")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "PlayerEventHandler$getAllSlots", "([ILnet/minecraft/entity/player/EntityPlayer;)[I", false));
                        method.instructions.insert(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformTAHooksCommon(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("checkElytra")) {
                AbstractInsnNode node = method.instructions.getFirst();
                InsnList list = new InsnList();
                LabelNode l_con = new LabelNode();
                list.add(new InsnNode(ICONST_1));
                list.add(new JumpInsnNode(IFEQ, l_con));
                list.add(new VarInsnNode(ALOAD, 1));
                list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "TAHooksCommon$checkElytra", "(Lnet/minecraft/entity/player/EntityPlayerMP;)V", false));
                list.add(new InsnNode(RETURN));
                list.add(l_con);
                list.add(new FrameNode(F_SAME, 0, null, 0, null));
                method.instructions.insertBefore(node, list);
            }
            else if (method.name.equals("updateElytraFlag")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "TAHooksCommon$updateElytraFlag", "(ILnet/minecraft/entity/EntityLivingBase;)I", false));
                        method.instructions.insertBefore(node, list);
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

        public static int[] General$getAllSlots(int[] validSlots, EntityPlayer player) {
            return CoreUtility.getSlotArray(player);
        }

        @SideOnly(Side.CLIENT)
        public static boolean TAHooksClient$checkPlayerSprintState(EntityPlayer player, boolean sprint) {
            if (sprint && !player.isCreative() && !player.isSpectator() && player.capabilities.isFlying) {
                IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
                for (int i = 0; i < handler.getSlots(); ++i) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        IAugmentableItem augmentable = stack.getCapability(CapabilityAugmentableItem.AUGMENTABLE_ITEM, null);
                        if (augmentable != null) {
                            if (augmentable.getUsedAugmentSlots() > 0) {
                                for (ItemStack augment : augmentable.getAllAugments()) {
                                    IAugment aug = augment.getCapability(CapabilityAugment.AUGMENT, null);
                                    if (aug instanceof IThaumostaticHarnessAugment && !((IThaumostaticHarnessAugment) aug).shouldAllowSprintFly(player)) return false;
                                }
                                return true;
                            }
                            else return false;
                        }
                    }
                }
            }
            return sprint;
        }

        @SideOnly(Side.CLIENT)
        public static void TAHooksClient$checkElytraBaubles(EntityPlayerSP player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IElytraCompat && ((IElytraCompat) stack.getItem()).allowElytraFlight(player, stack)) {
                    player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_FALL_FLYING));
                    break;
                }
            }
        }

        @SideOnly(Side.CLIENT)
        public static int TAHooksClient$shouldRenderCape$getElytraItem(int validSlot, EntityPlayer player) {
            return CoreUtility.getSlot(player, stack -> stack.getItem() instanceof IElytraCompat);
        }

        @SideOnly(Side.CLIENT)
        public static int RenderLayerHarness$getHarnessSlot(int validSlot, EntityPlayer player) {
            return CoreUtility.getSlot(player, stack -> stack.getItem() == TAItems.THAUMOSTATIC_HARNESS || stack.getItem() == TAItems.ELYTRA_HARNESS);
        }

        public static int[] PlayerEventHandler$getAllSlots(int[] validSlots, EntityPlayer player) {
            return CoreUtility.getSlotArray(player);
        }

        public static void TAHooksCommon$checkElytra(EntityPlayerMP player) {
            IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof IElytraCompat && ((IElytraCompat) stack.getItem()).allowElytraFlight(player, stack)) {
                    player.setElytraFlying();
                }
            }
        }

        public static int TAHooksCommon$updateElytraFlag(int validSlot, EntityLivingBase entity) {
            return CoreUtility.getSlot(entity, stack -> !stack.isEmpty() && stack.getItem() instanceof IElytraCompat && ((IElytraCompat) stack.getItem()).allowElytraFlight((EntityPlayer) entity, stack));
        }
    }
}
