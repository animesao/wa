package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ExplosionComponent implements ArtifactComponent {

    private float power;
    private boolean safe; // не наносит урон игроку

    public ExplosionComponent() {
        this.power = 3.0f;
        this.safe = false;
    }

    public float getPower() { return power; }
    public void setPower(float power) { this.power = power; }
    public boolean isSafe() { return safe; }
    public void setSafe(boolean safe) { this.safe = safe; }

    public void explode(Location location, Player source) {
        if (safe && source != null) {
            location.getWorld().createExplosion(location, power, false, true, source);
        } else {
            location.getWorld().createExplosion(location, power);
        }
    }

    @Override
    public @NotNull String getType() { return "EXPLOSION"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
