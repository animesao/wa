package me.darkcube.wa.feature.abilities;

import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.List;
import java.util.Map;

public class Ability {
    private final String id;
    private final String displayName;
    private final int cooldown;
    private final AbilityType type;
    private final double damage;
    private final double radius;
    private final double distance;
    private final double heal;
    private final int duration;
    private final String projectile;
    private final String command;
    private final double knockback;
    private final List<Map<String, Object>> effects;
    private final List<Map<String, Object>> attributes;
    private final Particle particle;
    private final Sound sound;
    private final List<String> lore;

    public Ability(String id, String displayName, int cooldown, AbilityType type,
                   double damage, double radius, double distance, double heal,
                   int duration, String projectile, String command, double knockback,
                   List<Map<String, Object>> effects, List<Map<String, Object>> attributes,
                   String particle, String sound, List<String> lore) {
        this.id = id; this.displayName = displayName; this.cooldown = cooldown; this.type = type;
        this.damage = damage; this.radius = radius; this.distance = distance; this.heal = heal;
        this.duration = duration; this.projectile = projectile; this.command = command; this.knockback = knockback;
        this.effects = effects; this.attributes = attributes;
        Particle p = null; try { if (particle != null) p = Particle.valueOf(particle); } catch (Exception e) {}
        this.particle = p;
        Sound s = null; try { if (sound != null) s = Sound.valueOf(sound); } catch (Exception e) {}
        this.sound = s;
        this.lore = lore;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getCooldown() { return cooldown; }
    public AbilityType getType() { return type; }
    public double getDamage() { return damage; }
    public double getRadius() { return radius; }
    public double getDistance() { return distance; }
    public double getHeal() { return heal; }
    public int getDuration() { return duration; }
    public String getProjectile() { return projectile; }
    public String getCommand() { return command; }
    public double getKnockback() { return knockback; }
    public List<Map<String, Object>> getEffects() { return effects; }
    public List<Map<String, Object>> getAttributes() { return attributes; }
    public Particle getParticle() { return particle; }
    public Sound getSound() { return sound; }
    public List<String> getLore() { return lore; }
}
