package me.darkcube.wa.artifact.builder;

import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.rarity.Rarity;
import me.darkcube.wa.artifact.trigger.Trigger;
import me.darkcube.wa.crafting.ArtifactRecipe;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ArtifactBuilder {

    private final String id;
    private String displayName;
    private List<String> lore = new ArrayList<>();
    private Material baseItem = Material.STICK;
    private float customModelData = 0;
    private Rarity rarity = Rarity.COMMON;
    private boolean unbreakable = false;
    private int maxStackSize = 1;
    private String skinTexture;
    private List<ArtifactComponent> components = new ArrayList<>();
    private List<Trigger> triggers = new ArrayList<>();
    @Nullable private ArtifactRecipe recipe;
    private Map<String, Object> extraData = new HashMap<>();

    public ArtifactBuilder(@NotNull String id) {
        this.id = id.toLowerCase().replaceAll("[^a-z0-9_]", "_");
        this.displayName = "<white>" + id;
    }

    public ArtifactBuilder displayName(@NotNull String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArtifactBuilder lore(@NotNull String... lines) {
        this.lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ArtifactBuilder lore(@NotNull List<String> lines) {
        this.lore.addAll(lines);
        return this;
    }

    public ArtifactBuilder baseItem(@NotNull Material baseItem) {
        this.baseItem = baseItem;
        return this;
    }

    public ArtifactBuilder customModelData(float customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ArtifactBuilder rarity(@NotNull Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    public ArtifactBuilder unbreakable(boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ArtifactBuilder maxStackSize(int maxStackSize) {
        this.maxStackSize = Math.max(1, Math.min(99, maxStackSize));
        return this;
    }

    public ArtifactBuilder skinTexture(@Nullable String skinTexture) {
        this.skinTexture = skinTexture;
        return this;
    }

    public ArtifactBuilder components(@NotNull ArtifactComponent... components) {
        this.components.addAll(Arrays.asList(components));
        return this;
    }

    public ArtifactBuilder component(@NotNull ArtifactComponent component) {
        this.components.add(component);
        return this;
    }

    public ArtifactBuilder triggers(@NotNull Trigger... triggers) {
        this.triggers.addAll(Arrays.asList(triggers));
        return this;
    }

    public ArtifactBuilder trigger(@NotNull Trigger trigger) {
        this.triggers.add(trigger);
        return this;
    }

    public ArtifactBuilder recipe(@Nullable ArtifactRecipe recipe) {
        this.recipe = recipe;
        return this;
    }

    public ArtifactBuilder extraData(@NotNull String key, @NotNull Object value) {
        this.extraData.put(key, value);
        return this;
    }

    public @NotNull Artifact build() {
        return new Artifact(
                id, displayName, lore, baseItem, customModelData,
                rarity, unbreakable, maxStackSize, skinTexture,
                List.copyOf(components), List.copyOf(triggers),
                recipe, Map.copyOf(extraData)
        );
    }
}
