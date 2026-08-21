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
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Защита крафта: кастомные предметы (артефакты, кастомные ингредиенты)
 * принимаются ТОЛЬКО если рецепт их явно ожидает.
 * Если рецепт ожидает ванильный материал — кастомный предмет отклоняется.
 */
public class CraftingProtectionListener implements Listener {

    private final WastelandArtifacts plugin;

    public CraftingProtectionListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (!hasCustomItems(matrix)) return;

        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            // Нет рецепта — кастомные предметы в сетке блокируют результат
            event.getInventory().setResult(null);
            return;
        }

        // Есть рецепт — проверяем, что каждый кастомный предмет совпадает с ожидаемым ингредиентом
        if (!recipeMatchesCustomItems(event.getInventory(), recipe)) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent event) {
        ItemStack[] matrix = event.getInventory().getMatrix();
        if (!hasCustomItems(matrix)) return;

        Recipe recipe = event.getRecipe();
        if (recipe == null || !recipeMatchesCustomItems(event.getInventory(), recipe)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack first = event.getInventory().getItem(0);
        ItemStack second = event.getInventory().getItem(1);
        if (isProtected(first) || isProtected(second)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        ItemStack top = event.getInventory().getItem(0);
        ItemStack bottom = event.getInventory().getItem(1);
        if (isProtected(top) || isProtected(bottom)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        ItemStack template = event.getInventory().getItem(0);
        ItemStack base = event.getInventory().getItem(1);
        ItemStack addition = event.getInventory().getItem(2);
        if (isProtected(template) || isProtected(base) || isProtected(addition)) {
            event.setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (isProtected(event.getSource())) {
            event.setCancelled(true);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Проверка: кастомный предмет совпадает с рецептом?
    // ═══════════════════════════════════════════════════════════════

    /**
     * Проверяет, что каждый кастомный предмет в сетке совпадает
     * с соответствующим Choice рецепта.
     */
    private boolean recipeMatchesCustomItems(CraftingInventory inv, Recipe recipe) {
        ItemStack[] matrix = inv.getMatrix();

        if (recipe instanceof ShapedRecipe shaped) {
            String[] shape = shaped.getShape();
            int width = shape[0].length();
            int height = shape.length;
            // getIngredientMap() возвращает Map<Character, ItemStack> — шаблоны ингредиентов
            Map<Character, ItemStack> ingredientMap = shaped.getIngredientMap();

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int idx = row * 3 + col;
                    if (idx >= matrix.length) continue;
                    ItemStack item = matrix[idx];
                    if (!isCustom(item)) continue;

                    char ch = shape[row].charAt(col);
                    if (ch == ' ') continue;
                    ItemStack expected = ingredientMap.get(ch);
                    // Если рецепт ожидает ванильный предмет, а положили кастомный — отклоняем
                    if (expected == null || (!isCustom(expected) && isCustom(item))) {
                        return false;
                    }
                }
            }
            return true;
        }

        if (recipe instanceof ShapelessRecipe shapeless) {
            List<ItemStack> expectedIngredients = new ArrayList<>(shapeless.getIngredientList());
            List<ItemStack> customItems = new ArrayList<>();
            for (ItemStack item : matrix) {
                if (isCustom(item)) customItems.add(item);
            }

            for (ItemStack custom : customItems) {
                boolean matched = false;
                for (var it = expectedIngredients.iterator(); it.hasNext(); ) {
                    ItemStack expected = it.next();
                    if (isCustom(expected) && expected.isSimilar(custom)) {
                        it.remove();
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }
            return true;
        }

        // Неизвестный тип рецепта — блокируем кастомные предметы
        return !hasCustomItems(matrix);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Утилиты
    // ═══════════════════════════════════════════════════════════════

    private boolean hasCustomItems(ItemStack[] items) {
        for (ItemStack item : items) {
            if (isCustom(item)) return true;
        }
        return false;
    }

    private boolean isCustom(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        return plugin.getArtifactManager().isArtifact(item)
                || plugin.getCustomItemRegistry().isCustomItem(item);
    }

    private boolean isProtected(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        if (plugin.getArtifactManager().isArtifact(item)) return true;
        if (plugin.getCustomItemRegistry().isCustomItem(item)) return true;
        return false;
    }
}
