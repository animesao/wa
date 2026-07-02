package me.darkcube.wa.api.event;

import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ArtifactLevelUpEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Artifact artifact;
    private final int oldLevel;
    private final int newLevel;

    public ArtifactLevelUpEvent(@NotNull Player player, @NotNull Artifact artifact, int oldLevel, int newLevel) {
        super(player);
        this.artifact = artifact;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public @NotNull Artifact getArtifact() { return artifact; }
    public int getOldLevel() { return oldLevel; }
    public int getNewLevel() { return newLevel; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
