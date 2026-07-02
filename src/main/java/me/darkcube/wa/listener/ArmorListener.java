package me.darkcube.wa.listener;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.api.event.ArtifactEquipEvent;
import me.darkcube.wa.artifact.Artifact;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArmorListener implements Listener {

    private final WastelandArtifacts plugin;
    private final Map<UUID, ItemStack[]> previousArmor = new HashMap<>();
    private final Map<UUID, ItemStack> previousMainHand = new HashMap<>();

    public ArmorListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        applyCurrentArmor(player);
        previousMainHand.put(player.getUniqueId(), player.getInventory().getItemInMainHand().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        previousArmor.remove(id);
        previousMainHand.remove(id);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;
        int rawSlot = event.getRawSlot();
        if (event.getSlotType() == InventoryType.SlotType.ARMOR || isArmorSlot(rawSlot)) {
            scheduleCheck(player);
        }
        // Offhand slot (40)
        if (rawSlot == 40) {
            scheduleOffhandCheck(player);
        }
        // Main hand — hotbar slots (36-44) or any click affecting hotbar
        if (rawSlot >= 36 && rawSlot <= 44) {
            scheduleMainHandCheck(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.isCancelled()) return;
        for (int slot : event.getNewItems().keySet()) {
            if (slot >= 5 && slot <= 8) {
                scheduleCheck(player);
                return;
            }
            if (slot == 40) {
                scheduleOffhandCheck(player);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClickArmor(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (isArmorItem(item.getType()) || plugin.getArtifactManager().isArtifact(item)) {
            scheduleCheck(event.getPlayer());
            scheduleOffhandCheck(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        scheduleMainHandCheck(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSwapHands(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            scheduleMainHandCheck(player);
            plugin.getArtifactBagManager().recalcEffects(player);
        });
    }

    private void scheduleOffhandCheck(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.getArtifactBagManager().recalcEffects(player);
        });
    }

    private void scheduleCheck(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> checkArmorChange(player));
    }

    private void scheduleMainHandCheck(Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> checkMainHandChange(player));
    }

    private void applyCurrentArmor(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        previousArmor.put(player.getUniqueId(), armor.clone());

        for (ItemStack piece : armor) {
            if (piece != null && piece.getType() != Material.AIR) {
                Artifact art = plugin.getArtifactManager().getArtifactFromItem(piece);
                if (art != null) {
                    art.getComponents().forEach(c -> c.onEquip(player));
                }
            }
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        previousMainHand.put(player.getUniqueId(), mainHand.clone());
        Artifact mainArt = plugin.getArtifactManager().getArtifactFromItem(mainHand);
        if (mainArt != null) {
            mainArt.getComponents().forEach(c -> c.onEquip(player));
        }
        if (plugin.getSetManager() != null) {
            plugin.getSetManager().applySetBonuses(player);
        }
    }

    private void checkMainHandChange(Player player) {
        ItemStack current = player.getInventory().getItemInMainHand();
        ItemStack prev = previousMainHand.get(player.getUniqueId());

        if (isSame(current, prev)) return;

        if (prev != null && prev.getType() != Material.AIR) {
            Artifact oldArt = plugin.getArtifactManager().getArtifactFromItem(prev);
            if (oldArt != null) {
                oldArt.getComponents().forEach(c -> c.onUnequip(player));
                Bukkit.getPluginManager().callEvent(new ArtifactEquipEvent(player, oldArt,
                        ArtifactEquipEvent.EquipAction.UNEQUIP, ArtifactEquipEvent.EquipSlot.MAIN_HAND));
            }
        }

        if (current != null && current.getType() != Material.AIR) {
            Artifact newArt = plugin.getArtifactManager().getArtifactFromItem(current);
            if (newArt != null) {
                Bukkit.getPluginManager().callEvent(new ArtifactEquipEvent(player, newArt,
                        ArtifactEquipEvent.EquipAction.EQUIP, ArtifactEquipEvent.EquipSlot.MAIN_HAND));
                newArt.getComponents().forEach(c -> c.onEquip(player));
            }
        }

        previousMainHand.put(player.getUniqueId(), current != null ? current.clone() : null);
    }

    private void checkArmorChange(Player player) {
        ItemStack[] current = player.getInventory().getArmorContents();
        ItemStack[] prev = previousArmor.get(player.getUniqueId());

        if (prev == null) {
            applyCurrentArmor(player);
            return;
        }

        for (int i = 0; i < 4; i++) {
            ItemStack oldItem = prev[i];
            ItemStack newItem = current[i];

            if (isSame(oldItem, newItem)) continue;

            if (oldItem != null && oldItem.getType() != Material.AIR) {
                Artifact oldArt = plugin.getArtifactManager().getArtifactFromItem(oldItem);
                if (oldArt != null) {
                    oldArt.getComponents().forEach(c -> c.onUnequip(player));
                    Bukkit.getPluginManager().callEvent(new ArtifactEquipEvent(player, oldArt,
                            ArtifactEquipEvent.EquipAction.UNEQUIP, ArtifactEquipEvent.EquipSlot.ARMOR));
                }
            }

            if (newItem != null && newItem.getType() != Material.AIR) {
                Artifact newArt = plugin.getArtifactManager().getArtifactFromItem(newItem);
                if (newArt != null) {
                    Bukkit.getPluginManager().callEvent(new ArtifactEquipEvent(player, newArt,
                            ArtifactEquipEvent.EquipAction.EQUIP, ArtifactEquipEvent.EquipSlot.ARMOR));
                    newArt.getComponents().forEach(c -> c.onEquip(player));
                }
            }
        }

        previousArmor.put(player.getUniqueId(), current.clone());
        if (plugin.getSetManager() != null) {
            plugin.getSetManager().applySetBonuses(player);
        }
    }

    private boolean isSame(ItemStack a, ItemStack b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.getType() == b.getType()
                && a.getAmount() == b.getAmount()
                && Objects.equals(
                a.getItemMeta() != null ? a.getItemMeta().getPersistentDataContainer().get(
                        new org.bukkit.NamespacedKey("wastelandartifacts", "artifact_id"),
                        org.bukkit.persistence.PersistentDataType.STRING) : null,
                b.getItemMeta() != null ? b.getItemMeta().getPersistentDataContainer().get(
                        new org.bukkit.NamespacedKey("wastelandartifacts", "artifact_id"),
                        org.bukkit.persistence.PersistentDataType.STRING) : null
        );
    }

    private boolean isArmorSlot(int rawSlot) {
        InventoryType.SlotType slotType = InventoryType.SlotType.ARMOR;
        return rawSlot >= 5 && rawSlot <= 8;
    }

    private boolean isArmorItem(Material material) {
        return material.name().endsWith("_HELMET")
                || material.name().endsWith("_CHESTPLATE")
                || material.name().endsWith("_LEGGINGS")
                || material.name().endsWith("_BOOTS")
                || material == Material.ELYTRA
                || material == Material.CARVED_PUMPKIN
                || material.name().endsWith("_HEAD")
                || material == Material.SKELETON_SKULL
                || material == Material.WITHER_SKELETON_SKULL
                || material == Material.ZOMBIE_HEAD
                || material == Material.CREEPER_HEAD
                || material == Material.DRAGON_HEAD;
    }
}
