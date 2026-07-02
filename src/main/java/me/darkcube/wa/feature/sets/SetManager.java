package me.darkcube.wa.feature.sets;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SetManager {
    private final WastelandArtifacts plugin;
    private final Map<String, ArtifactSet> sets = new HashMap<>();
    private final Map<UUID, Set<String>> activeBonusKeys = new HashMap<>();

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
            if (item == null || item.getType() == org.bukkit.Material.AIR) continue;
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
        Set<String> newKeys = new HashSet<>();
        var active = getActiveSets(player);

        for (var entry : active.entrySet()) {
            ArtifactSet set = entry.getKey();
            int pieces = entry.getValue();
            for (ArtifactSet.SetBonus bonus : set.getBonuses()) {
                if (pieces >= bonus.getPiecesRequired()) {
                    String key = set.getId() + ":" + bonus.getPiecesRequired();
                    newKeys.add(key);
                    for (String effect : bonus.getEffects()) {
                        applyEffect(player, effect);
                    }
                }
            }
        }

        Set<String> oldKeys = activeBonusKeys.getOrDefault(player.getUniqueId(), Collections.emptySet());
        for (String oldKey : oldKeys) {
            if (!newKeys.contains(oldKey)) {
                removeEffect(player, oldKey);
            }
        }
        activeBonusKeys.put(player.getUniqueId(), newKeys);
    }

    private void applyEffect(Player player, String effect) {
        try {
            String[] parts = effect.split(":", 3);
            String typeKey = parts[0].toUpperCase();
            switch (typeKey) {
                case "POTION" -> {
                    PotionEffectType type = PotionEffectType.getByName(parts[1].toUpperCase());
                    if (type != null) {
                        int amplifier = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                        player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, amplifier, true, false));
                    }
                }
                case "ATTRIBUTE" -> {
                    Attribute attr = Attribute.valueOf(parts[1].toUpperCase());
                    double amount = Double.parseDouble(parts[2]);
                    player.getAttribute(attr).addModifier(new AttributeModifier(
                            UUID.nameUUIDFromBytes(("set_" + effect).getBytes()),
                            "set_bonus", amount, AttributeModifier.Operation.ADD_NUMBER));
                }
                case "COMMAND" -> {
                    String cmd = effect.substring(8).replace("%player%", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
                default -> {
                    PotionEffectType type = PotionEffectType.getByName(typeKey);
                    if (type != null) {
                        int amplifier = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                        player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, amplifier, true, false));
                    }
                }
            }
        } catch (Exception ignored) {}
    }

    private void removeEffect(Player player, String key) {
        player.getActivePotionEffects().forEach(e -> {
            if (e.getDuration() >= 1000000 || e.getDuration() == PotionEffect.INFINITE_DURATION) {
                player.removePotionEffect(e.getType());
            }
        });
        // Убираем атрибуты, добавленные сет-бонусами
        for (var attr : Attribute.values()) {
            var instance = player.getAttribute(attr);
            if (instance == null) continue;
            var toRemove = instance.getModifiers().stream()
                    .filter(m -> m.getName().equals("set_bonus"))
                    .toList();
            toRemove.forEach(instance::removeModifier);
        }
    }

    public Map<String, ArtifactSet> getAllSets() { return Collections.unmodifiableMap(sets); }
}
