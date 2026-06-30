package me.darkcube.wa.feature.xp;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.database.DatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ArtifactXPManager {
    private final WastelandArtifacts plugin;
    private final DatabaseManager db;
    private int xpPerKill = 10;
    private int xpPerLevel = 100;
    private int maxLevel = 50;
    private double damagePerLevel = 0.05;

    public ArtifactXPManager(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public void loadConfig(Map<String, Object> config) {
        if (config == null) return;
        if (config.containsKey("xpPerKill")) xpPerKill = ((Number) config.get("xpPerKill")).intValue();
        if (config.containsKey("xpPerLevel")) xpPerLevel = ((Number) config.get("xpPerLevel")).intValue();
        if (config.containsKey("maxLevel")) maxLevel = ((Number) config.get("maxLevel")).intValue();
        if (config.containsKey("damagePerLevel")) damagePerLevel = ((Number) config.get("damagePerLevel")).doubleValue();
    }

    public void addKill(Player player, ItemStack weapon) {
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(weapon);
        if (artifact == null) return;
        String aid = artifact.getId();
        String uuid = player.getUniqueId().toString();

        int level = getLevel(player, aid);
        long xp = getXP(player, aid) + xpPerKill;
        long needed = getXPForNextLevel(level);

        if (xp >= needed && level < maxLevel) {
            level++;
            xp = 0;
            db.execute("UPDATE wa_artifact_data SET level=?, xp=?, kills=kills+1 WHERE id=? AND owner_uuid=?",
                    level, xp, aid, uuid);
        } else {
            db.execute("UPDATE wa_artifact_data SET xp=?, kills=kills+1 WHERE id=? AND owner_uuid=?",
                    xp, aid, uuid);
        }
    }

    public int getLevel(Player player, String artifactId) {
        Integer lvl = db.query("SELECT level FROM wa_artifact_data WHERE id=? AND owner_uuid=?",
                rs -> rs.next() ? rs.getInt("level") : 1,
                artifactId, player.getUniqueId().toString());
        return lvl != null ? lvl : 1;
    }

    public long getXP(Player player, String artifactId) {
        Long xp = db.query("SELECT xp FROM wa_artifact_data WHERE id=? AND owner_uuid=?",
                rs -> rs.next() ? rs.getLong("xp") : 0L,
                artifactId, player.getUniqueId().toString());
        return xp != null ? xp : 0;
    }

    public long getXPForNextLevel(int level) {
        return (long) xpPerLevel * level;
    }

    public int getMaxLevel() { return maxLevel; }
    public double getDamageBonus(int level) { return 1.0 + (level - 1) * damagePerLevel; }
}
