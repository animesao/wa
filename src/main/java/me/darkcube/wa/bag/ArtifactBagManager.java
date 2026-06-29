package me.darkcube.wa.bag;

import com.fasterxml.jackson.core.type.TypeReference;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.config.BalanceConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArtifactBagManager {

    private final WastelandArtifacts plugin;
    private final Map<UUID, ItemStack[]> bags = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Integer>> trackedEffects = new ConcurrentHashMap<>();
    private File bagDir;

    public ArtifactBagManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void init() {
        bagDir = new File(plugin.getDataFolder(), "bags");
        bagDir.mkdirs();
    }

    public ItemStack[] getBag(@NotNull Player player) {
        return bags.computeIfAbsent(player.getUniqueId(), k -> loadBag(player));
    }

    public void setSlot(@NotNull Player player, int slot, ItemStack item) {
        ItemStack[] bag = getBag(player);
        if (slot < 0 || slot >= bag.length) return;
        bag[slot] = (item != null && item.getType().isAir()) ? null : item;
        saveBag(player);
        recalcEffects(player);
    }

    public ItemStack getSlot(@NotNull Player player, int slot) {
        ItemStack[] bag = getBag(player);
        if (slot < 0 || slot >= bag.length) return null;
        return bag[slot];
    }

    public void recalcEffects(@NotNull Player player) {
        // Убираем все старые эффекты (полная перезапись)
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        trackedEffects.remove(player.getUniqueId());

        ItemStack[] bag = getBag(player);
        Map<PotionEffectType, Integer> effectMap = new HashMap<>();
        Map<PotionEffectType, Integer> effectLimits = new HashMap<>();
        Set<Integer> newEffects = new HashSet<>();

        // Лимиты из конфига (хардкод пока)
        int globalMax = 4;

        // Собираем эффекты из сумки
        for (ItemStack item : bag) {
            if (item == null || item.getType().isAir()) continue;
            Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
            if (artifact == null) continue;
            for (ArtifactComponent comp : artifact.getComponents()) {
                if (comp instanceof me.darkcube.wa.artifact.component.components.PotionEffectOnEquipComponent pc) {
                    PotionEffectType type = pc.getEffect();
                    effectMap.merge(type, pc.getAmplifier() + 1, Integer::sum);
                    effectLimits.putIfAbsent(type, globalMax);
                }
                comp.onEquip(player);
            }
        }

        // Собираем эффекты из оффхенда (левая рука)
        ItemStack offhand = player.getInventory().getItemInOffHand();
        if (offhand != null && offhand.getType() != Material.AIR) {
            Artifact offArt = plugin.getArtifactManager().getArtifactFromItem(offhand);
            if (offArt != null) {
                for (ArtifactComponent comp : offArt.getComponents()) {
                    if (comp instanceof me.darkcube.wa.artifact.component.components.PotionEffectOnEquipComponent pc) {
                        PotionEffectType type = pc.getEffect();
                        effectMap.merge(type, pc.getAmplifier() + 1, Integer::sum);
                        effectLimits.putIfAbsent(type, globalMax);
                    }
                    comp.onEquip(player);
                }
            }
        }

        // Применяем с учётом лимита
        for (var entry : effectMap.entrySet()) {
            PotionEffectType type = entry.getKey();
            int total = entry.getValue();
            int limit = effectLimits.getOrDefault(type, globalMax);
            int amp = Math.min(total - 1, limit);
            if (amp >= 0) {
                player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amp, true, false, true));
                newEffects.add(Objects.hash(type, amp));
            }
        }

        trackedEffects.put(player.getUniqueId(), newEffects);
    }

    public void applyOnJoin(@NotNull Player player) {
        ItemStack[] bag = getBag(player);
        for (ItemStack item : bag) {
            if (item == null || item.getType().isAir()) continue;
            Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
            if (artifact != null) {
                for (ArtifactComponent comp : artifact.getComponents()) {
                    comp.onEquip(player);
                }
            }
        }
        recalcEffects(player);
    }

    public void removeAll(@NotNull Player player) {
        ItemStack[] bag = getBag(player);
        for (ItemStack item : bag) {
            if (item == null || item.getType().isAir()) continue;
            Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
            if (artifact != null) {
                for (ArtifactComponent comp : artifact.getComponents()) {
                    comp.onUnequip(player);
                }
            }
        }
        trackedEffects.remove(player.getUniqueId());
    }

    public void saveBag(@NotNull Player player) {
        ItemStack[] bag = bags.get(player.getUniqueId());
        if (bag == null) return;
        File file = new File(bagDir, player.getUniqueId() + ".json");
        try {
            List<String> list = new ArrayList<>();
            for (ItemStack item : bag) {
                list.add(me.darkcube.wa.util.MojangItemCodec.encode(item));
            }
            plugin.getConfigManager().getYamlMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, Map.of("slots", list, "format", "mojang_base64"));
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка сохранения сумки " + player.getName() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack[] loadBag(Player player) {
        ItemStack[] bag = new ItemStack[54];
        File file = new File(bagDir, player.getUniqueId() + ".json");
        if (!file.exists()) return bag;

        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(file, Map.class);
            List<String> slots = (List<String>) root.get("slots");
            if (slots == null) return bag;

            for (int i = 0; i < Math.min(slots.size(), 54); i++) {
                String b64 = slots.get(i);
                if (b64 != null && !b64.isEmpty()) {
                    bag[i] = me.darkcube.wa.util.MojangItemCodec.decode(b64);
                }
            }
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки сумки " + player.getName() + ": " + e.getMessage());
        }
        return bag;
    }
}
