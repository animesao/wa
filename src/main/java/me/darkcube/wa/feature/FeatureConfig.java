package me.darkcube.wa.feature;

import me.darkcube.wa.config.MainConfig;

public class FeatureConfig {
    public boolean placeholderAPI = true;
    public boolean mythicmobs = true;
    public boolean nexo = true;
    public boolean oraxen = true;
    public boolean collection = true;
    public boolean artifactSets = true;
    public boolean activeAbilities = true;
    public boolean upgrades = true;
    public boolean fishing = true;
    public boolean customMobs = true;
    public boolean artifactXP = true;
    public boolean bossArena = true;

    public static FeatureConfig from(MainConfig main) {
        return main.features != null ? main.features : new FeatureConfig();
    }
}
