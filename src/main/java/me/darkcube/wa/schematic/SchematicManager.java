package me.darkcube.wa.schematic;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.schematic.structure.NBTSchematic;
import me.darkcube.wa.schematic.worldedit.WESchematic;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class SchematicManager {

    private final WastelandArtifacts plugin;
    private final WESchematic weSchematic;
    private final NBTSchematic nbtSchematic;
    private final Map<String, File> schematicCache = new HashMap<>();

    public SchematicManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.weSchematic = new WESchematic(plugin);
        this.nbtSchematic = new NBTSchematic(plugin);
    }

    public void loadCache() {
        schematicCache.clear();
        File schemDir = new File(plugin.getDataFolder(), "schematics");
        if (!schemDir.exists()) {
            schemDir.mkdirs();
            return;
        }

        File[] files = schemDir.listFiles();
        if (files == null) return;

        for (File file : files) {
            String name = file.getName();
            schematicCache.put(name.toLowerCase(), file);
        }
        plugin.getComponentLogger().info("<green>Загружено " + schematicCache.size() + " схем");
    }

    public boolean paste(@NotNull String schematicName, @NotNull Location location) {
        File file = schematicCache.get(schematicName.toLowerCase());
        if (file == null) {
            // Пробуем доп. расширения
            file = findSchematic(schematicName);
            if (file == null) {
                plugin.getComponentLogger().warn("<red>Схема не найдена: " + schematicName);
                return false;
            }
        }

        String name = file.getName().toLowerCase();
        if (name.endsWith(".schem") || name.endsWith(".schematic")) {
            return weSchematic.paste(file, location);
        } else if (name.endsWith(".nbt")) {
            return nbtSchematic.paste(file, location);
        }

        plugin.getComponentLogger().warn("<red>Неподдерживаемый формат: " + file.getName());
        return false;
    }

    public boolean saveSchematic(@NotNull String name, @NotNull Location pos1, @NotNull Location pos2) {
        // Копирование выделенной области в схему
        plugin.getComponentLogger().info("<yellow>Сохранение схемы " + name + "...");
        return false; // TODO
    }

    public @NotNull List<String> listSchematics() {
        return new ArrayList<>(schematicCache.keySet());
    }

    private @Nullable File findSchematic(@NotNull String name) {
        String lower = name.toLowerCase();
        File direct = new File(plugin.getDataFolder(), "schematics/" + lower);
        if (direct.exists()) return direct;

        // Ищем с расширениями
        for (String ext : List.of(".schem", ".schematic", ".nbt")) {
            File f = new File(plugin.getDataFolder(), "schematics/" + lower + ext);
            if (f.exists()) return f;
        }
        return null;
    }
}
