package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ParticleAmbientComponent implements ArtifactComponent {

    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;

    public ParticleAmbientComponent() {
        this.particle = Particle.SMOKE;
        this.count = 2;
        this.offsetX = 0.3;
        this.offsetY = 0.5;
        this.offsetZ = 0.3;
    }

    public Particle getParticle() { return particle; }
    public void setParticle(Particle particle) { this.particle = particle; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    @Override
    public @NotNull String getType() { return "PARTICLE_AMBIENT"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {
        player.getWorld().spawnParticle(particle,
                player.getLocation().add(0, 1.5, 0),
                count, offsetX, offsetY, offsetZ, 0
        );
    }

    @Override
    public void onUnequip(@NotNull Player player) {}
}
