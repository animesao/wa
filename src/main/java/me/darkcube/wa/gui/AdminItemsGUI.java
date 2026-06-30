package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.altar.AltarBlockTracker;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class AdminItemsGUI extends GUIBase {

    private static final int ITEMS_PER_PAGE = 36;
    private static final int ITEMS_START = 9;
    private static final int PREV_BTN = 45;
    private static final int NEXT_BTN = 53;
    private static final int INFO_SLOT = 49;

    private final List<ItemStack> allItems = new ArrayList<>();
    private final List<String> itemOrigins = new ArrayList<>();
    private int currentPage = 0;
    private int maxPage = 0;
    private Tab currentTab = Tab.ARTIFACTS;

    private enum Tab {
        ARTIFACTS(Material.NETHER_STAR, "<gold>✦ Артефакты"),
        CUSTOM_ITEMS(Material.ENDER_CHEST, "<green>✦ Кастомные предметы"),
        BLUEPRINTS(Material.PAPER, "<aqua>✦ Чертежи");

        final Material icon;
        final String name;
        Tab(Material icon, String name) { this.icon = icon; this.name = name; }
    }

    public AdminItemsGUI(WastelandArtifacts plugin, Player player) {
        super(plugin, player, "<dark_gray>📦 Админ: все предметы", 6);
    }

    @Override
    protected void build() {
        loadItems();
        renderPage();
    }

    private void loadItems() {
        allItems.clear();
        itemOrigins.clear();

        switch (currentTab) {
            case ARTIFACTS -> loadArtifacts();
            case CUSTOM_ITEMS -> loadCustomItems();
            case BLUEPRINTS -> loadBlueprints();
        }

        maxPage = Math.max(0, (allItems.size() - 1) / ITEMS_PER_PAGE);
        if (currentPage > maxPage) currentPage = maxPage;
    }

    private void loadArtifacts() {
        for (Artifact art : plugin.getArtifactRegistry().getAll()) {
            ItemStack item = plugin.getArtifactManager().createItemStack(art);
            allItems.add(item);
            itemOrigins.add("artifact:" + art.getId());
        }
    }

    private void loadCustomItems() {
        for (var entry : plugin.getCustomItemRegistry().getAll().entrySet()) {
            ItemStack item = entry.getValue().build();
            allItems.add(item);
            itemOrigins.add("custom:" + entry.getKey());
        }
    }

    private void loadBlueprints() {
        var craftingManager = plugin.getAltarManager().getCraftingManager();
        if (craftingManager == null) return;
        var recipes = plugin.getAltarManager().getAllTiers();
        for (var tier : recipes.values()) {
            if (tier.recipes == null) continue;
            for (var recipeEntry : tier.recipes) {
                String fullId = null;
                for (var cmEntry : craftingManager.getAllRecipes().entrySet()) {
                    if (cmEntry.getValue().getResultId().equals(recipeEntry.result)
                            && cmEntry.getKey().endsWith(recipeEntry.id)) {
                        fullId = cmEntry.getKey();
                        break;
                    }
                }
                if (fullId == null) continue;

                var artifact = plugin.getArtifactRegistry().get(recipeEntry.result);
                String name = artifact != null ? artifact.getDisplayName() : recipeEntry.result;
                ItemStack bp = AltarBlockTracker.createBlueprint(fullId, name,
                        recipeEntry.blueprintMaterial, recipeEntry.blueprintName,
                        recipeEntry.blueprintLore, recipeEntry.blueprintCustomModelData);
                allItems.add(bp);
                itemOrigins.add("blueprint:" + fullId);
            }
        }
    }

    private void renderPage() {
        inventory.clear();
        clickHandlers.clear();

        // Табы
        int tabSlot = 0;
        for (Tab tab : Tab.values()) {
            ItemStack tabItem = new ItemStack(tab == currentTab ? Material.ENDER_EYE : tab.icon);
            ItemMeta meta = tabItem.getItemMeta();
            meta.displayName(miniMessage.deserialize(tab.name));
            if (tab == currentTab) {
                meta.lore(List.of(miniMessage.deserialize("<green>✔ выбран")));
            }
            tabItem.setItemMeta(meta);
            setItem(tabSlot, tabItem, e -> switchTab(tab));
            tabSlot += 2;
        }

        // Предметы текущей страницы
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allItems.size());
        int slot = ITEMS_START;
        for (int i = start; i < end; i++) {
            ItemStack display = allItems.get(i).clone();
            ItemMeta meta = display.getItemMeta();
            List<Component> lore = meta != null && meta.lore() != null
                    ? new ArrayList<>(meta.lore()) : new ArrayList<>();
            lore.add(miniMessage.deserialize("<dark_gray>🖱 Клик — получить"));
            if (meta != null) {
                meta.lore(lore);
                display.setItemMeta(meta);
            }
            final int idx = i;
            setItem(slot, display, e -> giveItem(e.getWhoClicked(), idx));
            slot++;
        }

        // Навигация
        ItemStack prev = new ItemStack(currentPage > 0 ? Material.ARROW : Material.BARRIER);
        ItemMeta prevMeta = prev.getItemMeta();
        prevMeta.displayName(miniMessage.deserialize(currentPage > 0 ? "<green>◀ Назад" : "<gray>◀ Назад"));
        prev.setItemMeta(prevMeta);
        setItem(PREV_BTN, prev, currentPage > 0 ? e -> prevPage() : null);

        ItemStack next = new ItemStack(currentPage < maxPage ? Material.ARROW : Material.BARRIER);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(miniMessage.deserialize(currentPage < maxPage ? "<green>Вперёд ▶" : "<gray>Вперёд ▶"));
        next.setItemMeta(nextMeta);
        setItem(NEXT_BTN, next, currentPage < maxPage ? e -> nextPage() : null);

        // Инфо
        setItem(INFO_SLOT, Material.BOOK,
                "<gold>📦 " + currentTab.name,
                List.of("<gray>Всего: <white>" + allItems.size(),
                        "<gray>Страница: <white>" + (currentPage + 1) + "/" + (maxPage + 1),
                        "", "<dark_gray>ЛКМ — получить предмет"),
                null);

        // Заполнение
        fillBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    private void switchTab(Tab tab) {
        currentTab = tab;
        currentPage = 0;
        build();
    }

    private void prevPage() {
        if (currentPage > 0) {
            currentPage--;
            build();
        }
    }

    private void nextPage() {
        if (currentPage < maxPage) {
            currentPage++;
            build();
        }
    }

    private void giveItem(org.bukkit.command.CommandSender sender, int index) {
        if (index < 0 || index >= allItems.size()) return;
        ItemStack item = allItems.get(index).clone();
        if (sender instanceof Player p) {
            p.getInventory().addItem(item).forEach((i, leftover) ->
                    p.getWorld().dropItem(p.getLocation(), leftover));
            p.sendMessage(plugin.getConfigManager().getLang("admin.customitem-given",
                    itemOrigins.get(index), item.getAmount()));
        }
    }

    @Override
    protected void onSlotClick(InventoryClickEvent event, int slot) {
        event.setCancelled(true);
    }
}
