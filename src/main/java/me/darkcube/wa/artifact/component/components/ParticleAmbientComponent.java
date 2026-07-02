package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleAmbientComponent implements ArtifactComponent {

    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private long interval;

    private static final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();

    public ParticleAmbientComponent() {
        this.particle = Particle.SMOKE;
        this.count = 2;
        this.offsetX = 0.3;
        this.offsetY = 0.5;
        this.offsetZ = 0.3;
        this.interval = 10;
    }

    public Particle getParticle() { return particle; }
    public void setParticle(Particle particle) { this.particle = particle; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    public long getInterval() { return interval; }
    public void setInterval(long interval) { this.interval = interval; }

    @Override
    public @NotNull String getType() { return "PARTICLE_AMBIENT"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {
        UUID id = player.getUniqueId();
        var plugin = Bukkit.getPluginManager().getPlugin("WastelandArtifacts");
        if (plugin == null) return;
        // Обновляем частицы: стоп старое, стартуем новое
        cancelTask(id);
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                cancelTask(id);
                return;
            }
            player.getWorld().spawnParticle(particle,
                    player.getLocation().add(0, 1.5, 0),
                    count, offsetX, offsetY, offsetZ, 0);
        }, 0L, interval);
        tasks.put(id, task);
    }

    @Override
    public void onUnequip(@NotNull Player player) {
        cancelTask(player.getUniqueId());
    }

    private static void cancelTask(UUID id) {
        BukkitTask task = tasks.remove(id);
        if (task != null) task.cancel();
    }
}
