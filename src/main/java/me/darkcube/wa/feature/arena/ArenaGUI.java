package me.darkcube.wa.feature.arena;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.util.ComponentUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArenaGUI implements Listener {
    private final WastelandArtifacts plugin;
    private final BossArenaManager manager;

    public ArenaGUI(WastelandArtifacts plugin, BossArenaManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27,
                ComponentUtil.fromMini(plugin.getConfigManager().getLang("arena.title")));

        ItemStack start = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta startMeta = start.getItemMeta();
        startMeta.displayName(ComponentUtil.fromMini("<green>▶ Начать арену"));
        startMeta.lore(List.of(
                ComponentUtil.fromMini("<gray>Телепорт на арену и начало битвы")));
        start.setItemMeta(startMeta);
        gui.setItem(13, start);

        player.openInventory(gui);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getView().title() instanceof net.kyori.adventure.text.Component title)) return;
        String titleStr = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(title);
        if (!titleStr.contains("Арена") && !titleStr.contains("Arena")) return;
        event.setCancelled(true);
        if (event.getSlot() == 13 && event.getWhoClicked() instanceof Player player) {
            manager.startArena(player);
            player.closeInventory();
        }
    }
}
