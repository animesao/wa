package me.darkcube.wa.api.event;

import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ArtifactFoundEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Artifact artifact;
    private final FoundSource source;
    private final String dungeonId;

    public enum FoundSource { DUNGEON_LOOT, MOB_DROP, FISHING, COMMAND, CRAFT }

    public ArtifactFoundEvent(@NotNull Player player, @NotNull Artifact artifact, @NotNull FoundSource source, @NotNull String dungeonId) {
        super(player);
        this.artifact = artifact;
        this.source = source;
        this.dungeonId = dungeonId;
    }

    public ArtifactFoundEvent(@NotNull Player player, @NotNull Artifact artifact, @NotNull FoundSource source) {
        this(player, artifact, source, "");
    }

    public @NotNull Artifact getArtifact() { return artifact; }
    public @NotNull FoundSource getSource() { return source; }
    public @NotNull String getDungeonId() { return dungeonId; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
