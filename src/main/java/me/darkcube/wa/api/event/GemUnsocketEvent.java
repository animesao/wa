package me.darkcube.wa.api.event;

import me.darkcube.wa.feature.socket.Gem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Вызывается при извлечении самоцвета из артефакта.
 */
public class GemUnsocketEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ItemStack artifactItem;
    private final Gem gem;
    private final int slotIndex;
    private boolean cancelled;

    public GemUnsocketEvent(@NotNull Player player, @NotNull ItemStack artifactItem, @NotNull Gem gem, int slotIndex) {
        super(player);
        this.artifactItem = artifactItem;
        this.gem = gem;
        this.slotIndex = slotIndex;
    }

    public @NotNull ItemStack getArtifactItem() { return artifactItem; }
    public @NotNull Gem getGem() { return gem; }
    public int getSlotIndex() { return slotIndex; }

    @Override
    public boolean isCancelled() { return cancelled; }
    @Override
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    @Override
    public @NotNull HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
