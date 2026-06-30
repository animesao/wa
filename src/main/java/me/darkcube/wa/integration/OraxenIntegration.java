package me.darkcube.wa.integration;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class OraxenIntegration {

    private static final String PLUGIN_NAME = "Oraxen";
    private static boolean enabled = false;
    private static Method getItemByIdMethod;
    private static Method getIdByItemMethod;
    private static Method existsMethod;

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            enabled = false;
            return;
        }
        try {
            Class<?> oraxenItemsClass = Class.forName("io.oraxen.oraxen.api.OraxenItems");
            getItemByIdMethod = oraxenItemsClass.getMethod("getItemById", String.class);
            getIdByItemMethod = oraxenItemsClass.getMethod("getIdByItem", ItemStack.class);
            existsMethod = oraxenItemsClass.getMethod("exists", String.class);
            enabled = true;
        } catch (Exception e) {
            enabled = false;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public static String getItemId(ItemStack item) {
        if (!enabled || item == null) return null;
        try {
            return (String) getIdByItemMethod.invoke(null, item);
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean isCustomItem(ItemStack item) {
        return getItemId(item) != null;
    }

    @Nullable
    public static ItemStack createItem(String id) {
        if (!enabled || id == null) return null;
        try {
            return (ItemStack) getItemByIdMethod.invoke(null, id);
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean exists(String id) {
        if (!enabled || id == null) return false;
        try {
            return (boolean) existsMethod.invoke(null, id);
        } catch (Exception ignored) {}
        return false;
    }
}
