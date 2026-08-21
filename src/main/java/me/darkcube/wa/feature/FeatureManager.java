package me.darkcube.wa.feature;

import me.darkcube.wa.WastelandArtifacts;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class FeatureManager {
    private final WastelandArtifacts plugin;
    private FeatureConfig config;
    private final Map<String, BooleanSupplier> featureChecks = new HashMap<>();

    public FeatureManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void init(FeatureConfig cfg) {
        this.config = cfg;
        registerFeatures();
        initIntegrations();
    }

    private void registerFeatures() {
        featureChecks.put("placeholderAPI", () -> config.placeholderAPI);
        featureChecks.put("mythicmobs", () -> config.mythicmobs);
        featureChecks.put("nexo", () -> config.nexo);
        featureChecks.put("oraxen", () -> config.oraxen);
        featureChecks.put("collection", () -> config.collection);
        featureChecks.put("sets", () -> config.artifactSets);
        featureChecks.put("abilities", () -> config.activeAbilities);
        featureChecks.put("upgrades", () -> config.upgrades);
        featureChecks.put("fishing", () -> config.fishing);
        featureChecks.put("elites", () -> config.customMobs);
        featureChecks.put("xp", () -> config.artifactXP);
        featureChecks.put("achievements", () -> config.achievements);
        featureChecks.put("sockets", () -> config.sockets);
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
        BooleanSupplier check = featureChecks.get(feature);
        return check != null && check.getAsBoolean();
    }

    /** Позволяет другим плагинам регистрировать кастомные фичи. */
    public void registerFeature(String name, BooleanSupplier check) {
        featureChecks.put(name, check);
    }

    public FeatureConfig getConfig() { return config; }
}
