package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AltarCraftingManager {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<String, AltarRecipe> recipes = new LinkedHashMap<>();

    public AltarCraftingManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes(@NotNull AltarConfig config) {
        recipes.clear();
        if (config == null || config.altars == null) return;

        for (var altarEntry : config.altars.entrySet()) {
            AltarConfig.AltarTier tier = altarEntry.getValue();
            if (tier.recipes == null) continue;

            for (var recipeEntry : tier.recipes) {
                String recipeId = altarEntry.getKey() + "_" + recipeEntry.id;
                List<AltarRecipe.Ingredient> ingredients = new ArrayList<>();
                for (var ing : recipeEntry.ingredients) {
                    ItemStack template = ing.buildTemplate();
                    if (template != null && (ing.name != null || ing.lore != null || ing.customModelData != null)) {
                        ingredients.add(new AltarRecipe.Ingredient(template, ing.amount, ing.slot));
                    } else {
                        ingredients.add(new AltarRecipe.Ingredient(ing.type, ing.amount, ing.slot));
                    }
                }

                AltarRecipe.CatalystConfig catalyst = null;
                if (recipeEntry.catalyst != null) {
                    ItemStack catTemplate = recipeEntry.catalyst.buildTemplate();
                    if (catTemplate != null && (recipeEntry.catalyst.name != null || recipeEntry.catalyst.customModelData != null)) {
                        catalyst = new AltarRecipe.CatalystConfig(
                                recipeEntry.catalyst.item, recipeEntry.catalyst.consume, catTemplate
                        );
                    } else {
                        catalyst = new AltarRecipe.CatalystConfig(
                                recipeEntry.catalyst.item, recipeEntry.catalyst.consume
                        );
                    }
                }

                AltarRecipe recipe = new AltarRecipe(
                        recipeId, recipeEntry.result, tier.tier,
                        catalyst, recipeEntry.experience, recipeEntry.cooldown,
                        ingredients
                );
                recipes.put(recipeId, recipe);
            }
        }
    }

    public @Nullable AltarRecipe findRecipe(@NotNull ItemStack[] slots, int altarTier) {
        for (AltarRecipe recipe : recipes.values()) {
            if (recipe.getTier() > altarTier) continue;
            if (recipe.matches(slots)) {
                return recipe;
            }
        }
        return null;
    }

    public boolean craft(@NotNull Player player, @NotNull AltarRecipe recipe,
                          @NotNull LocationData altarLoc, @NotNull AltarInventory inventory) {
        ItemStack[] slots = inventory.getAllSlots(altarLoc.toLocation(player.getWorld()));

        if (!recipe.matches(slots)) return false;

        if (recipe.getExperience() > 0 && player.getLevel() < recipe.getExperience()) {
            player.sendMessage(mm.deserialize("<red>Нужно " + recipe.getExperience() + " уровней опыта!"));
            return false;
        }

        if (recipe.getExperience() > 0) {
            player.setLevel(player.getLevel() - recipe.getExperience());
        }

        for (AltarRecipe.Ingredient ing : recipe.getIngredients()) {
            ItemStack slotItem = slots[ing.slot];
            if (slotItem != null) {
                int remaining = slotItem.getAmount() - ing.amount;
                if (remaining <= 0) {
                    inventory.removeSlot(altarLoc.toLocation(player.getWorld()), ing.slot);
                } else {
                    slotItem.setAmount(remaining);
                    inventory.setSlot(altarLoc.toLocation(player.getWorld()), ing.slot, slotItem);
                }
            }
        }

        String resultId = recipe.getResultId();
        plugin.getArtifactManager().giveArtifact(player, resultId, 1);
        player.sendMessage(mm.deserialize(plugin.getConfigManager().getLang("altar-craft-success")));

        return true;
    }

    public @Nullable AltarRecipe getRecipe(String id) {
        return recipes.get(id);
    }

    public @NotNull Map<String, AltarRecipe> getAllRecipes() {
        return Collections.unmodifiableMap(recipes);
    }

    public int getRecipeCount() {
        return recipes.size();
    }

    public static class LocationData {
        public final String world;
        public final int x, y, z;

        public LocationData(String world, int x, int y, int z) {
            this.world = world; this.x = x; this.y = y; this.z = z;
        }

        public org.bukkit.Location toLocation(org.bukkit.World w) {
            return new org.bukkit.Location(w, x, y, z);
        }
    }
}
