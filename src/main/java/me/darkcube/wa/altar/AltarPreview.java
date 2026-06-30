package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Display.Brightness;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AltarPreview {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, List<BlockDisplay>> activePreviews = new ConcurrentHashMap<>();
    private final Map<UUID, Long> previewCooldowns = new ConcurrentHashMap<>();

    public AltarPreview(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public boolean showPreview(@NotNull Player player, @NotNull AltarConfig.AltarTier tier,
                                int variantIndex) {
        if (tier.structures == null || tier.structures.isEmpty()) return false;
        if (variantIndex >= tier.structures.size()) variantIndex = 0;

        stopPreview(player);

        AltarConfig.AltarStructure struct = tier.structures.get(variantIndex);
        Block target = player.getTargetBlockExact(10);
        if (target == null) return false;

        Location center = target.getLocation().add(0, 1, 0);
        List<BlockDisplay> displays = new ArrayList<>();

        List<String> layers = struct.layers;
        int width = layers.get(0).length();
        int rowsPerLayer = width;
        int numLayers = layers.size() / rowsPerLayer;

        Map<Character, Material> mapping = new HashMap<>();
        for (var e : struct.mapping.entrySet()) {
            if (e.getKey().length() == 1) {
                Material mat = Material.matchMaterial(e.getValue());
                if (mat != null) mapping.put(e.getKey().charAt(0), mat);
            }
        }

        int cx = width / 2;
        int cz = rowsPerLayer / 2;

        for (int layerY = 0; layerY < numLayers; layerY++) {
            for (int z = 0; z < rowsPerLayer; z++) {
                String row = layers.get(layerY * rowsPerLayer + z);
                for (int x = 0; x < width; x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') continue;
                    Material mat = mapping.get(symbol);
                    if (mat == null || !mat.isBlock()) continue;

                    int bx = center.getBlockX() + (x - cx);
                    int by = center.getBlockY() + layerY;
                    int bz = center.getBlockZ() + (z - cz);

                    Location blockLoc = new Location(player.getWorld(), bx + 0.5, by + 0.5, bz + 0.5);
                    BlockData data = mat.createBlockData();

                    try {
                        BlockDisplay display = player.getWorld().spawn(blockLoc, BlockDisplay.class, bd -> {
                            bd.setBlock(data);
                            bd.setGlowing(true);
                            bd.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
                            bd.setInterpolationDuration(5);
                            bd.setGravity(false);
                            bd.setPersistent(false);
                            bd.setVisibleByDefault(true);
                        });
                        displays.add(display);
                    } catch (Exception ignored) {}
                }
            }
        }

        previewCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + 60000);
        activePreviews.put(player.getUniqueId(), displays);

        player.sendMessage(mm.deserialize(plugin.msg("altar.preview-activated", tier.displayName)));

        // Показываем список блоков для постройки
        int w = layers.get(0).length();
        int rpl = w;
        int nl = layers.size() / rpl;
        Map<Character, Material> matMap = new HashMap<>();
        for (var e : struct.mapping.entrySet()) {
            if (e.getKey().length() == 1) {
                Material m = Material.matchMaterial(e.getValue());
                if (m != null) matMap.put(e.getKey().charAt(0), m);
            }
        }
        Map<Material, Integer> needed = new HashMap<>();
        for (int ly = 0; ly < nl; ly++) {
            for (int z = 0; z < rpl; z++) {
                String row = layers.get(ly * rpl + z);
                for (int x = 0; x < w; x++) {
                    char sym = row.charAt(x);
                    if (sym == ' ') continue;
                    Material mat = matMap.get(sym);
                    if (mat != null && mat.isBlock()) needed.merge(mat, 1, Integer::sum);
                }
            }
        }

        player.sendMessage(mm.deserialize(plugin.msg("altar.preview.blocks-title")));
        for (var e : needed.entrySet()) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.preview.block-entry", me.darkcube.wa.util.ItemNameUtil.getRussianName(e.getKey()), e.getValue())));
        }
        player.sendMessage(mm.deserialize(plugin.msg("altar.preview.build-hint")));
        if (player.hasPermission("wastelandartifacts.admin.altar")) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.preview.admin-hint", findTierKey(tier))));
        }
        return true;
    }

    public void stopPreview(@NotNull Player player) {
        List<BlockDisplay> displays = activePreviews.remove(player.getUniqueId());
        if (displays != null) {
            for (BlockDisplay d : displays) {
                if (d.isValid()) d.remove();
            }
        }
    }

    public boolean buildAltar(@NotNull Player player, @NotNull AltarConfig.AltarTier tier,
                               int variantIndex, boolean consumeItems) {
        if (tier.structures == null || tier.structures.isEmpty()) return false;
        if (variantIndex >= tier.structures.size()) variantIndex = 0;

        stopPreview(player);

        AltarConfig.AltarStructure struct = tier.structures.get(variantIndex);
        Block target = player.getTargetBlockExact(10);
        if (target == null) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.preview.look-at-block")));
            return false;
        }

        Location center = target.getLocation().add(0, 1, 0);

        List<String> layers = struct.layers;
        int width = layers.get(0).length();
        int rowsPerLayer = width;
        int numLayers = layers.size() / rowsPerLayer;

        Map<Character, Material> mapping = new HashMap<>();
        for (var e : struct.mapping.entrySet()) {
            if (e.getKey().length() == 1) {
                Material mat = Material.matchMaterial(e.getValue());
                if (mat != null) mapping.put(e.getKey().charAt(0), mat);
            }
        }

        int cx = width / 2;
        int cz = rowsPerLayer / 2;

        if (consumeItems) {
            Map<Material, Integer> needed = new HashMap<>();
            for (int layerY = 0; layerY < numLayers; layerY++) {
                for (int z = 0; z < rowsPerLayer; z++) {
                    String row = layers.get(layerY * rowsPerLayer + z);
                    for (int x = 0; x < width; x++) {
                        char symbol = row.charAt(x);
                        if (symbol == ' ') continue;
                        Material mat = mapping.get(symbol);
                        if (mat != null && mat.isBlock()) needed.merge(mat, 1, Integer::sum);
                    }
                }
            }

            List<String> missing = new ArrayList<>();
            for (var e : needed.entrySet()) {
                if (!player.getInventory().containsAtLeast(new org.bukkit.inventory.ItemStack(e.getKey()), e.getValue())) {
                    int has = 0;
                    for (var item : player.getInventory().all(e.getKey()).values()) {
                        has += item.getAmount();
                    }
                    missing.add(me.darkcube.wa.util.ItemNameUtil.getRussianName(e.getKey()) + " — нужно " + e.getValue() + ", есть " + has);
                }
            }

            if (!missing.isEmpty()) {
                player.sendMessage(mm.deserialize(plugin.msg("altar.build.missing-blocks")));
                for (String m : missing) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.build.missing-entry", m)));
                }
                return false;
            }

            for (var e : needed.entrySet()) {
                org.bukkit.inventory.ItemStack stack = new org.bukkit.inventory.ItemStack(e.getKey(), e.getValue());
                player.getInventory().removeItem(stack);
            }
        }

        for (int layerY = 0; layerY < numLayers; layerY++) {
            for (int z = 0; z < rowsPerLayer; z++) {
                String row = layers.get(layerY * rowsPerLayer + z);
                for (int x = 0; x < width; x++) {
                    char symbol = row.charAt(x);
                    if (symbol == ' ') continue;
                    Material mat = mapping.get(symbol);
                    if (mat == null || !mat.isBlock()) continue;

                    int bx = center.getBlockX() + (x - cx);
                    int by = center.getBlockY() + layerY;
                    int bz = center.getBlockZ() + (z - cz);

                    player.getWorld().getBlockAt(bx, by, bz).setType(mat);
                }
            }
        }

        player.sendMessage(mm.deserialize(plugin.msg("altar.build.success", tier.displayName)));
        return true;
    }

    public boolean hasActivePreview(@NotNull Player player) {
        return activePreviews.containsKey(player.getUniqueId());
    }

    private @Nullable String findTierKey(AltarConfig.AltarTier tier) {
        var config = plugin.getAltarManager().getConfig();
        if (config == null) return null;
        for (var e : config.altars.entrySet()) {
            if (e.getValue() == tier) return e.getKey();
        }
        return null;
    }
}
