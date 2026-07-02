package me.darkcube.wa.feature.socket;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class SocketListener implements Listener {

    private final WastelandArtifacts plugin;
    private final GemManager gemManager;
    private BukkitTask refreshTask;

    public SocketListener(WastelandArtifacts plugin, GemManager gemManager) {
        this.plugin = plugin;
        this.gemManager = gemManager;
    }

    public void start() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                applyAllGems(player);
            }
        }, 20L, 20L);
    }

    public void stop() {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        applyAllGems(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Эффекты снимаются автоматически при выходе
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getSlotType() == InventoryType.SlotType.ARMOR || event.getSlotType() == InventoryType.SlotType.CONTAINER) {
            plugin.getServer().getScheduler().runTask(plugin, () -> applyAllGems(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack item = event.getItem().getItemStack();
        if (plugin.getArtifactManager().isArtifact(item)) {
            plugin.getServer().getScheduler().runTask(plugin, () -> applyAllGems(player));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemHeld(PlayerItemHeldEvent event) {
        plugin.getServer().getScheduler().runTask(plugin, () -> applyAllGems(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getPlayer().isSneaking()) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || !plugin.getArtifactManager().isArtifact(item)) return;

        int sockets = gemManager.getSocketCount(item);
        if (sockets <= 0) return;

        event.setCancelled(true);
        new GemBagGUI(plugin, player, item).open();
    }

    public void applyAllGems(Player player) {
        gemManager.clearGemEffects(player);
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null && item.getType() != Material.AIR && plugin.getArtifactManager().isArtifact(item)) {
                List<String> gemIds = gemManager.getSocketedGems(item);
                if (!gemIds.isEmpty()) {
                    gemManager.applyGemEffects(player, gemIds);
                }
            }
        }
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main.getType() != Material.AIR && plugin.getArtifactManager().isArtifact(main)) {
            List<String> gemIds = gemManager.getSocketedGems(main);
            if (!gemIds.isEmpty()) {
                gemManager.applyGemEffects(player, gemIds);
            }
        }
        ItemStack off = player.getInventory().getItemInOffHand();
        if (off.getType() != Material.AIR && plugin.getArtifactManager().isArtifact(off)) {
            List<String> gemIds = gemManager.getSocketedGems(off);
            if (!gemIds.isEmpty()) {
                gemManager.applyGemEffects(player, gemIds);
            }
        }
    }
}
