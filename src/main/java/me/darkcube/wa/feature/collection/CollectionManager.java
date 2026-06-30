package me.darkcube.wa.feature.collection;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.database.DatabaseManager;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.util.*;

public class CollectionManager {
    private final WastelandArtifacts plugin;
    private final DatabaseManager db;

    public CollectionManager(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public boolean hasFound(Player player, String artifactId) {
        String sql = "SELECT 1 FROM wa_collection WHERE player_uuid=? AND artifact_id=?";
        Boolean result = db.query(sql, rs -> rs.next() ? true : null, player.getUniqueId().toString(), artifactId);
        return result != null && result;
    }

    public void markFound(Player player, String artifactId) {
        db.execute("INSERT OR IGNORE INTO wa_collection (player_uuid, artifact_id, found_date) VALUES (?,?,?)",
                player.getUniqueId().toString(), artifactId, System.currentTimeMillis());
        db.execute("UPDATE wa_players SET total_found = total_found + 1 WHERE uuid=?",
                player.getUniqueId().toString());
    }

    public List<String> getFoundArtifacts(Player player) {
        return db.query("SELECT artifact_id FROM wa_collection WHERE player_uuid=?",
                rs -> {
                    List<String> list = new ArrayList<>();
                    while (rs.next()) list.add(rs.getString("artifact_id"));
                    return list;
                }, player.getUniqueId().toString());
    }

    public int getFoundCount(Player player) {
        Integer count = db.query("SELECT COUNT(*) as c FROM wa_collection WHERE player_uuid=?",
                rs -> rs.next() ? rs.getInt("c") : 0, player.getUniqueId().toString());
        return count != null ? count : 0;
    }

    public Set<String> getAllArtifactIds() {
        return plugin.getArtifactRegistry().getAll().stream()
                .map(a -> a.getId()).collect(java.util.stream.Collectors.toSet());
    }
}
