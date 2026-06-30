package me.darkcube.wa.integration;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class PlaceholderAPIExpansion {

    private static boolean enabled = false;

    public static void init(WastelandArtifacts plugin) {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            enabled = false;
            return;
        }
        try {
            Class<?> expansionClass = Class.forName("me.clip.placeholderapi.expansion.PlaceholderExpansion");
            Object expansion = Proxy.newProxyInstance(
                    expansionClass.getClassLoader(),
                    new Class[]{expansionClass},
                    (proxy, method, args) -> {
                        String name = method.getName();
                        if (name.equals("getIdentifier")) return "wa";
                        if (name.equals("getAuthor")) return "darkcube";
                        if (name.equals("getVersion")) return plugin.getDescription().getVersion();
                        if (name.equals("persist")) return true;
                        if (name.equals("onPlaceholderRequest")) {
                            if (args != null && args.length >= 2 && args[1] instanceof String params) {
                                return switch (params) {
                                    case "artifacts_found" -> "0";
                                    case "artifacts_total" -> "0";
                                    case "collection_progress" -> "0/0";
                                    case "bag_used" -> "0";
                                    case "kills" -> "0";
                                    case "level" -> "1";
                                    default -> null;
                                };
                            }
                            return null;
                        }
                        if (name.equals("register")) return null;
                        if (name.equals("getClass")) return expansionClass;
                        return null;
                    });
            Class<?> placeholderAPI = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method regMethod = placeholderAPI.getMethod("registerExpansion", expansionClass);
            regMethod.invoke(null, expansion);
            enabled = true;
        } catch (Exception e) {
            enabled = false;
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
