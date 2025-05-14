package baubles.common.integration.groovyscript;

import baubles.common.Baubles;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class BaublesGroovyPlugin implements GroovyPlugin {

    @Override
    public @NotNull String getModId() {
        return Baubles.MODID;
    }

    @Override
    public @NotNull String getContainerName() {
        return "Bubbles";
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        ExpansionHelper.mixinClass(ItemStack.class, BaublesItemStackExpansion.class);
        ExpansionHelper.mixinClass(EntityLivingBase.class, BaublesEntityLivingBaseExpansion.class);
        ExpansionHelper.mixinClass(EntityPlayer.class, BaublesEntityPlayerExpansion.class);
    }
}
