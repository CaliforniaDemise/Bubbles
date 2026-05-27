package baubles.core.transformers;

import org.objectweb.asm.tree.*;

public final class CosmeticArmorsTransformer extends BaseTransformer{

    public static byte[] transform(String _name, String name, byte[] bytes) {
        if (name.equals("lain.mods.cos.ModConfigs")) return transformModConfigs(bytes);
        return bytes;
    }

    private static byte[] transformModConfigs(byte[] bytes) {
        ClassNode cls = read(bytes);
        for (MethodNode method : cls.methods) {
            if (method.name.equals("loadConfigs")) {
                AbstractInsnNode node = method.instructions.getLast();
                while (node.getOpcode() != RETURN) node = node.getPrevious();
                InsnList list = new InsnList();
                list.add(new InsnNode(ICONST_0));
                list.add(new FieldInsnNode(PUTSTATIC, "lain/mods/cos/ModConfigs", "CosArmorToggleButton_Baubles", "Z"));
                method.instructions.insertBefore(node, list);
                break;
            }
        }
        return write(cls);
    }

    private CosmeticArmorsTransformer() {}
}
