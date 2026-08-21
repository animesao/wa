package me.darkcube.wa.crafting;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.altar.AltarBlockTracker;
import me.darkcube.wa.altar.AltarConfig;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.crafting.ArtifactRecipe.IngredientDef;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class CraftingManager {

    private final WastelandArtifacts plugin;
    private final List<NamespacedKey> discoverableRecipes = new ArrayList<>();

    public CraftingManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        for (Artifact artifact : plugin.getArtifactRegistry().getAll()) {
            if (artifact.getRecipe() != null) {
                registerRecipe(artifact);
            }
        }
        registerBlueprintRecipes();
    }

    @SuppressWarnings("unchecked")
    private void loadExternalWorkbenchRecipes(Map<String, WorkbenchDef> normals, List<DirectArtifactRecipe> directs) {
        File file = new File(plugin.getDataFolder(), "blueprint_workbench.yml");
        if (!file.exists()) return;
        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(file, Map.class);
            Map<String, Object> recipes = (Map<String, Object>) root.get("recipes");
            if (recipes == null) return;
            for (var entry : recipes.entrySet()) {
                Map<String, Object> def = (Map<String, Object>) entry.getValue();
                if (def == null || Boolean.FALSE.equals(def.get("enabled"))) continue;

                String type = (String) def.get("type");
                List<String> pattern = (List<String>) def.get("pattern");
                Map<String, String> ingredients = (Map<String, String>) def.get("ingredients");
                if (pattern == null || pattern.isEmpty() || ingredients == null) continue;

                if ("direct_artifact".equals(type)) {
                    String resultId = (String) def.get("result");
                    if (resultId != null) {
                        directs.add(new DirectArtifactRecipe(entry.getKey(), resultId, pattern, ingredients));
                    }
                } else {
                    normals.put(entry.getKey(), new WorkbenchDef(pattern, ingredients));
                }
            }
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<yellow>Ошибка загрузки blueprint_workbench.yml: " + e.getMessage());
        }
    }

    private void registerBlueprintRecipes() {
        var altarManager = plugin.getAltarManager();
        if (altarManager == null || altarManager.getCraftingManager() == null) return;
        Map<String, WorkbenchDef> external = new HashMap<>();
        List<DirectArtifactRecipe> directRecipes = new ArrayList<>();
        loadExternalWorkbenchRecipes(external, directRecipes);

        // Регистрируем прямые рецепты артефактов (type: direct_artifact)
        for (var dr : directRecipes) {
            Artifact art = plugin.getArtifactRegistry().get(dr.resultId);
            if (art == null) {
                plugin.getComponentLogger().warn("<yellow>Артефакт '" + dr.resultId + "' не найден для рецепта " + dr.id);
                continue;
            }
            ItemStack result = plugin.getArtifactManager().createItemStack(art);
            NamespacedKey key = new NamespacedKey(plugin, "direct_" + dr.id.replaceAll("[^a-z0-9]", "_"));
            registerShaped(key, result, dr.pattern, dr.ingredients);
        }

        List<AltarConfig.RecipeEntry> recipeEntries = new ArrayList<>();
        for (var tier : altarManager.getAllTiers().values()) {
            if (tier.recipes != null) recipeEntries.addAll(tier.recipes);
        }
        for (var configEntry : recipeEntries) {
            String fullId = null;
            for (var cmEntry : altarManager.getCraftingManager().getAllRecipes().entrySet()) {
                if (cmEntry.getValue().getResultId().equals(configEntry.result)
                        && cmEntry.getKey().endsWith(configEntry.id)) {
                    fullId = cmEntry.getKey();
                    break;
                }
            }
            if (fullId == null) continue;
            String safeId = "bp_" + fullId.replaceAll("[^a-z0-9]", "_");
            NamespacedKey key = new NamespacedKey(plugin, safeId);
            var artifact = plugin.getArtifactRegistry().get(configEntry.result);
            String name = artifact != null ? artifact.getDisplayName() : configEntry.result;
            ItemStack bp = AltarBlockTracker.createBlueprint(fullId, name,
                    configEntry.blueprintMaterial, configEntry.blueprintName,
                    configEntry.blueprintLore, configEntry.blueprintCustomModelData);
            WorkbenchDef externalDef = external.get(fullId);
            if (externalDef != null) {
                registerShaped(key, bp, externalDef.pattern, externalDef.ingredients);
            } else if (configEntry.workbench != null && !configEntry.workbench.pattern.isEmpty()) {
                registerShaped(key, bp, configEntry.workbench.pattern, configEntry.workbench.ingredients);
            } else {
                int tierLevel = configEntry.cooldown >= 30 ? 3 : configEntry.cooldown >= 15 ? 2 : 1;
                autoGenRecipe(key, bp, tierLevel);
            }
        }
    }

        private void registerShaped(NamespacedKey key, ItemStack result, List<String> pattern, Map<String, String> ingredients) {
        try {
            for (String row : pattern) {
                if (row.length() < 1 || row.length() > 3) {
                    plugin.getComponentLogger().warn("<yellow>Пропущен рецепт " + key + ": строка '" + row + "' имеет " + row.length() + " символов");
                    return;
                }
            }
            ShapedRecipe sr = new ShapedRecipe(key, result);
            sr.shape(pattern.toArray(new String[0]));
            for (var ing : ingredients.entrySet()) {
                if (ing.getKey().length() != 1) continue;
                char ch = ing.getKey().charAt(0);
                RecipeChoice choice = parseSimpleIngredient(ing.getValue());
                if (choice != null) sr.setIngredient(ch, choice);
            }
            Bukkit.addRecipe(sr);
            discoverableRecipes.add(key);
        } catch (IllegalArgumentException e) {
            plugin.getComponentLogger().warn("<yellow>Пропущен рецепт " + key + ": " + e.getMessage());
        }
    }

    private @NotNull RecipeChoice parseSimpleIngredient(String value) {
        if (value.startsWith("artifact:")) {
            String artId = value.substring(9);
            Artifact art = plugin.getArtifactRegistry().get(artId);
            if (art != null) {
                return new RecipeChoice.ExactChoice(plugin.getArtifactManager().createItemStack(art));
            }
        }
        if (value.startsWith("custom:")) {
            String customId = value.substring(7);
            ItemStack customItem = plugin.getCustomItemRegistry().create(customId);
            if (customItem != null) return new RecipeChoice.ExactChoice(customItem);
        }
        if (value.startsWith("itemsadder:")) {
            String iaId = value.substring(11);
            ItemStack iaItem = plugin.getCustomItemRegistry().create("itemsadder:" + iaId);
            if (iaItem != null) return new RecipeChoice.ExactChoice(iaItem);
        }
        Material mat = Material.matchMaterial(value);
        if (mat != null) return new RecipeChoice.MaterialChoice(mat);
        return new RecipeChoice.MaterialChoice(Material.STONE);
    }

    private @NotNull RecipeChoice parseIngredient(IngredientDef def) {
        if (def == null) return new RecipeChoice.MaterialChoice(Material.STONE);

        String type = def.type;
        if (type == null) return new RecipeChoice.MaterialChoice(Material.STONE);

        if (type.startsWith("artifact:")) {
            String artId = type.substring(9);
            Artifact art = plugin.getArtifactRegistry().get(artId);
            if (art != null) {
                return new RecipeChoice.ExactChoice(plugin.getArtifactManager().createItemStack(art));
            }
        }
        if (type.startsWith("custom:")) {
            String customId = type.substring(7);
            ItemStack customItem = plugin.getCustomItemRegistry().create(customId);
            if (customItem != null) return new RecipeChoice.ExactChoice(customItem);
        }
        if (type.startsWith("itemsadder:")) {
            String iaId = type.substring(11);
            ItemStack iaItem = plugin.getCustomItemRegistry().create("itemsadder:" + iaId);
            if (iaItem != null) return new RecipeChoice.ExactChoice(iaItem);
        }

        ItemStack template = def.buildTemplate();
        if (template != null && (def.name != null || def.customModelData > 0)) {
            return new RecipeChoice.ExactChoice(template);
        }

        Material mat = Material.matchMaterial(type);
        if (mat != null) return new RecipeChoice.MaterialChoice(mat);
        return new RecipeChoice.MaterialChoice(Material.STONE);
    }

    private void autoGenRecipe(NamespacedKey key, ItemStack bp, int tierLevel) {
        ShapedRecipe sr = new ShapedRecipe(key, bp);
        switch (tierLevel) {
            case 3 -> { sr.shape("NDN","DBD","NDN");
                sr.setIngredient('N', Material.NETHERITE_INGOT);
                sr.setIngredient('D', Material.DIAMOND_BLOCK);
                sr.setIngredient('B', Material.BOOK); }
            case 2 -> { sr.shape("DED","EBE","DED");
                sr.setIngredient('D', Material.DIAMOND);
                sr.setIngredient('E', Material.EMERALD);
                sr.setIngredient('B', Material.BOOK); }
            default -> { sr.shape(" I ","IBI"," I ");
                sr.setIngredient('I', Material.IRON_INGOT);
                sr.setIngredient('B', Material.BOOK); }
        }
        Bukkit.addRecipe(sr);
        discoverableRecipes.add(key);
    }

    public void discoverRecipes(org.bukkit.entity.Player player) {
        for (NamespacedKey key : discoverableRecipes) {
            player.discoverRecipe(key);
        }
    }

    private void registerRecipe(@NotNull Artifact artifact) {
        ArtifactRecipe recipe = artifact.getRecipe();
        if (recipe == null) return;

        ItemStack result = plugin.getArtifactManager().createItemStack(artifact);
        result.setAmount(recipe.getResultAmount());
        NamespacedKey key = new NamespacedKey(plugin, "artifact_" + artifact.getId());

        if ("shapeless".equals(recipe.getType())) {
            ShapelessRecipe slr = new ShapelessRecipe(key, result);
            for (var entry : recipe.getIngredients().entrySet()) {
                slr.addIngredient(parseIngredient(entry.getValue()));
            }
            Bukkit.addRecipe(slr);
        } else {
            ShapedRecipe sr = new ShapedRecipe(key, result);
            sr.shape(recipe.getPattern().toArray(new String[0]));
            for (var entry : recipe.getIngredients().entrySet()) {
                if (entry.getKey().length() == 1) {
                    sr.setIngredient(entry.getKey().charAt(0), parseIngredient(entry.getValue()));
                }
            }
            Bukkit.addRecipe(sr);
        }
    }

    public void unregisterRecipes() {
        List<NamespacedKey> keys = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(r -> {
            if (r instanceof Keyed k && k.getKey().getNamespace().equals(plugin.getName().toLowerCase())) {
                keys.add(k.getKey());
            }
        });
        keys.forEach(Bukkit::removeRecipe);
    }

    private record WorkbenchDef(List<String> pattern, Map<String, String> ingredients) {}
    private record DirectArtifactRecipe(String id, String resultId, List<String> pattern, Map<String, String> ingredients) {}
}
