package baubles.core.transformers;

import org.objectweb.asm.tree.*;

public final class CosmeticArmorsTransformer extends BaseTransformer{

    public static byte[] transform(String _name, String name, byte[] basicClass) {
        if (name.equals("lain.mods.cos.ModConfigs")) return transformModConfigs(basicClass);
        return basicClass;
    }

    private static byte[] transformModConfigs(byte[] basicClass) {
        ClassNode cls = read(basicClass);
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
}
