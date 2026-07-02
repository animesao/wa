package me.darkcube.wa.api.event;

import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class ArtifactEquipEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Artifact artifact;
    private final EquipAction action;
    private final EquipSlot slot;

    public enum EquipAction { EQUIP, UNEQUIP }
    public enum EquipSlot { ARMOR, MAIN_HAND, OFF_HAND, BAG }

    public ArtifactEquipEvent(@NotNull Player player, @NotNull Artifact artifact, @NotNull EquipAction action, @NotNull EquipSlot slot) {
        super(player);
        this.artifact = artifact;
        this.action = action;
        this.slot = slot;
    }

    public @NotNull Artifact getArtifact() { return artifact; }
    public @NotNull EquipAction getAction() { return action; }
    public @NotNull EquipSlot getSlot() { return slot; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
