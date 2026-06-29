package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.dungeon.DungeonManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    public DungeonListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.dungeonManager = plugin.getDungeonManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onStructureGenerate(AsyncStructureGenerateEvent event) {
        if (!plugin.getConfigManager().getMainConfig().dungeons.enabled) return;

        World world = event.getWorld();
        Structure structure = event.getStructure();
        String structName = structure != null ? structure.getKey().getKey().toLowerCase() : "unknown";

        // Определяем ID данжа по имени структуры
        String dungeonId = mapStructureToDungeon(structName);
        if (dungeonId == null) return;

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.enabled) return;

        // Запоминаем позицию структуры для последующей обработки
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
            // Спавним боссов
            if (config.bosses.enabled && config.bosses.types != null) {
                for (var bossConfig : config.bosses.types) {
                    if (bossConfig.enabled && random.nextDouble() < 0.3) {
                        Location bossLoc = baseLoc.clone().add(
                                random.nextInt(10) - 5,
                                1,
                                random.nextInt(10) - 5
                        );
                        bossLoc.setY(world.getHighestBlockYAt(bossLoc));
                        dungeonManager.spawnBoss(bossLoc, bossConfig);
                    }
                }
            }

            // Генерируем специальные сундуки
            if (config.specialChests.enabled && config.specialChests.positions != null) {
                for (var pos : config.specialChests.positions) {
                    Location chestLoc = baseLoc.clone().add(pos.rx, pos.ry, pos.rz);
                    Block block = chestLoc.getBlock();
                    if (block.getType() == Material.CHEST) {
                        List<ItemStack> loot = dungeonManager.generateLoot(dungeonId);
                        if (!loot.isEmpty() && block.getState() instanceof Chest chest) {
                            Inventory inv = chest.getInventory();
                            for (ItemStack item : loot) {
                                int slot = random.nextInt(inv.getSize());
                                inv.setItem(slot, item);
                            }
                        }
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLootGenerate(LootGenerateEvent event) {
        if (!plugin.getConfigManager().getMainConfig().dungeons.lootInjection) return;
        if (event.isCancelled()) return;

        InventoryHolder holder = event.getInventoryHolder();
        if (holder == null) return;

        // Определяем, находится ли этот контейнер в структуре
        Location loc;
        if (holder instanceof BlockState state) {
            loc = state.getLocation();
        } else if (holder instanceof DoubleChest dc) {
            loc = dc.getLocation();
        } else {
            return;
        }

        // Ищем подходящий данж
        String dungeonId = findDungeonAtLocation(loc.getWorld(), loc);
        if (dungeonId == null) return;

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.loot.enabled) return;

        // Режим INJECT — добавляем артефакты к существующему луту
        if ("INJECT".equalsIgnoreCase(config.loot.mode)) {
            List<ItemStack> extraLoot = dungeonManager.generateLoot(dungeonId);
            if (!extraLoot.isEmpty()) {
                List<ItemStack> existingLoot = event.getLoot();
                existingLoot.addAll(extraLoot);
                event.setLoot(existingLoot);
            }
        }
        // Режим REPLACE — заменяем часть лута
        else if ("REPLACE".equalsIgnoreCase(config.loot.mode)) {
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

        // Помечаем контейнер как обработанный
        if (holder instanceof org.bukkit.block.TileState tile) {
            tile.getPersistentDataContainer().set(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN, true);
        }
    }

    private static final NamespacedKey LOOT_POPULATED_KEY = new NamespacedKey("wastelandartifacts", "loot_populated");

    @EventHandler(priority = EventPriority.HIGH)
    public void onChestOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || (block.getType() != Material.CHEST && block.getType() != Material.BARREL
                && block.getType() != Material.SHULKER_BOX)) return;

        if (!(block.getState() instanceof InventoryHolder holder)) return;
        if (!(holder instanceof org.bukkit.block.TileState tile)) return;
        Inventory inv = holder.getInventory();

        // Пропускаем, если лут уже был сгенерирован плагином
        if (tile.getPersistentDataContainer().has(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN)) return;

        // Определяем ID данжа по местоположению
        String dungeonId = findDungeonAtLocation(block.getWorld(), block.getLocation());
        if (dungeonId == null) return;

        DungeonManager.DungeonConfig config = dungeonManager.getConfig(dungeonId);
        if (config == null || !config.loot.enabled) return;

        // Проверяем — если в сундуке уже есть лут (ванильный), пропускаем (он уже обработан LootGenerateEvent)
        boolean hasExistingLoot = false;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                hasExistingLoot = true;
                break;
            }
        }
        if (!hasExistingLoot) {
            // Пустой сундук в зоне данжа — генерируем лут
            List<ItemStack> loot = dungeonManager.generateLoot(dungeonId);
            for (ItemStack item : loot) {
                int slot = random.nextInt(inv.getSize());
                inv.setItem(slot, item);
            }
        }

        // Помечаем сундук как обработанный
        tile.getPersistentDataContainer().set(LOOT_POPULATED_KEY, PersistentDataType.BOOLEAN, true);
        tile.update(true, false);

        // Спавним босса при открытии сундука
        if (config.bosses.enabled && config.bosses.types != null && random.nextDouble() < 0.2) {
            var bossConfig = config.bosses.types.get(random.nextInt(config.bosses.types.size()));
            if (bossConfig.enabled) {
                Location bossLoc = block.getLocation().add(2, 1, 0);
                dungeonManager.spawnBoss(bossLoc, bossConfig);
                Player player = event.getPlayer();
                player.sendMessage(plugin.getConfigManager().getLang("boss-spawned"));
            }
        }
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();
        if (killer == null) return;

        var pdc = entity.getPersistentDataContainer();

        // Артефакт
        String artifactId = pdc.get(
                new NamespacedKey(plugin, "boss_artifact"),
                PersistentDataType.STRING
        );
        if (artifactId != null && random.nextDouble() < 0.25) {
            Artifact artifact = plugin.getArtifactRegistry().get(artifactId);
            if (artifact != null) {
                ItemStack item = plugin.getArtifactManager().createItemStack(artifact);
                entity.getWorld().dropItemNaturally(entity.getLocation(), item);
                killer.sendMessage(plugin.getConfigManager().getLang("artifact-found"));
            }
        }

        // Чертёж
        String bpId = pdc.get(
                new NamespacedKey(plugin, "boss_blueprint"),
                PersistentDataType.STRING
        );
        if (bpId != null && random.nextDouble() < 0.5) {
            var recipe = plugin.getAltarManager().getCraftingManager().getRecipe(bpId);
            if (recipe != null) {
                var art = plugin.getArtifactRegistry().get(recipe.getResultId());
                String name = art != null ? art.getDisplayName() : recipe.getResultId();
                ItemStack bp = me.darkcube.wa.altar.AltarBlockTracker.createBlueprint(
                        bpId, name, "PAPER", "<gold>📜 Чертёж: " + name, null, 5001);
                entity.getWorld().dropItemNaturally(entity.getLocation(), bp);
                killer.sendMessage(plugin.getConfigManager().getLang("artifact-found"));
            }
        }
    }

    private String mapStructureToDungeon(String structName) {
        for (var entry : dungeonManager.getAllConfigs().entrySet()) {
            if (structName.contains(entry.getKey().toLowerCase())) {
                return entry.getKey();
            }
        }
        // Маппинг ванильных структур
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
                            if (dist < 10000) { // в радиусе 100 блоков
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
