package me.darkcube.wa.schematic.worldedit;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class WESchematic {

    private final WastelandArtifacts plugin;
    private boolean worldEditAvailable;

    public WESchematic(WastelandArtifacts plugin) {
        this.plugin = plugin;
        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
        Plugin fawe = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
        this.worldEditAvailable = (we != null && we.isEnabled()) || (fawe != null && fawe.isEnabled());
        if (worldEditAvailable) {
            plugin.getComponentLogger().info("<green>WorldEdit найден, схемы доступны");
        } else {
            plugin.getComponentLogger().warn("<yellow>WorldEdit не найден, схемы .schem недоступны");
        }
    }

    public boolean paste(@NotNull File file, @NotNull Location location) {
        if (!worldEditAvailable) {
            plugin.getComponentLogger().warn("<red>WorldEdit не установлен");
            return false;
        }
        // TODO: реальная паста через WE API
        plugin.getComponentLogger().info("<yellow>Вставка схемы " + file.getName() + " в " + location);
        return true;
    }

    public boolean isAvailable() {
        return worldEditAvailable;
    }
}
