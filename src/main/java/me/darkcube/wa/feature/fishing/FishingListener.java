package me.darkcube.wa.feature.fishing;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class FishingListener implements Listener {
    private final WastelandArtifacts plugin;
    private final Random random = new Random();
    private List<FishingLootEntry> lootTable = new ArrayList<>();

    public FishingListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(List<Map<String, Object>> entries) {
        lootTable.clear();
        if (entries == null) return;
        for (Map<String, Object> entry : entries) {
            FishingLootEntry e = new FishingLootEntry();
            e.item = (String) entry.get("item");
            e.weight = entry.containsKey("weight") ? ((Number) entry.get("weight")).doubleValue() : 10;
            e.minCount = entry.containsKey("minCount") ? ((Number) entry.get("minCount")).intValue() : 1;
            e.maxCount = entry.containsKey("maxCount") ? ((Number) entry.get("maxCount")).intValue() : 1;
            e.message = (String) entry.get("message");
            lootTable.add(e);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (lootTable.isEmpty()) return;

        for (FishingLootEntry entry : lootTable) {
            if (random.nextDouble() * 100 < entry.weight) {
                event.getCaught().remove();
                int count = entry.minCount + (entry.maxCount > entry.minCount ? random.nextInt(entry.maxCount - entry.minCount + 1) : 0);
                ItemStack drop = plugin.getCustomItemRegistry().create(entry.item, count);
                if (drop == null) {
                    Material mat = Material.matchMaterial(entry.item);
                    if (mat != null) drop = new ItemStack(mat, count);
                }
                if (drop != null) {
                    event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), drop);
                    if (entry.message != null) {
                        event.getPlayer().sendMessage(me.darkcube.wa.util.ComponentUtil.fromMini(entry.message));
                    }
                }
                break;
            }
        }
    }

    private static class FishingLootEntry {
        String item, message;
        double weight;
        int minCount, maxCount;
    }
}
