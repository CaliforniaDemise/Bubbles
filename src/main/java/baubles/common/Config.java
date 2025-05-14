package baubles.common;

import baubles.api.IBaubleType;
import baubles.api.cap.InjectableBauble;
import baubles.common.init.BaubleTypes;
import com.google.gson.*;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static baubles.api.BaubleType.*;

public class Config {

    public static final String CATEGORY_COMPATIBILITY = "compatibility";

    public static Configuration config;
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static File CONFIG_DIR;

    private static final Map<ResourceLocation, Object2IntMap<IBaubleType>> TYPE_MAP = new HashMap<>();
    private static final Map<ItemStack, O2IPair<IBaubleType>> STACK_MAP = new Object2ObjectOpenCustomHashMap<>(new ItemStackStrategy());

    // Configuration Options
    public static boolean renderBaubles = true;
    public static boolean expandedMode = false;

    public static boolean compat_actuallyadditions = true;
    public static boolean compat_reliquary = true;

    public static void initialize(File configFile) {
        initConfig(configFile);
        CONFIG_DIR = configFile.getParentFile();
    }

    @Nullable
    public static Pair<IBaubleType, Integer> getItemType(ItemStack stack) {
        if (STACK_MAP.isEmpty()) return null;
        O2IPair<IBaubleType> pair = STACK_MAP.get(stack);
        if (pair != null) return Pair.of(pair.left, pair.right);
        return null;
    }

    @Nullable
    public static Object2IntMap<IBaubleType> getDefaultSlotMap(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return null;
        File entities = new File(CONFIG_DIR, Baubles.MODID + "/entities");
        if (!entities.exists()) {
            Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
            int multiplier = expandedMode ? 2 : 1;
            map.put(AMULET, multiplier);
            map.put(RING, 2 * multiplier);
            map.put(BELT, multiplier);
            map.put(HEAD, multiplier);
            map.put(BODY, multiplier);
            map.put(CHARM, multiplier);
            return map;
        }
        ResourceLocation location;
        if (entity instanceof EntityPlayer) location = new ResourceLocation("player");
        else location = EntityList.getKey(entity);
        if (location == null) return null;
        {
            Object2IntMap<IBaubleType> map = TYPE_MAP.get(location);
            if (map != null) return map;
        }
        try {
            if (entity instanceof EntityPlayer) {
                Object2IntMap<IBaubleType> map = checkSlotsJson(CONFIG_DIR);
                if (map != null) {
                    TYPE_MAP.put(location, map);
                    return map;
                }
            }
            File file = new File(CONFIG_DIR, Baubles.MODID + "/entities/" + location.toString().replace(':', '/') + ".json");
            if (file.exists()) {
                Object2IntMap<IBaubleType> map = readSlotMapJson(file);
                TYPE_MAP.put(location, map);
                return map;
            }
            else if (entity instanceof EntityPlayer) {
                Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
                int multiplier = expandedMode ? 2 : 1;
                map.put(AMULET, multiplier);
                map.put(RING, 2 * multiplier);
                map.put(BELT, multiplier);
                map.put(HEAD, multiplier);
                map.put(BODY, multiplier);
                map.put(CHARM, multiplier);
                TYPE_MAP.put(location, map);
                return map;
            }
            return null;
        }
        catch (IOException e) { throw new RuntimeException("Error occurred while reading bauble slot json", e); }
    }

    private static Object2IntMap<IBaubleType> readSlotMapJson(File file) throws IOException {
        Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
        InputStreamReader reader = new InputStreamReader(Files.newInputStream(file.toPath()));
        JsonObject object = GSON.fromJson(reader, JsonObject.class);
        reader.close();
        object.entrySet().forEach(entry -> {
            IBaubleType type = BaubleTypes.get(getLocation(entry.getKey()));
            if (type == null) {
                Baubles.log.warn("Could not find {} type while reading file {}", entry.getKey(), file.getName());
                return;
            }
            if (!entry.getValue().isJsonPrimitive()) {
                Baubles.log.warn("Bauble type {}'s slot count is not integer in file {}", entry.getKey(), file.getName());
            }
            map.put(type, entry.getValue().getAsInt());
        });
        return map;
    }

    @Nullable
    private static Object2IntMap<IBaubleType> checkSlotsJson(File configFile) throws IOException {
        File slots = new File(configFile.getParentFile(), Baubles.MODID + "/slots.json");
        if (slots.exists()) {
            Baubles.log.info("Found {}, generating new jsons from it.", slots.getName());
            final String
                    NORMAL = "[\n \"amulet\",\n \"ring\",\n \"ring\",\n \"belt\",\n \"head\",\n \"body\",\n \"charm\"\n]",
                    EXPANDED = "[\n \"amulet\",\n \"amulet\",\n \"ring\",\n \"ring\",\n \"ring\",\n \"ring\",\n \"belt\",\n \"belt\",\n \"head\",\n \"head\",\n \"body\",\n \"body\",\n \"charm\",\n \"charm\"\n]";
            byte[] fileByte = Files.readAllBytes(slots.toPath());
            if (Arrays.equals(fileByte, NORMAL.getBytes(StandardCharsets.UTF_8)) || Arrays.equals(fileByte, EXPANDED.getBytes(StandardCharsets.UTF_8))) {
                slots.delete();
                return null;
            }
            JsonArray array = GSON.fromJson(new String(fileByte, StandardCharsets.UTF_8), JsonArray.class);
            Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
            for (int i = 0; i < array.size(); i++) {
                String slot = array.get(i).getAsString();
                ResourceLocation location;
                if (!slot.contains(":")) location = new ResourceLocation(Baubles.MODID, slot);
                else location = new ResourceLocation(slot);
                IBaubleType type = BaubleTypes.get(location);
                if (type == null) {
                    Baubles.log.error("Could not find bauble type from {}", location);
                    continue;
                }
                map.put(type, map.get(type) + 1);
            }
            return map;
        }
        return null;
    }

