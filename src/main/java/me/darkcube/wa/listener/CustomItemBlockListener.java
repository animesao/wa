package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomItemBlockListener implements Listener {

    private final WastelandArtifacts plugin;
    private static final NamespacedKey CUSTOM_ITEM_KEY = new NamespacedKey("wastelandartifacts", "placed_custom_item");

    private final Map<BlockKey, String> placedBlocks = new ConcurrentHashMap<>();

    public CustomItemBlockListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        ItemStack item = event.getItemInHand();
        String customId = plugin.getCustomItemRegistry().getId(item);
        if (customId == null) return;

        Block block = event.getBlockPlaced();
        placedBlocks.put(BlockKey.of(block), customId);

        if (block.getState() instanceof org.bukkit.block.TileState tile) {
            tile.getPersistentDataContainer().set(CUSTOM_ITEM_KEY, PersistentDataType.STRING, customId);
            tile.update(true, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Block block = event.getBlock();

        String customId = getCustomItemId(block);
        if (customId == null) return;

        event.setDropItems(false);
        event.setExpToDrop(0);

        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        ItemStack drop = plugin.getCustomItemRegistry().create(customId);
        if (drop != null) {
            block.getWorld().dropItemNaturally(loc, drop);
        }
    }

    private String getCustomItemId(Block block) {
        String id = placedBlocks.get(BlockKey.of(block));
        if (id != null) return id;

        if (block.getState() instanceof org.bukkit.block.TileState tile) {
            return tile.getPersistentDataContainer().get(CUSTOM_ITEM_KEY, PersistentDataType.STRING);
        }
        return null;
    }

    private record BlockKey(String world, int x, int y, int z) {
        static BlockKey of(Block block) {
            return new BlockKey(
                block.getWorld().getName(),
                block.getX(),
                block.getY(),
                block.getZ()
            );
        }
    }
}
