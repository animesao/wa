package me.darkcube.wa;

import me.darkcube.wa.altar.AltarBlockListener;
import me.darkcube.wa.altar.AltarListener;
import me.darkcube.wa.altar.AltarManager;
import me.darkcube.wa.api.WastelandArtifactsAPI;
import me.darkcube.wa.artifact.ArtifactManager;
import me.darkcube.wa.artifact.ArtifactRegistry;
import me.darkcube.wa.artifact.rarity.RarityManager;
import me.darkcube.wa.artifact.component.ComponentRegistry;
import me.darkcube.wa.bag.ArtifactBagListener;
import me.darkcube.wa.bag.ArtifactBagManager;
import me.darkcube.wa.commands.*;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.config.MainConfig;
import me.darkcube.wa.crafting.CraftingManager;
import me.darkcube.wa.database.DatabaseManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.dungeon.MobLootListener;
import me.darkcube.wa.feature.FeatureConfig;
import me.darkcube.wa.feature.FeatureManager;
import me.darkcube.wa.feature.abilities.Ability;
import me.darkcube.wa.feature.abilities.AbilityListener;
import me.darkcube.wa.feature.abilities.AbilityManager;
import me.darkcube.wa.feature.abilities.AbilityType;
import me.darkcube.wa.feature.arena.ArenaGUI;
import me.darkcube.wa.feature.arena.ArenaListener;
import me.darkcube.wa.feature.arena.BossArenaManager;
import me.darkcube.wa.feature.collection.CollectionGUI;
import me.darkcube.wa.feature.collection.CollectionManager;
import me.darkcube.wa.feature.elites.EliteMobListener;
import me.darkcube.wa.feature.elites.EliteMobManager;
import me.darkcube.wa.feature.fishing.FishingListener;
import me.darkcube.wa.feature.sets.SetManager;
import me.darkcube.wa.feature.upgrades.UpgradeManager;
import me.darkcube.wa.feature.xp.ArtifactXPManager;
import me.darkcube.wa.feature.xp.XPListener;
import me.darkcube.wa.gui.ArtifactEditorGUI;
import me.darkcube.wa.gui.ChatInputManager;
import me.darkcube.wa.integration.ItemsAdderIntegration;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.listener.*;
import me.darkcube.wa.resourcepack.ResourcePackManager;
import me.darkcube.wa.schematic.SchematicManager;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;

public final class WastelandArtifacts extends JavaPlugin {

    private static WastelandArtifacts instance;
    private ConfigManager configManager;
    private RarityManager rarityManager;
    private ArtifactRegistry artifactRegistry;
    private ArtifactManager artifactManager;
    private ComponentRegistry componentRegistry;
    private DungeonManager dungeonManager;
    private AltarManager altarManager;
    private CraftingManager craftingManager;
    private SchematicManager schematicManager;
    private ResourcePackManager resourcePackManager;
    private ArtifactEditorGUI artifactEditorGUI;
    private ChatInputManager chatInputManager;
    private CustomItemRegistry customItemRegistry;
    private ArtifactBagManager artifactBagManager;
    private WastelandArtifactsAPI api;
    private ComponentLogger componentLogger;

    // НОВЫЕ: Database + Features
    private DatabaseManager databaseManager;
    private FeatureManager featureManager;
    private CollectionManager collectionManager;
    private CollectionGUI collectionGUI;
    private SetManager setManager;
    private AbilityManager abilityManager;
    private UpgradeManager upgradeManager;
    private FishingListener fishingListener;
    private EliteMobManager eliteMobManager;
    private ArtifactXPManager artifactXPManager;
    private BossArenaManager bossArenaManager;
    private ArenaGUI arenaGUI;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.componentLogger = ComponentLogger.logger(getLogger().getName());

        saveDefaultConfig();
        saveResource("altars.yml", false);
        for (String lang : List.of("en_US", "ru_RU", "de_DE", "fr_FR", "zh_CN")) {
            saveResource("lang/" + lang + ".yml", false);
        }
        saveResource("artifacts/examples.yml", false);
        saveResource("dungeons/default.yml", false);
        saveResource("mob_loot.yml", false);
        saveResource("custom_items.yml", false);
        saveResource("blueprint_workbench.yml", false);
        saveResource("rarities.yml", false);
        saveResource("balance.yml", false);

        saveResource("features/collection.yml", false);
        saveResource("features/sets.yml", false);
        saveResource("features/abilities.yml", false);
        saveResource("features/upgrades.yml", false);
        saveResource("features/fishing_loot.yml", false);
        saveResource("features/elites.yml", false);
        saveResource("features/xp.yml", false);
        saveResource("features/arena.yml", false);

        this.configManager = new ConfigManager(this);
        this.componentRegistry = new ComponentRegistry(this);
        this.rarityManager = new RarityManager(this);
        this.artifactRegistry = new ArtifactRegistry();
        this.artifactManager = new ArtifactManager(this);
        this.altarManager = new AltarManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.craftingManager = new CraftingManager(this);
        this.schematicManager = new SchematicManager(this);
        this.resourcePackManager = new ResourcePackManager(this);
        this.customItemRegistry = new CustomItemRegistry(this);
        this.artifactBagManager = new ArtifactBagManager(this);
        this.chatInputManager = new ChatInputManager(this);
        this.artifactEditorGUI = new ArtifactEditorGUI(this);
        this.api = new WastelandArtifactsAPI(this);

