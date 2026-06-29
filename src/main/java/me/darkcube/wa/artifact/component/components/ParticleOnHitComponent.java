package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ParticleOnHitComponent implements ArtifactComponent {

    private Particle particle;
    private int count;
    private double offsetX;
    private double offsetY;
    private double offsetZ;
    private double speed;

    public ParticleOnHitComponent() {
        this.particle = Particle.FLAME;
        this.count = 10;
        this.offsetX = 0.5;
        this.offsetY = 0.5;
        this.offsetZ = 0.5;
        this.speed = 0.1;
    }

    public ParticleOnHitComponent(Particle particle, int count) {
        this();
        this.particle = particle;
        this.count = count;
    }

    public Particle getParticle() { return particle; }
    public void setParticle(Particle particle) { this.particle = particle; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public void spawn(Location location) {
        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
    }

    @Override
    public @NotNull String getType() { return "PARTICLE_ON_HIT"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
