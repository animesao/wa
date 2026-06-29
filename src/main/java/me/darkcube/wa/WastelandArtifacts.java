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
import me.darkcube.wa.commands.AdminCommand;
import me.darkcube.wa.commands.AltarCommand;
import me.darkcube.wa.commands.ArtifactCommand;
import me.darkcube.wa.commands.BagCommand;
import me.darkcube.wa.commands.ItemCommand;
import me.darkcube.wa.commands.DungeonCommand;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.crafting.CraftingManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.dungeon.MobLootListener;
import me.darkcube.wa.gui.ArtifactEditorGUI;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.gui.ChatInputManager;
import me.darkcube.wa.listener.ArmorListener;
import me.darkcube.wa.listener.ArtifactListener;
import me.darkcube.wa.listener.CraftingListener;
import me.darkcube.wa.listener.CraftingProtectionListener;
import me.darkcube.wa.listener.CustomItemBlockListener;
import me.darkcube.wa.listener.DungeonListener;
import me.darkcube.wa.resourcepack.ResourcePackManager;
import me.darkcube.wa.schematic.SchematicManager;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

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
        craftingManager.registerRecipes();

        registerListeners();
        registerCommands();

        if (configManager.getMainConfig().dungeons.scanOnStartup) {
            dungeonManager.scanAllWorlds();
        }

        resourcePackManager.start();

        getComponentLogger().info("<gradient:gold:red>Wasteland Artifacts</gradient> <green>загружен за " + (System.currentTimeMillis() - start) + "ms");
    }

    @Override
    public void onDisable() {
        if (resourcePackManager != null) {
            resourcePackManager.stop();
        }
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
    }

    private void registerCommands() {
        var artifactCmd = getServer().getCommandMap();
        artifactCmd.register("wastelandartifacts", new ArtifactCommand(this));
        artifactCmd.register("wastelandartifacts", new AdminCommand(this));
        artifactCmd.register("wastelandartifacts", new DungeonCommand(this));
        artifactCmd.register("wastelandartifacts", new AltarCommand(this));
        artifactCmd.register("wastelandartifacts", new BagCommand(this));
        artifactCmd.register("wastelandartifacts", new ItemCommand(this));
    }

    public static WastelandArtifacts getInstance() {
        return instance;
    }

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

    // Короткий доступ к локализации
    public String msg(String key, Object... args) {
        return configManager != null ? configManager.getLang(key, args) : key;
    }

    public String msgFor(Player player, String key, Object... args) {
        return configManager != null ? configManager.getLangFor(player, key, args) : key;
    }
}
