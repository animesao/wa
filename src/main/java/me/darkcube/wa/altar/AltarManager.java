package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class AltarManager {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private AltarConfig config;
    private AltarInventory altarInventory;
    private AltarCraftingManager craftingManager;
    private AltarPreview preview;
    private AltarSchematic schematic;
    private AltarBlockTracker blockTracker;
    private final Map<String, Long> cooldowns = new HashMap<>();

    public AltarManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        File file = new File(plugin.getDataFolder(), "altars.yml");
        if (!file.exists()) {
            plugin.saveResource("altars.yml", false);
        }
        try {
            config = plugin.getConfigManager().getYamlMapper().readValue(file, AltarConfig.class);
            plugin.getComponentLogger().info("<green>Загружено " + config.altars.size() + " тиров алтарей");

            altarInventory = new AltarInventory(plugin);
            altarInventory.init();

            craftingManager = new AltarCraftingManager(plugin);
            craftingManager.registerRecipes(config);
            plugin.getComponentLogger().info("<green>Загружено " + craftingManager.getRecipeCount() + " рецептов");

            preview = new AltarPreview(plugin);
            schematic = new AltarSchematic(plugin);
            schematic.loadCache();
            blockTracker = new AltarBlockTracker(plugin);

        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки altars.yml: " + e.getMessage());
            config = new AltarConfig();
        }
    }

    public @Nullable AltarConfig.AltarTier detectAltar(@NotNull Block activatorBlock) {
        if (config == null) return null;
        var entries = new ArrayList<>(config.altars.entrySet());
        entries.sort((a, b) -> Integer.compare(b.getValue().tier, a.getValue().tier));
        for (var entry : entries) {
            AltarConfig.AltarTier tier = entry.getValue();
            if (!tier.enabled) continue;
            if (activatorBlock.getType() != tier.activatorBlock) continue;
            if (matchPattern3D(activatorBlock, tier)) {
                return tier;
            }
        }
        return null;
    }

    private boolean matchPattern3D(@NotNull Block activator, @NotNull AltarConfig.AltarTier tier) {
        for (AltarConfig.AltarStructure struct : tier.structures) {
            if (checkLayers(activator, struct)) return true;
        }
        return false;
    }

    private boolean checkLayers(@NotNull Block activator, @NotNull AltarConfig.AltarStructure struct) {
        List<String> layers = struct.layers;
        if (layers == null || layers.isEmpty()) return false;

        Map<String, String> mapping = struct.mapping;
        if (mapping == null) return false;

        int width = layers.get(0).length();
        for (String row : layers) {
            if (row.length() != width) return false;
        }

        Map<Character, Material> matMap = new HashMap<>();
        for (var e : mapping.entrySet()) {
            if (e.getKey().length() == 1) {
                Material mat = Material.matchMaterial(e.getValue());
                if (mat != null) matMap.put(e.getKey().charAt(0), mat);
            }
        }

        int rowsPerLayer = width;
        int numLayers = layers.size() / rowsPerLayer;
        int cx = width / 2;
        int cz = rowsPerLayer / 2;

        for (int layerY = 0; layerY < numLayers; layerY++) {
            for (int z = 0; z < rowsPerLayer; z++) {
                String row = layers.get(layerY * rowsPerLayer + z);
                for (int x = 0; x < width; x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') continue;
                    Material expected = matMap.get(symbol);
                    if (expected == null || !expected.isBlock()) continue;

                    int bx = activator.getX() + (x - cx);
                    int by = activator.getY() + layerY;
                    int bz = activator.getZ() + (z - cz);

                    Block checkBlock = activator.getWorld().getBlockAt(bx, by, bz);
                    if (checkBlock.getType() != expected) return false;
                }
            }
        }
        return true;
    }

    public void openAltarGUI(@NotNull Player player, @NotNull Block block, @NotNull AltarConfig.AltarTier tier) {
        new AltarGUI(plugin, player, block, tier, altarInventory, craftingManager).open();
    }

    public boolean craftOnAltar(@NotNull Player player, @NotNull Block block,
                                 @NotNull AltarConfig.AltarTier tier) {
        String cooldownKey = "global:" + block.getLocation();
        Long cdEnd = cooldowns.get(cooldownKey);
        if (cdEnd != null && System.currentTimeMillis() < cdEnd) {
            long left = (cdEnd - System.currentTimeMillis()) / 1000;
            player.sendMessage(plugin.getConfigManager().getLang("altar-cooldown", left));
            return false;
        }

        Location loc = block.getLocation();
        ItemStack[] slots = altarInventory.getAllSlots(loc);
        AltarRecipe recipe = craftingManager.findRecipe(slots, tier.tier);

        if (recipe == null) {
            player.sendMessage(mm.deserialize("<red>Нет подходящего рецепта! Проверь ингредиенты"));
            spawnEffects(loc, tier.effects.fail);
            return false;
        }

        AltarCraftingManager.LocationData locData =
                new AltarCraftingManager.LocationData(
                        loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()
                );

        boolean result = craftingManager.craft(player, recipe, locData, altarInventory);
        if (result) {
            cooldowns.put(cooldownKey, System.currentTimeMillis() + tier.globalCooldown * 1000L);
            spawnEffects(loc, tier.effects.craft);
        }
        return result;
    }

    public void spawnEffects(@NotNull Location location, @NotNull AltarConfig.EffectConfig effect) {
        if (!config.settings.particlesEnabled || effect.particle == null) return;
        location.getWorld().spawnParticle(effect.particle, location.add(0.5, 1, 0.5),
                effect.count, 1, 1, 1, 0.1);
        if (config.settings.soundsEnabled && effect.sound != null) {
            location.getWorld().playSound(location, effect.sound, 1.0f, 1.0f);
        }
    }

    public @Nullable AltarConfig.AltarTier getTier(String id) {
        return config != null ? config.altars.get(id) : null;
    }

    public @NotNull Map<String, AltarConfig.AltarTier> getAllTiers() {
        return config != null ? Collections.unmodifiableMap(config.altars) : Collections.emptyMap();
    }

    public AltarConfig getConfig() { return config; }
    public AltarInventory getAltarInventory() { return altarInventory; }
    public AltarCraftingManager getCraftingManager() { return craftingManager; }
    public AltarPreview getPreview() { return preview; }
    public AltarSchematic getSchematic() { return schematic; }
    public AltarBlockTracker getBlockTracker() { return blockTracker; }
}
