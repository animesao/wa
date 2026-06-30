package me.darkcube.wa.feature.abilities;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AbilityManager {
    private final WastelandArtifacts plugin;
    private final Map<String, Ability> abilities = new HashMap<>();
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    public AbilityManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
    }

    public Ability getAbility(String id) { return abilities.get(id); }
    public Map<String, Ability> getAllAbilities() { return Collections.unmodifiableMap(abilities); }

    public boolean hasCooldown(Player player, String abilityId) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;
        Long expires = playerCooldowns.get(abilityId);
        return expires != null && System.currentTimeMillis() < expires;
    }

    public long getCooldownSeconds(Player player, String abilityId) {
        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0;
        Long expires = playerCooldowns.get(abilityId);
        if (expires == null || System.currentTimeMillis() >= expires) return 0;
        return (expires - System.currentTimeMillis()) / 1000;
    }

    public void setCooldown(Player player, String abilityId, int seconds) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(abilityId, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void executeAbility(Player player, Ability ability) {
        if (ability == null || hasCooldown(player, ability.getId())) return;
        setCooldown(player, ability.getId(), ability.getCooldown());
        if (ability.getSound() != null) player.getWorld().playSound(player.getLocation(), ability.getSound(), 1, 1);
        if (ability.getParticle() != null) player.getWorld().spawnParticle(ability.getParticle(), player.getLocation(), 20, 0.5, 0.5, 0.5, 0);

        switch (ability.getType()) {
            case PROJECTILE -> executeProjectile(player, ability);
            case DASH -> executeDash(player, ability);
            case HEAL -> executeHeal(player, ability);
            case AOE -> executeAOE(player, ability);
            case TELEPORT -> executeTeleport(player, ability);
            case SHIELD -> executeShield(player, ability);
            case COMMAND -> executeCommand(player, ability);
        }
    }

    private void executeProjectile(Player player, Ability ability) {
        try {
            org.bukkit.entity.EntityType type = org.bukkit.entity.EntityType.valueOf(ability.getProjectile());
            org.bukkit.entity.Projectile proj = (org.bukkit.entity.Projectile) player.getWorld().spawnEntity(
                    player.getEyeLocation().add(player.getLocation().getDirection()), type);
            proj.setShooter(player);
            proj.setVelocity(player.getLocation().getDirection().multiply(2));
        } catch (Exception e) {
            player.launchProjectile(org.bukkit.entity.Snowball.class);
        }
    }

    private void executeDash(Player player, Ability ability) {
        var dir = player.getLocation().getDirection().setY(0).normalize();
        if (ability.getDistance() > 0) dir.multiply(ability.getDistance());
        else dir.multiply(5);
        player.setVelocity(dir);
    }

    private void executeHeal(Player player, Ability ability) {
        double healAmount = ability.getHeal() > 0 ? ability.getHeal() : 4;
        player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + healAmount));
    }

    private void executeAOE(Player player, Ability ability) {
        double radius = ability.getRadius() > 0 ? ability.getRadius() : 5;
        double dmg = ability.getDamage();
        for (org.bukkit.entity.Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if (e instanceof org.bukkit.entity.LivingEntity living && !e.equals(player)) {
                living.damage(dmg > 0 ? dmg : 1, player);
                if (ability.getKnockback() > 0) {
                    living.setVelocity(living.getLocation().toVector().subtract(
                            player.getLocation().toVector()).normalize().multiply(ability.getKnockback()));
                }
            }
        }
        player.getWorld().createExplosion(player.getLocation(), 0, false, false);
    }

    private void executeTeleport(Player player, Ability ability) {
        double dist = ability.getDistance() > 0 ? ability.getDistance() : 15;
        var target = player.getTargetBlockExact((int) dist);
        if (target != null) {
            player.teleport(target.getLocation().add(0.5, 1, 0.5));
        } else {
            var loc = player.getLocation().add(player.getLocation().getDirection().multiply(dist));
            player.teleport(loc);
        }
    }

    private void executeShield(Player player, Ability ability) {
        int dur = ability.getDuration() > 0 ? ability.getDuration() : 100;
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.ABSORPTION, dur, 2, false, true));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.RESISTANCE, dur, 1, false, true));
    }

    private void executeCommand(Player player, Ability ability) {
        if (ability.getCommand() != null && !ability.getCommand().isEmpty()) {
            String cmd = ability.getCommand().replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }
}
