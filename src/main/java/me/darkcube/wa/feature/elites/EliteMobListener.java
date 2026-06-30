package me.darkcube.wa.feature.elites;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class EliteMobListener implements Listener {
    private final WastelandArtifacts plugin;
    private final EliteMobManager manager;

    public EliteMobListener(WastelandArtifacts plugin, EliteMobManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            manager.trySpawnElite(living);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof LivingEntity living && manager.isElite(living)) {
            event.setDamage(event.getDamage() * 1.5);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!manager.isElite(entity)) return;
        var drops = manager.getDrops(entity);
        for (String dropId : drops) {
            var item = plugin.getCustomItemRegistry().create(dropId);
            if (item != null) {
                entity.getWorld().dropItemNaturally(entity.getLocation(), item);
            }
        }
    }
}
