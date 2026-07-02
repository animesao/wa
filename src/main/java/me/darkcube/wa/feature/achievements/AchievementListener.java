package me.darkcube.wa.feature.achievements;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.api.event.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {

    private final WastelandArtifacts plugin;
    private final AchievementManager manager;

    public AchievementListener(WastelandArtifacts plugin, AchievementManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArtifactFound(ArtifactFoundEvent event) {
        manager.check(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArtifactCraft(ArtifactCraftEvent event) {
        if (event.isCancelled()) return;
        manager.check(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArtifactUpgrade(ArtifactUpgradeEvent event) {
        manager.check(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onArtifactLevelUp(ArtifactLevelUpEvent event) {
        manager.check(event.getPlayer());
    }
}
