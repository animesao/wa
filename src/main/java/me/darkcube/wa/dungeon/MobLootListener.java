package me.darkcube.wa.dungeon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MobLootListener implements Listener {

    private final WastelandArtifacts plugin;
    private final Random random = new Random();
    private Map<String, MobConfig> mobConfigs = new HashMap<>();

    public MobLootListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void reloadConfig() {
        loadConfig();
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "mob_loot.yml");
        if (!file.exists()) {
            plugin.saveResource("mob_loot.yml", false);
        }
        try {
            ObjectMapper mapper = plugin.getConfigManager().getYamlMapper();
            MobLootRoot root = mapper.readValue(file, MobLootRoot.class);
            mobConfigs = root.mobs;
            plugin.getComponentLogger().info("<green>Загружено " + mobConfigs.size() + " конфигов дропа мобов");
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки mob_loot.yml: " + e.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        // 1) Пробуем найти по EntityType.name() (ванильные мобы)
        MobConfig config = mobConfigs.get(entity.getType().name());

        // 2) Если не нашли — ищем по кастомному имени (MythicMobs, EliteMobs и т.д.)
        if (config == null) {
            config = findByCustomName(entity);
        }

        // 3) Если не нашли — ищем по scoreboard tags (другие плагины)
        if (config == null) {
            config = findByScoreboardTags(entity);
        }

        if (config == null || !config.enabled) return;

        for (var drop : config.drops) {
            if (random.nextDouble() > drop.chance) continue;
            int amount = drop.minAmount + random.nextInt(drop.maxAmount - drop.minAmount + 1);
            ItemStack item = drop.buildItem(plugin, amount);
            if (item != null) {
                event.getDrops().add(item);
            }
        }
    }

    private @org.jetbrains.annotations.Nullable MobConfig findByCustomName(LivingEntity entity) {
        String name = entity.getCustomName();
        if (name == null || name.isEmpty()) return null;

        // Ищем конфиг по displayName (можно настроить в mob_loot.yml)
        for (var entry : mobConfigs.entrySet()) {
            if (entry.getValue().displayName != null && name.contains(entry.getValue().displayName)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private @org.jetbrains.annotations.Nullable MobConfig findByScoreboardTags(LivingEntity entity) {
        for (var tag : entity.getScoreboardTags()) {
            MobConfig config = mobConfigs.get(tag);
            if (config != null) return config;
        }
        return null;
    }

    private static class MobLootRoot {
        public Map<String, MobConfig> mobs = new HashMap<>();
    }

    private static class MobConfig {
        public boolean enabled = true;
        public String displayName; // опционально: матчинг по кастомному имени моба
        public List<String> tags;  // опционально: матчинг по scoreboard тегам
        public List<DropEntry> drops = new ArrayList<>();
    }

    private static class DropEntry {
        public String item;
        public int minAmount = 1;
        public int maxAmount = 1;
        public double chance = 0.5;
        public String name;
        public boolean custom;

        public @org.jetbrains.annotations.Nullable ItemStack buildItem(WastelandArtifacts plugin, int amount) {
            if (item == null || item.isEmpty()) return null;

            if (custom) {
                ItemStack ci = plugin.getCustomItemRegistry().create(item, amount);
                if (ci == null) {
                    plugin.getComponentLogger().warn("<red>Кастомный предмет '" + item + "' не найден");
                }
                return ci;
            }

            // Авто-определение: custom item > Material
            ItemStack ci = plugin.getCustomItemRegistry().create(item, amount);
            if (ci != null) return ci;

            Material mat = Material.matchMaterial(item);
            if (mat == null) {
                plugin.getComponentLogger().warn("<red>Неизвестный предмет '" + item + "'");
                return null;
            }
            ItemStack stack = new ItemStack(mat, amount);
            if (name != null && !name.isEmpty()) {
                ItemMeta meta = stack.getItemMeta();
                meta.displayName(me.darkcube.wa.util.ComponentUtil.fromMini(name));
                stack.setItemMeta(meta);
            }
            return stack;
        }
    }
}
