package me.darkcube.wa.artifact;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtifactManager {

    private static final String PDC_KEY = "artifact_id";

    private final WastelandArtifacts plugin;
    private final ItemBuilder itemBuilder;

    public ArtifactManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.itemBuilder = new ItemBuilder(plugin);
    }

    public @NotNull ItemStack createItemStack(@NotNull Artifact artifact) {
        return itemBuilder.build(artifact);
    }

    public @NotNull ItemStack createItemStack(@NotNull String artifactId) {
        Artifact artifact = plugin.getArtifactRegistry().get(artifactId);
        if (artifact == null) {
            throw new IllegalArgumentException("Артефакт '" + artifactId + "' не найден");
        }
        return createItemStack(artifact);
    }

    public @Nullable Artifact getArtifactFromItem(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        String id = meta.getPersistentDataContainer().get(
                ItemBuilder.getPDCKey(PDC_KEY),
                PersistentDataType.STRING
        );
        return id != null ? plugin.getArtifactRegistry().get(id) : null;
    }

    public boolean isArtifact(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(
                ItemBuilder.getPDCKey(PDC_KEY),
                PersistentDataType.STRING
        );
    }

    public @NotNull String getArtifactId(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return "";
        String id = item.getItemMeta().getPersistentDataContainer().get(
                ItemBuilder.getPDCKey(PDC_KEY),
                PersistentDataType.STRING
        );
        return id != null ? id : "";
    }

    public void giveArtifact(@NotNull Player player, @NotNull String artifactId, int amount) {
        Artifact artifact = plugin.getArtifactRegistry().get(artifactId);
        if (artifact == null) return;
        ItemStack item = createItemStack(artifact);
        item.setAmount(Math.max(1, amount));
        player.getInventory().addItem(item).forEach((i, leftover) ->
                player.getWorld().dropItem(player.getLocation(), leftover)
        );
    }

    public @NotNull ItemBuilder getItemBuilder() {
        return itemBuilder;
    }
}
