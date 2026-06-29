package me.darkcube.wa.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Base64;

public class MojangItemCodec {

    /**
     * Сериализует ItemStack в Mojang Base64 (NBT-формат Minecraft)
     * Используется для /give, сундуков, обмена между серверами
     */
    public static @NotNull String encode(@Nullable ItemStack item) {
        if (item == null || item.getType().isAir()) return "";
        try {
            byte[] bytes = ItemStack.serializeItemsAsBytes(new ItemStack[]{item});
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Сериализует массив ItemStack[] в Mojang Base64
     */
    public static @NotNull String encodeArray(@Nullable ItemStack[] items) {
        if (items == null || items.length == 0) return "";
        try {
            byte[] bytes = ItemStack.serializeItemsAsBytes(items);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Десериализует Mojang Base64 в ItemStack
     */
    public static @Nullable ItemStack decode(@Nullable String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ItemStack[] items = ItemStack.deserializeItemsFromBytes(bytes);
            return (items != null && items.length > 0) ? items[0] : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Десериализует Mojang Base64 в ItemStack[]
     */
    public static @Nullable ItemStack[] decodeArray(@Nullable String base64) {
        if (base64 == null || base64.isEmpty()) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return ItemStack.deserializeItemsFromBytes(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Проверяет, является ли строка валидным Mojang Base64
     */
    public static boolean isValid(@Nullable String base64) {
        if (base64 == null || base64.isEmpty()) return false;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            ItemStack[] items = ItemStack.deserializeItemsFromBytes(bytes);
            return items != null && items.length > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
