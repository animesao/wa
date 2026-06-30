package me.darkcube.wa.integration;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class ItemsAdderIntegration {

    private static final String PLUGIN_NAME = "ItemsAdder";
    private static boolean enabled = false;
    private static Method byItemStackMethod;
    private static Method getNamespacedIDMethod;
    private static Method getCustomItemMethod;
    private static Method isInRegistryMethod;
    private static Method getInstanceMethod;

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            enabled = false;
            return;
        }
        try {
            Class<?> customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            Class<?> itemsAdderClass = Class.forName("dev.lone.itemsadder.api.ItemsAdder");
            byItemStackMethod = customStackClass.getMethod("byItemStack", ItemStack.class);
            getNamespacedIDMethod = customStackClass.getMethod("getNamespacedID");
            getCustomItemMethod = itemsAdderClass.getMethod("getCustomItem", String.class);
            isInRegistryMethod = customStackClass.getMethod("isInRegistry", String.class);
            getInstanceMethod = customStackClass.getMethod("getInstance", String.class);
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
            Object customStack = byItemStackMethod.invoke(null, item);
            if (customStack != null) {
                return (String) getNamespacedIDMethod.invoke(customStack);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean isCustomItem(ItemStack item) {
        return getItemId(item) != null;
    }

    @Nullable
    public static ItemStack createItem(String namespacedId) {
        if (!enabled || namespacedId == null) return null;
        try {
            return (ItemStack) getCustomItemMethod.invoke(null, namespacedId);
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean exists(String namespacedId) {
        if (!enabled || namespacedId == null) return false;
        try {
            return (boolean) isInRegistryMethod.invoke(null, namespacedId);
        } catch (Exception ignored) {}
        return false;
    }
}
