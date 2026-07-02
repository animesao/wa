package me.darkcube.wa.api.event;

import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ArtifactCraftEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Artifact artifact;
    private final String recipeId;
    private final int altarTier;
    private boolean cancelled;

    public ArtifactCraftEvent(@NotNull Player player, @NotNull Artifact artifact, @NotNull String recipeId, int altarTier) {
        super(player);
        this.artifact = artifact;
        this.recipeId = recipeId;
        this.altarTier = altarTier;
    }

    public @NotNull Artifact getArtifact() { return artifact; }
    public @NotNull String getRecipeId() { return recipeId; }
    public int getAltarTier() { return altarTier; }

    @Override
    public boolean isCancelled() { return cancelled; }
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
