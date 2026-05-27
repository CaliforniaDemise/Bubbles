package baubles.core.transformers;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.core.CoreUtility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.tree.*;
import ru.poopycoders.improvedbackpacks.items.ItemBackpack;
import ru.poopycoders.improvedbackpacks.items.ItemEnderBackpack;

import java.util.Iterator;

public final class ImprovedBackpacksTransformer extends BaseTransformer {

    private static final String HOOK = "baubles/core/transformers/ImprovedBackpacksTransformer$Hooks";

    public static byte[] transform(String _name, String name, byte[] bytes) {
        switch (name) {
            case "ru.poopycoders.improvedbackpacks.client.LayerBackpack": return transformLayerBackpack(bytes);
            case "ru.poopycoders.improvedbackpacks.init.ModGui": return transformModGui(bytes);
            case "ru.poopycoders.improvedbackpacks.inventory.containers.ContainerBackpack": return transformContainerBackpack(bytes);
            case "ru.poopycoders.improvedbackpacks.listeners.EventListener": return transformEventListener(bytes);
            case "ru.poopycoders.improvedbackpacks.network.client.CMessageOpenBackpack$Handler": return transformCMessageOpenBackpack$Handler(bytes);
            default: return bytes;
        }
    }

    private static byte[] transformLayerBackpack(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals(getName("doRenderLayer", "func_177141_a"))) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getBackpackSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformModGui(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getServerGuiElement") || method.name.equals("getClientGuiElement")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new FieldInsnNode(GETSTATIC, "ru/poopycoders/improvedbackpacks/init/ModItems", "BACKPACK", "Lru/poopycoders/improvedbackpacks/items/ItemBackpack;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getBackpackSlot", "(ILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/Item;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
        }
        return write(cls);
    }

    private static byte[] transformContainerBackpack(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("getRealBackpack")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, "ru/poopycoders/improvedbackpacks/inventory/containers/ContainerBackpack", "backpackInventory", "Lru/poopycoders/improvedbackpacks/inventory/InventoryBackpack;"));
                        list.add(new FieldInsnNode(GETFIELD, "ru/poopycoders/improvedbackpacks/inventory/InventoryBackpack", "backpack", "Lnet/minecraft/item/ItemStack;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ContainerBackpack$getBackpackSlot", "(ILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
            }
            else if (method.name.equals("saveBackpack")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == ISTORE) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 1));
                        list.add(new VarInsnNode(ALOAD, 0));
                        list.add(new FieldInsnNode(GETFIELD, "ru/poopycoders/improvedbackpacks/inventory/containers/ContainerBackpack", "backpackInventory", "Lru/poopycoders/improvedbackpacks/inventory/InventoryBackpack;"));
                        list.add(new FieldInsnNode(GETFIELD, "ru/poopycoders/improvedbackpacks/inventory/InventoryBackpack", "backpack", "Lnet/minecraft/item/ItemStack;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "ContainerBackpack$getBackpackSlot", "(ILnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformEventListener(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("onClientTick")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new VarInsnNode(ALOAD, 2));
                        list.add(new FieldInsnNode(GETFIELD, "net/minecraft/client/Minecraft", getName("player", "field_71439_g"), "Lnet/minecraft/client/entity/EntityPlayerSP;"));
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "General$getBackpackSlot", "(ILnet/minecraft/entity/player/EntityPlayer;)I", false));
                        method.instructions.insertBefore(node, list);
                        break;
                    }
                }
                break;
            }
        }
        return write(cls);
    }

    private static byte[] transformCMessageOpenBackpack$Handler(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("run")) {
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node.getOpcode() == INVOKEINTERFACE && ((MethodInsnNode) node).name.equals("getStackInSlot")) {
                        InsnList list = new InsnList();
                        list.add(new MethodInsnNode(INVOKESTATIC, HOOK, "CMessageOpenBackpack$getStackInSlot", "(Lbaubles/api/cap/IBaublesItemHandler;I)Lnet/minecraft/item/ItemStack;", false));
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

    @SuppressWarnings("unused")
    public static final class Hooks {

        public static int General$getBackpackSlot(int validSlot, EntityPlayer player) {
            return CoreUtility.getSlot(player, stack -> !stack.isEmpty() && stack.getItem() instanceof ItemBackpack || stack.getItem() instanceof ItemEnderBackpack);
        }

        public static int General$getBackpackSlot(int validSlot, EntityPlayer player, Item item) {
            return BaublesApi.isBaubleEquipped(player, item);
        }

        public static int ContainerBackpack$getBackpackSlot(int validSlot, EntityPlayer player, final ItemStack backpack) {
            return CoreUtility.getSlot(player, stack -> !stack.isEmpty() && stack == backpack);
        }

        public static ItemStack CMessageOpenBackpack$getStackInSlot(IBaublesItemHandler handler, int slot) {
            for (int i = 0; i < slot; ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack || stack.getItem() instanceof ItemEnderBackpack) return stack;
            }
            return ItemStack.EMPTY;
        }
    }
}
