package me.darkcube.wa;

import me.darkcube.wa.altar.AltarManager;
import me.darkcube.wa.api.WastelandArtifactsAPI;
import me.darkcube.wa.artifact.ArtifactManager;
import me.darkcube.wa.artifact.ArtifactRegistry;
import me.darkcube.wa.artifact.component.ComponentRegistry;
import me.darkcube.wa.artifact.rarity.RarityManager;
import me.darkcube.wa.bag.ArtifactBagManager;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.config.MainConfig;
import me.darkcube.wa.crafting.CraftingManager;
import me.darkcube.wa.database.DatabaseManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.feature.FeatureConfig;
import me.darkcube.wa.feature.FeatureManager;
import me.darkcube.wa.feature.abilities.AbilityManager;
import me.darkcube.wa.feature.achievements.AchievementManager;
import me.darkcube.wa.feature.collection.CollectionManager;
import me.darkcube.wa.feature.elites.EliteMobManager;
import me.darkcube.wa.feature.fishing.FishingListener;
import me.darkcube.wa.feature.sets.SetManager;
import me.darkcube.wa.feature.socket.GemManager;
import me.darkcube.wa.feature.socket.SocketListener;
import me.darkcube.wa.feature.upgrades.UpgradeManager;
import me.darkcube.wa.feature.xp.ArtifactXPManager;
import me.darkcube.wa.gui.ChatInputManager;
import me.darkcube.wa.gui.GUIConfig;
import me.darkcube.wa.gui.menu.MenuManager;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.resourcepack.ResourcePackManager;
import me.darkcube.wa.schematic.SchematicManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Главный класс плагина Wasteland Artifacts.
 * <p>
 * Все менеджеры хранятся в {@link ManagerRegistry}. Доступ — через
 * {@link #getRegistry()} (новый код) или через делегирующие геттеры
 * (обратная совместимость).
 */
public final class WastelandArtifacts extends JavaPlugin {

    private static WastelandArtifacts instance;
    private final ManagerRegistry registry = new ManagerRegistry();
    private ComponentLogger componentLogger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        this.componentLogger = ComponentLogger.logger(getLogger().getName());

        var bootstrapper = new FeatureBootstrapper(this);
        bootstrapper.saveDefaultResources();

        // ─── Core managers ───
        var configManager = new ConfigManager(this);
        registry.register(ConfigManager.class, configManager);

        var componentRegistry = new ComponentRegistry(this);
        componentRegistry.registerDefaults();
        registry.register(ComponentRegistry.class, componentRegistry);

        var rarityManager = new RarityManager(this);
        registry.register(RarityManager.class, rarityManager);

        var artifactRegistry = new ArtifactRegistry();
        registry.register(ArtifactRegistry.class, artifactRegistry);

        var artifactManager = new ArtifactManager(this);
        registry.register(ArtifactManager.class, artifactManager);

        var altarManager = new AltarManager(this);
        registry.register(AltarManager.class, altarManager);

        var dungeonManager = new DungeonManager(this);
        registry.register(DungeonManager.class, dungeonManager);

        var craftingManager = new CraftingManager(this);
        registry.register(CraftingManager.class, craftingManager);

        var schematicManager = new SchematicManager(this);
        registry.register(SchematicManager.class, schematicManager);

        var resourcePackManager = new ResourcePackManager(this);
        registry.register(ResourcePackManager.class, resourcePackManager);

        var customItemRegistry = new CustomItemRegistry(this);
        registry.register(CustomItemRegistry.class, customItemRegistry);

        var artifactBagManager = new ArtifactBagManager(this);
        registry.register(ArtifactBagManager.class, artifactBagManager);

        var chatInputManager = new ChatInputManager(this);
        registry.register(ChatInputManager.class, chatInputManager);

        var api = new WastelandArtifactsAPI(this);
        registry.register(WastelandArtifactsAPI.class, api);

        var guiConfig = new GUIConfig(this);
        guiConfig.load();
        registry.register(GUIConfig.class, guiConfig);

        var menuManager = new MenuManager(this);
        menuManager.loadAll();
        registry.register(MenuManager.class, menuManager);

        // ─── Load configs ───
        rarityManager.loadConfig();
        configManager.loadAll();
        customItemRegistry.loadConfig();
        artifactBagManager.init();
        altarManager.loadConfig();
        dungeonManager.loadConfigs();
        schematicManager.loadCache();

        // ─── Database ───
        MainConfig mainConfig = configManager.getMainConfig();
        if (mainConfig.database.enabled) {
            var dbManager = new DatabaseManager(this);
            registry.register(DatabaseManager.class, dbManager);
            if (!dbManager.init(mainConfig.database)) {
                getComponentLogger().warn("<yellow>БД не подключена — фичи, требующие БД, будут отключены");
            }
        }

        // ─── Feature managers ───
        FeatureConfig featureCfg = mainConfig.features;
        if (featureCfg != null) {
            var featureManager = new FeatureManager(this);
            featureManager.init(featureCfg);
            registry.register(FeatureManager.class, featureManager);
            bootstrapper.initFeatures(featureCfg);
        }

        // ─── Post-init ───
        craftingManager.registerRecipes();
        new ListenerRegistrar(this).registerAll();
        new CommandRegistrar(this).registerAll();

        if (mainConfig.dungeons.scanOnStartup) {
            getComponentLogger().warn("<yellow>⚠ scanOnStartup включён — это может загружать и генерировать чанки. Рекомендуется отключить и использовать события.");
            dungeonManager.scanAllWorlds();
        }

        resourcePackManager.start();

        // ─── bStats ───
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
            () -> String.valueOf(artifactRegistry.size())
        ));

        getComponentLogger().info("<gradient:gold:red>Wasteland Artifacts</gradient> <green>загружен за "
                + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        var sl = registry.get(SocketListener.class);
        if (sl != null) sl.stop();
        var rp = registry.get(ResourcePackManager.class);
        if (rp != null) rp.stop();
        var db = registry.get(DatabaseManager.class);
        if (db != null) db.close();
        instance = null;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Registry access
    // ═══════════════════════════════════════════════════════════════

    public static WastelandArtifacts getInstance() { return instance; }

    /** Централизованное хранилище менеджеров. Предпочтительный способ доступа. */
    public @org.jetbrains.annotations.NotNull ManagerRegistry getRegistry() { return registry; }

    public ComponentLogger getComponentLogger() { return componentLogger; }

    // ═══════════════════════════════════════════════════════════════
    // ║  Делегирующие геттеры (обратная совместимость)
    // ═══════════════════════════════════════════════════════════════

    public ConfigManager getConfigManager() { return registry.configManager(); }
    public RarityManager getRarityManager() { return registry.rarityManager(); }
    public ArtifactRegistry getArtifactRegistry() { return registry.artifactRegistry(); }
    public ArtifactManager getArtifactManager() { return registry.artifactManager(); }
    public ComponentRegistry getComponentRegistry() { return registry.componentRegistry(); }
    public AltarManager getAltarManager() { return registry.altarManager(); }
    public DungeonManager getDungeonManager() { return registry.dungeonManager(); }
    public CraftingManager getCraftingManager() { return registry.craftingManager(); }
    public SchematicManager getSchematicManager() { return registry.schematicManager(); }
    public ResourcePackManager getResourcePackManager() { return registry.resourcePackManager(); }
    public ChatInputManager getChatInputManager() { return registry.chatInputManager(); }
    public CustomItemRegistry getCustomItemRegistry() { return registry.customItemRegistry(); }
    public ArtifactBagManager getArtifactBagManager() { return registry.artifactBagManager(); }
    public WastelandArtifactsAPI getApi() { return registry.api(); }
    public GUIConfig getGuiConfig() { return registry.guiConfig(); }
    public MenuManager getMenuManager() { return registry.menuManager(); }

    public DatabaseManager getDatabaseManager() { return registry.databaseManager(); }
    public FeatureManager getFeatureManager() { return registry.featureManager(); }
    public CollectionManager getCollectionManager() { return registry.collectionManager(); }
    public SetManager getSetManager() { return registry.setManager(); }
    public AbilityManager getAbilityManager() { return registry.abilityManager(); }
    public UpgradeManager getUpgradeManager() { return registry.upgradeManager(); }
    public FishingListener getFishingListener() { return registry.fishingListener(); }
    public EliteMobManager getEliteMobManager() { return registry.eliteMobManager(); }
    public ArtifactXPManager getArtifactXPManager() { return registry.artifactXPManager(); }
    public AchievementManager getAchievementManager() { return registry.achievementManager(); }
    public GemManager getGemManager() { return registry.gemManager(); }
    public SocketListener getSocketListener() { return registry.socketListener(); }

    // ═══════════════════════════════════════════════════════════════
    // ║  Делегирующие сеттеры (обратная совместимость)
    // ═══════════════════════════════════════════════════════════════

    public void setCollectionManager(CollectionManager m) { registry.register(CollectionManager.class, m); }
    public void setSetManager(SetManager m) { registry.register(SetManager.class, m); }
    public void setAbilityManager(AbilityManager m) { registry.register(AbilityManager.class, m); }
    public void setUpgradeManager(UpgradeManager m) { registry.register(UpgradeManager.class, m); }
    public void setFishingListener(FishingListener m) { registry.register(FishingListener.class, m); }
    public void setEliteMobManager(EliteMobManager m) { registry.register(EliteMobManager.class, m); }
    public void setArtifactXPManager(ArtifactXPManager m) { registry.register(ArtifactXPManager.class, m); }
    public void setAchievementManager(AchievementManager m) { registry.register(AchievementManager.class, m); }
    public void setGemManager(GemManager m) { registry.register(GemManager.class, m); }
    public void setSocketListener(SocketListener m) { registry.register(SocketListener.class, m); }
    public void setGuiConfig(GUIConfig m) { registry.register(GUIConfig.class, m); }

    // ═══════════════════════════════════════════════════════════════
    // ║  Утилиты
    // ═══════════════════════════════════════════════════════════════

    public String msg(String key, Object... args) {
        var cm = registry.get(ConfigManager.class);
        return cm != null ? cm.getLang(key, args) : key;
    }

    public String msgFor(Player player, String key, Object... args) {
        var cm = registry.get(ConfigManager.class);
        return cm != null ? cm.getLangFor(player, key, args) : key;
    }
}
