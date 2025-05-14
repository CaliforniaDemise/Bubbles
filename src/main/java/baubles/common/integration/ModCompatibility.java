package baubles.common.integration;

import baubles.api.BaubleType;
import baubles.api.IBaubleType;
import baubles.api.cap.InjectableBauble;
import baubles.client.gui.GuiPlayerExpanded;
import baubles.common.Config;
import lain.mods.cos.client.GuiCosArmorInventory;
import me.paulf.wings.server.item.ItemWings;
import mod.acgaming.universaltweaks.config.UTConfigTweaks;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;
import snownee.minieffects.api.InjectedMiniEffectsHolder;
import snownee.minieffects.api.Vec2i;
import snownee.minieffects.handlers.MiniEffectsOffsets;
import yalter.mousetweaks.MTConfig;
import yalter.mousetweaks.Main;

import static baubles.api.cap.InjectableBauble.*;

public class ModCompatibility {

    public static final String MOUSE_TWEAKS = "mousetweaks";
    public static final String NO_RECIPE_BOOK = "norecipebook";
    public static final String UNIVERSAL_TWEAKS = "universaltweaks";
    public static final String COSMETIC_ARMOR = "cosmeticarmorreworked";
    public static final String ACTUALLY_ADDITIONS = "actuallyadditions";
    public static final String WINGS = "wings";
    public static final String MINI_EFFECTS = "minieffects";

    public static final boolean MOUSE_TWEAKS_LOADED = Loader.isModLoaded(MOUSE_TWEAKS);
    public static final boolean NO_RECIPE_BOOK_LOADED = Loader.isModLoaded(NO_RECIPE_BOOK);
    public static final boolean UNIVERSAL_TWEAKS_LOADED = Loader.isModLoaded(UNIVERSAL_TWEAKS);
    public static final boolean COSMETIC_ARMOR_LOADED = Loader.isModLoaded(COSMETIC_ARMOR);
    public static final boolean WINGS_LOADED = Loader.isModLoaded(WINGS);
    public static final boolean MINI_EFFECTS_LOADED = Loader.isModLoaded(MINI_EFFECTS);

    private static boolean isLoaded = false;
    private static boolean MT_isOld = false;

    private static void initContainers() {
        if (isLoaded) return;
        isLoaded = true;
        for (ModContainer container : Loader.instance().getActiveModList()) {
            if (container.getModId().equals("mousetweaks")) MT_isOld = container.getVersion().startsWith("2.");
        }
    }

    // Mouse Tweaks scrolling
    public static boolean MT$shouldScroll(Slot slot) {
        if (!MOUSE_TWEAKS_LOADED) return true;
        return !MT$getWheelTweak() || slot == null || !slot.getHasStack();
    }

    private static boolean MT$getWheelTweak() {
        initContainers();
        return MT_isOld ? Main.config.wheelTweak : MTConfig.wheelTweak;
    }

    // No Recipe Book
    public static boolean RecipeBook$isDisabled() {
        boolean disabled = NO_RECIPE_BOOK_LOADED;
        if (!disabled && UNIVERSAL_TWEAKS_LOADED) disabled = UTConfigTweaks.MISC.utRecipeBookToggle;
        return disabled;
    }

    // Cosmetic Armor
    public static boolean CA$isCAInventory(Gui gui) {
        return COSMETIC_ARMOR_LOADED && gui instanceof GuiCosArmorInventory;
    }

    // Wings
    public static void Wings$applyEvents() {
        if (WINGS_LOADED) {
            MinecraftForge.EVENT_BUS.register(StupidWingsEvents.class);
        }
    }

    // Mini Effects
    public static boolean ME$shouldMoveLeft(InventoryEffectRenderer gui) {
        if (!MINI_EFFECTS_LOADED) return true;
        InjectedMiniEffectsHolder holder = (InjectedMiniEffectsHolder) gui;
        return holder.miniEff$getInjected().shouldExpand(gui.mc, Mouse.getX(), Mouse.getY());
    }

    public static void ME$applyOffset() {
        if (!MINI_EFFECTS_LOADED) return;
        MiniEffectsOffsets.ADDITIONAL.put(GuiPlayerExpanded.class, new Vec2i(-28, 0));
    }

    // Compatibility
    public static InjectableBauble getBaubleToInject(ItemStack stack) {
        Pair<IBaubleType, Integer> type = Config.getItemType(stack);
        if (type != null) return new InjectableBauble(type.getKey(), type.getValue());
        Item item = stack.getItem();
        ResourceLocation loc = item.getRegistryName();
        if (loc == null) return null;
        if (loc.getNamespace().equals(ACTUALLY_ADDITIONS)) {
            if (loc.getNamespace().endsWith("suction_ring")) return new InjectableBauble(BaubleType.RING, INVENTORY | PASSIVE);
            if (loc.getPath().endsWith("potion_ring_advanced")) return new InjectableBauble(BaubleType.RING, INVENTORY | PASSIVE);
            if (loc.getPath().startsWith("item_battery")) return new InjectableBauble(BaubleType.RING, INVENTORY | PASSIVE);
            if (loc.getPath().endsWith("wings_of_the_bats")) return new InjectableBauble(BaubleType.TRINKET, INVENTORY | PASSIVE);
        }
        if (loc.getNamespace().equals(WINGS) && item instanceof ItemWings) return new InjectableBauble(BaubleType.BODY, ARMOR);
        return null;
    }
}