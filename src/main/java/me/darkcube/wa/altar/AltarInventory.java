package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AltarInventory {

    private final WastelandArtifacts plugin;
    private final Map<String, AltarData> storage = new ConcurrentHashMap<>();
    private File saveFile;

    public AltarInventory(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void init() {
        saveFile = new File(plugin.getDataFolder(), "altar_data.json");
        load();
    }

    public AltarData getOrCreate(Location loc) {
        String key = makeKey(loc);
        return storage.computeIfAbsent(key, k -> new AltarData(loc, getMaxSlots()));
    }

    public AltarData get(Location loc) {
        return storage.get(makeKey(loc));
    }

    public void setSlot(Location loc, int slot, ItemStack item) {
        getOrCreate(loc).setSlot(slot, item);
        save();
    }

    public ItemStack getSlot(Location loc, int slot) {
        AltarData data = get(loc);
        return data != null ? data.getSlot(slot) : null;
    }

    public ItemStack removeSlot(Location loc, int slot) {
        AltarData data = get(loc);
        if (data == null) return null;
        ItemStack removed = data.removeSlot(slot);
        save();
        return removed;
    }

    public ItemStack[] getAllSlots(Location loc) {
        AltarData data = get(loc);
        return data != null ? data.getAllSlots() : new ItemStack[getMaxSlots()];
    }

    public int countItems(Location loc) {
        AltarData data = get(loc);
        return data != null ? data.countItems() : 0;
    }

    public void clear(Location loc) {
        AltarData data = get(loc);
        if (data != null) {
            data.clear();
            save();
        }
    }

    public void removeAltar(Location loc) {
        storage.remove(makeKey(loc));
        save();
    }

    public void save() {
        if (saveFile == null) return;
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            for (AltarData data : storage.values()) {
                list.add(data.serialize());
            }
            plugin.getConfigManager().getYamlMapper().writerWithDefaultPrettyPrinter()
                    .writeValue(saveFile, Map.of("altars", list));
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка сохранения altar_data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        if (saveFile == null || !saveFile.exists()) return;
        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper()
                    .readValue(saveFile, Map.class);
            List<Map<String, Object>> list = (List<Map<String, Object>>) root.get("altars");
            if (list != null) {
                storage.clear();
                for (Map<String, Object> map : list) {
                    AltarData data = AltarData.deserialize(map, getMaxSlots());
                    storage.put(data.getLocationKey(), data);
                }
            }
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки altar_data: " + e.getMessage());
        }
    }

    private String makeKey(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    private int getMaxSlots() {
        return plugin.getAltarManager().getConfig().settings.maxSlots;
    }
}
