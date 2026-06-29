package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.dungeon.DungeonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.event.world.AsyncStructureGenerateEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class DungeonListener implements Listener {

    private final WastelandArtifacts plugin;
    private final DungeonManager dungeonManager;
    private final Random random = new Random();
    private final Set<String> trackedStructures = new HashSet<>();
    private final Map<String, String> chunkDungeonCache = new HashMap<>();
    private static final long CHUNK_CACHE_TTL = 600_000L; // 10 минут
    private final Map<String, Long> chunkCacheTime = new HashMap<>();

    public DungeonListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.dungeonManager = plugin.getDungeonManager();
    }

    private static final NamespacedKey LOOT_POPULATED_KEY = new NamespacedKey("wastelandartifacts", "loot_populated");
    private static final NamespacedKey DUNGEON_ID_KEY = new NamespacedKey("wastelandartifacts", "dungeon_id");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStructureGenerate(AsyncStructureGenerateEvent event) {
        if (!plugin.getConfigManager().getMainConfig().dungeons.enabled) return;

        World world = event.getWorld();
        Structure structure = event.getStructure();
        String structName = structure != null ? structure.getKey().getKey().toLowerCase() : "unknown";

        String dungeonId = mapStructureToDungeon(structName);
        if (dungeonId == null) return;

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.enabled) return;

        String trackKey = world.getName() + ":" + structName + ":" +
                event.getBoundingBox().getMinX() + ":" +
                event.getBoundingBox().getMinZ();
        trackedStructures.add(trackKey);

        Location baseLoc = new Location(world,
                event.getBoundingBox().getMinX(),
                event.getBoundingBox().getMinY(),
                event.getBoundingBox().getMinZ()
        );

        plugin.getComponentLogger().info("<aqua>Структура " + structName +
                " сгенерирована в " + world.getName() +
                " [" + (int)baseLoc.getX() + ", " + (int)baseLoc.getZ() + "]");

        Bukkit.getScheduler().runTask(plugin, () -> {
            var bb = event.getBoundingBox();
            int minX = (int) Math.floor(bb.getMinX());
            int minY = (int) Math.floor(bb.getMinY());
            int minZ = (int) Math.floor(bb.getMinZ());
            int maxX = (int) Math.ceil(bb.getMaxX());
            int maxY = (int) Math.ceil(bb.getMaxY());
            int maxZ = (int) Math.ceil(bb.getMaxZ());
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        Material type = block.getType();
                        if (type == Material.CHEST || type == Material.BARREL || type.name().endsWith("_SHULKER_BOX")) {
                            if (block.getState() instanceof org.bukkit.block.TileState tile) {
                                tile.getPersistentDataContainer().set(
                                        DUNGEON_ID_KEY, PersistentDataType.STRING, dungeonId);
                                tile.update(true, false);
                            }
                        }
                    }
                }
            }
        });
    }

    // ─── Путь 1: LootGenerateEvent (для ванильных структур) ───

    @EventHandler(priority = EventPriority.HIGH)
    public void onLootGenerate(LootGenerateEvent event) {
        if (!plugin.getConfigManager().getMainConfig().dungeons.lootInjection) return;
        if (event.isCancelled()) return;

        var lootTable = event.getLootTable();
        if (lootTable == null) {
            plugin.getComponentLogger().warn("<yellow>LootGenerateEvent: lootTable = null");
            return;
        }
        NamespacedKey lootTableKey = lootTable.getKey();
        plugin.getComponentLogger().info("<aqua>LootGenerateEvent: " + lootTableKey);

        String dungeonId = mapLootTableToDungeon(lootTableKey);
        if (dungeonId == null) {
            plugin.getComponentLogger().info("<yellow>LootGenerateEvent: не удалось определить данж для " + lootTableKey);
            return;
        }

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.loot.enabled) return;

        if ("INJECT".equalsIgnoreCase(config.loot.mode)) {
            List<ItemStack> extraLoot = dungeonManager.generateLoot(dungeonId);
            if (!extraLoot.isEmpty()) {
                List<ItemStack> existingLoot = event.getLoot();
                existingLoot.addAll(extraLoot);
                event.setLoot(existingLoot);
                plugin.getComponentLogger().info("<green>Добавлено " + extraLoot.size() + " предметов в " + dungeonId);
            }
        } else if ("REPLACE".equalsIgnoreCase(config.loot.mode)) {
            List<ItemStack> existingLoot = event.getLoot();
            List<ItemStack> modifiedLoot = new ArrayList<>(existingLoot);
            int replaceCount = (int) (modifiedLoot.size() * config.loot.replaceChance);
            if (replaceCount > 0) {
                List<ItemStack> artifactLoot = dungeonManager.generateLoot(dungeonId);
                if (!artifactLoot.isEmpty()) {
                    for (int i = 0; i < replaceCount && i < artifactLoot.size(); i++) {
                        int index = random.nextInt(modifiedLoot.size());
                        modifiedLoot.set(index, artifactLoot.get(i));
                    }
                    event.setLoot(modifiedLoot);
                }
            }
        }

        InventoryHolder holder = event.getInventoryHolder();
        if (holder instanceof org.bukkit.block.TileState tile) {
            tile.getPersistentDataContainer().set(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN, true);
        }
    }

    // ─── Путь 2: InventoryOpenEvent (после генерации лутейбла) ───

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        if (inv.getType() != org.bukkit.event.inventory.InventoryType.CHEST
                && inv.getType() != org.bukkit.event.inventory.InventoryType.BARREL
                && inv.getType() != org.bukkit.event.inventory.InventoryType.SHULKER_BOX
                && inv.getType() != org.bukkit.event.inventory.InventoryType.HOPPER) return;

        InventoryHolder holder = inv.getHolder();
        if (holder == null) return;

        // Определяем позицию для поиска данжа
        boolean isMinecart = holder instanceof org.bukkit.entity.Entity entity
                && (entity.getType() == org.bukkit.entity.EntityType.CHEST_MINECART
                || entity.getType() == org.bukkit.entity.EntityType.HOPPER_MINECART);
        Location chestLoc;
        if (holder instanceof org.bukkit.block.TileState tile) {
            if (tile.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN)) return;
            Inventory topInv = event.getView().getTopInventory();
            if (topInv != inv) inv = topInv;
            holder = inv.getHolder();
            if (!(holder instanceof org.bukkit.block.TileState tile2)) return;
            tile = tile2;
            if (tile.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN)) return;
            chestLoc = ((org.bukkit.block.BlockState) holder).getBlock().getLocation();
        } else if (isMinecart) {
            var minecartEntity = (org.bukkit.entity.Entity) holder;
            if (minecartEntity.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN)) return;
            chestLoc = minecartEntity.getLocation();
        } else {
            return;
        }

        String dungeonId = findDungeonForChest(chestLoc.getWorld(), chestLoc, holder);
        if (dungeonId == null) return;

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.loot.enabled) return;

        plugin.getComponentLogger().info("<aqua>InventoryOpen: данж=" + dungeonId + " в " + chestLoc);

        // Помечаем как обработанное (блокируем повторную генерацию)
        if (holder instanceof org.bukkit.block.TileState tile) {
            tile.getPersistentDataContainer().set(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN, true);
            tile.update(true, false);
        } else if (holder instanceof org.bukkit.entity.Entity entity) {
            entity.getPersistentDataContainer().set(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN, true);
        }

        Location finalChestLoc = chestLoc.clone();
        String finalDungeonId = dungeonId;

        Bukkit.getScheduler().runTask(plugin, () -> {
            Inventory targetInv;
            if (isMinecart) {
                var entities = finalChestLoc.getWorld().getNearbyEntities(finalChestLoc, 0.5, 0.5, 0.5,
                        e -> e instanceof org.bukkit.entity.Entity en
                                && (en.getType() == org.bukkit.entity.EntityType.CHEST_MINECART
                                || en.getType() == org.bukkit.entity.EntityType.HOPPER_MINECART)
                                && en.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN));
                if (entities.isEmpty()) return;
                var minecartEntity = entities.iterator().next();
                if (minecartEntity instanceof org.bukkit.inventory.InventoryHolder ih) {
                    targetInv = ih.getInventory();
                } else {
                    return;
                }
            } else {
                Block b = finalChestLoc.getBlock();
                if (!(b.getState() instanceof InventoryHolder h)) return;
                if (h instanceof org.bukkit.block.TileState t
                        && !t.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN)) return;
                targetInv = h.getInventory();
            }

            List<ItemStack> loot = dungeonManager.generateLoot(finalDungeonId);
            boolean chestEmpty = true;
            for (ItemStack item : targetInv.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    chestEmpty = false;
                    break;
                }
            }
            if (chestEmpty) {
                loot.addAll(0, dungeonManager.generateVanillaLoot(finalDungeonId));
            }

            if (!loot.isEmpty()) {
                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < targetInv.getSize(); i++) {
                    ItemStack content = targetInv.getItem(i);
                    if (content == null || content.getType() == Material.AIR) {
                        slots.add(i);
                    }
                }
                Collections.shuffle(slots, random);
                int idx = 0;
                for (ItemStack item : loot) {
                    if (idx < slots.size()) {
                        targetInv.setItem(slots.get(idx++), item);
                    } else {
                        targetInv.addItem(item);
                    }
                }
                plugin.getComponentLogger().info("<green>InventoryOpen: добавлено " + loot.size() + " предметов в " + finalDungeonId);
            }

            if (event.getPlayer() instanceof org.bukkit.entity.Player player) {
                player.updateInventory();
            }
        });

        // Спавним босса рядом с контейнером
        if (config.bosses.enabled && config.bosses.types != null && !config.bosses.types.isEmpty() && random.nextDouble() < 0.2) {
            var bossConfig = config.bosses.types.get(random.nextInt(config.bosses.types.size()));
            if (bossConfig.enabled) {
                Location bossLoc = chestLoc.clone().add(2, 1, 0);
                dungeonManager.spawnBoss(bossLoc, bossConfig);
                if (event.getPlayer() instanceof org.bukkit.entity.Player player) {
                    player.sendMessage(plugin.getConfigManager().getLang("dungeon.boss-spawned"));
                }
            }
        }
    }

    // ─── Определение данжа для сундука ───

    private void cacheChunk(String key, String value) {
        chunkDungeonCache.put(key, value);
        chunkCacheTime.put(key, System.currentTimeMillis());
    }

    private String findDungeonForChest(World world, Location loc, InventoryHolder holder) {
        // 1) PDC на сундуке (кастомные схематики)
        if (holder instanceof org.bukkit.block.TileState tile) {
            String fromPdc = tile.getPersistentDataContainer().get(DUNGEON_ID_KEY, PersistentDataType.STRING);
            if (fromPdc != null) return fromPdc;
        }

        String chunkKey = world.getName() + ":" + (loc.getBlockX() >> 4) + ":" + (loc.getBlockZ() >> 4);

        // 2) Кеш чанков — только ПОЛОЖИТЕЛЬНЫЙ результат (данж найден)
        Long cachedTime = chunkCacheTime.get(chunkKey);
        if (cachedTime != null && System.currentTimeMillis() - cachedTime < CHUNK_CACHE_TTL) {
            String cached = chunkDungeonCache.get(chunkKey);
            if (cached != null && !cached.isEmpty()) return cached;
            // null/пусто — не возвращаем, проверяем другие источники
        }

        // 3) Поиск структуры рядом через Registry.STRUCTURE
        String fromStructure = findDungeonByStructure(world, loc);
        if (fromStructure != null) {
            cacheChunk(chunkKey, fromStructure);
            return fromStructure;
        }

        // 4) По отслеженным структурам (для новых структур)
        String fromTracked = findDungeonAtLocation(world, loc);
        if (fromTracked != null) {
            cacheChunk(chunkKey, fromTracked);
            return fromTracked;
        }

        // 5) Все методы не нашли — кешируем отрицательный результат
        cacheChunk(chunkKey, "");
        return null;
    }

    private String findDungeonByStructure(World world, Location loc) {
        var structureRegistry = Registry.STRUCTURE;
        if (structureRegistry == null) return null;

        String[][] structureMap = {
            {"stronghold", "stronghold"},
            {"fortress", "fortress"},
            {"ancient_city", "ancient_city"},
            {"bastion_remnant", "bastion"},
            {"mansion", "mansion"},
            {"monument", "monument"},
            {"desert_pyramid", "temple"},
            {"jungle_pyramid", "jungle_temple"},
            {"igloo", "igloo"},
            {"village_plains", "village"},
            {"shipwreck", "shipwreck"},
            {"end_city", "end_city"},
            {"pillager_outpost", "pillager_outpost"},
            {"ruined_portal", "ruined_portal"},
            {"buried_treasure", "buried_treasure"},
            {"mineshaft", "mineshaft"},
            {"trail_ruins", "trail_ruins"},
            {"trial_chambers", "trial_chambers"},
            {"ocean_ruin_cold", "ocean_ruins"},
            {"ocean_ruin_warm", "ocean_ruins"}
        };
        for (String[] pair : structureMap) {
            try {
                var structure = Registry.STRUCTURE.get(NamespacedKey.minecraft(pair[0]));
                if (structure == null) continue;
                var found = world.locateNearestStructure(loc, structure, 200, false);
                if (found != null && found.getLocation().distanceSquared(loc) < 40000) {
                    plugin.getComponentLogger().info("<aqua>Найдена структура " + pair[0] + " рядом с сундуком");
                    return pair[1];
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        var pdc = entity.getPersistentDataContainer();
        var bossArtifactKey = new NamespacedKey(plugin, "boss_artifact");

        // Если это босс плагина — очищаем ванильный дроп
        if (pdc.has(bossArtifactKey, PersistentDataType.STRING)
                || pdc.has(new NamespacedKey(plugin, "boss_blueprint"), PersistentDataType.STRING)) {
            event.getDrops().clear();
            event.setDroppedExp(0);
        }

        // Шансы из конфига (по умолчанию 0.25)
        double artifactChance = pdc.getOrDefault(
                new NamespacedKey(plugin, "boss_drop_chance"), PersistentDataType.DOUBLE, 0.25);
        double blueprintChance = pdc.getOrDefault(
                new NamespacedKey(plugin, "boss_bp_chance"), PersistentDataType.DOUBLE, 0.5);

        String artifactId = pdc.get(bossArtifactKey, PersistentDataType.STRING);
        if (artifactId != null && random.nextDouble() < artifactChance) {
            Artifact artifact = plugin.getArtifactRegistry().get(artifactId);
            if (artifact != null) {
                ItemStack item = plugin.getArtifactManager().createItemStack(artifact);
                entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                killer.sendMessage(plugin.getConfigManager().getLang("dungeon.artifact-found"));
            }
        }

        String bpId = pdc.get(
                new NamespacedKey(plugin, "boss_blueprint"), PersistentDataType.STRING);
        if (bpId != null && random.nextDouble() < blueprintChance) {
            var recipe = plugin.getAltarManager().getCraftingManager().getRecipe(bpId);
            if (recipe != null) {
                var art = plugin.getArtifactRegistry().get(recipe.getResultId());
                String name = art != null ? art.getDisplayName() : recipe.getResultId();
                ItemStack bp = me.darkcube.wa.altar.AltarBlockTracker.createBlueprint(
                        bpId, name, "PAPER", "<gold>📜 Чертёж: " + name, null, 5001);
                entity.getWorld().dropItemNaturally(entity.getLocation(), bp);
                killer.sendMessage(plugin.getConfigManager().getLang("dungeon.artifact-found"));
            }
        }
    }

    // ─── Маппинг лутейблов Minecraft → ID данжа ───

    private String mapLootTableToDungeon(NamespacedKey lootTable) {
        if (lootTable == null) return null;
        String key = lootTable.getKey().toLowerCase();

        if (key.contains("stronghold")) return "stronghold";
        if (key.contains("nether_bridge") || key.contains("fortress")) return "fortress";
        if (key.contains("ancient_city")) return "ancient_city";
        if (key.contains("bastion")) return "bastion";
        if (key.contains("mansion")) return "mansion";
        if (key.contains("underwater_ruin_big") || key.contains("monument")) return "monument";
        if (key.contains("underwater_ruin_small")) return "ocean_ruins";
        if (key.contains("desert_pyramid") || key.contains("temple")) return "temple";
        if (key.contains("jungle_pyramid") || key.contains("jungle_temple")) return "jungle_temple";
        if (key.contains("igloo")) return "igloo";
        if (key.contains("village")) return "village";
        if (key.contains("shipwreck")) return "shipwreck";
        if (key.contains("end_city")) return "end_city";
        if (key.contains("pillager_outpost")) return "pillager_outpost";
        if (key.contains("ruined_portal")) return "ruined_portal";
        if (key.contains("buried_treasure")) return "buried_treasure";
        if (key.contains("abandoned_mineshaft") || key.contains("mineshaft")) return "mineshaft";
        if (key.contains("simple_dungeon") || key.contains("monster_room")) return "mineshaft";
        if (key.contains("trail_ruins")) return "trail_ruins";
        if (key.contains("trial_chambers")) return "trial_chambers";

        return null;
    }

    private String mapStructureToDungeon(String structName) {
        for (var entry : dungeonManager.getAllConfigs().entrySet()) {
            if (structName.contains(entry.getKey().toLowerCase())) {
                return entry.getKey();
            }
        }
        if (structName.contains("stronghold")) return "stronghold";
        if (structName.contains("fortress")) return "fortress";
        if (structName.contains("ancient_city") || structName.contains("ancientcity")) return "ancient_city";
        if (structName.contains("bastion")) return "bastion";
        if (structName.contains("mansion")) return "mansion";
        if (structName.contains("monument")) return "monument";
        if (structName.contains("temple") || structName.contains("desert_pyramid")) return "temple";
        if (structName.contains("jungle_pyramid")) return "jungle_temple";
        if (structName.contains("igloo")) return "igloo";
        if (structName.contains("village")) return "village";
        if (structName.contains("shipwreck")) return "shipwreck";
        if (structName.contains("pillager_outpost")) return "pillager_outpost";
        if (structName.contains("ruined_portal")) return "ruined_portal";
        if (structName.contains("buried_treasure")) return "buried_treasure";
        if (structName.contains("mineshaft")) return "mineshaft";
        if (structName.contains("trail_ruins")) return "trail_ruins";
        if (structName.contains("trial_chambers") || structName.contains("trialchamber")) return "trial_chambers";
        if (structName.contains("ocean_ruin")) return "ocean_ruins";
        return null;
    }

    private String findDungeonAtLocation(World world, Location loc) {
        for (var entry : dungeonManager.getAllConfigs().entrySet()) {
            String dungeonId = entry.getKey();
            for (String trackKey : trackedStructures) {
                if (trackKey.startsWith(world.getName() + ":")) {
                    String[] parts = trackKey.split(":");
                    if (parts.length >= 3) {
                        try {
                            double minX = Double.parseDouble(parts[2]);
                            double minZ = Double.parseDouble(parts[3]);
                            double dist = loc.distanceSquared(new Location(world, minX, loc.getY(), minZ));
                            if (dist < 10000) {
                                return dungeonId;
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        return null;
    }
}
