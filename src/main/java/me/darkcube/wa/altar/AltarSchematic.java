package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AltarSchematic {

    private final WastelandArtifacts plugin;
    private final Map<String, NamespacedKey> structureCache = new HashMap<>();

    public AltarSchematic(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public boolean saveAltar(@NotNull World world, @NotNull String name,
                              @NotNull Location pos1, @NotNull Location pos2) {
        plugin.getComponentLogger().warn("<yellow>Сохранение схем алтарей временно недоступно (используйте /altar build)");
        return false;
    }

    public boolean pasteAltar(@NotNull Location loc, @NotNull String name) {
        try {
            File schemDir = new File(plugin.getDataFolder(), "schematics");
            File nbtFile = new File(schemDir, "altar_" + name.toLowerCase() + ".nbt");
            if (!nbtFile.exists()) {
                plugin.getComponentLogger().warn("<red>Схема не найдена: " + name);
                return false;
            }

            StructureManager manager = Bukkit.getStructureManager();
            Structure structure = manager.loadStructure(nbtFile);

            if (structure == null) {
                plugin.getComponentLogger().warn("<red>Не удалось загрузить схему: " + name);
                return false;
            }

            structure.place(loc, true, StructureRotation.NONE, Mirror.NONE,
                    0, 1.0f, new Random());

            plugin.getComponentLogger().info("<green>Алтарь размещён: " + name);
            return true;
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Ошибка загрузки схемы: " + e.getMessage());
            return false;
        }
    }

    public @NotNull List<String> listAltars() {
        File schemDir = new File(plugin.getDataFolder(), "schematics");
        if (!schemDir.exists()) return Collections.emptyList();

        File[] files = schemDir.listFiles((d, name) -> name.startsWith("altar_") && name.endsWith(".nbt"));
        if (files == null) return Collections.emptyList();

        return Arrays.stream(files)
                .map(f -> f.getName().replace("altar_", "").replace(".nbt", ""))
                .collect(Collectors.toList());
    }

    public void loadCache() {
        structureCache.clear();
        for (String name : listAltars()) {
            structureCache.put(name, new NamespacedKey(plugin, "altar_" + name));
        }
    }
}
