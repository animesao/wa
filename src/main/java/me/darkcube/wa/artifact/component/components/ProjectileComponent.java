package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class ProjectileComponent implements ArtifactComponent {

    private EntityType projectileType;
    private double speed;
    private float yield;

    public ProjectileComponent() {
        this.projectileType = EntityType.FIREBALL;
        this.speed = 1.5;
        this.yield = 2.0f;
    }

    public EntityType getProjectileType() { return projectileType; }
    public void setProjectileType(EntityType projectileType) { this.projectileType = projectileType; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public float getYield() { return yield; }
    public void setYield(float yield) { this.yield = yield; }

    public void shoot(ProjectileSource source) {
        if (!(source instanceof Player player)) return;
        org.bukkit.Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize().multiply(speed);

        Entity projectile = player.getWorld().spawnEntity(
                eyeLoc.clone().add(direction.clone().multiply(2)),
                projectileType
        );

        if (projectile instanceof Fireball fb) {
            fb.setVelocity(direction);
            fb.setShooter(player);
            fb.setYield(yield);
            fb.setIsIncendiary(false);
        } else if (projectile instanceof AbstractArrow arrow) {
            arrow.setVelocity(direction);
            arrow.setShooter(player);
            arrow.setDamage(yield);
        } else if (projectile instanceof Snowball sb) {
            sb.setVelocity(direction);
            sb.setShooter(player);
        } else if (projectile instanceof Projectile proj) {
            proj.setVelocity(direction);
            proj.setShooter(player);
        }
    }

    @Override
    public @NotNull String getType() { return "PROJECTILE"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
