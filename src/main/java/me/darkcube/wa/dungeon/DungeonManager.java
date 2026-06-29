package me.darkcube.wa.dungeon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.altar.AltarBlockTracker;
import me.darkcube.wa.artifact.Artifact;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonManager {

    private final WastelandArtifacts plugin;
    private final Map<String, DungeonConfig> dungeonConfigs = new HashMap<>();
    private final Random random = new Random();

    public DungeonManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        dungeonConfigs.clear();
        File dir = new File(plugin.getDataFolder(), "dungeons");
        if (!dir.exists()) return;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                ObjectMapper mapper = plugin.getConfigManager().getYamlMapper();
                DungeonConfig config = mapper.readValue(file, DungeonConfig.class);
                String name = file.getName().replace(".yml", "");
                dungeonConfigs.put(name, config);
                plugin.getComponentLogger().info("<green>Загружен конфиг данжа: " + name);
            } catch (IOException e) {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public void scanAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            scanWorld(world);
        }
    }

    public void scanWorld(World world) {
        plugin.getComponentLogger().info("<yellow>Сканирование мира " + world.getName() + "...");
    }

    @Nullable
    public List<ItemStack> generateLoot(@NotNull String dungeonId) {
        DungeonConfig config = dungeonConfigs.get(dungeonId);
        if (config == null || !config.loot.enabled) return Collections.emptyList();

        List<ItemStack> loot = new ArrayList<>();
        for (var entry : config.loot.artifacts) {
            if (random.nextDouble() * 100 < entry.weight) {
                Artifact artifact = plugin.getArtifactRegistry().get(entry.id);
                if (artifact != null) {
                    ItemStack item = plugin.getArtifactManager().createItemStack(artifact);
                    int count = entry.minCount + random.nextInt(entry.maxCount - entry.minCount + 1);
                    item.setAmount(count);
                    loot.add(item);
                }
            }
        }
        // Blueprint loot
        for (var entry : config.loot.blueprints) {
            if (random.nextDouble() * 100 < entry.weight) {
                ItemStack bp = createBlueprintDrop(entry.recipeId);
                if (bp != null) {
                    bp.setAmount(entry.amount);
                    loot.add(bp);
                }
            }
        }
        return loot;
    }

    private @Nullable ItemStack createBlueprintDrop(String recipeId) {
        var recipe = plugin.getAltarManager().getCraftingManager().getRecipe(recipeId);
        if (recipe == null) return null;
        var artifact = plugin.getArtifactRegistry().get(recipe.getResultId());
        String name = artifact != null ? artifact.getDisplayName() : recipe.getResultId();
        return AltarBlockTracker.createBlueprint(recipeId, name, "PAPER",
                "<gold>📜 Чертёж: " + name, null, 5001);
    }

    public void injectLoot(@NotNull Chest chest, @NotNull String dungeonId) {
        List<ItemStack> loot = generateLoot(dungeonId);
        for (ItemStack item : loot) {
            chest.getInventory().addItem(item);
        }
    }

    public void spawnBoss(@NotNull Location location, @NotNull DungeonConfig.BossConfig bossConfig) {
        if (!bossConfig.enabled) return;

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(bossConfig.entity);
        } catch (IllegalArgumentException e) {
            plugin.getComponentLogger().warn("<red>Неизвестный тип сущности: " + bossConfig.entity);
            return;
        }

        var entity = location.getWorld().spawnEntity(location, entityType);
        if (entity instanceof LivingEntity living) {
            living.setCustomName(plugin.getConfigManager().getLang("boss-spawned"));
            living.setCustomNameVisible(true);

            if (bossConfig.health > 0) {
                var attr = living.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                if (attr != null) { attr.setBaseValue(bossConfig.health); }
                living.setHealth(bossConfig.health);
            }

            if (bossConfig.equipment != null) {
                for (var eq : bossConfig.equipment.entrySet()) {
                    Material mat = Material.matchMaterial(eq.getValue());
                    if (mat != null) {
                        String slot = eq.getKey().toLowerCase();
                        switch (slot) {
                            case "mainhand" -> living.getEquipment().setItemInMainHand(new ItemStack(mat));
                            case "offhand" -> living.getEquipment().setItemInOffHand(new ItemStack(mat));
                            case "helmet" -> living.getEquipment().setHelmet(new ItemStack(mat));
                            case "chestplate" -> living.getEquipment().setChestplate(new ItemStack(mat));
                            case "leggings" -> living.getEquipment().setLeggings(new ItemStack(mat));
                            case "boots" -> living.getEquipment().setBoots(new ItemStack(mat));
                        }
                    }
                }
            }

            if (bossConfig.potionEffects != null) {
                for (var effect : bossConfig.potionEffects) {
                    org.bukkit.potion.PotionEffectType type =
                            org.bukkit.potion.PotionEffectType.getByName(effect.effect);
                    if (type != null) {
                        int duration = effect.duration < 0 ? Integer.MAX_VALUE : effect.duration * 20;
                        living.addPotionEffect(new org.bukkit.potion.PotionEffect(
                                type, duration, effect.amplifier, true, false
                        ));
                    }
                }
            }

            if (bossConfig.artifact != null) {
                living.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "boss_artifact"),
                        PersistentDataType.STRING, bossConfig.artifact
                );
            }
            if (bossConfig.blueprint != null) {
                living.getPersistentDataContainer().set(
                        new NamespacedKey(plugin, "boss_blueprint"),
                        PersistentDataType.STRING, bossConfig.blueprint
                );
            }
        }
    }

    public @Nullable DungeonConfig getConfig(String id) {
        return dungeonConfigs.get(id);
    }

    public @NotNull Map<String, DungeonConfig> getAllConfigs() {
        return Map.copyOf(dungeonConfigs);
    }

    public static class DungeonConfig {
        public boolean enabled = true;
        public LootConfig loot = new LootConfig();
        public SpecialChestConfig specialChests = new SpecialChestConfig();
        public BossConfigSection bosses = new BossConfigSection();

        public static class LootConfig {
            public boolean enabled = true;
            public String mode = "INJECT";
            public double replaceChance = 0.15;
            public List<LootEntry> artifacts = new ArrayList<>();
            public List<BlueprintLootEntry> blueprints = new ArrayList<>();
        }

        public static class LootEntry {
            public String id;
            public double weight;
            public int minCount = 1;
            public int maxCount = 1;
        }

        public static class BlueprintLootEntry {
            public String recipeId;
            public double weight = 5;
            public int amount = 1;
        }

        public static class SpecialChestConfig {
            public boolean enabled = true;
            public List<ChestPosition> positions = new ArrayList<>();
        }

        public static class ChestPosition {
            public int rx, ry, rz;
        }

        public static class BossConfigSection {
            public boolean enabled = true;
            public List<BossConfig> types = new ArrayList<>();
        }

        public static class BossConfig {
            public boolean enabled = true;
            public String entity;
            public String name;
            public String artifact;
            public String blueprint;
            public double dropChance = 0.25;
            public double health = 100;
            public Map<String, String> equipment = new HashMap<>();
            public List<PotionEffectEntry> potionEffects = new ArrayList<>();
        }

        public static class PotionEffectEntry {
            public String effect;
            public int amplifier;
            public int duration;
        }
    }
}
