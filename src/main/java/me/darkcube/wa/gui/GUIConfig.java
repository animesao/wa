package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class GUIConfig {

    private final WastelandArtifacts plugin;
    private final Map<String, GUISettings> guis = new HashMap<>();

    public GUIConfig(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void load() {
        guis.clear();
        File file = new File(plugin.getDataFolder(), "gui.yml");
        if (!file.exists()) {
            plugin.saveResource("gui.yml", false);
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = yaml.getConfigurationSection("gui");
        if (sec == null) return;
        for (String id : sec.getKeys(false)) {
            boolean enabled = sec.getBoolean(id + ".enabled", true);
            String title = sec.getString(id + ".title", "<dark_gray>" + id);
            int rows = sec.getInt(id + ".rows", 6);
            guis.put(id, new GUISettings(enabled, title, rows));
        }
    }

    public boolean isEnabled(String id) {
        GUISettings s = guis.get(id);
        return s != null && s.enabled;
    }

    public String getTitle(String id, String def) {
        GUISettings s = guis.get(id);
        return s != null ? s.title : def;
    }

    public int getRows(String id, int def) {
        GUISettings s = guis.get(id);
        return s != null ? s.rows : def;
    }

    public static class GUISettings {
        public final boolean enabled;
        public final String title;
        public final int rows;

        public GUISettings(boolean enabled, String title, int rows) {
            this.enabled = enabled;
            this.title = title;
            this.rows = rows;
        }
    }
}
