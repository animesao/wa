package me.darkcube.wa.api.event;

import me.darkcube.wa.feature.sets.ArtifactSet;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Вызывается при активации/деактивации сет-бонуса.
 */
public class SetBonusActivateEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final ArtifactSet artifactSet;
    private final int piecesRequired;
    private final Action action;

    public enum Action { ACTIVATE, DEACTIVATE }

    public SetBonusActivateEvent(@NotNull Player player, @NotNull ArtifactSet artifactSet, int piecesRequired, @NotNull Action action) {
        super(player);
        this.artifactSet = artifactSet;
        this.piecesRequired = piecesRequired;
        this.action = action;
    }

    public @NotNull ArtifactSet getArtifactSet() { return artifactSet; }
    public int getPiecesRequired() { return piecesRequired; }
    public @NotNull Action getAction() { return action; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
