package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArmorListener implements Listener {

    private final WastelandArtifacts plugin;
    private final Map<UUID, ItemStack[]> previousArmor = new HashMap<>();
    private final Map<UUID, ItemStack> previousMainHand = new HashMap<>();
    private final Map<UUID, ItemStack> previousOffHand = new HashMap<>();

    public ArmorListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        saveCurrentState(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        previousArmor.remove(id);
        previousMainHand.remove(id);
        previousOffHand.remove(id);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        previousArmor.remove(player.getUniqueId());
        previousMainHand.remove(player.getUniqueId());
        previousOffHand.remove(player.getUniqueId());
        Bukkit.getScheduler().runTask(plugin, () -> {
            saveCurrentState(player);
            plugin.getArtifactBagManager().recalcEffects(player);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;
        scheduleCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;
        scheduleCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClickArmor(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (isArmorItem(item.getType()) || plugin.getArtifactManager().isArtifact(item)) {
            scheduleCheck(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        scheduleCheck(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        scheduleCheck(event.getPlayer());
    }

    private void scheduleCheck(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> checkEquipmentChange(player));
    }

    private void saveCurrentState(Player player) {
        previousArmor.put(player.getUniqueId(), player.getInventory().getArmorContents().clone());
        previousMainHand.put(player.getUniqueId(), player.getInventory().getItemInMainHand().clone());
        previousOffHand.put(player.getUniqueId(), player.getInventory().getItemInOffHand().clone());
    }

    private void checkEquipmentChange(Player player) {
        boolean changed = false;

        ItemStack[] currentArmor = player.getInventory().getArmorContents();
        ItemStack[] prevArmor = previousArmor.get(player.getUniqueId());

        if (prevArmor == null) {
            changed = true;
        } else {
            for (int i = 0; i < 4; i++) {
                if (!isSame(prevArmor[i], currentArmor[i])) {
                    changed = true;
                    break;
                }
            }
        }

        ItemStack currentMain = player.getInventory().getItemInMainHand();
        ItemStack prevMain = previousMainHand.get(player.getUniqueId());
        if (!isSame(currentMain, prevMain)) changed = true;

        ItemStack currentOff = player.getInventory().getItemInOffHand();
        ItemStack prevOff = previousOffHand.get(player.getUniqueId());
        if (!isSame(currentOff, prevOff)) changed = true;

        if (!changed) return;

        previousArmor.put(player.getUniqueId(), currentArmor.clone());
        previousMainHand.put(player.getUniqueId(), currentMain.clone());
        previousOffHand.put(player.getUniqueId(), currentOff.clone());

        plugin.getArtifactBagManager().recalcEffects(player);
    }

    private boolean isSame(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.getType() == b.getType()
                && a.getAmount() == b.getAmount()
                && Objects.equals(
                a.getItemMeta() != null ? a.getItemMeta().getPersistentDataContainer().get(
                        new org.bukkit.NamespacedKey("wastelandartifacts", "artifact_id"),
                        org.bukkit.persistence.PersistentDataType.STRING) : null,
                b.getItemMeta() != null ? b.getItemMeta().getPersistentDataContainer().get(
                        new org.bukkit.NamespacedKey("wastelandartifacts", "artifact_id"),
                        org.bukkit.persistence.PersistentDataType.STRING) : null
        );
    }

    private boolean isArmorItem(Material material) {
        return material.name().endsWith("_HELMET")
                || material.name().endsWith("_CHESTPLATE")
                || material.name().endsWith("_LEGGINGS")
                || material.name().endsWith("_BOOTS")
                || material == Material.ELYTRA
                || material == Material.CARVED_PUMPKIN
                || material.name().endsWith("_HEAD")
                || material == Material.SKELETON_SKULL
                || material == Material.WITHER_SKELETON_SKULL
                || material == Material.ZOMBIE_HEAD
                || material == Material.CREEPER_HEAD
                || material == Material.DRAGON_HEAD;
    }
}
