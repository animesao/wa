package me.darkcube.wa.feature.socket;

import org.bukkit.Material;

import java.util.List;
import java.util.Map;

public class Gem {

    private final String id;
    private final String name;
    private final List<String> lore;
    private final Material material;
    private final int customModelData;
    private final String rarity;
    private final List<GemEffect> effects;

    public Gem(String id, String name, List<String> lore, Material material, int customModelData, String rarity, List<GemEffect> effects) {
        this.id = id;
        this.name = name;
        this.lore = lore;
        this.material = material;
        this.customModelData = customModelData;
        this.rarity = rarity;
        this.effects = effects;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public Material getMaterial() { return material; }
    public int getCustomModelData() { return customModelData; }
    public String getRarity() { return rarity; }
    public List<GemEffect> getEffects() { return effects; }

    public static class GemEffect {
        private final String type;
        private final Map<String, Object> params;

        public GemEffect(String type, Map<String, Object> params) {
            this.type = type;
            this.params = params;
        }

        public String getType() { return type; }
        public Map<String, Object> getParams() { return params; }
    }
}
