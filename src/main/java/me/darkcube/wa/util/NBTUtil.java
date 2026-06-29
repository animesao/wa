package me.darkcube.wa.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NBTUtil {

    private NBTUtil() {}

    public static @NotNull NamespacedKey key(@NotNull String key) {
        return new NamespacedKey("wastelandartifacts", key);
    }

    public static boolean hasTag(@NotNull ItemStack item, @NotNull String key) {
        if (!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(
                key(key), PersistentDataType.STRING
        );
    }

    public static @Nullable String getString(@NotNull ItemStack item, @NotNull String key) {
        if (!item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(
                key(key), PersistentDataType.STRING
        );
    }

    public static void setString(@NotNull ItemStack item, @NotNull String key, @NotNull String value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key(key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static int getInt(@NotNull ItemStack item, @NotNull String key, int def) {
        if (!item.hasItemMeta()) return def;
        Integer val = item.getItemMeta().getPersistentDataContainer().get(
                key(key), PersistentDataType.INTEGER
        );
        return val != null ? val : def;
    }

    public static void setInt(@NotNull ItemStack item, @NotNull String key, int value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    public static double getDouble(@NotNull ItemStack item, @NotNull String key, double def) {
        if (!item.hasItemMeta()) return def;
        Double val = item.getItemMeta().getPersistentDataContainer().get(
                key(key), PersistentDataType.DOUBLE
        );
        return val != null ? val : def;
    }

    public static void setDouble(@NotNull ItemStack item, @NotNull String key, double value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key(key), PersistentDataType.DOUBLE, value);
        item.setItemMeta(meta);
    }

    public static void removeTag(@NotNull ItemStack item, @NotNull String key) {
        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().remove(key(key));
        item.setItemMeta(meta);
    }
}
