package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class AoeComponent implements ArtifactComponent {

    private double radius;
    private double damage;
    private PotionEffectType effect;
    private int effectAmplifier;
    private int effectDuration;
    private boolean affectPlayers;
    private boolean affectMobs;

    public AoeComponent() {
        this.radius = 5.0;
        this.damage = 0;
        this.affectPlayers = false;
        this.affectMobs = true;
    }

    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
    public PotionEffectType getEffect() { return effect; }
    public void setEffect(PotionEffectType effect) { this.effect = effect; }
    public int getEffectAmplifier() { return effectAmplifier; }
    public void setEffectAmplifier(int amplifier) { this.effectAmplifier = amplifier; }
    public int getEffectDuration() { return effectDuration; }
    public void setEffectDuration(int duration) { this.effectDuration = duration; }
    public boolean isAffectPlayers() { return affectPlayers; }
    public void setAffectPlayers(boolean affectPlayers) { this.affectPlayers = affectPlayers; }
    public boolean isAffectMobs() { return affectMobs; }
    public void setAffectMobs(boolean affectMobs) { this.affectMobs = affectMobs; }

    public void apply(Location center, Entity source) {
        Collection<Entity> nearby = center.getWorld().getNearbyEntities(center, radius, radius, radius);
        for (Entity entity : nearby) {
            if (entity.equals(source)) continue;
            if (entity instanceof Player && !affectPlayers) continue;
            if (!(entity instanceof Mob) && !(entity instanceof Player)) continue;
            if (!affectMobs && entity instanceof Mob) continue;

            if (entity instanceof LivingEntity living) {
                if (damage > 0) {
                    living.damage(damage, source);
                }
                if (effect != null && effectDuration > 0) {
                    living.addPotionEffect(new PotionEffect(effect, effectDuration, effectAmplifier));
                }
            }
        }
    }

    @Override
    public @NotNull String getType() { return "AOE"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
