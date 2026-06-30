package me.darkcube.wa.feature;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class FeatureManager {
    private final WastelandArtifacts plugin;
    private FeatureConfig config;

    public FeatureManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void init(FeatureConfig cfg) {
        this.config = cfg;
        initIntegrations();
    }

    private void initIntegrations() {
        me.darkcube.wa.integration.ItemsAdderIntegration.init();
        if (config.nexo) me.darkcube.wa.integration.NexoIntegration.init();
        if (config.oraxen) me.darkcube.wa.integration.OraxenIntegration.init();
        if (config.mythicmobs) me.darkcube.wa.integration.MythicMobsIntegration.init();
        if (config.placeholderAPI) me.darkcube.wa.integration.PlaceholderAPIExpansion.init(plugin);
    }

    public boolean isEnabled(String feature) {
        if (config == null) return false;
        switch (feature) {
            case "placeholderAPI": return config.placeholderAPI;
            case "mythicmobs": return config.mythicmobs;
            case "nexo": return config.nexo;
            case "oraxen": return config.oraxen;
            case "collection": return config.collection;
            case "sets": return config.artifactSets;
            case "abilities": return config.activeAbilities;
            case "upgrades": return config.upgrades;
            case "fishing": return config.fishing;
            case "elites": return config.customMobs;
            case "xp": return config.artifactXP;
            case "arena": return config.bossArena;
            default: return false;
        }
    }

    public FeatureConfig getConfig() { return config; }
}
