package baubles.core;

import baubles.core.transformers.*;
import net.minecraft.launchwrapper.IClassTransformer;

@SuppressWarnings("unused")
public class BubblesTransformer implements IClassTransformer {

    private boolean isRLArtifact = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("artifacts.Artifacts")) this.isRLArtifact = ArtifactsTransformer.checkArtifacts(basicClass);
        basicClass = ArtifactsTransformer.transform(name, transformedName, basicClass, this.isRLArtifact); // Artifacts - Fix hardcoded stuff.
        basicClass = BotaniaTransformer.transform(name, transformedName, basicClass); // Botania - Fix hardcoded slots.
        basicClass = CorailTombstoneTransformer.transform(name, transformedName, basicClass); // Corail Tombstone - Fix drops on death.
        basicClass = CreativeInvTransformer.transform(name, transformedName, basicClass); // Minecraft - Make creative inventory delete all action delete items in bauble slots too.
        basicClass = EBWizardryTransformer.transform(name, transformedName, basicClass); // Electroblob's Wizardry - Fix bauble items not working.
        basicClass = EnchantmentTransformer.transform(name, transformedName, basicClass); // Minecraft - Apply enchants of bauble items.
        basicClass = PotionFingersTransformer.transform(name, transformedName, basicClass); // Potion Fingers - Fix hardcoded slots.
        basicClass = QualityToolsTransformer.transform(name, transformedName, basicClass); // Quality Tools - Change it to check bauble capability instead of super class and add support for custom bauble types.
        basicClass = ReliquaryTransformer.transform(name, transformedName, basicClass); // Reliquary - Support reliquary items.
        basicClass = SpartanWeaponryTransformer.transform(name, transformedName, basicClass); // Spartan Weaponry - Fix Quiver.
        basicClass = TrinketsAndBaublesTransformer.transform(name, transformedName, basicClass); // Trinkets and Baubles - Fix crash.
        basicClass = WearableBackpacksTransformer.transform(name, transformedName, basicClass); // Wearable Backpacks - Fix casting crash.
        return basicClass;
    }
}
