package me.darkcube.wa.bag;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArtifactBagListener implements Listener {

    private final WastelandArtifacts plugin;

    public ArtifactBagListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getArtifactBagManager().applyOnJoin(player);
        }, 40L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getArtifactBagManager().removeAll(player);
        plugin.getArtifactBagManager().saveBag(player);
    }
}
