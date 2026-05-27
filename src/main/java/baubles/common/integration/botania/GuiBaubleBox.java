package baubles.common.integration.botania;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.gui.ContainerBaubles;
import baubles.client.gui.GuiSlotButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.client.gui.box.InventoryBaubleBox;
import vazkii.botania.client.lib.LibResources;

public class GuiBaubleBox extends vazkii.botania.client.gui.box.GuiBaubleBox {

    private static final ResourceLocation texture = new ResourceLocation(LibResources.GUI_BAUBLE_BOX);

    private final IBaublesItemHandler handler;
    private final ContainerBaubles container;

    public GuiBaubleBox(EntityPlayer player, InventoryBaubleBox box) {
        super(player, box);
        this.handler = BaublesApi.getBaublesHandler(player);
        this.container = (ContainerBaubles) this.inventorySlots;
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiSlotButton up = new GuiSlotButton(56, container, guiLeft - 26, guiTop - 9, 27, 14, false);
        GuiSlotButton down = new GuiSlotButton(57, container, guiLeft - 26, guiTop + 7 + (18 * Math.min(8, handler.getSlots())), 27, 14, true);
        up.visible = handler.getSlots() > 8;
        down.visible = up.visible;
        buttonList.add(up);
        buttonList.add(down);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int maxSlots = Math.min(8, handler.getSlots());
        if (maxSlots > 0) {
            if (maxSlots == 1) {
                this.drawTexturedModalRect(this.guiLeft - 28, this.guiTop, 176, 34, 28, 28);
            } else {
                for (int i = 0; i < maxSlots; i++) {
                    int textureY = 39;
                    int height = 20;
                    int y = this.guiTop + (i * 18);
                    if (i == 0) {
                        textureY = 34;
                        height += 4;
                    } else y += 5;
                    if (i == maxSlots - 1) height += 4;
                    this.drawTexturedModalRect(this.guiLeft - 28, y, 176, textureY, 28, height);
                }
            }
        }
    }
}
