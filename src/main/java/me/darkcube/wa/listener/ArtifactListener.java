package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.components.*;
import me.darkcube.wa.artifact.trigger.TriggerContext;
import me.darkcube.wa.artifact.trigger.TriggerType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArtifactListener implements Listener {

    private final WastelandArtifacts plugin;
    private final Map<UUID, Long> cooldownTracker = new HashMap<>();

    public ArtifactListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof LivingEntity target)) return;

        // Проверяем обе руки
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(weapon);
        if (artifact == null) {
            weapon = attacker.getInventory().getItemInOffHand();
            artifact = plugin.getArtifactManager().getArtifactFromItem(weapon);
        }
        if (artifact == null) return;

        for (var component : artifact.getComponents()) {
            if (component instanceof FireAspectComponent) {
                target.setFireTicks(((FireAspectComponent) component).getLevel() * 100);
            }
            if (component instanceof LightningComponent lc) {
                lc.strike(target.getLocation());
            }
            if (component instanceof ParticleOnHitComponent pc) {
                pc.spawn(target.getLocation());
            }
            if (component instanceof SoundOnHitComponent sc) {
                sc.play(target.getLocation());
            }
            if (component instanceof LifeStealComponent ls && target instanceof Player targetPlayer) {
                ls.steal(event.getFinalDamage(), targetPlayer, attacker);
            }
        }

        fireTriggers(artifact, attacker, artifact, weapon, TriggerType.ON_ATTACK, event, target);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Сначала проверяем предмет в руке (любой)
        ItemStack item = event.getItem();
        Artifact artifact = null;
        if (item != null) {
            artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        }
        // Если в руке нет артефакта — проверяем левую руку
        if (artifact == null) {
            item = player.getInventory().getItemInOffHand();
            if (item != null) {
                artifact = plugin.getArtifactManager().getArtifactFromItem(item);
            }
        }
        if (artifact == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            boolean hasCooldown = false;
            for (var comp : artifact.getComponents()) {
                if (comp instanceof CooldownComponent cd) {
                    if (player.getCooldown(item.getType()) > 0) {
                        hasCooldown = true;
                        break;
                    }
                    cd.applyCooldown(player);
                }
            }
            if (hasCooldown) return;

            for (var comp : artifact.getComponents()) {
                if (comp instanceof ExplosionComponent ec) {
                    ec.explode(player.getLocation(), player);
                }
                if (comp instanceof SummonComponent sc) {
                    sc.summon(player.getLocation(), player);
                }
                if (comp instanceof ProjectileComponent pc) {
                    pc.shoot(player);
                }
                if (comp instanceof AoeComponent ac) {
                    ac.apply(player.getLocation(), player);
                }
                if (comp instanceof ChargeComponent cc) {
                    cc.useCharge(item, player);
                }
                if (comp instanceof SoundOnUseComponent suc) {
                    suc.play(player.getLocation());
                }
            }

            fireTriggers(artifact, player, artifact, item, TriggerType.ON_USE, event, null);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack prev = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack next = player.getInventory().getItem(event.getNewSlot());

        if (prev != null) {
            Artifact prevArt = plugin.getArtifactManager().getArtifactFromItem(prev);
            if (prevArt != null) {
                for (var comp : prevArt.getComponents()) {
                    comp.onUnequip(player);
                }
                fireTriggers(prevArt, player, prevArt, prev, TriggerType.ON_UNEQUIP, null, null);
            }
        }
        if (next != null) {
            Artifact nextArt = plugin.getArtifactManager().getArtifactFromItem(next);
            if (nextArt != null) {
                for (var comp : nextArt.getComponents()) {
                    comp.onEquip(player);
                }
                fireTriggers(nextArt, player, nextArt, next, TriggerType.ON_EQUIP, null, null);
            }
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        if (artifact != null) {
            fireTriggers(artifact, event.getPlayer(), artifact, item, TriggerType.ON_PICKUP, null, null);
        }
    }

    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        // Снимаем эффекты с предмета который уходит из off-hand
        ItemStack oldOffhand = event.getOffHandItem();
        Artifact oldArtifact = plugin.getArtifactManager().getArtifactFromItem(oldOffhand);
        if (oldArtifact != null) {
            for (var comp : oldArtifact.getComponents()) {
                comp.onUnequip(player);
            }
            fireTriggers(oldArtifact, player, oldArtifact, oldOffhand, TriggerType.ON_UNEQUIP, null, null);
        }

        // Надеваем эффекты на предмет кото��ый приходит в off-hand
        ItemStack newOffhand = event.getMainHandItem();
        Artifact newArtifact = plugin.getArtifactManager().getArtifactFromItem(newOffhand);
        if (newArtifact != null) {
            for (var comp : newArtifact.getComponents()) {
                comp.onEquip(player);
            }
            fireTriggers(newArtifact, player, newArtifact, newOffhand, TriggerType.ON_EQUIP, null, null);
        }

        plugin.getArtifactBagManager().recalcEffects(player);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItemInMainHand();
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        if (artifact == null) {
            item = player.getInventory().getItemInOffHand();
            artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        }
        if (artifact != null) {
            fireTriggers(artifact, player, artifact, item, TriggerType.ON_SNEAK, event, null);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        ItemStack weapon = killer.getInventory().getItemInMainHand();
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(weapon);
        if (artifact == null) {
            weapon = killer.getInventory().getItemInOffHand();
            artifact = plugin.getArtifactManager().getArtifactFromItem(weapon);
        }
        if (artifact != null) {
            fireTriggers(artifact, killer, artifact, weapon, TriggerType.ON_KILL, event, event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        ItemStack item = player.getInventory().getItemInMainHand();
        Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        if (artifact == null) {
            item = player.getInventory().getItemInOffHand();
            artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        }
        if (artifact != null) {
            fireTriggers(artifact, player, artifact, item, TriggerType.ON_DEATH, event, player.getKiller());
        }
    }

    private void fireTriggers(Artifact artifact, Player player, Artifact art, ItemStack item,
                              TriggerType type, org.bukkit.event.Event event, Entity target) {
        TriggerContext ctx = new TriggerContext(player, art, item, type, event, target);
        for (var trigger : artifact.getTriggers()) {
            trigger.execute(ctx);
        }
        for (var trigger : plugin.getArtifactRegistry().getTriggers(type)) {
            trigger.execute(ctx);
        }
    }
}
