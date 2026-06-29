package me.darkcube.wa.artifact.rarity;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RarityManager {

    private final WastelandArtifacts plugin;
    private final Map<String, RarityDef> rarities = new LinkedHashMap<>();

    public RarityManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        rarities.clear();
        File file = new File(plugin.getDataFolder(), "rarities.yml");
        if (!file.exists()) {
            plugin.saveResource("rarities.yml", false);
        }
        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(file, Map.class);
            Map<String, Object> raritiesRaw = (Map<String, Object>) root.get("rarities");
            if (raritiesRaw == null) {
                loadDefaults();
                return;
            }

            for (var entry : raritiesRaw.entrySet()) {
                String key = entry.getKey().toUpperCase();
                Map<String, Object> def = (Map<String, Object>) entry.getValue();
                if (def == null) continue;

                String displayName = (String) def.getOrDefault("displayName", key);
                String colorHex = (String) def.getOrDefault("color", "#FFFFFF");

                TextColor color;
                try {
                    color = TextColor.fromHexString(colorHex);
                } catch (Exception e) {
                    color = TextColor.color(0xFFFFFF);
                }

                TextDecoration decoration = TextDecoration.ITALIC;
                String decStr = (String) def.get("decoration");
                if (decStr != null) {
                    try {
                        decoration = TextDecoration.valueOf(decStr.toUpperCase());
                    } catch (Exception ignored) {}
                }

                int order = def.containsKey("order") ? ((Number) def.get("order")).intValue() : 0;

                rarities.put(key, new RarityDef(key, displayName, color, decoration, order));
            }

            plugin.getComponentLogger().info("<green>Загружено " + rarities.size() + " редкостей из конфига");
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки rarities.yml: " + e.getMessage());
            loadDefaults();
        }
    }

    private void loadDefaults() {
        for (Rarity r : Rarity.values()) {
            rarities.put(r.name(), new RarityDef(
                    r.name(), r.getDisplayName(), r.getColor(), r.getDecoration(), r.ordinal()
            ));
        }
    }

    public @NotNull RarityDef get(@NotNull String id) {
        return rarities.getOrDefault(id.toUpperCase(), rarities.values().iterator().next());
    }

    public @NotNull RarityDef get(@NotNull Rarity rarity) {
        return rarities.getOrDefault(rarity.name(), new RarityDef(
                rarity.name(), rarity.getDisplayName(), rarity.getColor(), rarity.getDecoration(), rarity.ordinal()
        ));
    }

    public @NotNull RarityDef getByOrder(int order) {
        for (RarityDef def : rarities.values()) {
            if (def.order() == order) return def;
        }
        return rarities.values().iterator().next();
    }

    public @Nullable RarityDef getByName(@NotNull String name) {
        return rarities.get(name.toUpperCase());
    }

    public String resolve(String configValue) {
        if (configValue == null) return "COMMON";
        // Пытаемся как ID редкости
        RarityDef def = getByName(configValue);
        if (def != null) return def.id();
        // Пытаемся как display name
        for (RarityDef d : rarities.values()) {
            if (d.displayName().equals(configValue)) return d.id();
        }
        return "COMMON";
    }

    public @NotNull Map<String, RarityDef> getAll() {
        return Collections.unmodifiableMap(rarities);
    }

    public record RarityDef(String id, String displayName, TextColor color, TextDecoration decoration, int order) {
        public String toMiniTag() {
            return "<color:" + color.asHexString() + ">" + displayName;
        }
    }
}
