package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.components.CommandComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class QuickAccessGUI extends GUIBase {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private int page = 0;

    public QuickAccessGUI(WastelandArtifacts plugin, Player player) {
        super(plugin, player, "<dark_gray>📡 Быстрый доступ", 6);
    }

    @Override
    protected void build() {
        clickHandlers.clear();
        inventory.clear();

        ItemStack[] bag = plugin.getArtifactBagManager().getBag(player);
        List<ArtifactInfo> artifacts = new ArrayList<>();

        for (ItemStack item : bag) {
            if (item == null) continue;
            Artifact a = plugin.getArtifactManager().getArtifactFromItem(item);
            if (a != null) artifacts.add(new ArtifactInfo(a, item));
        }

        int start = page * 45;
        int end = Math.min(start + 45, artifacts.size());
        int slot = 0;

        for (int i = start; i < end; i++) {
            var info = artifacts.get(i);
            ItemStack display = info.item.clone();
            ItemMeta meta = display.getItemMeta();

            List<Component> lore = new ArrayList<>();
            lore.add(mm.deserialize("<dark_gray>ЛКМ — использовать"));
            lore.add(mm.deserialize("<dark_gray>ПКМ — инфо"));

            for (var comp : info.artifact.getComponents()) {
                if (comp instanceof CommandComponent cmd) {
                    lore.add(mm.deserialize("<gray>⚡ /" + cmd.getCommand().split(" ")[0] + "..."));
                }
            }
            meta.lore(lore);
            display.setItemMeta(meta);

            int guiSlot = slot;
            inventory.setItem(guiSlot, display);
            final ArtifactInfo finalInfo = info;
            clickHandlers.put(guiSlot, e -> {
                if (e.isRightClick() || e.isLeftClick()) {
                    plugin.getArtifactManager().getItemBuilder().applyTriggers(
                            finalInfo.artifact, player, finalInfo.item,
                            me.darkcube.wa.artifact.trigger.TriggerType.ON_USE,
                            null, null
                    );
                    finalInfo.artifact.getComponents().stream()
                            .filter(c -> c instanceof CommandComponent)
                            .map(c -> (CommandComponent) c)
                            .forEach(c -> c.execute(player));
                    player.sendMessage(mm.deserialize("<green>✅ Активирован: " + finalInfo.artifact.getDisplayName()));
                    close();
                }
            });
            slot++;
        }

        if (page > 0) {
            setItem(45, Material.ARROW, "<yellow>← Назад", null, e -> { page--; build(); });
        }
        if (end < artifacts.size()) {
            setItem(53, Material.ARROW, "<yellow>Вперёд →", null, e -> { page++; build(); });
        }

        setItem(49, Material.COMPASS, "<gold>📡 Рация",
                List.of("<gray>Артефактов: " + artifacts.size(),
                        "<gray>Стр. " + (page + 1) + "/" + Math.max(1, (artifacts.size() + 44) / 45)),
                null);

        setItem(53, Material.BARRIER, "<red>Закрыть", null, e -> close());
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);
    }

    private static class ArtifactInfo {
        final Artifact artifact;
        final ItemStack item;
        ArtifactInfo(Artifact a, ItemStack i) { this.artifact = a; this.item = i; }
    }
}
