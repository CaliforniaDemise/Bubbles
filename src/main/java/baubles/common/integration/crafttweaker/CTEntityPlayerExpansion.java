package baubles.common.integration.crafttweaker;

import baubles.api.BaublesApi;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesContainer;
import baubles.common.init.BaubleTypes;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CrafttweakerMC;
import crafttweaker.api.player.IPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@SuppressWarnings("unused")
@ZenExpansion("crafttweaker.player.IPlayer")
@ZenRegister
public class CTEntityPlayerExpansion {

    @ZenMethod
    public static void growBaubleSlot(IPlayer player, String typeStr, int amount) {
        IBaubleType type = BaubleTypes.get(new ResourceLocation(typeStr));
        if (type == null) return;
        BaublesContainer container = BaublesApi.getBaublesContainer(CrafttweakerMC.getPlayer(player));
        container.grow(type, amount);
    }

    @ZenMethod
    public static void shrinkBaubleSlot(IPlayer player, String typeStr, int amount) {
        IBaubleType type = BaubleTypes.get(new ResourceLocation(typeStr));
        if (type == null) return;
        BaublesContainer container = BaublesApi.getBaublesContainer(CrafttweakerMC.getPlayer(player));
        container.shrink(type, amount);
    }

    @ZenMethod
    public static void setBaubleSlot(IPlayer player, String typeStr, int amount) {
        IBaubleType type = BaubleTypes.get(new ResourceLocation(typeStr));
        if (type == null) return;
        BaublesContainer container = BaublesApi.getBaublesContainer(CrafttweakerMC.getPlayer(player));
        container.set(type, amount);
    }

    @ZenMethod
    public static void resetBaubleSlots(IPlayer player) {
        EntityPlayer entity = CrafttweakerMC.getPlayer(player);
        BaublesContainer container = BaublesApi.getBaublesContainer(entity);
        container.reset();
    }
}
