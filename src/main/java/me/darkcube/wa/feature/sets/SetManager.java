package me.darkcube.wa.feature.sets;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SetManager {
    private final WastelandArtifacts plugin;
    private final Map<String, ArtifactSet> sets = new HashMap<>();

    public SetManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerSet(ArtifactSet set) {
        sets.put(set.getId(), set);
    }

    public Map<ArtifactSet, Integer> getActiveSets(Player player) {
        Map<ArtifactSet, Integer> active = new HashMap<>();
        List<String> equippedIds = new ArrayList<>();

        for (ItemStack item : player.getInventory().getArmorContents()) {
            Artifact art = plugin.getArtifactManager().getArtifactFromItem(item);
            if (art != null) equippedIds.add(art.getId());
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        Artifact mainArt = plugin.getArtifactManager().getArtifactFromItem(mainHand);
        if (mainArt != null) equippedIds.add(mainArt.getId());
        ItemStack offHand = player.getInventory().getItemInOffHand();
        Artifact offArt = plugin.getArtifactManager().getArtifactFromItem(offHand);
        if (offArt != null) equippedIds.add(offArt.getId());

        for (ArtifactSet set : sets.values()) {
            int count = 0;
            for (String aid : set.getArtifacts()) {
                if (equippedIds.contains(aid)) count++;
            }
            if (count > 0) active.put(set, count);
        }
        return active;
    }

    public void applySetBonuses(Player player) {
        var active = getActiveSets(player);
        for (var entry : active.entrySet()) {
            ArtifactSet set = entry.getKey();
            int pieces = entry.getValue();
            for (ArtifactSet.SetBonus bonus : set.getBonuses()) {
                if (pieces >= bonus.getPiecesRequired()) {
                    for (String effect : bonus.getEffects()) {
                        plugin.getComponentLogger().info("<aqua>Set bonus applied: " + effect);
                    }
                }
            }
        }
    }

    public Map<String, ArtifactSet> getAllSets() { return Collections.unmodifiableMap(sets); }
}
