package me.darkcube.wa.dungeon;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.gui.GUIBase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class DungeonLootGUI extends GUIBase {

    private final DungeonManager dungeonManager;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private int page = 0;

    public DungeonLootGUI(WastelandArtifacts plugin, Player player) {
        super(plugin, player, "<dark_gray>Лут подземелий", 6);
        this.dungeonManager = plugin.getDungeonManager();
    }

    @Override
    protected void build() {
        clickHandlers.clear();
        inventory.clear();

        var configs = dungeonManager.getAllConfigs();
        List<String> keys = new ArrayList<>(configs.keySet());

        int start = page * 28;
        int end = Math.min(start + 28, keys.size());
        int slot = 0;

        for (int i = start; i < end; i++) {
            String id = keys.get(i);
            var config = configs.get(id);
            if (config == null) continue;

            int row = slot / 7;
            int col = slot % 7;
            int guiSlot = row * 9 + col;

            Material icon = config.enabled ? Material.CHEST : Material.BARRIER;
            ItemStack item = new ItemStack(icon);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(mm.deserialize(config.enabled ? "<gold>" + id : "<red>" + id + " (откл)"));

            List<String> lore = new ArrayList<>();
            lore.add("<gray>Режим: <white>" + config.loot.mode);
            lore.add("<gray>Артефактов: <white>" + config.loot.artifacts.size());
            lore.add("<gray>Боссов: <white>" + (config.bosses.types != null ? config.bosses.types.size() : 0));
            if (config.loot.replaceChance > 0) {
                lore.add("<gray>Шанс замены: <white>" + (config.loot.replaceChance * 100) + "%");
            }
            meta.lore(lore.stream().map(mm::deserialize).toList());
            item.setItemMeta(meta);
            inventory.setItem(guiSlot, item);

            final String dungeonId = id;
            clickHandlers.put(guiSlot, e -> showDungeonDetail(dungeonId));
            slot++;
        }

        // Навигация
        if (page > 0) {
            setItem(45, Material.ARROW, "<yellow>← Назад", null, e -> { page--; build(); });
        }
        if (end < keys.size()) {
            setItem(53, Material.ARROW, "<yellow>Вперёд →", null, e -> { page++; build(); });
        }

        setItem(49, Material.BARRIER, "<red>Закрыть", null, e -> close());
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);
    }

    private void showDungeonDetail(String dungeonId) {
        new DungeonDetailGUI(plugin, player, dungeonId).open();
    }

    private class DungeonDetailGUI extends GUIBase {
        private final String dungeonId;
        private final DungeonManager.DungeonConfig config;
        private int detailPage = 0;

        DungeonDetailGUI(WastelandArtifacts plugin, Player player, String dungeonId) {
            super(plugin, player, "<dark_gray>" + dungeonId, 6);
            this.dungeonId = dungeonId;
            this.config = dungeonManager.getConfig(dungeonId);
        }

        @Override
        protected void build() {
            clickHandlers.clear();
            inventory.clear();

            if (config == null) {
                setItem(22, Material.BARRIER, "<red>Конфиг не найден", null, null);
                setItem(49, Material.BARRIER, "<red>Назад", null, e -> close());
                return;
            }

            setItem(4, Material.CHEST, "<gold>" + dungeonId,
                    List.of("<gray>Статус: " + (config.enabled ? "<green>вкл" : "<red>откл")), null);

            // Артефакты
            int slot = 9;
            List<DungeonManager.DungeonConfig.LootEntry> artifacts = config.loot.artifacts;
            int start = detailPage * 14;
            int end = Math.min(start + 14, artifacts.size());

            for (int i = start; i < end; i++) {
                var entry = artifacts.get(i);
                var art = plugin.getArtifactRegistry().get(entry.id);
                String name = art != null ? art.getDisplayName() : entry.id;
                ItemStack icon = art != null
                        ? plugin.getArtifactManager().createItemStack(art)
                        : new ItemStack(Material.PAPER);
                ItemMeta meta = icon.getItemMeta();
                meta.lore(List.of(
                        mm.deserialize("<gray>Вес: <white>" + entry.weight),
                        mm.deserialize("<gray>Кол-во: " + entry.minCount + "-" + entry.maxCount)
                ));
                icon.setItemMeta(meta);
                inventory.setItem(slot, icon);
                slot++;
            }

            // Навигация
            if (detailPage > 0) {
                setItem(45, Material.ARROW, "<yellow>← Пред.", null, e -> { detailPage--; build(); });
            }
            if (end < artifacts.size()) {
                setItem(53, Material.ARROW, "<yellow>След. →", null, e -> { detailPage++; build(); });
            }

            setItem(49, Material.BARRIER, "<red>Назад к списку", null, e -> {
                new DungeonLootGUI(plugin, player).open();
            });
            fillBorder(Material.BLACK_STAINED_GLASS_PANE);
        }
    }
}
