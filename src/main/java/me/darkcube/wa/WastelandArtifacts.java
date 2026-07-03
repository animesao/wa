package me.darkcube.wa;

import me.darkcube.wa.altar.AltarManager;
import me.darkcube.wa.api.WastelandArtifactsAPI;
import me.darkcube.wa.artifact.ArtifactManager;
import me.darkcube.wa.artifact.ArtifactRegistry;
import me.darkcube.wa.artifact.rarity.RarityManager;
import me.darkcube.wa.artifact.component.ComponentRegistry;
import me.darkcube.wa.bag.ArtifactBagManager;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.config.MainConfig;
import me.darkcube.wa.crafting.CraftingManager;
import me.darkcube.wa.database.DatabaseManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.feature.FeatureConfig;
import me.darkcube.wa.feature.FeatureManager;
import me.darkcube.wa.feature.collection.CollectionManager;
import me.darkcube.wa.feature.sets.SetManager;
import me.darkcube.wa.feature.abilities.AbilityManager;
import me.darkcube.wa.feature.upgrades.UpgradeManager;
import me.darkcube.wa.feature.fishing.FishingListener;
import me.darkcube.wa.feature.elites.EliteMobManager;

import me.darkcube.wa.feature.achievements.AchievementManager;
import me.darkcube.wa.feature.xp.ArtifactXPManager;
import me.darkcube.wa.gui.ChatInputManager;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.resourcepack.ResourcePackManager;
import me.darkcube.wa.schematic.SchematicManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
    private ChatInputManager chatInputManager;
    private CustomItemRegistry customItemRegistry;
    private ArtifactBagManager artifactBagManager;
    private WastelandArtifactsAPI api;
    private ComponentLogger componentLogger;

    private DatabaseManager databaseManager;
    private FeatureManager featureManager;
    private CollectionManager collectionManager;
    private SetManager setManager;
    private AbilityManager abilityManager;
    private UpgradeManager upgradeManager;
    private FishingListener fishingListener;
    private EliteMobManager eliteMobManager;
    private ArtifactXPManager artifactXPManager;
    private AchievementManager achievementManager;
    private me.darkcube.wa.feature.socket.GemManager gemManager;
    private me.darkcube.wa.feature.socket.SocketListener socketListener;
    private me.darkcube.wa.gui.GUIConfig guiConfig;
    private me.darkcube.wa.gui.menu.MenuManager menuManager;


    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.componentLogger = ComponentLogger.logger(getLogger().getName());

        var bootstrapper = new FeatureBootstrapper(this);
        bootstrapper.saveDefaultResources();

        this.configManager = new ConfigManager(this);
        this.componentRegistry = new ComponentRegistry(this);
        componentRegistry.registerDefaults();
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
        this.api = new WastelandArtifactsAPI(this);

        this.guiConfig = new me.darkcube.wa.gui.GUIConfig(this);
        guiConfig.load();
        this.menuManager = new me.darkcube.wa.gui.menu.MenuManager(this);
        menuManager.loadAll();

        rarityManager.loadConfig();
        configManager.loadAll();
        customItemRegistry.loadConfig();
        artifactBagManager.init();
        altarManager.loadConfig();
        dungeonManager.loadConfigs();
        schematicManager.loadCache();

        MainConfig mainConfig = configManager.getMainConfig();
        if (mainConfig.database.enabled) {
            databaseManager = new DatabaseManager(this);
            if (!databaseManager.init(mainConfig.database)) {
                getComponentLogger().warn("<yellow>БД не подключена — фичи, требующие БД, будут отключены");
            }
        }

        FeatureConfig featureCfg = mainConfig.features;
        if (featureCfg != null) {
            featureManager = new FeatureManager(this);
            featureManager.init(featureCfg);
            bootstrapper.initFeatures(featureCfg);
        }

        craftingManager.registerRecipes();
        new ListenerRegistrar(this).registerAll();
        new CommandRegistrar(this).registerAll();

        if (mainConfig.dungeons.scanOnStartup) {
            dungeonManager.scanAllWorlds();
        }

        resourcePackManager.start();

        int pluginId = 32340;
        var metrics = new org.bstats.bukkit.Metrics(this, pluginId);

        if (mainConfig != null) {
            metrics.addCustomChart(new org.bstats.charts.SimplePie(
                "database_type",
                () -> mainConfig.database.type.toLowerCase()
            ));
        }
        metrics.addCustomChart(new org.bstats.charts.SimplePie(
            "artifacts_loaded",
            () -> String.valueOf(artifactRegistry != null ? artifactRegistry.size() : 0)
        ));

        getComponentLogger().info("<gradient:gold:red>Wasteland Artifacts</gradient> <green>загружен за "
                + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        if (socketListener != null) socketListener.stop();
        if (resourcePackManager != null) resourcePackManager.stop();
        if (databaseManager != null) databaseManager.close();
        instance = null;
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
    public ChatInputManager getChatInputManager() { return chatInputManager; }
    public CustomItemRegistry getCustomItemRegistry() { return customItemRegistry; }
    public ArtifactBagManager getArtifactBagManager() { return artifactBagManager; }
    public WastelandArtifactsAPI getApi() { return api; }
    public ComponentLogger getComponentLogger() { return componentLogger; }

    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public FeatureManager getFeatureManager() { return featureManager; }
    public CollectionManager getCollectionManager() { return collectionManager; }
    public SetManager getSetManager() { return setManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public UpgradeManager getUpgradeManager() { return upgradeManager; }
    public FishingListener getFishingListener() { return fishingListener; }
    public EliteMobManager getEliteMobManager() { return eliteMobManager; }
    public ArtifactXPManager getArtifactXPManager() { return artifactXPManager; }
    public AchievementManager getAchievementManager() { return achievementManager; }
    public me.darkcube.wa.feature.socket.GemManager getGemManager() { return gemManager; }
    public me.darkcube.wa.gui.GUIConfig getGuiConfig() { return guiConfig; }


    // ─── Сеттеры для FeatureBootstrapper ───

    public void setCollectionManager(CollectionManager collectionManager) { this.collectionManager = collectionManager; }
    public void setSetManager(SetManager setManager) { this.setManager = setManager; }
    public void setAbilityManager(AbilityManager abilityManager) { this.abilityManager = abilityManager; }
    public void setUpgradeManager(UpgradeManager upgradeManager) { this.upgradeManager = upgradeManager; }
    public void setFishingListener(FishingListener fishingListener) { this.fishingListener = fishingListener; }
    public void setEliteMobManager(EliteMobManager eliteMobManager) { this.eliteMobManager = eliteMobManager; }
    public void setArtifactXPManager(ArtifactXPManager artifactXPManager) { this.artifactXPManager = artifactXPManager; }
    public void setAchievementManager(AchievementManager achievementManager) { this.achievementManager = achievementManager; }
    public void setGemManager(me.darkcube.wa.feature.socket.GemManager gemManager) { this.gemManager = gemManager; }
    public me.darkcube.wa.feature.socket.SocketListener getSocketListener() { return socketListener; }
    public void setSocketListener(me.darkcube.wa.feature.socket.SocketListener socketListener) { this.socketListener = socketListener; }
    public void setGuiConfig(me.darkcube.wa.gui.GUIConfig guiConfig) { this.guiConfig = guiConfig; }
    public me.darkcube.wa.gui.menu.MenuManager getMenuManager() { return menuManager; }


    public String msg(String key, Object... args) {
        return configManager != null ? configManager.getLang(key, args) : key;
    }

    public String msgFor(Player player, String key, Object... args) {
        return configManager != null ? configManager.getLangFor(player, key, args) : key;
    }
}
