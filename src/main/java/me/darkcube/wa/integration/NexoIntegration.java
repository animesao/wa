package me.darkcube.wa.integration;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public class NexoIntegration {

    private static final String PLUGIN_NAME = "Nexo";
    private static boolean enabled = false;
    private static Method itemFromIdMethod;
    private static Method idFromItemMethod;

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            enabled = false;
            return;
        }
        try {
            Class<?> nexoItemsClass = Class.forName("it.nexomc.nexo.api.NexoItems");
            itemFromIdMethod = nexoItemsClass.getMethod("itemFromId", String.class);
            idFromItemMethod = nexoItemsClass.getMethod("idFromItem", ItemStack.class);
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
            return (String) idFromItemMethod.invoke(null, item);
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
            return (ItemStack) itemFromIdMethod.invoke(null, id);
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean exists(String id) {
        if (!enabled || id == null) return false;
        return createItem(id) != null;
    }
}
