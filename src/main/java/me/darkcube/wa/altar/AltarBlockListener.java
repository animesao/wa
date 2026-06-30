package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class AltarBlockListener implements Listener {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final AltarBlockTracker tracker;

    public AltarBlockListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.tracker = plugin.getAltarManager().getBlockTracker();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item dropped = event.getItemDrop();

        // Пробуем принять на алтарь через 2 тика
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            tracker.tryAcceptDrop(player, dropped);
        }, 2L);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        tracker.removeAltar(event.getBlock());
        var tier = plugin.getAltarManager().detectAltar(event.getBlock());
        if (tier != null) {
            event.getPlayer().sendMessage(mm.deserialize("<red>Алтарь разрушен! Ингредиенты пропали."));
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntity().hasMetadata("wa_altar_item")) {
            event.setCancelled(true);
            event.getEntity().setTicksLived(1);
            if (!event.getEntity().hasGravity()) {
                event.getEntity().setGravity(false);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Item item && item.hasMetadata("wa_altar_item")) {
            event.setCancelled(true);
        }
    }
}
