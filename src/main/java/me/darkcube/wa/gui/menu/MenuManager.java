package me.darkcube.wa.gui.menu;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class MenuManager {

    private final WastelandArtifacts plugin;
    private final Map<String, MenuConfig> menus = new HashMap<>();

    public MenuManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        menus.clear();
        File guiDir = new File(plugin.getDataFolder(), "gui");
        if (!guiDir.exists()) {
            guiDir.mkdirs();
        }
        saveDefaults();

        File[] files = guiDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                String id = file.getName().replace(".yml", "");
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                loadMenu(id, yaml);
            } catch (Exception e) {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки GUI '" + file.getName() + "': " + e.getMessage());
            }
        }
        plugin.getComponentLogger().info("<green>Загружено GUI: " + menus.size());
    }

    private void saveDefaults() {
        for (String name : List.of("socket", "bag", "upgrade", "achievements",
                "admin_items", "quick_access", "dungeon_loot", "collection")) {
            try {
                plugin.saveResource("gui/" + name + ".yml", false);
            } catch (Exception ignored) {}
        }
    }

    private void loadMenu(String id, YamlConfiguration yaml) {
        boolean enabled = yaml.getBoolean("enabled", true);
        String title = yaml.getString("title", "<dark_gray>" + id);
        int rows = yaml.getInt("rows", 6);

        List<MenuItem> items = new ArrayList<>();
        ConfigurationSection itemsSec = yaml.getConfigurationSection("items");
        if (itemsSec != null) {
            for (String itemId : itemsSec.getKeys(false)) {
                try {
                    String mat = itemsSec.getString(itemId + ".material", "STONE");
                    int cmd = itemsSec.getInt(itemId + ".custom-model-data", 0);
                    String name = itemsSec.getString(itemId + ".name", "");
                    List<String> lore = itemsSec.getStringList(itemId + ".lore");
                    List<String> actions = itemsSec.getStringList(itemId + ".actions");
                    boolean fill = itemsSec.getBoolean(itemId + ".fill", false);
                    boolean dynamic = itemsSec.getBoolean(itemId + ".dynamic", false);

                    List<Integer> slots = new ArrayList<>();
                    if (fill) {
                        // fill handled separately
                    } else if (itemsSec.get(itemId + ".slot") != null) {
                        slots.add(itemsSec.getInt(itemId + ".slot"));
                    } else {
                        slots.addAll(itemsSec.getIntegerList(itemId + ".slots"));
                    }

                    items.add(new MenuItem(itemId, mat, cmd, name, lore, slots, actions, fill, dynamic));
                } catch (Exception e) {
                    plugin.getComponentLogger().warn("<red>Ошибка загрузки предмета '" + itemId + "' в GUI '" + id + "': " + e.getMessage());
                }
            }
        }

        menus.put(id, new MenuConfig(id, title, rows, items, enabled));
    }

    public MenuConfig getMenu(String id) {
        return menus.get(id);
    }

    public boolean isEnabled(String id) {
        MenuConfig cfg = menus.get(id);
        return cfg != null && cfg.enabled;
    }

    public void reload() {
        loadAll();
    }
}
