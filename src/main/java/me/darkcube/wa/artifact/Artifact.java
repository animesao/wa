package me.darkcube.wa.artifact;

import me.darkcube.wa.artifact.builder.ArtifactBuilder;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.rarity.Rarity;
import me.darkcube.wa.artifact.trigger.Trigger;
import me.darkcube.wa.crafting.ArtifactRecipe;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Artifact {

    private final String id;
    private final String displayName;
    private final List<String> lore;
    private final Material baseItem;
    private final float customModelData;
    private final Rarity rarity;
    private final boolean unbreakable;
    private final int maxStackSize;
    private final String skinTexture;
    private final List<ArtifactComponent> components;
    private final List<Trigger> triggers;
    @Nullable private ArtifactRecipe recipe;
    private final Map<String, Object> extraData;

    public Artifact(
            @NotNull String id,
            @NotNull String displayName,
            @NotNull List<String> lore,
            @NotNull Material baseItem,
            float customModelData,
            @NotNull Rarity rarity,
            boolean unbreakable,
            int maxStackSize,
            @NotNull String skinTexture,
            @NotNull List<ArtifactComponent> components,
            @NotNull List<Trigger> triggers,
            @Nullable ArtifactRecipe recipe,
            @NotNull Map<String, Object> extraData
    ) {
        this.id = id;
        this.displayName = displayName;
        this.lore = Collections.unmodifiableList(lore);
        this.baseItem = baseItem;
        this.customModelData = customModelData;
        this.rarity = rarity;
        this.unbreakable = unbreakable;
        this.maxStackSize = maxStackSize;
        this.skinTexture = skinTexture;
        this.components = Collections.unmodifiableList(components);
        this.triggers = Collections.unmodifiableList(triggers);
        this.recipe = recipe;
        this.extraData = Collections.unmodifiableMap(extraData);
    }

    public static ArtifactBuilder builder(String id) {
        return new ArtifactBuilder(id);
    }

    public @NotNull String getId() { return id; }
    public @NotNull String getDisplayName() { return displayName; }
    public @NotNull List<String> getLore() { return lore; }
    public @NotNull Material getBaseItem() { return baseItem; }
    public float getCustomModelData() { return customModelData; }
    public @NotNull Rarity getRarity() { return rarity; }
    public boolean isUnbreakable() { return unbreakable; }
    public int getMaxStackSize() { return maxStackSize; }
    public @Nullable String getSkinTexture() { return skinTexture; }
    public @NotNull List<ArtifactComponent> getComponents() { return components; }
    public @NotNull List<Trigger> getTriggers() { return triggers; }
    public @Nullable ArtifactRecipe getRecipe() { return recipe; }
    public @NotNull Map<String, Object> getExtraData() { return extraData; }

    public void setRecipe(@Nullable ArtifactRecipe recipe) {
        this.recipe = recipe;
    }

    public boolean hasComponent(Class<? extends ArtifactComponent> type) {
        return components.stream().anyMatch(type::isInstance);
    }

    @SuppressWarnings("unchecked")
    public <T extends ArtifactComponent> T getComponent(Class<T> type) {
        return (T) components.stream().filter(type::isInstance).findFirst().orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Artifact a)) return false;
        return id.equals(a.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Artifact{id='" + id + "', rarity=" + rarity + "}";
    }
}
