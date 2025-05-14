package baubles.common;

import baubles.api.IBaubleType;
import baubles.common.init.BaubleTypes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;

import static baubles.api.BaubleType.*;

public class Config {

    public static Configuration config;
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static File CONFIG_DIR;

    // Configuration Options
    public static boolean renderBaubles = true;
    public static boolean expandedMode = false;

    public static void initialize(File configFile) {
        initConfig(configFile);
        CONFIG_DIR = configFile.getParentFile();
    }

    @Nullable
    public static Object2IntMap<IBaubleType> getDefaultSlotMap(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) return null;
        try {
            ResourceLocation location;
            if (entity instanceof EntityPlayer) {
                Object2IntMap<IBaubleType> map = checkOldJson(CONFIG_DIR);
                if (map != null) return map;
                location = new ResourceLocation("player");
            }
            else location = EntityList.getKey(entity);
            if (location == null) return null;
            File file = new File(CONFIG_DIR, Baubles.MODID + "/entities/" + location.toString().replace(':', '/') + ".json");
            if (file.exists()) return readSlotMapJson(file);
            else if (entity instanceof EntityPlayer) {
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
            return null;
        }
        catch (IOException e) { throw new RuntimeException("Error occurred while reading bauble slot json", e); }
    }

    private static Object2IntMap<IBaubleType> readSlotMapJson(File file) throws IOException {
        Object2IntMap<IBaubleType> map = new Object2IntOpenHashMap<>();
        JsonObject object = GSON.fromJson(new InputStreamReader(Files.newInputStream(file.toPath())), JsonObject.class);
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
    private static Object2IntMap<IBaubleType> checkOldJson(File configFile) throws IOException {
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
}
