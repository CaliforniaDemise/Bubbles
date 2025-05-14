package baubles.common.integration.groovyscript;

import baubles.api.BaublesApi;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesContainer;
import net.minecraft.entity.player.EntityPlayer;

public class BaublesEntityPlayerExpansion {

    public static BaublesContainer getBaublesContainer(EntityPlayer player) {
        return BaublesApi.getBaublesContainer(player);
    }

    public static void growBaubleSlot(EntityPlayer player, IBaubleType type, int amount) {
        BaublesContainer container = BaublesApi.getBaublesContainer(player);
        container.grow(type, amount);
    }

    public static void shrinkBaubleSlot(EntityPlayer player, IBaubleType type, int amount) {
        BaublesContainer container = BaublesApi.getBaublesContainer(player);
        container.shrink(type, amount);
    }

    public static void setBaubleSlot(EntityPlayer player, IBaubleType type, int amount) {
        BaublesContainer container = BaublesApi.getBaublesContainer(player);
        container.set(type, amount);
    }
}