    public static void initDefaultBaubles() {
        File types = new File(CONFIG_DIR, Baubles.MODID + "/types");
        if (!types.exists()) return;
        try (Stream<Path> stream = Files.walk(types.toPath())) {
            stream.filter(p -> !p.toFile().isDirectory()).forEach(p -> {
                ResourceLocation location;
                {
                    String path = p.getName(p.getNameCount() - 1).toString();
                    path = path.substring(0, path.length() - 5);
                    location = new ResourceLocation(p.getName(p.getNameCount() - 2).toString(), path);
                }
                IBaubleType type = BaubleTypes.get(location);
                if (type == null) {
                    Baubles.log.error("Could not find bauble type {} while reading file {}", location, p.getFileName().toFile().getName());
                    return;
                }
                try { checkTypeJson(type, p.toFile()); } catch (IOException e) { throw new RuntimeException(e); }
            });
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private static void checkTypeJson(IBaubleType type, File typeFile) throws IOException {
        InputStreamReader reader = new InputStreamReader(Files.newInputStream(typeFile.toPath()));
        JsonObject object = GSON.fromJson(reader, JsonObject.class);
        if (object.has("items")) {
            for (JsonElement e : object.getAsJsonArray("items")) {
                ResourceLocation location;
                int metadata = 0;
                int value = 0;
                String str = e.getAsString();
                String[] split = str.split("#");
                {
                    if (split.length != 1) {
                        String s = split[1];
                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            switch (Character.toLowerCase(c)) {
                                case 'a': value |= InjectableBauble.ARMOR; break;
                                case 'i': value |= InjectableBauble.INVENTORY; break;
                                case 'p': value |= InjectableBauble.INVENTORY | InjectableBauble.PASSIVE; break;
                            }
                        }
                    }
                }
                {
                    split = split[0].split(":");
                    if (split.length == 1) location = new ResourceLocation(split[0]);
                    else location = new ResourceLocation(split[0], split[1]);
                    if (split.length > 2) {
                        if (split[2].equals("*")) metadata = OreDictionary.WILDCARD_VALUE;
                        else metadata = Integer.parseInt(split[2]);
                    }
                }
                Item item = ForgeRegistries.ITEMS.getValue(location);
                if (item == null || item == Items.AIR) {
                    Baubles.log.error("Could not find item from " + e.getAsString());
                    continue;
                }
                ItemStack stack = new ItemStack(item, 1, metadata);
                STACK_MAP.put(stack, new O2IPair<>(type, value));
            }
        }
        if (object.has("ores")) {
            for (JsonElement e : object.getAsJsonArray("ores")) {
                int value = 0;
                String[] split = e.getAsString().split("#");
                {
                    if (split.length != 1) {
                        String s = split[1];
                        for (int i = 0; i < s.length(); i++) {
                            char c = s.charAt(i);
                            switch (Character.toLowerCase(c)) {
                                case 'a': value |= InjectableBauble.ARMOR; break;
                                case 'i': value |= InjectableBauble.INVENTORY; break;
                                case 'p': value |= InjectableBauble.INVENTORY | InjectableBauble.PASSIVE; break;
                            }
                        }
                    }
                }
                NonNullList<ItemStack> list = OreDictionary.getOres(split[0]);
                if (list == null || list.isEmpty()) continue;
                for (ItemStack stack : list) {
                    STACK_MAP.put(stack, new O2IPair<>(type, value));
                }
            }
        }
    }

    private static ResourceLocation getLocation(String regName) {
        ResourceLocation location;
        if (!regName.contains(":")) location = new ResourceLocation(Baubles.MODID, regName);
        else location = new ResourceLocation(regName);
        return location;
    }

    private static void initConfig(File file) {
        config = new Configuration(file);
        config.load();
        loadConfigs();
        MinecraftForge.EVENT_BUS.register(ConfigChangeListener.class);
        config.save();
    }

    public static void loadConfigs() {
        expandedMode = config.getBoolean("baubleExpanded.enabled", Configuration.CATEGORY_GENERAL, expandedMode, "Set this to true to have more slots than normal.");
        renderBaubles = config.getBoolean("baubleRender.enabled", Configuration.CATEGORY_CLIENT, renderBaubles, "Set this to false to disable rendering of baubles in the player.");
        compat_actuallyadditions = config.getBoolean("actuallyAdditions.enabled", CATEGORY_COMPATIBILITY, compat_actuallyadditions, "Make some of AA's items baubles.");
        compat_reliquary = config.getBoolean("reliquary.enabled", CATEGORY_COMPATIBILITY, compat_reliquary, "Make some of Reliquary's items baubles.");
        if (config.hasChanged()) config.save();
    }

    public static void save() {
        config.save();
    }

    public static class ConfigChangeListener {
        @SuppressWarnings("unused")
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
            String modId = eventArgs.getModID();
            if (modId.equals(Baubles.MODID)) loadConfigs();
        }
    }

    private static class ItemStackStrategy implements Hash.Strategy<ItemStack> {

        @Override
        public int hashCode(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return 0;
            return stack.getItem().hashCode() << 13 ^ stack.getMetadata() << 31;
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            if (a == null || b == null) return false;
            if (a.isEmpty() && b.isEmpty()) return true;
            return ItemStack.areItemsEqual(a, b);
        }
    }

    private static class O2IPair<K> {
        final K left;
        final int right;
        O2IPair(K left, int right) {
            this.left = left;
            this.right = right;
        }
    }
}
