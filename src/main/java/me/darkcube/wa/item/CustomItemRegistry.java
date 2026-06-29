package me.darkcube.wa.item;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomItemRegistry {

    private final WastelandArtifacts plugin;
    private final Map<String, CustomItemDef> items = new LinkedHashMap<>();

    public CustomItemRegistry(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "custom_items.yml");
        if (!file.exists()) {
            plugin.saveResource("custom_items.yml", false);
        }
        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(file, Map.class);
            Object itemsRaw = root.get("items");

            items.clear();

            if (itemsRaw instanceof List) {
                // List format: - { id: ..., material: ... }
                List<Map<String, Object>> itemsList = (List<Map<String, Object>>) itemsRaw;
                for (Map<String, Object> def : itemsList) {
                    String id = (String) def.get("id");
                    if (id == null) continue;
                    items.put(id, new CustomItemDef(
                            id,
                            (String) def.get("material"),
                            (String) def.get("name"),
                            (List<String>) def.get("lore"),
                            def.containsKey("customModelData") ? ((Number) def.get("customModelData")).intValue() : 0,
                            (String) def.get("rarity")
                    ));
                }
            } else if (itemsRaw instanceof Map) {
                // Map format: id: { material: ..., name: ... }
                Map<String, Object> itemsMap = (Map<String, Object>) itemsRaw;
                for (var entry : itemsMap.entrySet()) {
                    Map<String, Object> def = (Map<String, Object>) entry.getValue();
                    if (def == null) continue;
                    items.put(entry.getKey(), new CustomItemDef(
                            entry.getKey(),
                            (String) def.get("material"),
                            (String) def.get("name"),
                            (List<String>) def.get("lore"),
                            def.containsKey("customModelData") ? ((Number) def.get("customModelData")).intValue() : 0,
                            (String) def.get("rarity")
                    ));
                }
            }

            plugin.getComponentLogger().info("<green>Загружено " + items.size() + " кастомных предметов");
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки custom_items.yml: " + e.getMessage());
        }
    }

    public @Nullable ItemStack create(@NotNull String id) {
        CustomItemDef def = items.get(id);
        if (def == null) return null;
        return def.build();
    }

    public @Nullable ItemStack create(@NotNull String id, int amount) {
        ItemStack item = create(id);
        if (item != null) item.setAmount(amount);
        return item;
    }

    public @Nullable CustomItemDef getDef(@NotNull String id) {
        return items.get(id);
    }

    public @NotNull Map<String, CustomItemDef> getAll() {
        return Collections.unmodifiableMap(items);
    }

    public boolean isCustomItem(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return false;
        for (CustomItemDef def : items.values()) {
            if (item.isSimilar(def.build())) return true;
        }
        return false;
    }

    public @Nullable String getId(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return null;
        for (var entry : items.entrySet()) {
            if (item.isSimilar(entry.getValue().build())) return entry.getKey();
        }
        return null;
    }

    public static class CustomItemDef {
        public final String id;
        public final Material material;
        public final String name;
        public final List<String> lore;
        public final int customModelData;
        public final String rarity;

        public CustomItemDef(String id, String material, String name,
                              List<String> lore, int customModelData, String rarity) {
            this.id = id;
            this.material = Material.matchMaterial(material != null ? material : "STONE");
            this.name = name;
            this.lore = lore != null ? lore : Collections.emptyList();
            this.customModelData = customModelData;
            this.rarity = rarity != null ? rarity : "COMMON";
        }

        public ItemStack build() {
            ItemStack item = new ItemStack(material != null ? material : Material.STONE);
            ItemMeta meta = item.getItemMeta();
            if (name != null) {
                meta.displayName(MiniMessage.miniMessage().deserialize(name));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(l -> MiniMessage.miniMessage().deserialize(l))
                        .toList());
            }
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            item.setItemMeta(meta);
            return item;
        }
    }
}
