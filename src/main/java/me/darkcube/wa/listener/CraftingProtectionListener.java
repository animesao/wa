package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;

public class CraftingProtectionListener implements Listener {

    private final WastelandArtifacts plugin;

    public CraftingProtectionListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (containsProtectedItem(event.getInventory().getMatrix())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        if (containsProtectedItem(event.getInventory().getMatrix())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);
        if (containsProtectedItem(first, second)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        ItemStack top = event.getInventory().getItem(0);
        ItemStack bottom = event.getInventory().getItem(1);
        if (containsProtectedItem(top, bottom)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack template = event.getInventory().getItem(0);
        ItemStack base = event.getInventory().getItem(1);
        ItemStack addition = event.getInventory().getItem(2);
        if (containsProtectedItem(template, base, addition)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (isProtected(event.getSource())) {
            event.setCancelled(true);
        }
    }

    private boolean containsProtectedItem(ItemStack... items) {
        for (ItemStack item : items) {
            if (isProtected(item)) return true;
        }
        return false;
    }

    private boolean isProtected(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (plugin.getArtifactManager().isArtifact(item)) return true;
        if (plugin.getCustomItemRegistry().isCustomItem(item)) return true;
        return false;
    }
}
