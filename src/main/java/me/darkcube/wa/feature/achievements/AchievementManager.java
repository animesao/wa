package me.darkcube.wa.feature.achievements;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class AchievementManager {

    private final WastelandArtifacts plugin;
    private final DatabaseManager db;
    private final Map<String, Achievement> achievements = new LinkedHashMap<>();
    private final Map<String, String> typeDataKeys = new LinkedHashMap<>();

    public AchievementManager(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
        db.execute("CREATE TABLE IF NOT EXISTS wa_achievements (" +
                "player_uuid VARCHAR(36), " +
                "achievement_id VARCHAR(64), " +
                "progress INT DEFAULT 0, " +
                "completed BOOLEAN DEFAULT FALSE, " +
                "completed_date BIGINT, " +
                "PRIMARY KEY (player_uuid, achievement_id))");
    }

    public void loadConfig(ConfigurationSection section) {
        achievements.clear();
        typeDataKeys.clear();

        typeDataKeys.put("ARTIFACTS_FOUND", "total_found");
        typeDataKeys.put("ARTIFACTS_CRAFTED", "total_crafted");
        typeDataKeys.put("ARTIFACTS_UPGRADED", "total_upgraded");
        typeDataKeys.put("DUNGEONS_CLEARED", "dungeons_cleared");
        typeDataKeys.put("ELITE_KILLS", "elite_kills");
        typeDataKeys.put("BOSS_KILLS", "boss_kills");
        typeDataKeys.put("COLLECTION_PROGRESS", "collection_progress");
        typeDataKeys.put("FISHING_CAUGHT", "fishing_caught");
        typeDataKeys.put("XP_LEVELS", "xp_levels");

        if (section == null) return;
        for (String id : section.getKeys(false)) {
            if (!section.getBoolean(id + ".enabled", true)) continue;
            achievements.put(id, new Achievement(
                    id,
                    section.getString(id + ".name", id),
                    section.getString(id + ".description", ""),
                    section.getString(id + ".icon", "PAPER"),
                    section.getString(id + ".category", "general"),
                    section.getString(id + ".type", "ARTIFACTS_FOUND"),
                    section.getInt(id + ".target", 1),
                    section.getStringList(id + ".rewards"),
                    section.getString(id + ".dataKey", typeDataKeys.getOrDefault(
                            section.getString(id + ".type", ""), "total_found"))
            ));
        }
        plugin.getComponentLogger().info("<green>Загружено " + achievements.size() + " достижений");
    }

    public int getProgress(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) return 0;
        String type = achievements.get(achievementId).getType();
        String dataKey = achievements.get(achievementId).getDataKey();

        if ("COLLECTION_PROGRESS".equals(type) && plugin.getCollectionManager() != null) {
            return plugin.getCollectionManager().getFoundCount(player);
        }
        if ("XP_LEVELS".equals(type)) {
            return db.query("SELECT COALESCE(SUM(level),0) FROM wa_artifact_data WHERE owner_uuid=?",
                    rs -> rs.next() ? rs.getInt(1) : 0, player.getUniqueId().toString());
        }

        Integer val = db.query("SELECT " + dataKey + " FROM wa_players WHERE uuid=?",
                rs -> rs.next() ? rs.getInt(dataKey) : 0, player.getUniqueId().toString());
        return val != null ? val : 0;
    }

    public boolean isCompleted(Player player, String achievementId) {
        Boolean comp = db.query("SELECT completed FROM wa_achievements WHERE player_uuid=? AND achievement_id=?",
                rs -> rs.next() ? rs.getBoolean("completed") : false,
                player.getUniqueId().toString(), achievementId);
        return comp != null && comp;
    }

    public void check(Player player) {
        for (Achievement ach : achievements.values()) {
            if (isCompleted(player, ach.getId())) continue;
            int progress = getProgress(player, ach.getId());
            if (progress >= ach.getTarget()) {
                complete(player, ach);
            }
        }
    }

    private void complete(Player player, Achievement ach) {
        db.execute("INSERT OR REPLACE INTO wa_achievements (player_uuid, achievement_id, progress, completed, completed_date) VALUES (?,?,?,?,?)",
                player.getUniqueId().toString(), ach.getId(), ach.getTarget(), true, System.currentTimeMillis());

        plugin.getComponentLogger().info("<gold>🏆 Игрок " + player.getName() + " выполнил достижение: " + ach.getDisplayName());

        for (String reward : ach.getRewards()) {
            giveReward(player, reward);
        }
    }

    private void giveReward(Player player, String reward) {
        String upper = reward.toUpperCase();
        if (upper.startsWith("COMMAND:")) {
            String cmd = reward.substring(8).replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else if (upper.startsWith("ITEM:")) {
            String itemId = reward.substring(5).trim();
            plugin.getArtifactManager().giveArtifact(player, itemId, 1);
        } else if (upper.startsWith("MESSAGE:")) {
            player.sendMessage(me.darkcube.wa.util.ComponentUtil.fromMini(plugin.getConfigManager().getLang(reward.substring(8).trim())));
        }
    }

    public Collection<Achievement> getAll() { return achievements.values(); }
    public Achievement get(String id) { return achievements.get(id); }
    public int getCount() { return achievements.size(); }
}
