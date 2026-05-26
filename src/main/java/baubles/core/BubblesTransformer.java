package baubles.core;

import baubles.core.transformers.*;
import net.minecraft.launchwrapper.IClassTransformer;

@SuppressWarnings("unused")
public class BubblesTransformer implements IClassTransformer {

    private boolean isRLArtifact = false;

    @Override
    public byte[] transform(String _name, String name, byte[] bytes) {
        if (name.startsWith("baubles.core.transformers.")) return bytes;
        if (name.equals("artifacts.Artifacts")) this.isRLArtifact = ArtifactsTransformer.checkArtifacts(bytes);
        ArmoredArmsTransformer.transform(_name, name, bytes);
        AstralSorceryTransformer.transform(_name, name, bytes);
        BetterAnimalsPlusTransformer.transform(_name, name, bytes);
        CosmeticArmorsTransformer.transform(_name, name, bytes);
        PlethoraTransformer.transform(_name, name, bytes);

        bytes = ArtifactsTransformer.transform(_name, name, bytes, this.isRLArtifact); // Artifacts - Fix hardcoded stuff.
        bytes = BotaniaTransformer.transform(_name, name, bytes); // Botania - Fix hardcoded slots.
        bytes = CorailTombstoneTransformer.transform(_name, name, bytes); // Corail Tombstone - Fix drops on death.
        bytes = CreativeInvTransformer.transform(_name, name, bytes); // Minecraft - Make creative inventory delete all action delete items in bauble slots too.
        bytes = EBWizardryTransformer.transform(_name, name, bytes); // Electroblob's Wizardry - Fix bauble items not working.
        bytes = EnchantmentTransformer.transform(_name, name, bytes); // Minecraft - Apply enchants of bauble items.
        bytes = PotionFingersTransformer.transform(_name, name, bytes); // Potion Fingers - Fix hardcoded slots.
        bytes = QualityToolsTransformer.transform(_name, name, bytes); // Quality Tools - Change it to check bauble capability instead of super class and add support for custom bauble types.
        bytes = ReliquaryTransformer.transform(_name, name, bytes); // Reliquary - Support reliquary items.
        bytes = RootsTransformer.transform(_name, name, bytes);
        bytes = SpartanWeaponryTransformer.transform(_name, name, bytes); // Spartan Weaponry - Fix Quiver.
        bytes = TrinketsAndBaublesTransformer.transform(_name, name, bytes); // Trinkets and Baubles - Fix crash.
        bytes = WearableBackpacksTransformer.transform(_name, name, bytes); // Wearable Backpacks - Fix casting crash.
        bytes = WizardryTransformer.transform(_name, name, bytes); // Wizardry - Fix bauble items not working.
        return bytes;
    }
}
