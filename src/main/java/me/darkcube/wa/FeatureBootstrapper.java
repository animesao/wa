package me.darkcube.wa;

import me.darkcube.wa.feature.FeatureConfig;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FeatureBootstrapper {

    private final WastelandArtifacts plugin;

    public FeatureBootstrapper(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void saveDefaultResources() {
        plugin.saveDefaultConfig();
        plugin.saveResource("altars.yml", false);
        for (String lang : List.of("en_US", "ru_RU", "de_DE", "fr_FR", "zh_CN")) {
            plugin.saveResource("lang/" + lang + ".yml", false);
        }
        plugin.saveResource("artifacts/examples.yml", false);
        plugin.saveResource("dungeons/default.yml", false);
        plugin.saveResource("mob_loot.yml", false);
        plugin.saveResource("custom_items.yml", false);
        plugin.saveResource("blueprint_workbench.yml", false);
        plugin.saveResource("rarities.yml", false);
        plugin.saveResource("balance.yml", false);

        plugin.saveResource("features/collection.yml", false);
        plugin.saveResource("features/sets.yml", false);
        plugin.saveResource("features/abilities.yml", false);
        plugin.saveResource("features/upgrades.yml", false);
        plugin.saveResource("features/fishing_loot.yml", false);
        plugin.saveResource("features/elites.yml", false);
        plugin.saveResource("features/xp.yml", false);
        plugin.saveResource("features/achievements.yml", false);
    }

    @SuppressWarnings("unchecked")
    public void initFeatures(FeatureConfig cfg) {
        if (cfg.collection && plugin.getDatabaseManager() != null) {
            var collectionManager = new me.darkcube.wa.feature.collection.CollectionManager(plugin, plugin.getDatabaseManager());
            plugin.setCollectionManager(collectionManager);
            plugin.getComponentLogger().info("<green>Feature: Collection активна");
        }
        if (cfg.artifactSets) {
            var setManager = new me.darkcube.wa.feature.sets.SetManager(plugin);
            plugin.setSetManager(setManager);
            loadSets();
            plugin.getComponentLogger().info("<green>Feature: Sets активна");
        }
        if (cfg.activeAbilities) {
            var abilityManager = new me.darkcube.wa.feature.abilities.AbilityManager(plugin);
            plugin.setAbilityManager(abilityManager);
            loadAbilities();
            plugin.getComponentLogger().info("<green>Feature: Abilities активна");
        }
        if (cfg.upgrades && plugin.getDatabaseManager() != null) {
            var upgradeManager = new me.darkcube.wa.feature.upgrades.UpgradeManager(plugin, plugin.getDatabaseManager());
            plugin.setUpgradeManager(upgradeManager);
            loadUpgradeConfig();
            plugin.getComponentLogger().info("<green>Feature: Upgrades активна");
        }
        if (cfg.fishing) {
            var fishingListener = new me.darkcube.wa.feature.fishing.FishingListener(plugin);
            plugin.setFishingListener(fishingListener);
            loadFishingConfig();
            plugin.getComponentLogger().info("<green>Feature: Fishing активна");
        }
        if (cfg.customMobs) {
            var eliteMobManager = new me.darkcube.wa.feature.elites.EliteMobManager(plugin);
            plugin.setEliteMobManager(eliteMobManager);
            loadEliteConfig();
            plugin.getComponentLogger().info("<green>Feature: EliteMobs активна");
        }
        if (cfg.artifactXP && plugin.getDatabaseManager() != null) {
            var artifactXPManager = new me.darkcube.wa.feature.xp.ArtifactXPManager(plugin, plugin.getDatabaseManager());
            plugin.setArtifactXPManager(artifactXPManager);
            loadXPConfig();
            plugin.getComponentLogger().info("<green>Feature: ArtifactXP активна");
        }
        if (cfg.achievements && plugin.getDatabaseManager() != null) {
            var achManager = new me.darkcube.wa.feature.achievements.AchievementManager(plugin, plugin.getDatabaseManager());
            plugin.setAchievementManager(achManager);
            loadAchievements();
            plugin.getComponentLogger().info("<green>Feature: Achievements активна");
        }
        if (cfg.sockets) {
            var gemManager = new me.darkcube.wa.feature.socket.GemManager(plugin);
            plugin.setGemManager(gemManager);
            loadGems();
            plugin.getComponentLogger().info("<green>Feature: Sockets активна");
        }
    }


    @SuppressWarnings("unchecked")
    public void loadSets() {
        File file = new File(plugin.getDataFolder(), "features/sets.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var setsSec = yaml.getConfigurationSection("sets");
            if (setsSec == null) return;
            for (String id : setsSec.getKeys(false)) {
                if (!setsSec.getBoolean(id + ".enabled", true)) continue;
                String name = setsSec.getString(id + ".name", id);
                List<String> artifacts = setsSec.getStringList(id + ".artifacts");
                var bonuses = setsSec.getMapList(id + ".bonuses");
                var setBonuses = bonuses.stream().map(b -> new me.darkcube.wa.feature.sets.ArtifactSet.SetBonus(
                        (int) b.get("pieces"),
                        (String) b.get("description"),
                        (List<String>) b.get("effects")
                )).toList();
                plugin.getSetManager().registerSet(new me.darkcube.wa.feature.sets.ArtifactSet(
                        id, name, artifacts, setBonuses));
            }
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки sets.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadAbilities() {
        File file = new File(plugin.getDataFolder(), "features/abilities.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var abilSec = yaml.getConfigurationSection("abilities");
            if (abilSec == null) return;
            for (String id : abilSec.getKeys(false)) {
                if (!abilSec.getBoolean(id + ".enabled", true)) continue;
                var ability = new me.darkcube.wa.feature.abilities.Ability(
                        id,
                        abilSec.getString(id + ".name", id),
                        abilSec.getInt(id + ".cooldown", 10),
                        me.darkcube.wa.feature.abilities.AbilityType.valueOf(
                                abilSec.getString(id + ".type", "AOE").toUpperCase()),
                        abilSec.getDouble(id + ".damage", 0),
                        abilSec.getDouble(id + ".radius", 0),
                        abilSec.getDouble(id + ".distance", 0),
                        abilSec.getDouble(id + ".heal", 0),
                        abilSec.getInt(id + ".duration", 0),
                        abilSec.getString(id + ".projectile", ""),
                        abilSec.getString(id + ".command", ""),
                        abilSec.getDouble(id + ".knockback", 0),
                        (List<Map<String, Object>>) (List) abilSec.getMapList(id + ".effects"),
                        (List<Map<String, Object>>) (List) abilSec.getMapList(id + ".attributes"),
                        abilSec.getString(id + ".particle"),
                        abilSec.getString(id + ".sound"),
                        abilSec.getStringList(id + ".lore")
                );
                plugin.getAbilityManager().registerAbility(ability);
            }
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки abilities.yml: " + e.getMessage());
        }
    }

    public void loadUpgradeConfig() {
        File file = new File(plugin.getDataFolder(), "features/upgrades.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            plugin.getUpgradeManager().loadConfig(yaml.getConfigurationSection("upgrades").getValues(false));
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки upgrades.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFishingConfig() {
        File file = new File(plugin.getDataFolder(), "features/fishing_loot.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var entries = (List<Map<String, Object>>) (List) yaml.getMapList("fishing.entries");
            plugin.getFishingListener().loadConfig(entries);
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки fishing_loot.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadEliteConfig() {
        File file = new File(plugin.getDataFolder(), "features/elites.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var types = (List<Map<String, Object>>) (List) yaml.getMapList("elites.types");
            plugin.getEliteMobManager().loadConfig(types);
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки elites.yml: " + e.getMessage());
        }
    }

    public void loadXPConfig() {
        File file = new File(plugin.getDataFolder(), "features/xp.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            plugin.getArtifactXPManager().loadConfig(yaml.getConfigurationSection("xp").getValues(false));
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки xp.yml: " + e.getMessage());
        }
    }

    public void loadAchievements() {
        File file = new File(plugin.getDataFolder(), "features/achievements.yml");
        if (!file.exists()) {
        plugin.saveResource("features/achievements.yml", false);
        plugin.saveResource("features/gems.yml", false);
        }
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            plugin.getAchievementManager().loadConfig(yaml.getConfigurationSection("achievements"));
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки achievements.yml: " + e.getMessage());
        }
    }

    public void loadGems() {
        File file = new File(plugin.getDataFolder(), "features/gems.yml");
        if (!file.exists()) {
            plugin.saveResource("features/gems.yml", false);
        }
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            plugin.getGemManager().loadConfig(yaml.getConfigurationSection("gems"));
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки gems.yml: " + e.getMessage());
        }
    }
}