        registerComponents();
        rarityManager.loadConfig();
        configManager.loadAll();
        customItemRegistry.loadConfig();
        artifactBagManager.init();
        altarManager.loadConfig();
        dungeonManager.loadConfigs();
        schematicManager.loadCache();

        // Инициализация БД
        MainConfig mainConfig = configManager.getMainConfig();
        if (mainConfig.database.enabled) {
            databaseManager = new DatabaseManager(this);
            databaseManager.init(mainConfig.database);
        }

        // Инициализация Feature System
        FeatureConfig featureCfg = mainConfig.features;
        if (featureCfg != null) {
            featureManager = new FeatureManager(this);
            featureManager.init(featureCfg);
            initFeatures(featureCfg);
        }

        craftingManager.registerRecipes();
        registerListeners();
        registerCommands();

        if (mainConfig.dungeons.scanOnStartup) {
            dungeonManager.scanAllWorlds();
        }

        resourcePackManager.start();

        getComponentLogger().info("<gradient:gold:red>Wasteland Artifacts</gradient> <green>загружен за " + (System.currentTimeMillis() - start) + "ms");
    }

    @SuppressWarnings("unchecked")
    private void initFeatures(FeatureConfig cfg) {
        if (cfg.collection && databaseManager != null) {
            collectionManager = new CollectionManager(this, databaseManager);
            collectionGUI = new CollectionGUI(this, collectionManager);
            getComponentLogger().info("<green>Feature: Collection активна");
        }
        if (cfg.artifactSets) {
            setManager = new SetManager(this);
            loadSets();
            getComponentLogger().info("<green>Feature: Sets активна");
        }
        if (cfg.activeAbilities) {
            abilityManager = new AbilityManager(this);
            loadAbilities();
            getComponentLogger().info("<green>Feature: Abilities активна");
        }
        if (cfg.upgrades && databaseManager != null) {
            upgradeManager = new UpgradeManager(this, databaseManager);
            loadUpgradeConfig();
            getComponentLogger().info("<green>Feature: Upgrades активна");
        }
        if (cfg.fishing) {
            fishingListener = new FishingListener(this);
            loadFishingConfig();
            getComponentLogger().info("<green>Feature: Fishing активна");
        }
        if (cfg.customMobs) {
            eliteMobManager = new EliteMobManager(this);
            loadEliteConfig();
            getComponentLogger().info("<green>Feature: EliteMobs активна");
        }
        if (cfg.artifactXP && databaseManager != null) {
            artifactXPManager = new ArtifactXPManager(this, databaseManager);
            loadXPConfig();
            getComponentLogger().info("<green>Feature: ArtifactXP активна");
        }
        if (cfg.bossArena && databaseManager != null) {
            bossArenaManager = new BossArenaManager(this, databaseManager);
            arenaGUI = new ArenaGUI(this, bossArenaManager);
            loadArenaConfig();
            getComponentLogger().info("<green>Feature: BossArena активна");
        }
    }

    @SuppressWarnings("unchecked")
    private void loadSets() {
        File file = new File(getDataFolder(), "features/sets.yml");
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
                setManager.registerSet(new me.darkcube.wa.feature.sets.ArtifactSet(
                        id, name, artifacts, setBonuses));
            }
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки sets.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAbilities() {
        File file = new File(getDataFolder(), "features/abilities.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var abilSec = yaml.getConfigurationSection("abilities");
            if (abilSec == null) return;
            for (String id : abilSec.getKeys(false)) {
                if (!abilSec.getBoolean(id + ".enabled", true)) continue;
                Ability ability = new Ability(
                        id,
                        abilSec.getString(id + ".name", id),
                        abilSec.getInt(id + ".cooldown", 10),
                        AbilityType.valueOf(abilSec.getString(id + ".type", "AOE").toUpperCase()),
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
                abilityManager.registerAbility(ability);
            }
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки abilities.yml: " + e.getMessage());
        }
    }

    private void loadUpgradeConfig() {
        File file = new File(getDataFolder(), "features/upgrades.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            upgradeManager.loadConfig(yaml.getConfigurationSection("upgrades").getValues(false));
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки upgrades.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFishingConfig() {
        File file = new File(getDataFolder(), "features/fishing_loot.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var entries = (List<Map<String, Object>>) (List) yaml.getMapList("fishing.entries");
            fishingListener.loadConfig(entries);
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки fishing_loot.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadEliteConfig() {
        File file = new File(getDataFolder(), "features/elites.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            var types = (List<Map<String, Object>>) (List) yaml.getMapList("elites.types");
            eliteMobManager.loadConfig(types);
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки elites.yml: " + e.getMessage());
        }
    }

    private void loadXPConfig() {
        File file = new File(getDataFolder(), "features/xp.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            artifactXPManager.loadConfig(yaml.getConfigurationSection("xp").getValues(false));
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки xp.yml: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadArenaConfig() {
        File file = new File(getDataFolder(), "features/arena.yml");
        if (!file.exists()) return;
        try {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            bossArenaManager.loadConfig(yaml.getConfigurationSection("arena").getValues(false));
        } catch (Exception e) {
            getComponentLogger().warn("<red>Ошибка загрузки arena.yml: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (resourcePackManager != null) resourcePackManager.stop();
        if (databaseManager != null) databaseManager.close();
        instance = null;
    }

    private void registerComponents() {
        componentRegistry.registerDefaults();
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new ArtifactListener(this), this);
        pm.registerEvents(new CraftingListener(this), this);
        pm.registerEvents(new DungeonListener(this), this);
        pm.registerEvents(chatInputManager, this);
        pm.registerEvents(new AltarListener(this), this);
        pm.registerEvents(new AltarBlockListener(this), this);
        pm.registerEvents(new ArmorListener(this), this);
        pm.registerEvents(new MobLootListener(this), this);
        pm.registerEvents(new ArtifactBagListener(this), this);
        pm.registerEvents(new CraftingProtectionListener(this), this);
        pm.registerEvents(new CustomItemBlockListener(this), this);

        if (featureManager != null) {
            if (featureManager.isEnabled("abilities") && abilityManager != null) {
                pm.registerEvents(new AbilityListener(this, abilityManager), this);
            }
            if (featureManager.isEnabled("fishing") && fishingListener != null) {
                pm.registerEvents(fishingListener, this);
            }
            if (featureManager.isEnabled("elites") && eliteMobManager != null) {
                pm.registerEvents(new EliteMobListener(this, eliteMobManager), this);
            }
            if (featureManager.isEnabled("xp") && artifactXPManager != null) {
                pm.registerEvents(new XPListener(this, artifactXPManager), this);
            }
            if (featureManager.isEnabled("arena") && bossArenaManager != null) {
                pm.registerEvents(new ArenaListener(bossArenaManager), this);
            }
        }
    }

    private void registerCommands() {
        var artifactCmd = getServer().getCommandMap();
        artifactCmd.register("wastelandartifacts", new ArtifactCommand(this));
        artifactCmd.register("wastelandartifacts", new AdminCommand(this));
        artifactCmd.register("wastelandartifacts", new DungeonCommand(this));
        artifactCmd.register("wastelandartifacts", new AltarCommand(this));
        artifactCmd.register("wastelandartifacts", new BagCommand(this));
        artifactCmd.register("wastelandartifacts", new ItemCommand(this));
        if (featureManager != null && featureManager.isEnabled("arena") && bossArenaManager != null) {
            artifactCmd.register("wastelandartifacts", new ArenaCommand(this, bossArenaManager, arenaGUI));
        }
        if (featureManager != null && featureManager.isEnabled("collection") && collectionGUI != null) {
            artifactCmd.register("wastelandartifacts", new CollectionCommand(this, collectionGUI, collectionManager));
        }
    }

    // ─── Геттеры ───

    public static WastelandArtifacts getInstance() { return instance; }

    public ConfigManager getConfigManager() { return configManager; }
    public RarityManager getRarityManager() { return rarityManager; }
    public ArtifactRegistry getArtifactRegistry() { return artifactRegistry; }
    public ArtifactManager getArtifactManager() { return artifactManager; }
    public ComponentRegistry getComponentRegistry() { return componentRegistry; }
    public AltarManager getAltarManager() { return altarManager; }
    public DungeonManager getDungeonManager() { return dungeonManager; }
    public CraftingManager getCraftingManager() { return craftingManager; }
    public SchematicManager getSchematicManager() { return schematicManager; }
    public ResourcePackManager getResourcePackManager() { return resourcePackManager; }
    public ArtifactEditorGUI getArtifactEditorGUI() { return artifactEditorGUI; }
    public ChatInputManager getChatInputManager() { return chatInputManager; }
    public CustomItemRegistry getCustomItemRegistry() { return customItemRegistry; }
    public ArtifactBagManager getArtifactBagManager() { return artifactBagManager; }
    public WastelandArtifactsAPI getApi() { return api; }
    public ComponentLogger getComponentLogger() { return componentLogger; }

    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public FeatureManager getFeatureManager() { return featureManager; }
    public CollectionManager getCollectionManager() { return collectionManager; }
    public CollectionGUI getCollectionGUI() { return collectionGUI; }
    public SetManager getSetManager() { return setManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public UpgradeManager getUpgradeManager() { return upgradeManager; }
    public EliteMobManager getEliteMobManager() { return eliteMobManager; }
    public ArtifactXPManager getArtifactXPManager() { return artifactXPManager; }
    public BossArenaManager getBossArenaManager() { return bossArenaManager; }
    public ArenaGUI getArenaGUI() { return arenaGUI; }

    public String msg(String key, Object... args) {
        return configManager != null ? configManager.getLang(key, args) : key;
    }

    public String msgFor(Player player, String key, Object... args) {
        return configManager != null ? configManager.getLangFor(player, key, args) : key;
    }
}
