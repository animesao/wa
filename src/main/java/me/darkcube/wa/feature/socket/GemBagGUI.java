package me.darkcube.wa.feature.socket;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.gui.GUIBase;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GemBagGUI extends GUIBase {

    private static final int ARTIFACT_SLOT = 13;
    private static final int GEM_SLOTS_START = 20;
    private static final int GEM_SLOTS_MAX = 7;
    private static final int CLOSE_SLOT = 49;

    private final GemManager gemManager;
    private final ItemStack artifactItem;

    public GemBagGUI(WastelandArtifacts plugin, Player player, ItemStack artifactItem) {
        super(plugin, player, "<dark_purple>💎 Гнёзда артефакта", 6);
        this.gemManager = plugin.getGemManager();
        this.artifactItem = artifactItem.clone();
    }

    @Override
    protected void build() {
        inventory.clear();
        clickHandlers.clear();

        int socketCount = gemManager.getSocketCount(artifactItem);
        List<String> socketedGems = gemManager.getSocketedGems(artifactItem);

        // Артефакт в центре
        ItemStack displayArtifact = artifactItem.clone();
        ItemMeta am = displayArtifact.getItemMeta();
        if (am != null) {
            List<Component> lore = am.lore() != null ? new ArrayList<>(am.lore()) : new ArrayList<>();
            lore.add(ComponentUtil.fromMini(""));
            lore.add(ComponentUtil.fromMini("<dark_gray>💎 Гнёзда: <white>" + socketCount));
            lore.add(ComponentUtil.fromMini("<dark_gray>Заполнено: <white>" + socketedGems.size() + "/" + socketCount));
            am.lore(lore);
            displayArtifact.setItemMeta(am);
        }
        setItem(ARTIFACT_SLOT, displayArtifact, null);

        // Слоты для самоцветов
        for (int i = 0; i < GEM_SLOTS_MAX; i++) {
            int slot = GEM_SLOTS_START + i;
            if (i >= socketCount) {
                // Лишние слоты — заблокированы
                setItem(slot, Material.BARRIER,
                        "<red>🔒 Нет гнезда",
                        List.of("<gray>У этого артефакта нет столько гнёзд"), null);
                continue;
            }
            if (i < socketedGems.size()) {
                // Вставленный самоцвет
                String gemId = socketedGems.get(i);
                Gem gem = gemManager.getGem(gemId);
                if (gem != null) {
                    ItemStack gemItem = gemManager.createGemItem(gem);
                    ItemMeta gm = gemItem.getItemMeta();
                    if (gm != null) {
                        List<Component> lore = gm.lore() != null ? new ArrayList<>(gm.lore()) : new ArrayList<>();
                        lore.add(ComponentUtil.fromMini(""));
                        lore.add(ComponentUtil.fromMini("<red>🖱 Клик — извлечь самоцвет"));
                        gm.lore(lore);
                        gemItem.setItemMeta(gm);
                    }
                    final int idx = i;
                    setItem(slot, gemItem, e -> unsocketGem(idx));
                } else {
                    setItem(slot, Material.AIR, "", null, null);
                }
            } else {
                // Пустое гнездо
                setItem(slot, Material.GRAY_STAINED_GLASS_PANE,
                        "<green>✦ Пустое гнездо #" + (i + 1),
                        List.of("<gray>Кликни самоцветом в руке,",
                                "<gray>чтобы вставить его в это гнездо"), null);
            }
        }

        // Кнопка закрытия
        setItem(CLOSE_SLOT, Material.BARRIER,
                "<red>✕ Закрыть",
                List.of("<gray>Закрыть меню гнёзд"), e -> close());
    }

    @Override
    protected void onSlotClick(InventoryClickEvent event, int slot) {
        if (slot < GEM_SLOTS_START || slot >= GEM_SLOTS_START + GEM_SLOTS_MAX) {
            event.setCancelled(true);
            return;
        }

        int gemIndex = slot - GEM_SLOTS_START;
        int socketCount = gemManager.getSocketCount(artifactItem);
        List<String> socketedGems = gemManager.getSocketedGems(artifactItem);

        if (gemIndex >= socketCount) {
            event.setCancelled(true);
            return;
        }

        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();

        if (gemIndex < socketedGems.size()) {
            // Заполненный слот — клик извлекает
            event.setCancelled(true);
            return;
        }

        // Пустой слот — пробуем вставить самоцвет из курсора
        if (cursor == null || cursor.getType() == Material.AIR) {
            event.setCancelled(true);
            return;
        }

        if (!gemManager.isGem(cursor)) {
            event.setCancelled(true);
            player.sendMessage(ComponentUtil.fromMini("<red>Это не самоцвет!"));
            return;
        }

        String gemId = gemManager.getGemId(cursor);
        if (gemId == null) {
            event.setCancelled(true);
            return;
        }

        // Проверяем duplicate
        if (socketedGems.contains(gemId)) {
            event.setCancelled(true);
            player.sendMessage(ComponentUtil.fromMini("<red>Этот самоцвет уже вставлен!"));
            return;
        }

        // Сокетируем
        ItemStack artifactClone = player.getInventory().getItemInMainHand();
        if (!plugin.getArtifactManager().isArtifact(artifactClone)) {
            artifactClone = player.getInventory().getItemInOffHand();
        }
        if (!plugin.getArtifactManager().isArtifact(artifactClone)) {
            event.setCancelled(true);
            player.sendMessage(ComponentUtil.fromMini("<red>Артефакт не найден в руке!"));
            return;
        }

        if (!gemManager.socketGem(artifactClone, gemId)) {
            event.setCancelled(true);
            player.sendMessage(ComponentUtil.fromMini("<red>Не удалось вставить самоцвет!"));
            return;
        }

        // Забираем 1 самоцвет из курсора
        cursor.setAmount(cursor.getAmount() - 1);
        event.setCursor(cursor.getAmount() <= 0 ? null : cursor);

        // Обновляем слот артефакта
        if (artifactClone.equals(player.getInventory().getItemInMainHand())) {
            player.getInventory().setItemInMainHand(artifactClone);
        } else if (artifactClone.equals(player.getInventory().getItemInOffHand())) {
            player.getInventory().setItemInOffHand(artifactClone);
        }

        player.sendMessage(ComponentUtil.fromMini("<green>✦ Самоцвет вставлен в гнездо!"));

        // Перестраиваем GUI
        event.setCancelled(true);
        build();
    }

    private void unsocketGem(int index) {
        List<String> socketed = gemManager.getSocketedGems(artifactItem);
        if (index < 0 || index >= socketed.size()) return;

        String gemId = socketed.get(index);
        Gem gem = gemManager.getGem(gemId);
        if (gem == null) return;

        ItemStack artifactClone = player.getInventory().getItemInMainHand();
        if (!plugin.getArtifactManager().isArtifact(artifactClone)) {
            artifactClone = player.getInventory().getItemInOffHand();
        }
        if (!plugin.getArtifactManager().isArtifact(artifactClone)) {
            player.sendMessage(ComponentUtil.fromMini("<red>Артефакт не найден в руке!"));
            return;
        }

        String removed = gemManager.unsocketGem(artifactClone, index);
        if (removed == null) return;

        // Даём самоцвет игроку
        ItemStack gemItem = gemManager.createGemItem(gem);
        player.getInventory().addItem(gemItem).forEach((i, leftover) ->
                player.getWorld().dropItem(player.getLocation(), leftover));

        // Обновляем слот артефакта
        if (artifactClone.equals(player.getInventory().getItemInMainHand())) {
            player.getInventory().setItemInMainHand(artifactClone);
        } else if (artifactClone.equals(player.getInventory().getItemInOffHand())) {
            player.getInventory().setItemInOffHand(artifactClone);
        }

        player.sendMessage(ComponentUtil.fromMini("<yellow>✦ Самоцвет извлечён из гнезда!"));
        build();
    }
}
