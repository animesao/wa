package me.darkcube.wa.feature.arena;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class ArenaListener implements Listener {
    private final BossArenaManager manager;

    public ArenaListener(BossArenaManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            manager.onMobKilled(player);
        }
    }
}
