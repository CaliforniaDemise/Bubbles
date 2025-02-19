package baubles.core;

import baubles.core.transformers.*;
import net.minecraft.launchwrapper.IClassTransformer;

@SuppressWarnings("unused")
public class BubblesTransformer implements IClassTransformer {

    private boolean isRLArtifact = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        switch (transformedName) {
            // Minecraft - Apply baubles enchantments
            case "net.minecraft.enchantment.Enchantment": return EnchantmentTransformer.transformEnchantment(basicClass);
            case "net.minecraft.item.Item": return EnchantmentTransformer.transformItem(basicClass);

            // Botania - Make slots not hardcoded
            case "vazkii.botania.common.item.equipment.bauble.ItemDivaCharm": return BotaniaTransformer.transformItemDivaCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTiara": return BotaniaTransformer.transformItemTiara(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemGoddessCharm": return BotaniaTransformer.transformItemGoddessCharm(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemHolyCloak": return BotaniaTransformer.transformItemHolyCloak(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemMonocle": return BotaniaTransformer.transformItemMonocle(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemTravelBelt": return BotaniaTransformer.transformItemTravelBelt(basicClass);
            case "vazkii.botania.common.item.equipment.bauble.ItemWaterRing": return BotaniaTransformer.transformItemWaterRing(basicClass);

            // Potion Fingers - Vazkii moment
            case "vazkii.potionfingers.ItemRing": return PotionFingersTransformer.transformItemRing(basicClass);

            // Quality Tools - Make it check if item has bauble capability and make it work with custom bauble types. And also make it work with slot definitions.
            case "com.tmtravlr.qualitytools.baubles.BaublesHandler": return QualityToolsTransformer.transformBaublesHandler(basicClass);

            // Wearable Backpacks - Fix a crash
            case "net.mcft.copy.backpacks.api.BackpackHelper": return WearableBackpacksTransformer.transformBackpackHelper(basicClass);

            // Trinkets And Baubles - Fix crashes
            case "xzeroair.trinkets.util.compat.baubles.BaublesHelper": return TrinketsAndBaublesTransformer.transformBaublesHelper(basicClass);
            case "xzeroair.trinkets.client.gui.TrinketGuiButton": return TrinketsAndBaublesTransformer.transformTrinketGuiButton(basicClass);
            case "xzeroair.trinkets.container.TrinketInventoryContainer": return TrinketsAndBaublesTransformer.transformTrinketInventoryContainer(basicClass);

            // RLArtifacts - RLCraft moment
            case "artifacts.Artifacts": this.isRLArtifact = ArtifactsTransformer.checkArtifacts(basicClass); return basicClass;
            case "artifacts.client.model.layer.LayerAmulet": return ArtifactsTransformer.transformLayerAmulet(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerBelt": return ArtifactsTransformer.transformLayerBelt(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerCloak": return ArtifactsTransformer.transformLayerCloak(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerDrinkingHat": return ArtifactsTransformer.transformLayerDrinkingHat(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerGloves": return ArtifactsTransformer.transformLayerGloves(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerNightVisionGoggles": return ArtifactsTransformer.transformLayerNightVisionGoggles(basicClass, this.isRLArtifact);
            case "artifacts.client.model.layer.LayerSnorkel": return ArtifactsTransformer.transformLayerSnorkel(basicClass, this.isRLArtifact);
            case "artifacts.common.item.BaubleAmulet": return ArtifactsTransformer.transformBaubleAmulet(basicClass, this.isRLArtifact);
            case "artifacts.common.item.BaubleBottledCloud": return ArtifactsTransformer.transformBaubleBottledCloud(basicClass); // Non-RL

            default: return basicClass;
        }
    }
}
