package me.darkcube.wa.artifact.trigger;

import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TriggerContext {

    private final Player player;
    private final Artifact artifact;
    private final ItemStack item;
    private final TriggerType triggerType;
    private final Event event;
    private final Entity target;

    public TriggerContext(
            @NotNull Player player,
            @NotNull Artifact artifact,
            @NotNull ItemStack item,
            @NotNull TriggerType triggerType,
            @Nullable Event event,
            @Nullable Entity target
    ) {
        this.player = player;
        this.artifact = artifact;
        this.item = item;
        this.triggerType = triggerType;
        this.event = event;
        this.target = target;
    }

    public @NotNull Player getPlayer() { return player; }
    public @NotNull Artifact getArtifact() { return artifact; }
    public @NotNull ItemStack getItem() { return item; }
    public @NotNull TriggerType getTriggerType() { return triggerType; }
    public @Nullable Event getEvent() { return event; }
    public @Nullable Entity getTarget() { return target; }
}
