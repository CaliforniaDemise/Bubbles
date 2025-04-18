package baubles.core.transformers;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public abstract class BaseTransformer implements Opcodes {

    protected static String getName(String mcpName, String srgName) {
        return FMLLaunchHandler.isDeobfuscatedEnvironment() ? mcpName : srgName;
    }

    protected static ClassNode read(byte[] bytes) {
        ClassReader reader = new ClassReader(bytes);
        ClassNode cls = new ClassNode();
        reader.accept(cls, 0);
        return cls;
    }

    protected static byte[] write(ClassNode cls) {
        return write(cls, ClassWriter.COMPUTE_MAXS);
    }

    protected static byte[] write(ClassNode cls, int options) {
        ClassWriter writer = new ClassWriter(options);
        cls.accept(writer);
        return writer.toByteArray();
    }

    protected static void writeClass(ClassNode cls) {
        if (!FMLLaunchHandler.isDeobfuscatedEnvironment()) return;
        File file = new File("classOut/" + cls.name + ".class");
        file.getParentFile().mkdirs();
        try (OutputStream stream = Files.newOutputStream(file.toPath())) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cls.accept(writer);
            stream.write(writer.toByteArray());
        } catch (IOException ignored) {
        }
    }
}
