package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LightningComponent implements ArtifactComponent {

    private double chance;
    private boolean damage;

    public LightningComponent() {
        this.chance = 1.0;
        this.damage = true;
    }

    public LightningComponent(double chance) {
        this.chance = chance;
        this.damage = true;
    }

    public double getChance() { return chance; }
    public void setChance(double chance) { this.chance = chance; }
    public boolean isDamage() { return damage; }
    public void setDamage(boolean damage) { this.damage = damage; }

    public boolean strike(Location location) {
        if (Math.random() > chance) return false;
        if (damage) {
            location.getWorld().strikeLightning(location);
        } else {
            location.getWorld().strikeLightningEffect(location);
        }
        return true;
    }

    @Override
    public @NotNull String getType() { return "LIGHTNING"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
