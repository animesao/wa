package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PotionEffectAuraComponent implements ArtifactComponent {

    private PotionEffectType effect;
    private int amplifier;
    private double radius;

    public PotionEffectAuraComponent() {
        this.effect = PotionEffectType.SLOWNESS;
        this.amplifier = 2;
        this.radius = 6.0;
    }

    public void applyAura(@NotNull Player holder) {
        if (effect == null) return;
        Collection<org.bukkit.entity.Entity> nearby = holder.getNearbyEntities(radius, radius, radius);
        for (var entity : nearby) {
            if (entity instanceof LivingEntity target && !target.equals(holder)) {
                target.addPotionEffect(new PotionEffect(effect, 60, amplifier));
            }
        }
    }

    public PotionEffectType getEffect() { return effect; }
    public void setEffect(PotionEffectType effect) { this.effect = effect; }
    public int getAmplifier() { return amplifier; }
    public void setAmplifier(int amplifier) { this.amplifier = amplifier; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    @Override
    public @NotNull String getType() { return "POTION_EFFECT_AURA"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
