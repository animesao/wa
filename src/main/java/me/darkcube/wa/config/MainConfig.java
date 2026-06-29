package me.darkcube.wa.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MainConfig {

    public ResourcePackConfig resourcePack = new ResourcePackConfig();
    public DungeonsConfig dungeons = new DungeonsConfig();
    public CraftingConfig crafting = new CraftingConfig();
    public ArtifactsConfig artifacts = new ArtifactsConfig();
    public DatabaseConfig database = new DatabaseConfig();
    public GUIConfig gui = new GUIConfig();
    public LangConfig lang = new LangConfig();

    public static class ResourcePackConfig {
        public String mode = "AUTO";
        public boolean autoHost = true;
        public int hostPort = 8192;
        public boolean force = true;
        public String prompt = "<gold>Этот сервер использует кастомный ресурс-пак!";
        public String hash = "";
    }

    public static class DungeonsConfig {
        public boolean enabled = true;
        public boolean scanOnStartup = true;
        public boolean lootInjection = true;
        public boolean specialChests = true;
        public boolean bossSpawners = true;
    }

    public static class CraftingConfig {
        public boolean enabled = true;
        public boolean altarEnabled = true;
        public boolean keepIngredients = false;
    }

    public static class ArtifactsConfig {
        public int defaultAmount = 1;
        public boolean dropOnDeath = false;
        public boolean soulbind = false;
        public boolean allowInAnvil = false;
    }

    public static class DatabaseConfig {
        public boolean enabled = false;
        public String type = "SQLITE";
        public String host = "localhost";
        public int port = 3306;
        public String database = "artifacts";
        public String user = "root";
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        public String password = "";
    }

    public static class GUIConfig {
        public int rows = 6;
        public String title = "<dark_gray>Артефакты Пустоши";
        public boolean fillGlass = true;
    }

    public static class LangConfig {
        public String locale = "ru_RU";
    }
}
