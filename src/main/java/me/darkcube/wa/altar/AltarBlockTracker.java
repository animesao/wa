package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AltarBlockTracker {

    private final WastelandArtifacts plugin;
    private final Map<String, AltarState> altars = new ConcurrentHashMap<>();
    private final AltarHologram hologram;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private static final int SEARCH_RADIUS = 4;
    private static final NamespacedKey BP_KEY = new NamespacedKey("wastelandartifacts", "blueprint_recipe");


    public AltarBlockTracker(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.hologram = new AltarHologram(plugin);
        startTicker();
    }

    private String key(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public boolean tryAcceptDrop(@NotNull Player player, @NotNull Item droppedItem) {
        ItemStack stack = droppedItem.getItemStack();
        boolean isRelevant = getBlueprintRecipe(droppedItem) != null
                || plugin.getArtifactManager().isArtifact(stack)
                || plugin.getCustomItemRegistry().getId(stack) != null
                || stack.getType() == Material.PAPER || stack.getType() == Material.MAP;

        Block dropBlock = droppedItem.getLocation().getBlock();

        // Ищем сначала по highest tier, чтобы легендарный не определялся как продвинутый
        var tiers = new ArrayList<>(plugin.getAltarManager().getAllTiers().entrySet());
        tiers.sort((a, b) -> Integer.compare(b.getValue().tier, a.getValue().tier));

        for (var entry : tiers) {
            AltarConfig.AltarTier tier = entry.getValue();
            if (!tier.enabled) continue;

            Block activator = findNearestActvator(tier, dropBlock);
            if (activator == null) {
                activator = findNearestActvator(tier, player.getLocation().getBlock());
            }
            if (activator == null) continue;

            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.found", tier.displayName)));
            Location actLoc = activator.getLocation().clone();
            String tierId = entry.getKey();
            String altarKey = key(actLoc);
            AltarState state = altars.computeIfAbsent(altarKey,
                    k -> new AltarState(actLoc, tierId, tier));

            String recipeId = getBlueprintRecipe(droppedItem);
            if (recipeId != null) {
                return acceptBlueprint(player, droppedItem, activator, altarKey, state, tier, recipeId);
            }

            if (state.activeRecipe == null) {
                if (isRelevant) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.need-blueprint")));
                }
                return false;
            }

            return acceptIngredient(player, droppedItem, activator, altarKey, state, tier);
        }

        if (isRelevant) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.no-altar-nearby")));
        }
        return false;
    }

    private boolean acceptBlueprint(Player player, Item droppedItem, Block activator,
                                     String altarKey, AltarState state,
                                     AltarConfig.AltarTier tier, String recipeId) {
        AltarRecipe recipe = plugin.getAltarManager().getCraftingManager().getRecipe(recipeId);
        if (recipe == null || recipe.getTier() > tier.tier) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.wrong-blueprint")));
            return false;
        }

        // Очищаем старые предметы и катализатор
        for (int i = 1; i < 9; i++) {
            if (state.items[i] != null && state.items[i].isValid()) {
                state.items[i].remove();
                state.items[i] = null;
            }
        }
        if (state.catalyst != null && state.catalyst.isValid()) {
            state.catalyst.remove();
            state.catalyst = null;
        }

        state.activeRecipe = recipe;

        // Помещаем чертёж в слот 0
        droppedItem.setGravity(false);
        droppedItem.setPickupDelay(Integer.MAX_VALUE);
        droppedItem.setGlowing(true);
        droppedItem.setInvulnerable(true);
        droppedItem.setMetadata("wa_altar_item", new FixedMetadataValue(plugin, true));
        droppedItem.setMetadata("wa_altar_key", new FixedMetadataValue(plugin, altarKey));
        droppedItem.setMetadata("wa_altar_slot", new FixedMetadataValue(plugin, 0));

        Location pedLoc = getBlueprintLocation(activator.getLocation());
        if (pedLoc != null) droppedItem.teleport(pedLoc);
        state.items[0] = droppedItem;

        var resultArtifact = plugin.getArtifactRegistry().get(recipe.getResultId());
        String artDisplayName = resultArtifact != null ? resultArtifact.getDisplayName() : recipe.getResultId();
        player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.blueprint-accepted", artDisplayName)));
        player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.drop-ingredients")));

        for (var ing : recipe.getIngredients()) {
            String itemName = getIngredientDisplayName(ing);
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.ingredient-slot", itemName, ing.getAmount(), ing.getSlot())));
        }

        updateHologram(altarKey, activator.getLocation(), state);
        plugin.getAltarManager().spawnEffects(activator.getLocation().add(0.5, 1, 0.5), tier.effects.activate);
        return true;
    }

    private void acceptCatalyst(Player player, Item droppedItem, Block activator,
                                  String altarKey, AltarState state, AltarRecipe recipe) {
        // Убираем старый катализатор если был
        if (state.catalyst != null && state.catalyst.isValid()) {
            state.catalyst.remove();
        }
        droppedItem.setGravity(false);
        droppedItem.setPickupDelay(Integer.MAX_VALUE);
        droppedItem.setGlowing(true);
        droppedItem.setInvulnerable(true);
        droppedItem.setMetadata("wa_altar_item", new FixedMetadataValue(plugin, true));
        droppedItem.setMetadata("wa_altar_key", new FixedMetadataValue(plugin, altarKey));
        droppedItem.setMetadata("wa_altar_slot", new FixedMetadataValue(plugin, 9));
        Location pedLoc = getPedestalLocation(activator.getLocation(), 0);
        if (pedLoc != null) droppedItem.teleport(pedLoc.clone().add(1, 0, 0));
        state.catalyst = droppedItem;
        player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.catalyst-accepted")));
        updateHologram(altarKey, activator.getLocation(), state);
    }

    private boolean acceptIngredient(Player player, Item droppedItem, Block activator,
                                      String altarKey, AltarState state,
                                      AltarConfig.AltarTier tier) {
        AltarRecipe recipe = state.activeRecipe;
        if (recipe == null) return false;

        ItemStack dropStack = droppedItem.getItemStack();
        Material dropType = dropStack.getType();
        int totalAmount = dropStack.getAmount();

        // Сначала проверяем — может это катализатор? (только если не совпадает ни с одним ингредиентом)
        if (recipe.getCatalyst() != null && (state.catalyst == null || !state.catalyst.isValid())) {
            boolean isIngredient = false;
            for (var ing : recipe.getIngredients()) {
                if (dropType == ing.getType()) { isIngredient = true; break; }
                if (ing.getTemplate() != null && itemsMatch(dropStack, ing.getTemplate())) { isIngredient = true; break; }
            }
            if (!isIngredient) {
                boolean matchesCat = false;
                if (recipe.getCatalyst().getTemplate() != null) {
                    matchesCat = itemsMatch(dropStack, recipe.getCatalyst().getTemplate());
                } else {
                    matchesCat = dropType == recipe.getCatalyst().getItem();
                }
                if (matchesCat) {
                    acceptCatalyst(player, droppedItem, activator, altarKey, state, recipe);
                    return true;
                }
            }
        }

        List<AltarRecipe.Ingredient> matching = new ArrayList<>();
        boolean hasCustomName = dropStack.hasItemMeta() && dropStack.getItemMeta().hasDisplayName();

        // Этап 1: точные совпадения по шаблону
        if (hasCustomName) {
            for (var ing : recipe.getIngredients()) {
                int slot = ing.getSlot();
                if (slot < 1 || slot > 8) continue;
                if (ing.getTemplate() != null && itemsMatch(dropStack, ing.getTemplate())) {
                    matching.add(ing);
                }
            }
        }

        // Этап 2: если ничего не нашли — совпадение по типу
        if (matching.isEmpty()) {
            for (var ing : recipe.getIngredients()) {
                int slot = ing.getSlot();
                if (slot < 1 || slot > 8) continue;
                if (dropType == ing.getType()) {
                    matching.add(ing);
                }
            }
        }

        if (matching.isEmpty()) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.no-match", getItemDisplayName(dropStack))));
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.recipe-has",
                    recipe.getIngredients().stream()
                        .map(i -> getIngredientDisplayName(i))
                        .distinct()
                        .collect(java.util.stream.Collectors.joining(", ")))));
            return false;
        } else {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.slots-found", matching.size())));
        }

        matching.sort(Comparator.comparingInt(ing -> {
            Item ex = state.items[ing.getSlot()];
            if (ex == null || !ex.isValid()) return 0;
            if (ex.getItemStack().getType() == dropType) {
                return ex.getItemStack().getAmount() >= ing.getAmount() ? 2 : 1;
            }
            return 2;
        }));

        int remaining = totalAmount;

        for (var ing : matching) {
            if (remaining <= 0) break;
            int slot = ing.getSlot();
            int need = ing.getAmount();
            Item existing = state.items[slot];
            int has = 0;
            if (existing != null && existing.isValid() && existing.getItemStack().getType() == dropType) {
                has = existing.getItemStack().getAmount();
            }
            if (has >= need) continue;
            int toPlace = Math.min(remaining, need - has);

            if (existing == null || !existing.isValid()) {
                ItemStack newStack = droppedItem.getItemStack().clone();
                newStack.setAmount(toPlace);
                Location pedLoc = getPedestalLocation(activator.getLocation(), slot);
                World w = activator.getWorld();
                Item newItem = w.spawn(pedLoc != null ? pedLoc : activator.getLocation(), Item.class, item -> {
                    item.setItemStack(newStack);
                    item.setGravity(false);
                    item.setPickupDelay(Integer.MAX_VALUE);
                    item.setGlowing(true);
                    item.setInvulnerable(true);
                    item.setMetadata("wa_altar_item", new FixedMetadataValue(plugin, true));
                    item.setMetadata("wa_altar_key", new FixedMetadataValue(plugin, altarKey));
                    item.setMetadata("wa_altar_slot", new FixedMetadataValue(plugin, slot));
                });
                state.items[slot] = newItem;
                player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.item-placed", getItemName(ing), toPlace, slot)));
            } else {
                ItemStack exStack = existing.getItemStack();
                exStack.setAmount(has + toPlace);
                existing.setItemStack(exStack);
                String n = getItemName(ing);
                player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.item-added", toPlace, n, slot, has + toPlace)));
            }
            remaining -= toPlace;
        }

        if (remaining <= 0) {
            droppedItem.remove();
        } else if (remaining < totalAmount) {
            ItemStack leftover = droppedItem.getItemStack().clone();
            leftover.setAmount(remaining);
            activator.getWorld().dropItemNaturally(droppedItem.getLocation(), leftover);
            droppedItem.remove();
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.remainder", remaining)));
        } else {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.slots-full")));
            return false;
        }

        updateHologram(altarKey, activator.getLocation(), state);
        plugin.getAltarManager().spawnEffects(activator.getLocation().add(0.5, 1, 0.5), tier.effects.activate);
        return true;
    }

    private boolean itemsMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getType() != b.getType()) return false;
        if (!a.hasItemMeta() || !b.hasItemMeta()) return a.getType() == b.getType();

        var aMeta = a.getItemMeta();
        var bMeta = b.getItemMeta();

        // Сравниваем display name
        boolean nameMatch = true;
        if (aMeta.hasDisplayName() && bMeta.hasDisplayName()) {
            nameMatch = aMeta.displayName().equals(bMeta.displayName());
        } else if (aMeta.hasDisplayName() || bMeta.hasDisplayName()) {
            nameMatch = false;
        }
        if (!nameMatch) return false;

        // Сравниваем CustomModelData
        int aCmd = aMeta.hasCustomModelData() ? aMeta.getCustomModelData() : 0;
        int bCmd = bMeta.hasCustomModelData() ? bMeta.getCustomModelData() : 0;
        if (aCmd != bCmd && aCmd > 0 && bCmd > 0) return false;

        return true;
    }

    private String getItemName(AltarRecipe.Ingredient ing) {
        if (ing.getTemplate() != null && ing.getTemplate().hasItemMeta()
                && ing.getTemplate().getItemMeta().hasDisplayName()) {
            return MiniMessage.miniMessage().serialize(ing.getTemplate().getItemMeta().displayName());
        }
        return me.darkcube.wa.util.ItemNameUtil.getRussianName(ing.getType());
    }

    private @Nullable String getBlueprintRecipe(Item item) {
        ItemStack stack = item.getItemStack();
        if (!stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer()
                .get(BP_KEY, PersistentDataType.STRING);
    }

    public static ItemStack createBlueprint(String recipeId, String displayName,
                                              String materialName, String customName,
                                              List<String> lore, int cmd) {
        Material mat = Material.matchMaterial(materialName != null ? materialName : "PAPER");
        if (mat == null) mat = Material.PAPER;
        ItemStack bp = new ItemStack(mat);
        ItemMeta meta = bp.getItemMeta();
        if (customName != null && !customName.isEmpty()) {
            meta.displayName(MiniMessage.miniMessage().deserialize(customName));
        } else {
            meta.displayName(MiniMessage.miniMessage().deserialize("<gold>📜 Чертёж: " + displayName));
        }
        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore.stream()
                    .map(l -> MiniMessage.miniMessage().deserialize(l))
                    .toList());
        }
        if (cmd > 0) meta.setCustomModelData(cmd);
        meta.getPersistentDataContainer().set(BP_KEY, PersistentDataType.STRING, recipeId);
        bp.setItemMeta(meta);
        return bp;
    }

    private @Nullable Location getBlueprintLocation(Location activator) {
        return activator.clone().add(0.5, 3.5, 0.5);
    }

    private @Nullable Block findNearestActvator(AltarConfig.AltarTier tier, Block near) {
        // Сначала проверяем уже отслеживаемые алтари
        for (AltarState state : altars.values()) {
            if (!state.config.activatorBlock.name().equals(tier.activatorBlock.name())) continue;
            Block savedBlock = near.getWorld().getBlockAt(state.activator);
            if (savedBlock.getType() == tier.activatorBlock
                    && savedBlock.getLocation().distanceSquared(near.getLocation()) <= 64) {
                return savedBlock;
            }
        }

        // Полный поиск в радиусе 8 блоков
        int r = 8;
        for (int dx = -r; dx <= r; dx++) {
            for (int dz = -r; dz <= r; dz++) {
                for (int dy = -2; dy <= 4; dy++) {
                    Block check = near.getWorld().getBlockAt(
                            near.getX() + dx, near.getY() + dy, near.getZ() + dz);
                    if (check.getType() == tier.activatorBlock) {
                        if (checkStructure(tier, check)) {
                            return check;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean checkStructure(AltarConfig.AltarTier tier, Block activator) {
        for (var struct : tier.structures) {
            if (struct.layers == null || struct.layers.isEmpty()) continue;
            if (struct.mapping == null) continue;

            int width = struct.layers.get(0).length();
            int rowsPerLayer = width;
            int numLayers = struct.layers.size() / rowsPerLayer;
            int cx = width / 2;
            int cz = rowsPerLayer / 2;

            // Проверяем что активатор действительно в центре
            if (activator.getType() != tier.activatorBlock) continue;

            Map<Character, Material> matMap = new HashMap<>();
            for (var e : struct.mapping.entrySet()) {
                if (e.getKey().length() == 1) {
                    Material m = Material.matchMaterial(e.getValue());
                    if (m != null) matMap.put(e.getKey().charAt(0), m);
                }
            }

            boolean match = true;
            outer:
            for (int ly = 0; ly < numLayers; ly++) {
                for (int rz = 0; rz < rowsPerLayer; rz++) {
                    String row = struct.layers.get(ly * rowsPerLayer + rz);
                    for (int rx = 0; rx < width; rx++) {
                        char sym = row.charAt(rx);
                        if (sym == ' ') continue;
                        Material expected = matMap.get(sym);
                        if (expected == null) { match = false; break outer; }

                        int bx = activator.getX() + (rx - cx);
                        int by = activator.getY() + ly;
                        int bz = activator.getZ() + (rz - cz);

                        if (activator.getWorld().getBlockAt(bx, by, bz).getType() != expected) {
                            match = false;
                            break outer;
                        }
                    }
                }
            }
            if (match) return true;
        }
        return false;
    }

    private @Nullable Location getPedestalLocation(Location activator, int slot) {
        // 9 слотов по кругу, радиус 3.2 блока
        // ВЫСОТА: всегда на Y = activatorY + 2.5 (выше любой структуры)
        double angle = slot * 40.0 * Math.PI / 180.0;
        double px = activator.getX() + 0.5 + Math.sin(angle) * 3.2;
        double pz = activator.getZ() + 0.5 + Math.cos(angle) * 3.2;
        double py = activator.getY() + 2.5;
        return new Location(activator.getWorld(), px, py, pz);
    }

    private void updateHologram(String altarKey, Location activatorLoc, AltarState state) {
        ItemStack[] slots = buildSlotSnapshot(state);
        boolean hasCatalyst = state.catalyst != null && state.catalyst.isValid();
        hologram.update(altarKey, activatorLoc, state.activeRecipe, slots, hasCatalyst);
    }

    private ItemStack[] buildSlotSnapshot(AltarState state) {
        ItemStack[] slots = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            if (state.items[i] != null && state.items[i].isValid()) {
                slots[i] = state.items[i].getItemStack().clone();
            }
        }
        return slots;
    }

    private void checkAndCraft(AltarState state) {
        if (state.activeRecipe == null) return;
        boolean hasItems = false;
        for (int i = 0; i < 9; i++) {
            if (state.items[i] != null && state.items[i].isValid()) {
                hasItems = true;
                break;
            }
        }
        if (!hasItems) return;

        AltarConfig.AltarTier tier = state.config;
        ItemStack[] slots = buildSlotSnapshot(state);
        AltarRecipe recipe = state.activeRecipe;

        for (var ing : recipe.getIngredients()) {
            ItemStack slotItem = slots[ing.getSlot()];
            if (slotItem == null || slotItem.getType() != ing.getType()
                    || slotItem.getAmount() < ing.getAmount()) {
                return;
            }
        }

        // Проверяем катализатор (отдельный предмет)
        if (recipe.getCatalyst() != null) {
            if (state.catalyst == null || !state.catalyst.isValid()) return;
            ItemStack catStack = state.catalyst.getItemStack();
            if (recipe.getCatalyst().getTemplate() != null) {
                if (!itemsMatch(catStack, recipe.getCatalyst().getTemplate())) return;
            } else {
                if (catStack.getType() != recipe.getCatalyst().getItem()) return;
            }
        }

        // Удаляем чертёж и катализатор
        if (state.items[0] != null && state.items[0].isValid()) {
            state.items[0].remove();
            state.items[0] = null;
        }
        if (state.catalyst != null && state.catalyst.isValid()) {
            state.catalyst.remove();
            state.catalyst = null;
        }

        // Крафт!
        Location center = state.activator.clone().add(0.5, 1.5, 0.5);
        plugin.getAltarManager().spawnEffects(center, tier.effects.craft);

        for (var ing : recipe.getIngredients()) {
            int slot = ing.getSlot();
            int need = ing.getAmount();
            Item item = state.items[slot];
            if (item != null && item.isValid()) {
                int has = item.getItemStack().getAmount();
                if (has <= need) {
                    item.remove();
                    state.items[slot] = null;
                } else {
                    ItemStack s = item.getItemStack();
                    s.setAmount(has - need);
                    item.setItemStack(s);
                }
            }
        }

        state.activeRecipe = null;

        var artifact = plugin.getArtifactRegistry().get(recipe.getResultId());
        if (artifact != null) {
            ItemStack result = plugin.getArtifactManager().createItemStack(artifact);
            Item resultItem = center.getWorld().dropItem(center, result);
            resultItem.setGlowing(true);
            resultItem.setPickupDelay(20);

            Player nearest = null;
            double nearDist = Double.MAX_VALUE;
            for (Player p : center.getWorld().getPlayers()) {
                double d = p.getLocation().distance(center);
                if (d < nearDist) { nearDist = d; nearest = p; }
            }
            if (nearest != null) {
                nearest.sendMessage(plugin.getConfigManager().getLang("altar-craft-success"));
            }
        }

        hologram.remove(key(state.activator));
    }

    private void startTicker() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            altars.entrySet().removeIf(e -> {
                AltarState s = e.getValue();
                if (!s.activator.getWorld().isChunkLoaded(
                        s.activator.getBlockX() >> 4, s.activator.getBlockZ() >> 4)) {
                    hologram.remove(e.getKey());
                    return true;
                }
                return false;
            });

            for (var entry : altars.entrySet()) {
                AltarState state = entry.getValue();
                checkAndCraft(state);
                for (int i = 0; i < 9; i++) {
                    if (state.items[i] != null && !state.items[i].isValid()) {
                        state.items[i] = null;
                    }
                }
            }
        }, 40L, 10L);
    }

    public void removeAltar(@NotNull Block anyBlock) {
        String k = key(anyBlock.getLocation());
        AltarState existing = altars.get(k);
        if (existing != null) {
            dropStateItems(existing);
            hologram.remove(k);
            altars.remove(k);
            return;
        }

        for (var entry : altars.entrySet()) {
            AltarState s = entry.getValue();
            if (s.activator.getWorld() == anyBlock.getWorld()
                    && s.activator.distanceSquared(anyBlock.getLocation()) < 100) {
                dropStateItems(s);
                hologram.remove(entry.getKey());
                altars.remove(entry.getKey());
                return;
            }
        }
    }

    public void collectItems(@NotNull Player player, @NotNull Block activatorBlock) {
        String k = key(activatorBlock.getLocation());
        AltarState state = altars.get(k);
        if (state == null) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.no-items")));
            return;
        }

        int count = 0;
        for (int i = 0; i < 9; i++) {
            Item item = state.items[i];
            if (item != null && item.isValid()) {
                ItemStack stack = item.getItemStack();
                HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
                if (leftover.isEmpty()) {
                    item.remove();
                    count++;
                } else {
                    item.setItemStack(leftover.get(0));
                    item.setPickupDelay(0);
                    item.setGlowing(false);
                    item.setInvulnerable(false);
                    item.removeMetadata("wa_altar_item", plugin);
                    count++;
                }
                state.items[i] = null;
            }
        }
        // Собираем катализатор
        if (state.catalyst != null && state.catalyst.isValid()) {
            ItemStack catStack = state.catalyst.getItemStack();
            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(catStack);
            if (leftover.isEmpty()) {
                state.catalyst.remove();
            } else {
                state.catalyst.setItemStack(leftover.get(0));
            }
            state.catalyst = null;
            count++;
        }
        state.activeRecipe = null;
        hologram.remove(k);

        if (count > 0) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.collected", count)));
        } else {
            player.sendMessage(mm.deserialize(plugin.msg("altar.tracker.no-items")));
        }
    }

    private void dropStateItems(AltarState state) {
        Location dropLoc = state.activator.clone().add(0.5, 1.0, 0.5);
        for (Item item : state.items) {
            if (item != null && item.isValid()) {
                item.teleport(dropLoc);
                item.setPickupDelay(0);
                item.setGlowing(false);
                item.setInvulnerable(false);
                item.setGravity(true);
            }
        }
        if (state.catalyst != null && state.catalyst.isValid()) {
            state.catalyst.teleport(dropLoc);
            state.catalyst.setPickupDelay(0);
            state.catalyst.setGlowing(false);
            state.catalyst.setInvulnerable(false);
            state.catalyst.setGravity(true);
            state.catalyst = null;
        }
    }

    public static class AltarState {
        public final Location activator;
        public final String tierId;
        public final AltarConfig.AltarTier config;
        public final Item[] items = new Item[9];
        public Item catalyst; // отдельный предмет-катализатор
        public AltarRecipe activeRecipe;

        public AltarState(Location activator, String tierId, AltarConfig.AltarTier config) {
            this.activator = activator.clone();
            this.tierId = tierId;
            this.config = config;
        }

        public boolean hasItems() {
            for (Item i : items) {
                if (i != null && i.isValid()) return true;
            }
            return (catalyst != null && catalyst.isValid());
        }
    }

    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return mm.serialize(item.getItemMeta().displayName());
        }
        String customId = plugin.getCustomItemRegistry().getId(item);
        if (customId != null) {
            var def = plugin.getCustomItemRegistry().getDef(customId);
            if (def != null && def.name != null) return def.name;
        }
        return me.darkcube.wa.util.ItemNameUtil.getRussianName(item.getType());
    }

    private String getIngredientDisplayName(AltarRecipe.Ingredient ing) {
        if (ing.getTemplate() != null && ing.getTemplate().hasItemMeta()
                && ing.getTemplate().getItemMeta().hasDisplayName()) {
            return mm.serialize(ing.getTemplate().getItemMeta().displayName());
        }
        for (var def : plugin.getCustomItemRegistry().getAll().entrySet()) {
            if (def.getValue().material == ing.getType()
                    && def.getValue().name != null && !def.getValue().name.isEmpty()) {
                return def.getValue().name;
            }
        }
        return me.darkcube.wa.util.ItemNameUtil.getRussianName(ing.getType());
    }
}
