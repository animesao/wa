package me.darkcube.wa.integration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import java.lang.reflect.Method;

public class MythicMobsIntegration {

    private static final String PLUGIN_NAME = "MythicMobs";
    private static boolean enabled = false;
    private static Object apiHelper;
    private static Method spawnMobMethod;

    public static void init() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            enabled = false;
            return;
        }
        try {
            Class<?> mythicBukkitClass = Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            Method instMethod = mythicBukkitClass.getMethod("inst");
            Object instance = instMethod.invoke(null);
            Method getAPIHelperMethod = mythicBukkitClass.getMethod("getAPIHelper");
            apiHelper = getAPIHelperMethod.invoke(instance);
            spawnMobMethod = apiHelper.getClass().getMethod("spawnMob", String.class, Location.class);
            enabled = true;
        } catch (Exception e) {
            enabled = false;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean spawnMob(String mobName, Location loc) {
        if (!enabled || mobName == null || loc == null) return false;
        try {
            spawnMobMethod.invoke(apiHelper, mobName, loc);
            return true;
        } catch (Exception ignored) {}
        return false;
    }
}
