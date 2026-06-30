package me.darkcube.wa.feature.xp;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class XPListener implements Listener {
    private final WastelandArtifacts plugin;
    private final ArtifactXPManager manager;

    public XPListener(WastelandArtifacts plugin, ArtifactXPManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        var weapon = killer.getInventory().getItemInMainHand();
        manager.addKill(killer, weapon);
    }
}
