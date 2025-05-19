package baubles.common.integration;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import me.paulf.wings.server.flight.ConstructWingsAccessorEvent;
import me.paulf.wings.util.HandlerSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@SuppressWarnings("unused")
public class StupidWingsEvents {

    @SubscribeEvent
    public static void constructWingsAccessor(ConstructWingsAccessorEvent event) {
        event.addPlacing((player, handlers) -> {
            if (player instanceof EntityPlayer) {
                IBaublesItemHandler handler = BaublesApi.getBaublesHandler((EntityPlayer) player);
                for (int i = 0; i < handler.getSlots(); i++) {
                    handlers.add(HandlerSlot.create(handler, i));
                }
            }
        });
    }
}
