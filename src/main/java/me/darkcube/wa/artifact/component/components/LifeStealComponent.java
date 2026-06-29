package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LifeStealComponent implements ArtifactComponent {

    private double percentage;

    public LifeStealComponent() {
        this.percentage = 0.1;
    }

    public LifeStealComponent(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public double steal(double damage, Player target, Player damager) {
        double heal = damage * percentage;
        double oldHealth = damager.getHealth();
        double finalHealth = Math.min(oldHealth + heal, damager.getMaxHealth());
        damager.setHealth(finalHealth);
        return finalHealth - oldHealth;
    }

    @Override
    public @NotNull String getType() { return "LIFE_STEAL"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
