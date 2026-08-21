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
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArtifactBagManager {

    private final WastelandArtifacts plugin;
    private final Map<UUID, ItemStack[]> bags = new ConcurrentHashMap<>();
    private final Map<UUID, Set<Integer>> trackedEffects = new ConcurrentHashMap<>();
    private final Map<UUID, Set<ArtifactComponent>> trackedComponents = new ConcurrentHashMap<>();
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
        // 1. Снимаем ВСЕ старые эффекты артефактов
        Set<ArtifactComponent> oldComps = trackedComponents.remove(player.getUniqueId());
        if (oldComps != null) {
            for (ArtifactComponent comp : oldComps) {
                comp.onUnequip(player);
            }
        }

        Set<Integer> oldEffects = trackedEffects.remove(player.getUniqueId());
        if (oldEffects != null) {
            Set<PotionEffectType> toRemove = new HashSet<>();
            for (var effect : player.getActivePotionEffects()) {
                int key = Objects.hash(effect.getType(), effect.getAmplifier());
                if (oldEffects.contains(key)) {
                    toRemove.add(effect.getType());
                }
            }
            for (var type : toRemove) {
                player.removePotionEffect(type);
            }
        }

        // 2. Собираем эффекты из ВСЕХ источников
        ItemStack[] bag = getBag(player);
        Map<PotionEffectType, Integer> effectMap = new HashMap<>();
        Map<PotionEffectType, Integer> effectLimits = new HashMap<>();
        Set<Integer> newEffects = new HashSet<>();
        Set<ArtifactComponent> newComps = new HashSet<>();
        int globalMax = 4;

        // Предикат для обработки предмета
        java.util.function.BiConsumer<ItemStack, Boolean> process = (item, isBag) -> {
            if (item == null || item.getType().isAir()) return;
            Artifact artifact = plugin.getArtifactManager().getArtifactFromItem(item);
            if (artifact == null) return;
            for (ArtifactComponent comp : artifact.getComponents()) {
                if (comp instanceof me.darkcube.wa.artifact.component.components.PotionEffectOnEquipComponent pc) {
                    PotionEffectType type = pc.getEffect();
                    effectMap.merge(type, pc.getAmplifier() + 1, Integer::sum);
                    effectLimits.putIfAbsent(type, globalMax);
                }
                if (!isBag) {
                    comp.onEquip(player);
                    newComps.add(comp);
                }
            }
        };

        // Сумка (только зелья, без onEquip)
        for (ItemStack item : bag) process.accept(item, true);

        // Экипировка (артефакты в слотах)
        for (ItemStack piece : player.getInventory().getArmorContents()) process.accept(piece, false);
        process.accept(player.getInventory().getItemInMainHand(), false);
        process.accept(player.getInventory().getItemInOffHand(), false);

        // 3. Применяем зелья с учётом лимита
        for (var entry : effectMap.entrySet()) {
            PotionEffectType type = entry.getKey();
            int total = entry.getValue();
            int limit = effectLimits.getOrDefault(type, globalMax);
            int amp = Math.min(total - 1, limit);
            if (amp >= 0) {
                player.addPotionEffect(new PotionEffect(type, PotionEffect.INFINITE_DURATION, amp, true, false, true));
                newEffects.add(Objects.hash(type, amp));
            }
        }

        trackedEffects.put(player.getUniqueId(), newEffects);
        trackedComponents.put(player.getUniqueId(), newComps);
    }

    public void applyOnJoin(@NotNull Player player) {
        recalcEffects(player);
    }

    public void removeAll(@NotNull Player player) {
        Set<ArtifactComponent> oldComps = trackedComponents.remove(player.getUniqueId());
        if (oldComps != null) {
            for (ArtifactComponent comp : oldComps) {
                comp.onUnequip(player);
            }
        }
        Set<Integer> oldEffects = trackedEffects.remove(player.getUniqueId());
        if (oldEffects != null) {
            Set<PotionEffectType> toRemove = new HashSet<>();
            for (var effect : player.getActivePotionEffects()) {
                int key = Objects.hash(effect.getType(), effect.getAmplifier());
                if (oldEffects.contains(key)) {
                    toRemove.add(effect.getType());
                }
            }
            for (var type : toRemove) {
                player.removePotionEffect(type);
            }
        }
    }

    public void saveBag(@NotNull Player player) {
        ItemStack[] bag = bags.get(player.getUniqueId());
        if (bag == null) return;
        File file = new File(bagDir, player.getUniqueId() + ".json");
        File tmpFile = new File(bagDir, player.getUniqueId() + ".json.tmp");
        File bakFile = new File(bagDir, player.getUniqueId() + ".json.bak");
        try {
            // 1. Сериализуем во временный файл
            List<String> list = new ArrayList<>();
            for (ItemStack item : bag) {
                list.add(me.darkcube.wa.util.MojangItemCodec.encode(item));
            }
            plugin.getConfigManager().getYamlMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(tmpFile, Map.of("slots", list, "format", "mojang_base64"));

            // 2. Если основной файл существует — сохраняем бэкап
            if (file.exists()) {
                Files.copy(file.toPath(), bakFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 3. Атомарная замена: tmp → основной
            Files.move(tmpFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // Если tmp остался — удаляем
            tmpFile.delete();
            plugin.getComponentLogger().warn("<red>Ошибка сохранения сумки " + player.getName() + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private ItemStack[] loadBag(Player player) {
        ItemStack[] bag = new ItemStack[54];
        File file = new File(bagDir, player.getUniqueId() + ".json");
        File bakFile = new File(bagDir, player.getUniqueId() + ".json.bak");

        // Попытка загрузки: сначала основной, потом бэкап
        File source = file.exists() ? file : (bakFile.exists() ? bakFile : null);
        if (source == null) return bag;

        try {
            Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(source, Map.class);
            List<String> slots = (List<String>) root.get("slots");
            if (slots == null) return bag;

            for (int i = 0; i < Math.min(slots.size(), 54); i++) {
                String b64 = slots.get(i);
                if (b64 != null && !b64.isEmpty()) {
                    bag[i] = me.darkcube.wa.util.MojangItemCodec.decode(b64);
                }
            }
        } catch (Exception e) {
            // Если основной файл повреждён — пробуем бэкап
            if (source == file && bakFile.exists()) {
                try {
                    Map<String, Object> root = plugin.getConfigManager().getYamlMapper().readValue(bakFile, Map.class);
                    List<String> slots = (List<String>) root.get("slots");
                    if (slots != null) {
                        for (int i = 0; i < Math.min(slots.size(), 54); i++) {
                            String b64 = slots.get(i);
                            if (b64 != null && !b64.isEmpty()) {
                                bag[i] = me.darkcube.wa.util.MojangItemCodec.decode(b64);
                            }
                        }
                    }
                    // Восстанавливаем из бэкапа
                    Files.copy(bakFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    plugin.getComponentLogger().warn("<yellow>Сумка " + player.getName() + " восстановлена из бэкапа");
                } catch (Exception ex) {
                    plugin.getComponentLogger().warn("<red>Ошибка загрузки сумки " + player.getName() + ": " + e.getMessage());
                }
            } else {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки сумки " + player.getName() + ": " + e.getMessage());
            }
        }
        return bag;
    }
}
