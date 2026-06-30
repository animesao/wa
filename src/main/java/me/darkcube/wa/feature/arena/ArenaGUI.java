package me.darkcube.wa.feature.arena;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ArenaGUI {
    private final WastelandArtifacts plugin;
    private final BossArenaManager manager;

    public ArenaGUI(WastelandArtifacts plugin, BossArenaManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27,
                me.darkcube.wa.util.ComponentUtil.fromMini("<dark_red>Арена Боссов"));

        ItemStack start = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta startMeta = start.getItemMeta();
        startMeta.displayName(me.darkcube.wa.util.ComponentUtil.fromMini("<green>Начать арену"));
        startMeta.lore(List.of(
                me.darkcube.wa.util.ComponentUtil.fromMini("<gray>Телепорт на арену и начало битвы")));
        start.setItemMeta(startMeta);
        gui.setItem(13, start);

        player.openInventory(gui);
    }
}
