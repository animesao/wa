package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.component.components.SoulbindComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class SoulbindListener implements Listener {

    private final WastelandArtifacts plugin;

    public SoulbindListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack item = event.getItem().getItemStack();
        if (!SoulbindComponent.isOwner(item, player)) {
            event.setCancelled(true);
            player.sendMessage(me.darkcube.wa.util.ComponentUtil.fromMini(plugin.getConfigManager().getLang("soulbind.not-owner")));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack current = event.getCurrentItem();
        if (current != null && !SoulbindComponent.isOwner(current, player)) {
            event.setCancelled(true);
            player.sendMessage(me.darkcube.wa.util.ComponentUtil.fromMini(plugin.getConfigManager().getLang("soulbind.not-owner")));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        for (ItemStack item : event.getNewItems().values()) {
            if (!SoulbindComponent.isOwner(item, player)) {
                event.setCancelled(true);
                player.sendMessage(me.darkcube.wa.util.ComponentUtil.fromMini(plugin.getConfigManager().getLang("soulbind.not-owner")));
                return;
            }
        }
    }
}
