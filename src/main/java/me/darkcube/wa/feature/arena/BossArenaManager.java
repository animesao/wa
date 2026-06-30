package me.darkcube.wa.feature.arena;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.database.DatabaseManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class BossArenaManager {
    private final WastelandArtifacts plugin;
    private final DatabaseManager db;
    private Location arenaSpawn;
    private Location bossSpawn;
    private List<ArenaWave> waves = new ArrayList<>();
    private final Map<UUID, ArenaSession> sessions = new HashMap<>();

    public BossArenaManager(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public void loadConfig(Map<String, Object> config) {
        if (config == null) return;
        if (config.containsKey("waves")) {
            waves.clear();
            List<Map<String, Object>> waveList = (List<Map<String, Object>>) config.get("waves");
            for (Map<String, Object> w : waveList) {
                ArenaWave wave = new ArenaWave();
                wave.waveNumber = ((Number) w.get("wave")).intValue();
                wave.mobs = (List<String>) w.get("mobs");
                wave.count = ((Number) w.get("count")).intValue();
                if (w.containsKey("boss")) wave.boss = (String) w.get("boss");
                waves.add(wave);
            }
        }
    }

    public void setArenaSpawn(Location loc) { this.arenaSpawn = loc; }
    public void setBossSpawn(Location loc) { this.bossSpawn = loc; }

    public void startArena(Player player) {
        if (arenaSpawn == null) { player.sendMessage("§cАрена не настроена!"); return; }
        ArenaSession session = new ArenaSession(player.getUniqueId());
        session.currentWave = 0;
        sessions.put(player.getUniqueId(), session);
        player.teleport(arenaSpawn);
        startWave(player, 0);
    }

    private void startWave(Player player, int waveIndex) {
        if (waveIndex >= waves.size()) {
            completeArena(player);
            return;
        }
        ArenaWave wave = waves.get(waveIndex);
        ArenaSession session = sessions.get(player.getUniqueId());
        if (session == null) return;
        session.currentWave = waveIndex;
        session.mobsAlive = wave.count;

        for (int i = 0; i < wave.count; i++) {
            if (wave.boss != null && i == wave.count - 1) {
                spawnBoss(player.getWorld(), wave.boss);
            } else if (!wave.mobs.isEmpty()) {
                String mobType = wave.mobs.get(new Random().nextInt(wave.mobs.size()));
                spawnMob(player.getWorld(), mobType);
            }
        }
        player.sendMessage("§6Волна " + (waveIndex + 1) + "/" + waves.size());
    }

    private void spawnMob(World world, String type) {
        if (bossSpawn == null) return;
        try {
            org.bukkit.entity.EntityType eType = org.bukkit.entity.EntityType.valueOf(type);
            world.spawnEntity(bossSpawn, eType);
        } catch (Exception e) {}
    }

    private void spawnBoss(World world, String bossType) {
        spawnMob(world, bossType);
    }

    public void onMobKilled(Player player) {
        ArenaSession session = sessions.get(player.getUniqueId());
        if (session == null) return;
        session.mobsAlive--;
        if (session.mobsAlive <= 0) {
            startWave(player, session.currentWave + 1);
        }
    }

    private void completeArena(Player player) {
        sessions.remove(player.getUniqueId());
        db.execute("UPDATE wa_arena_stats SET waves_cleared = waves_cleared + 1, bosses_killed = bosses_killed + 1 WHERE player_uuid=?",
                player.getUniqueId().toString());
        player.sendMessage("§a§lАрена пройдена!");
        giveRewards(player);
    }

    private void giveRewards(Player player) {
        var artifacts = plugin.getArtifactRegistry().getAll();
        if (!artifacts.isEmpty()) {
            var art = artifacts.get(new Random().nextInt(artifacts.size()));
            var item = plugin.getArtifactManager().createItemStack(art);
            player.getInventory().addItem(item);
        }
    }

    public Location getArenaSpawn() { return arenaSpawn; }

    private static class ArenaWave {
        int waveNumber, count;
        List<String> mobs;
        String boss;
    }

    private static class ArenaSession {
        UUID playerId;
        int currentWave, mobsAlive;
        ArenaSession(UUID playerId) { this.playerId = playerId; }
    }
}
