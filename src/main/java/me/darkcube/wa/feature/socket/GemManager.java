package me.darkcube.wa.feature.socket;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.api.event.GemSocketEvent;
import me.darkcube.wa.api.event.GemUnsocketEvent;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class GemManager {

    private static final NamespacedKey GEM_ID_KEY = new NamespacedKey("wastelandartifacts", "gem_id");
    private static final NamespacedKey SOCKET_COUNT_KEY = new NamespacedKey("wastelandartifacts", "socket_count");
    private static final NamespacedKey GEMS_KEY = new NamespacedKey("wastelandartifacts", "gems");
    private static final String GEMS_DELIMITER = ",";

    private final WastelandArtifacts plugin;
    private final Map<String, Gem> gems = new HashMap<>();
    private final Map<UUID, Set<org.bukkit.potion.PotionEffectType>> trackedGemEffects = new HashMap<>();

    public GemManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(@Nullable ConfigurationSection section) {
        gems.clear();
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            if (!section.getBoolean(id + ".enabled", true)) continue;
            try {
                String name = section.getString(id + ".name", id);
                List<String> lore = section.getStringList(id + ".lore");
                Material material = Material.matchMaterial(section.getString(id + ".material", "AMETHYST_SHARD"));
                if (material == null) material = Material.AMETHYST_SHARD;
                int cmd = section.getInt(id + ".custom-model-data", 0);
                String rarity = section.getString(id + ".rarity", "common");

                List<Map<?, ?>> rawEffects = section.getMapList(id + ".effects");
                List<Gem.GemEffect> effects = new ArrayList<>();
                for (Map<?, ?> raw : rawEffects) {
                    String type = (String) raw.get("type");
                    Map<String, Object> params = new HashMap<>();
                    if (raw.get("params") instanceof Map<?, ?> p) {
                        for (Map.Entry<?, ?> e : p.entrySet()) {
                            params.put((String) e.getKey(), e.getValue());
                        }
                    }
                    effects.add(new Gem.GemEffect(type, params));
                }

                gems.put(id, new Gem(id, name, lore, material, cmd, rarity, effects));
            } catch (Exception e) {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки самоцвета '" + id + "': " + e.getMessage());
            }
        }
        plugin.getComponentLogger().info("<green>Загружено самоцветов: " + gems.size());
    }

    public @Nullable Gem getGem(String id) {
        return gems.get(id);
    }

    public @NotNull Collection<Gem> getAll() {
        return gems.values();
    }

    public boolean isGem(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(GEM_ID_KEY, PersistentDataType.STRING);
    }

    public @Nullable String getGemId(@NotNull ItemStack item) {
        if (!item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(GEM_ID_KEY, PersistentDataType.STRING);
    }

    public @NotNull ItemStack createGemItem(@NotNull Gem gem) {
        ItemStack item = new ItemStack(gem.getMaterial(), 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String rarityColor = switch (gem.getRarity()) {
            case "rare" -> "<blue>";
            case "epic" -> "<light_purple>";
            case "legendary" -> "<gold>";
            default -> "<green>";
        };

        meta.displayName(ComponentUtil.fromMini(rarityColor + "✦ " + gem.getName()));

        List<Component> lore = new ArrayList<>();
        lore.add(ComponentUtil.fromMini(""));
        lore.add(ComponentUtil.fromMini("<gray>Тип: " + gem.getRarity()));
        lore.add(ComponentUtil.fromMini(""));
        for (String line : gem.getLore()) {
            lore.add(ComponentUtil.fromMini(line));
        }
        if (!gem.getEffects().isEmpty()) {
            lore.add(ComponentUtil.fromMini(""));
            lore.add(ComponentUtil.fromMini("<dark_gray>✦ Эффекты:"));
            for (Gem.GemEffect effect : gem.getEffects()) {
                lore.add(ComponentUtil.fromMini("  <gray>• " + formatEffect(effect)));
            }
        }
        lore.add(ComponentUtil.fromMini(""));
        lore.add(ComponentUtil.fromMini("<dark_gray>Самоцвет для вставки в артефакт"));
        meta.lore(lore);

        if (gem.getCustomModelData() > 0) {
            meta.setCustomModelData(gem.getCustomModelData());
        }

        meta.getPersistentDataContainer().set(GEM_ID_KEY, PersistentDataType.STRING, gem.getId());
        item.setItemMeta(meta);
        return item;
    }

    public void giveGem(@NotNull Player player, @NotNull String gemId, int amount) {
        Gem gem = gems.get(gemId);
        if (gem == null) return;
        ItemStack item = createGemItem(gem);
        item.setAmount(Math.max(1, amount));
        player.getInventory().addItem(item).forEach((i, leftover) ->
                player.getWorld().dropItem(player.getLocation(), leftover));
    }

    public int getSocketCount(@NotNull ItemStack artifactItem) {
        if (!artifactItem.hasItemMeta()) return 0;
        Integer val = artifactItem.getItemMeta().getPersistentDataContainer().get(SOCKET_COUNT_KEY, PersistentDataType.INTEGER);
        return val != null ? val : 0;
    }

    public void setSocketCount(@NotNull ItemStack artifactItem, int count) {
        ItemMeta meta = artifactItem.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(SOCKET_COUNT_KEY, PersistentDataType.INTEGER, count);
        artifactItem.setItemMeta(meta);
    }

    public @NotNull List<String> getSocketedGems(@NotNull ItemStack artifactItem) {
        if (!artifactItem.hasItemMeta()) return List.of();
        String val = artifactItem.getItemMeta().getPersistentDataContainer().get(GEMS_KEY, PersistentDataType.STRING);
        if (val == null || val.isEmpty()) return List.of();
        return Arrays.asList(val.split(GEMS_DELIMITER));
    }

    public void setSocketedGems(@NotNull ItemStack artifactItem, @NotNull List<String> gemIds) {
        ItemMeta meta = artifactItem.getItemMeta();
        if (meta == null) return;
        String val = gemIds.isEmpty() ? "" : String.join(GEMS_DELIMITER, gemIds);
        meta.getPersistentDataContainer().set(GEMS_KEY, PersistentDataType.STRING, val);
        artifactItem.setItemMeta(meta);
    }

    public boolean socketGem(@NotNull Player player, @NotNull ItemStack artifactItem, @NotNull String gemId) {
        int max = getSocketCount(artifactItem);
        List<String> current = new ArrayList<>(getSocketedGems(artifactItem));
        if (current.size() >= max) return false;
        if (current.contains(gemId)) return false;

        Gem gem = gems.get(gemId);
        if (gem == null) return false;

        GemSocketEvent event = new GemSocketEvent(player, artifactItem, gem, current.size());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        current.add(gemId);
        setSocketedGems(artifactItem, current);
        return true;
    }

    /** @deprecated Используйте {@link #socketGem(Player, ItemStack, String)} */
    @Deprecated
    public boolean socketGem(@NotNull ItemStack artifactItem, @NotNull String gemId) {
        int max = getSocketCount(artifactItem);
        List<String> current = new ArrayList<>(getSocketedGems(artifactItem));
        if (current.size() >= max) return false;
        if (current.contains(gemId)) return false;
        current.add(gemId);
        setSocketedGems(artifactItem, current);
        return true;
    }

    public @Nullable String unsocketGem(@NotNull Player player, @NotNull ItemStack artifactItem, int index) {
        List<String> current = new ArrayList<>(getSocketedGems(artifactItem));
        if (index < 0 || index >= current.size()) return null;

        String gemId = current.get(index);
        Gem gem = gems.get(gemId);
        if (gem == null) return null;

        GemUnsocketEvent event = new GemUnsocketEvent(player, artifactItem, gem, index);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        String removed = current.remove(index);
        setSocketedGems(artifactItem, current);
        return removed;
    }

    /** @deprecated Используйте {@link #unsocketGem(Player, ItemStack, int)} */
    @Deprecated
    public @Nullable String unsocketGem(@NotNull ItemStack artifactItem, int index) {
        List<String> current = new ArrayList<>(getSocketedGems(artifactItem));
        if (index < 0 || index >= current.size()) return null;
        String removed = current.remove(index);
        setSocketedGems(artifactItem, current);
        return removed;
    }

    public void clearGemEffects(@NotNull Player player) {
        Set<org.bukkit.potion.PotionEffectType> old = trackedGemEffects.remove(player.getUniqueId());
        if (old != null) {
            for (var type : old) {
                player.removePotionEffect(type);
            }
        }
    }

    public void applyGemEffects(@NotNull Player player, @NotNull List<String> gemIds) {
        Set<org.bukkit.potion.PotionEffectType> newEffects = new HashSet<>();
        for (String gemId : gemIds) {
            Gem gem = gems.get(gemId);
            if (gem == null) continue;
            for (Gem.GemEffect effect : gem.getEffects()) {
                var type = applyEffect(player, effect);
                if (type != null) newEffects.add(type);
            }
        }
        trackedGemEffects.put(player.getUniqueId(), newEffects);
    }

    private org.bukkit.potion.PotionEffectType applyEffect(@NotNull Player player, @NotNull Gem.GemEffect effect) {
        switch (effect.getType().toUpperCase()) {
            case "POTION" -> {
                String effectType = (String) effect.getParams().get("effect");
                int amplifier = effect.getParams().getOrDefault("amplifier", 0) instanceof Number n ? n.intValue() : 0;
                if (effectType != null) {
                    var potionType = org.bukkit.potion.PotionEffectType.getByName(effectType.toUpperCase());
                    if (potionType != null) {
                        player.addPotionEffect(new org.bukkit.potion.PotionEffect(potionType, org.bukkit.potion.PotionEffect.INFINITE_DURATION, amplifier, true, false));
                        return potionType;
                    }
                }
            }
            case "COMMAND" -> {
                String cmd = (String) effect.getParams().get("command");
                if (cmd != null) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                            cmd.replace("%player%", player.getName()));
                }
            }
        }
        return null;
    }

    private String formatEffect(Gem.GemEffect effect) {
        return switch (effect.getType().toUpperCase()) {
            case "POTION" -> {
                String e = (String) effect.getParams().getOrDefault("effect", "?");
                int amp = effect.getParams().getOrDefault("amplifier", 0) instanceof Number n ? n.intValue() : 0;
                yield "<aqua>" + e + " " + (amp + 1);
            }
            case "COMMAND" -> "<yellow>Команда";
            default -> "<gray>" + effect.getType();
        };
    }
}
