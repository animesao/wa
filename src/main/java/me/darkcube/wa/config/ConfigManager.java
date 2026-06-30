package me.darkcube.wa.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.ArtifactSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.IllegalFormatException;

public class ConfigManager {

    private final WastelandArtifacts plugin;
    private final ObjectMapper yamlMapper;
    private MainConfig mainConfig;
    private Map<String, String> lang;
    private final Map<String, Map<String, String>> allLocales = new HashMap<>();
    private final ArtifactSerializer artifactSerializer;

    public ConfigManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.artifactSerializer = new ArtifactSerializer(plugin);
        this.yamlMapper = new ObjectMapper(
                new YAMLFactory()
                        .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                        .enable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)
        )
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .findAndRegisterModules();
    }

    public void loadAll() {
        loadMainConfig();
        loadLang();
        loadArtifacts();
    }

    public void reloadAll() {
        loadAll();
    }

    private void loadMainConfig() {
        try {
            plugin.reloadConfig();
            mainConfig = yamlMapper.readValue(
                    new File(plugin.getDataFolder(), "config.yml"),
                    MainConfig.class
            );
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Не удалось загрузить config.yml: " + e.getMessage());
            mainConfig = new MainConfig();
        }
    }

    private void loadLang() {
        lang = new HashMap<>();
        allLocales.clear();
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) return;

        File[] files = langDir.listFiles((d, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File langFile : files) {
            try {
                String localeName = langFile.getName().replace(".yml", "");
                @SuppressWarnings("unchecked")
                Map<String, Object> raw = yamlMapper.readValue(langFile, Map.class);
                Map<String, String> flat = new HashMap<>();
                flatMap(raw, "", flat);
                allLocales.put(localeName, flat);
                // default locale
                if (localeName.equals(mainConfig.lang.locale)) {
                    lang = flat;
                }
            } catch (IOException e) {
                plugin.getComponentLogger().warn("<red>Не удалось загрузить " + langFile.getName() + ": " + e.getMessage());
            }
        }

        // Fallback to ru_RU
        if (lang.isEmpty()) {
            lang = allLocales.getOrDefault("ru_RU", Collections.emptyMap());
        }
    }

    @SuppressWarnings("unchecked")
    private void flatMap(Map<String, Object> map, String prefix, Map<String, String> target) {
        for (var entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue() instanceof Map) {
                flatMap((Map<String, Object>) entry.getValue(), key, target);
            } else {
                target.put(key, entry.getValue().toString());
            }
        }
    }

    private void loadArtifacts() {
        File artifactsDir = new File(plugin.getDataFolder(), "artifacts");
        if (!artifactsDir.exists()) {
            artifactsDir.mkdirs();
            return;
        }
        File[] files = artifactsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        plugin.getArtifactRegistry().clear();
        for (File file : files) {
            try {
                List<Artifact> artifacts = artifactSerializer.deserializeAll(file);
                artifacts.forEach(a -> plugin.getArtifactRegistry().register(a));
                plugin.getComponentLogger().info("<green>Загружено " + artifacts.size() + " артефактов из " + file.getName());
            } catch (IOException e) {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки " + file.getName() + ": " + e.getMessage());
            }
        }
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public @NotNull String getLang(String key, Object... args) {
        return getLangFor(null, key, args);
    }

    public @NotNull String getLangFor(@Nullable Player player, String key, Object... args) {
        Map<String, String> locale = lang;
        if (player != null) {
            String playerLocale = player.getLocale();
            locale = allLocales.getOrDefault(playerLocale, lang);
        }
        String template = locale.getOrDefault(key, lang.getOrDefault(key, key));
        if (template == null || template.isBlank()) return "";
        try {
            String result = String.format(template, args);
            return result.isBlank() ? "" : result;
        } catch (IllegalFormatException e) {
            return template;
        }
    }

    public ObjectMapper getYamlMapper() {
        return yamlMapper;
    }
}
