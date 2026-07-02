package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SoulbindComponent implements ArtifactComponent {

    private static final NamespacedKey OWNER_KEY = new NamespacedKey("wastelandartifacts", "soulbound_owner");

    @Override
    public @NotNull String getType() { return "SOULBIND"; }

    @Override
    public void apply(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && !meta.getPersistentDataContainer().has(OWNER_KEY, PersistentDataType.STRING)) {
            meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, "pending");
            item.setItemMeta(meta);
        }
    }

    @Override
    public void onEquip(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String owner = meta.getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
            if ("pending".equals(owner) || owner == null) {
                meta.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
                item.setItemMeta(meta);
            }
        }
    }

    @Override
    public void onUnequip(@NotNull Player player) {}

    public static boolean isOwner(ItemStack item, Player player) {
        if (item == null || !item.hasItemMeta()) return true;
        ItemMeta meta = item.getItemMeta();
        String owner = meta.getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
        if (owner == null || "pending".equals(owner)) return true;
        return owner.equals(player.getUniqueId().toString());
    }

    public static String getOwner(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
    }

    public static NamespacedKey getOwnerKey() { return OWNER_KEY; }
}
