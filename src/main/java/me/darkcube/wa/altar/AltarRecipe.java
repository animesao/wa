package me.darkcube.wa.altar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AltarRecipe {

    private final String id;
    private final String resultId;
    private final int tier;
    private final CatalystConfig catalyst;
    private final int experience;
    private final int cooldown;
    private final List<Ingredient> ingredients;

    public AltarRecipe(String id, String resultId, int tier,
                       CatalystConfig catalyst, int experience, int cooldown,
                       List<Ingredient> ingredients) {
        this.id = id;
        this.resultId = resultId;
        this.tier = tier;
        this.catalyst = catalyst;
        this.experience = experience;
        this.cooldown = cooldown;
        this.ingredients = ingredients;
    }

    public String getId() { return id; }
    public String getResultId() { return resultId; }
    public int getTier() { return tier; }
    public CatalystConfig getCatalyst() { return catalyst; }
    public int getExperience() { return experience; }
    public int getCooldown() { return cooldown; }
    public List<Ingredient> getIngredients() { return ingredients; }

    public boolean matches(ItemStack[] slots) {
        for (Ingredient ing : ingredients) {
            int slot = ing.slot;
            if (slot < 0 || slot >= slots.length) return false;
            ItemStack item = slots[slot];
            if (item == null) return false;
            if (item.getAmount() < ing.amount) return false;

            if (ing.template != null) {
                if (!item.isSimilar(ing.template)) return false;
            } else {
                if (item.getType() != ing.type) return false;
            }
        }
        return true;
    }

    public static class Ingredient {
        public final Material type;
        public final int amount;
        public final int slot;
        public final ItemStack template;

        public Ingredient(Material type, int amount, int slot) {
            this.type = type;
            this.amount = amount;
            this.slot = slot;
            this.template = null;
        }

        public Ingredient(ItemStack template, int amount, int slot) {
            this.type = template.getType();
            this.amount = amount;
            this.slot = slot;
            this.template = template;
        }

        public Material getType() { return type; }
        public int getAmount() { return amount; }
        public int getSlot() { return slot; }
        public ItemStack getTemplate() { return template; }
    }

    public static class CatalystConfig {
        private final Material item;
        private final boolean consume;
        private final ItemStack template;

        public CatalystConfig(Material item, boolean consume) {
            this.item = item;
            this.consume = consume;
            this.template = null;
        }

        public CatalystConfig(Material item, boolean consume, ItemStack template) {
            this.item = item;
            this.consume = consume;
            this.template = template;
        }

        public Material getItem() { return item; }
        public boolean isConsume() { return consume; }
        public ItemStack getTemplate() { return template; }
    }
}
