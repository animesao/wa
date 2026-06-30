package me.darkcube.wa.feature.upgrades;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.database.DatabaseManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class UpgradeManager {
    private final WastelandArtifacts plugin;
    private final DatabaseManager db;
    private int maxLevel = 10;
    private int itemsPerUpgrade = 3;
    private double damageMultiplier = 1.15;
    private double healthMultiplier = 1.1;

    public UpgradeManager(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public void loadConfig(Map<String, Object> config) {
        if (config == null) return;
        if (config.containsKey("maxLevel")) maxLevel = (int) config.get("maxLevel");
        if (config.containsKey("itemsPerUpgrade")) itemsPerUpgrade = (int) config.get("itemsPerUpgrade");
        if (config.containsKey("damageMultiplier")) damageMultiplier = (double) config.get("damageMultiplier");
        if (config.containsKey("healthMultiplier")) healthMultiplier = (double) config.get("healthMultiplier");
    }

    public int getLevel(Player player, String artifactId) {
        Integer lvl = db.query("SELECT level FROM wa_artifact_data WHERE id=? AND owner_uuid=?",
                rs -> rs.next() ? rs.getInt("level") : 1,
                artifactId, player.getUniqueId().toString());
        return lvl != null ? lvl : 1;
    }

    public void setLevel(Player player, String artifactId, int level) {
        db.execute("INSERT OR REPLACE INTO wa_artifact_data (id, owner_uuid, level, xp, kills) VALUES (?,?,?,0,0)",
                artifactId, player.getUniqueId().toString(), Math.min(level, maxLevel));
    }

    public boolean canUpgrade(Player player, String artifactId) {
        int currentLevel = getLevel(player, artifactId);
        if (currentLevel >= maxLevel) return false;
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.getArtifactManager().getArtifactFromItem(item) != null) {
                Artifact art = plugin.getArtifactManager().getArtifactFromItem(item);
                if (art.getId().equals(artifactId)) count++;
            }
        }
        return count >= itemsPerUpgrade;
    }

    public int getMaxLevel() { return maxLevel; }
    public int getItemsPerUpgrade() { return itemsPerUpgrade; }
    public double getDamageMultiplier() { return damageMultiplier; }
    public double getHealthMultiplier() { return healthMultiplier; }
}
