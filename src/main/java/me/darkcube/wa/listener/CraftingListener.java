package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CraftingListener implements Listener {

    private final WastelandArtifacts plugin;

    public CraftingListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        // Blueprint recipes work automatically via ShapedRecipe registration
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Открываем рецепты чертежей в книге рецептов
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getCraftingManager().discoverRecipes(player);
        }, 20L);
    }
}
