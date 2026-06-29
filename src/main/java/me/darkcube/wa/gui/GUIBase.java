package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public abstract class GUIBase implements Listener, InventoryHolder {

    protected final WastelandArtifacts plugin;
    protected final MiniMessage miniMessage = MiniMessage.miniMessage();
    protected final Player player;
    protected Inventory inventory;
    protected final Map<Integer, Consumer<InventoryClickEvent>> clickHandlers = new HashMap<>();
    private boolean registered = false;

    public GUIBase(WastelandArtifacts plugin, Player player, String title, int rows) {
        this.plugin = plugin;
        this.player = player;
        this.inventory = Bukkit.createInventory(this, rows * 9, miniMessage.deserialize(title));
    }

    public void open() {
        if (!registered) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
            registered = true;
        }
        build();
        player.openInventory(inventory);
    }

    protected abstract void build();

    public void close() {
        if (registered) {
            HandlerList.unregisterAll(this);
            registered = false;
        }
        player.closeInventory();
    }

    protected void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        inventory.setItem(slot, item);
        if (handler != null) {
            clickHandlers.put(slot, handler);
        } else {
            clickHandlers.remove(slot);
        }
    }

    protected void setItem(int slot, Material material, String name, List<String> lore, Consumer<InventoryClickEvent> handler) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize(name));
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream().map(l -> miniMessage.deserialize(l)).toList());
            }
            item.setItemMeta(meta);
        }
        setItem(slot, item, handler);
    }

    protected void fillBorder(Material material) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                setItem(i, new ItemStack(material), null);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!event.getWhoClicked().equals(player)) return;
        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize()) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(player)) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot < 0 || rawSlot >= inventory.getSize()) return;

        int slot = event.getSlot();
        Consumer<InventoryClickEvent> handler = clickHandlers.get(slot);
        if (handler != null) {
            event.setCancelled(true);
            handler.accept(event);
            return;
        }

        onSlotClick(event, slot);
    }

    protected void onSlotClick(InventoryClickEvent event, int slot) {}

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(inventory)) return;
        if (!event.getPlayer().equals(player)) return;
        if (registered) {
            HandlerList.unregisterAll(this);
            registered = false;
        }
        onClose();
    }

    protected void onClose() {}

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
