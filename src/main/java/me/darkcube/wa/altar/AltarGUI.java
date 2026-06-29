package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.gui.GUIBase;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class AltarGUI extends GUIBase {

    private static final int[] SLOT_MAP = {0, 1, 2, 9, 10, 11, 18, 19, 20};
    private static final int CRAFT_BTN = 40;
    private static final int COLLECT_BTN = 38;
    private static final int STATUS_SLOT = 22;
    private static final int INFO_SLOT = 13;
    private static final int CLOSE_BTN = 49;

    private final Block altarBlock;
    private final AltarConfig.AltarTier tier;
    private final AltarInventory altarStorage;
    private final AltarCraftingManager cm;
    private final org.bukkit.Location loc;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final int[] itemSlots;

    public AltarGUI(WastelandArtifacts plugin, Player player, Block altarBlock,
                    AltarConfig.AltarTier tier, AltarInventory storage,
                    AltarCraftingManager cm) {
        super(plugin, player, tier.displayName, 6);
        this.altarBlock = altarBlock;
        this.tier = tier;
        this.altarStorage = storage;
        this.cm = cm;
        this.loc = altarBlock.getLocation();
        this.itemSlots = SLOT_MAP;
    }

    @Override
    protected void build() {
        clickHandlers.clear();
        inventory.clear();

        ItemStack[] saved = altarStorage.getAllSlots(loc);

        for (int i = 0; i < 9; i++) {
            int guiSlot = SLOT_MAP[i];
            if (saved[i] != null) {
                inventory.setItem(guiSlot, saved[i]);
            } else {
                String[] names = {"левый верх", "верх", "правый верх",
                        "левый центр", "★ центр", "правый центр",
                        "левый низ", "низ", "правый низ"};
                ItemStack bg = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta m = bg.getItemMeta();
                m.displayName(mm.deserialize("<aqua>" + names[i]));
                bg.setItemMeta(m);
                inventory.setItem(guiSlot, bg);
            }
        }

        setItem(INFO_SLOT, Material.BOOK, "<yellow>" + tier.displayName,
                List.of("<gray>Уровень: " + tier.tier, "<gray>Кулдаун: " + tier.globalCooldown + "с"), null);
        updateRecipe();
        setItem(COLLECT_BTN, Material.HOPPER, "<green>Забрать всё",
                List.of("<gray>Вернуть все предметы"), e -> collectAll());
        setItem(CLOSE_BTN, Material.BARRIER, "<red>Закрыть", null, e -> close());
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);
    }

    @Override
    protected void onSlotClick(InventoryClickEvent event, int slot) {
        if (!isItemSlot(slot)) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            syncFromInventory();
            refreshDisplay();
            updateRecipe();
        });
    }

    private boolean isItemSlot(int slot) {
        for (int s : SLOT_MAP) if (s == slot) return true;
        return false;
    }

    private void syncFromInventory() {
        for (int i = 0; i < 9; i++) {
            int guiSlot = SLOT_MAP[i];
            ItemStack inInv = inventory.getItem(guiSlot);
            if (inInv != null && inInv.getType() != Material.AIR && !isGlass(inInv)) {
                altarStorage.setSlot(loc, i, inInv.clone());
            } else {
                altarStorage.removeSlot(loc, i);
            }
        }
    }

    private void refreshDisplay() {
        String[] names = {"левый верх", "верх", "правый верх",
                "левый центр", "★ центр", "правый центр",
                "левый низ", "низ", "правый низ"};
        for (int i = 0; i < 9; i++) {
            int guiSlot = SLOT_MAP[i];
            ItemStack inv = inventory.getItem(guiSlot);
            if (inv == null || inv.getType().isAir() || isGlass(inv)) {
                ItemStack bg = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta m = bg.getItemMeta();
                m.displayName(mm.deserialize("<aqua>" + names[i]));
                bg.setItemMeta(m);
                inventory.setItem(guiSlot, bg);
            }
        }
    }

    private boolean isGlass(ItemStack item) {
        return item.getType().name().contains("STAINED_GLASS_PANE")
                || item.getType() == Material.GLASS_PANE;
    }

    private void updateRecipe() {
        ItemStack[] slots = altarStorage.getAllSlots(loc);
        var recipe = cm.findRecipe(slots, tier.tier);

        if (recipe != null) {
            var artifact = plugin.getArtifactRegistry().get(recipe.getResultId());
            if (artifact != null) {
                inventory.setItem(STATUS_SLOT, plugin.getArtifactManager().createItemStack(artifact));
            }
            ItemStack btn = new ItemStack(Material.ANVIL);
            ItemMeta m = btn.getItemMeta();
            m.displayName(mm.deserialize("<green>⚡ Создать: " + recipe.getResultId()));
            List<String> lore = new ArrayList<>();
            for (var ing : recipe.getIngredients()) {
                boolean ok = slots[ing.getSlot()] != null && slots[ing.getSlot()].getAmount() >= ing.getAmount();
                lore.add((ok ? "<green>✅" : "<red>❌") + " <gray>"
                        + ing.getType().name() + " x" + ing.getAmount());
            }
            m.lore(lore.stream().map(mm::deserialize).toList());
            btn.setItemMeta(m);
            inventory.setItem(CRAFT_BTN, btn);
            clickHandlers.put(CRAFT_BTN, e -> {
                close();
                plugin.getAltarManager().craftOnAltar(player, altarBlock, tier);
            });
        } else {
            setItem(STATUS_SLOT, Material.BARRIER, "<red>⛔ Нет рецепта",
                    List.of("<gray>Положи ингредиенты в слоты"), null);
            setItem(CRAFT_BTN, Material.RED_STAINED_GLASS_PANE,
                    "<red>Нет рецепта", List.of("<gray>Положи ингредиенты"), null);
        }
    }

    private void collectAll() {
        for (int i = 0; i < 9; i++) {
            ItemStack s = altarStorage.removeSlot(loc, i);
            if (s != null) {
                player.getInventory().addItem(s).values().forEach(drop ->
                        player.getWorld().dropItem(player.getLocation(), drop));
            }
        }
        player.sendMessage(mm.deserialize("<green>✅ Предметы забраны из алтаря"));
        build();
    }

    @Override
    protected void onClose() {
        syncFromInventory();
        altarStorage.save();
    }
}
