package me.darkcube.wa.schematic.structure;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Location;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;

public class NBTSchematic {

    private final WastelandArtifacts plugin;

    public NBTSchematic(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public boolean paste(@NotNull File file, @NotNull Location location) {
        try {
            StructureManager manager = org.bukkit.Bukkit.getStructureManager();
            Structure structure = manager.loadStructure(file);

            if (structure == null) {
                plugin.getComponentLogger().warn("<red>Не удалось загрузить структуру: " + file.getName());
                return false;
            }

            int size = structure.getSize().getBlockX();
            float integrity = 1.0f;
            Random random = new Random();

            structure.place(
                    location,
                    true,
                    StructureRotation.NONE,
                    Mirror.NONE,
                    0,
                    integrity,
                    random,
                    Collections.emptyList(),
                    Collections.emptyList()
            );

            plugin.getComponentLogger().info("<green>Структура " + file.getName() + " размещена в " +
                    location.getWorld().getName() + " [" +
                    location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "]");
            return true;
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки .nbt схемы: " + e.getMessage());
            return false;
        }
    }
}
