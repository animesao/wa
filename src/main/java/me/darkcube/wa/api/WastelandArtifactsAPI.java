package me.darkcube.wa.api;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.trigger.Trigger;
import me.darkcube.wa.artifact.trigger.TriggerType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WastelandArtifactsAPI {

    private final WastelandArtifacts plugin;

    public WastelandArtifactsAPI(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerArtifact(@NotNull Artifact artifact) {
        plugin.getArtifactRegistry().register(artifact);
    }

    public void unregisterArtifact(@NotNull String id) {
        plugin.getArtifactRegistry().unregister(id);
    }

    public @Nullable Artifact getArtifact(@NotNull String id) {
        return plugin.getArtifactRegistry().get(id);
    }

    public @NotNull List<Artifact> getAllArtifacts() {
        return plugin.getArtifactRegistry().getAll();
    }

    public @NotNull ItemStack createItem(@NotNull Artifact artifact) {
        return plugin.getArtifactManager().createItemStack(artifact);
    }

    public @NotNull ItemStack createItem(@NotNull String artifactId) {
        return plugin.getArtifactManager().createItemStack(artifactId);
    }

    public @Nullable Artifact getArtifactFromItem(@NotNull ItemStack item) {
        return plugin.getArtifactManager().getArtifactFromItem(item);
    }

    public boolean isArtifact(@NotNull ItemStack item) {
        return plugin.getArtifactManager().isArtifact(item);
    }

    public void giveArtifact(@NotNull Player player, @NotNull String artifactId, int amount) {
        plugin.getArtifactManager().giveArtifact(player, artifactId, amount);
    }

    public void registerComponent(@NotNull String id, @NotNull Class<? extends ArtifactComponent> clazz) {
        plugin.getComponentRegistry().register(id, clazz);
    }

    public void registerTriggerType(@NotNull TriggerType type, @NotNull Trigger trigger) {
        plugin.getArtifactRegistry().registerTrigger(type, trigger);
    }

    public void reload() {
        plugin.getConfigManager().reloadAll();
    }

    public @NotNull WastelandArtifacts getPlugin() {
        return plugin;
    }
}
