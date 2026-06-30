package me.darkcube.wa.crafting;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArtifactRecipe {

    private final String type;
    private final List<String> pattern;
    private final Map<String, IngredientDef> ingredients;
    private final int resultAmount;

    public ArtifactRecipe(String type, List<String> pattern, Map<String, IngredientDef> ingredients, int resultAmount) {
        this.type = type;
        this.pattern = pattern;
        this.ingredients = ingredients;
        this.resultAmount = resultAmount;
    }

    public String getType() { return type; }
    public List<String> getPattern() { return pattern; }
    public Map<String, IngredientDef> getIngredients() { return ingredients; }
    public int getResultAmount() { return resultAmount; }

    public static class IngredientDef {
        public final String type;       // material name, or "artifact:xxx", or "custom:xxx"
        public final String name;       // display name (MiniMessage), optional
        public final List<String> lore; // lore lines (MiniMessage), optional
        public final int customModelData;

        public IngredientDef(String type, String name, List<String> lore, int customModelData) {
            this.type = type;
            this.name = name;
            this.lore = lore;
            this.customModelData = customModelData;
        }

        public IngredientDef(String type) {
            this.type = type;
            this.name = null;
            this.lore = null;
            this.customModelData = 0;
        }

        public ItemStack buildTemplate() {
            ItemStack template = new ItemStack(Material.STONE);

            // Пытаемся создать из type
            if (type == null) return null;

            // Артефакт
            if (type.startsWith("artifact:")) {
                return null; // обрабатывается отдельно
            }
            // Кастомный предмет
            if (type.startsWith("custom:")) {
                return null; // обрабатывается отдельно
            }
            // ItemsAdder предмет
            if (type.startsWith("itemsadder:")) {
                return null; // обрабатывается отдельно
            }
            // Обычный материал
            Material mat = Material.matchMaterial(type);
            if (mat != null) {
                template = new ItemStack(mat);
            }

            ItemMeta meta = template.getItemMeta();
            if (meta == null) return template;

            if (name != null && !name.isEmpty()) {
                meta.displayName(MiniMessage.miniMessage().deserialize(name));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream().map(l -> MiniMessage.miniMessage().deserialize(l)).toList());
            }
            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            template.setItemMeta(meta);
            return template;
        }
    }
}
