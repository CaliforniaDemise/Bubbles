package baubles.api;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaubleTypeImpl implements IBaubleType {

    private final int order;
    private final ResourceLocation name;
    private final String translationKey;
    private final ResourceLocation textureLoc;

    public BaubleTypeImpl(String namespace, String path, int order) {
        this(new ResourceLocation(namespace, path), order);
    }

    public BaubleTypeImpl(String name, int order) {
        this(new ResourceLocation(name), order);
    }

    public BaubleTypeImpl(ResourceLocation name, int order) {
        this.order = order;
        this.name = name;
        this.translationKey = name.getNamespace() + ".type." + name.getPath();
        this.textureLoc = new ResourceLocation(name.getNamespace(), "gui/slots/" + name.getPath());
    }

    @NotNull
    @Override
    public ResourceLocation getRegistryName() {
        return this.name;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return translationKey;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Nullable
    @Override
    public ResourceLocation getBackgroundTexture() {
        return this.textureLoc;
    }
}