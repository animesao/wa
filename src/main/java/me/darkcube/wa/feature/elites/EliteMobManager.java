package me.darkcube.wa.feature.elites;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class EliteMobManager {
    private final WastelandArtifacts plugin;
    private final Random random = new Random();
    private final List<EliteConfig> elites = new ArrayList<>();

    public EliteMobManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(List<Map<String, Object>> entries) {
        elites.clear();
        if (entries == null) return;
        for (Map<String, Object> entry : entries) {
            EliteConfig cfg = new EliteConfig();
            cfg.entityType = (String) entry.get("entity");
            cfg.displayName = (String) entry.get("name");
            cfg.healthMultiplier = entry.containsKey("healthMultiplier") ? ((Number) entry.get("healthMultiplier")).doubleValue() : 2.0;
            cfg.damageMultiplier = entry.containsKey("damageMultiplier") ? ((Number) entry.get("damageMultiplier")).doubleValue() : 1.5;
            cfg.dropChance = entry.containsKey("dropChance") ? ((Number) entry.get("dropChance")).doubleValue() : 0.3;
            cfg.drops = (List<String>) entry.get("drops");
            elites.add(cfg);
        }
    }

    public void trySpawnElite(LivingEntity entity) {
        for (EliteConfig cfg : elites) {
            if (entity.getType().name().equalsIgnoreCase(cfg.entityType)) {
                if (random.nextDouble() < 0.1) {
                    applyElite(entity, cfg);
                }
                break;
            }
        }
    }

    private void applyElite(LivingEntity entity, EliteConfig cfg) {
        double baseHealth = entity.getMaxHealth();
        double newHealth = baseHealth * cfg.healthMultiplier;
        var attr = entity.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(newHealth);
            entity.setHealth(newHealth);
        }
        if (cfg.displayName != null) {
            entity.customName(me.darkcube.wa.util.ComponentUtil.fromMini(cfg.displayName));
            entity.setCustomNameVisible(true);
        }
        entity.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(plugin, "elite_mob"),
                PersistentDataType.STRING, cfg.entityType);
    }

    public boolean isElite(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(
                new org.bukkit.NamespacedKey(plugin, "elite_mob"),
                PersistentDataType.STRING);
    }

    public List<String> getDrops(LivingEntity entity) {
        for (EliteConfig cfg : elites) {
            if (cfg.drops != null && random.nextDouble() < cfg.dropChance) {
                return cfg.drops;
            }
        }
        return Collections.emptyList();
    }

    private static class EliteConfig {
        String entityType, displayName;
        double healthMultiplier, damageMultiplier, dropChance;
        List<String> drops;
    }
}
