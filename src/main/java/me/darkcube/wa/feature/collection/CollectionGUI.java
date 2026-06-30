package me.darkcube.wa.feature.collection;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CollectionGUI {
    private final WastelandArtifacts plugin;
    private final CollectionManager manager;

    public CollectionGUI(WastelandArtifacts plugin, CollectionManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    public void open(Player player) {
        List<Artifact> artifacts = new ArrayList<>(plugin.getArtifactRegistry().getAll());
        int size = Math.min(54, Math.max(9, ((artifacts.size() / 9) + 1) * 9));
        Inventory gui = Bukkit.createInventory(null, size, 
                me.darkcube.wa.util.ComponentUtil.fromMini(plugin.getConfigManager().getLang("collection.title")));

        Set<String> found = new HashSet<>(manager.getFoundArtifacts(player));
        int foundCount = 0;

        for (Artifact art : artifacts) {
            boolean isFound = found.contains(art.getId());
            ItemStack icon = isFound ? plugin.getArtifactManager().createItemStack(art) : new ItemStack(Material.GRAY_DYE);
            ItemMeta meta = icon.getItemMeta();
            List<Component> lore = new ArrayList<>();
            if (isFound) {
                foundCount++;
                lore.add(me.darkcube.wa.util.ComponentUtil.fromMini("<green>✔ Найден"));
            } else {
                lore.add(me.darkcube.wa.util.ComponentUtil.fromMini("<red>✘ Не найден"));
                meta.displayName(me.darkcube.wa.util.ComponentUtil.fromMini("<dark_gray>???"));
            }
            lore.add(me.darkcube.wa.util.ComponentUtil.fromMini("<gray>" + art.getRarity().getDisplayName()));
            meta.lore(lore);
            icon.setItemMeta(meta);
            gui.addItem(icon);
        }

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(me.darkcube.wa.util.ComponentUtil.fromMini(
                "<gold>Коллекция: <white>" + foundCount + "/" + artifacts.size()));
        info.setItemMeta(infoMeta);
        gui.setItem(size - 1, info);

        player.openInventory(gui);
    }
}
