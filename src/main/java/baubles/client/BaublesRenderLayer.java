/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * <p>
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 * <p>
 * File Created @ [Aug 27, 2014, 8:55:00 PM (GMT)]
 */
package baubles.client;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import baubles.api.render.IRenderBauble.RenderType;
import baubles.common.Config;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BaublesRenderLayer implements LayerRenderer<EntityPlayer> {

    @Override
    public void doRenderLayer(@NotNull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!Config.renderBaubles || player.getActivePotionEffect(MobEffects.INVISIBILITY) != null) return;
        IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
        dispatchRenders(inv, player, RenderType.BODY, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, partialTicks);
        float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
        float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
        float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.rotate(yawOffset, 0, -1, 0);
        GlStateManager.rotate(yaw - 270, 0, 1, 0);
        GlStateManager.rotate(pitch, 0, 0, 1);
        dispatchRenders(inv, player, RenderType.HEAD, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, partialTicks);
        GlStateManager.popMatrix();
    }

    private void dispatchRenders(IBaublesItemHandler inv, EntityPlayer player, RenderType type, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, float partialTicks) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                IBauble bauble = Objects.requireNonNull(stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null));
                if (bauble.shouldRender(stack, player)) {
                    IRenderBauble renderBauble = null;
                    if (bauble instanceof IRenderBauble) renderBauble = (IRenderBauble) bauble;
                    else if (stack.getItem() instanceof IRenderBauble) renderBauble = (IRenderBauble) stack.getItem();
                    if (renderBauble == null)
                        throw new RuntimeException("Render Bauble is null for " + stack.getItem().getRegistryName());
                    GlStateManager.pushMatrix();
                    GlStateManager.color(1F, 1F, 1F, 1F);
                    renderBauble.onPlayerBaubleRender(stack, player, type, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, partialTicks);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}