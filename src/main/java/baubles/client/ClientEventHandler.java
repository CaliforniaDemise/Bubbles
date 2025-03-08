package baubles.client;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.IBaubleType;
import baubles.api.cap.BaublesCapabilities;
import baubles.client.gui.GuiBaublesButton;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.init.BaubleTypes;
import baubles.common.integration.ModCompatibility;
import baubles.common.network.PacketHandler;
import baubles.common.network.PacketOpenBaublesInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused") // gets used by Forge event handler
public class ClientEventHandler {

    @SubscribeEvent
    public void tooltipEvent(ItemTooltipEvent event) {
        if (!event.getItemStack().isEmpty() && event.getItemStack().hasCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null)) {
            IBauble bauble = event.getItemStack().getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
            IBaubleType bt = Objects.requireNonNull(bauble).getType(event.getItemStack());
            event.getToolTip().add(TextFormatting.GOLD + I18n.format("baubles.item.desc", I18n.format(bt.getTranslationKey())));
        }
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre event) {
        TextureMap map = event.getMap();
        BaubleTypes.registerTextures(event);
    }

    @SubscribeEvent
    public void playerTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START && Minecraft.getMinecraft().inGameHasFocus && ClientProxy.KEY_BAUBLES.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketOpenBaublesInventory());
        }
    }

    @SubscribeEvent
    public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && BaublesApi.getBaublesHandler(player).getSlots() > 0) {
            Gui gui = event.getGui();
            boolean normalInv = gui instanceof GuiInventory || ModCompatibility.CA$isCAInventory(gui);
            boolean creativeInv = gui instanceof GuiContainerCreative;
            boolean expandedInv = gui instanceof GuiPlayerExpanded;
            if (creativeInv) { // TODO Polish
                GuiContainerCreative container = (GuiContainerCreative) gui;
                event.getButtonList().add(new GuiBaublesButton(55, container, 95, 6, 10, 10, "button.baubles") {
                    @Override
                    public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                        this.visible = container.getSelectedTabIndex() == CreativeTabs.INVENTORY.getIndex();
                        if (this.visible) {
                            super.drawButton(mc, mouseX, mouseY, partialTicks);
                        }
                    }
                });
            } else if (normalInv || expandedInv) {
                GuiContainer container = (GuiContainer) event.getGui();
                event.getButtonList().add(new GuiBaublesButton(55, container, 64, 9, 10, 10,
                        normalInv ? "button.baubles" : "button.normal"));
            }
        }
    }
}
