package me.darkcube.wa.database;

import me.darkcube.wa.WastelandArtifacts;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Простая система миграций БД.
 * <p>
 * Каждая миграция — набор SQL-выражений с уникальным ID.
 * Применённые миграции записываются в таблицу {@code wa_migrations}.
 * Миграции выполняются по порядку ID при каждом запуске.
 */
public class MigrationRunner {

    private final WastelandArtifacts plugin;
    private final DatabaseManager db;
    private final List<Migration> migrations = new ArrayList<>();

    public MigrationRunner(WastelandArtifacts plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    /** Зарегистрировать миграцию. */
    public void add(@NotNull Migration migration) {
        migrations.add(migration);
    }

    /** Зарегистрировать несколько миграций. */
    public void addAll(@NotNull Migration... migrations) {
        for (var m : migrations) add(m);
    }

    /**
     * Создать таблицу-трекер и выполнить все неприменённые миграции.
     * Миграции сортируются по ID (лексикографически).
     */
    public void runMigrations() {
        // 1. Создаём таблицу-трекер
        db.execute("CREATE TABLE IF NOT EXISTS wa_migrations (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "applied_at BIGINT NOT NULL)");

        // 2. Получаем список уже применённых
        Set<String> applied = getAppliedMigrations();

        // 3. Сортируем и выполняем неприменённые
        migrations.sort(Comparator.comparing(m -> m.id()));

        for (Migration migration : migrations) {
            if (applied.contains(migration.id())) continue;

            plugin.getComponentLogger().info("<yellow>⚡ Миграция " + migration.id() + ": " + migration.description());
            for (String sql : migration.statements()) {
                db.execute(sql);
            }
            recordMigration(migration.id());
            plugin.getComponentLogger().info("<green>✅ Миграция " + migration.id() + " применена");
        }
    }

    private Set<String> getAppliedMigrations() {
        Set<String> applied = new TreeSet<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM wa_migrations")) {
            while (rs.next()) {
                applied.add(rs.getString("id"));
            }
        } catch (SQLException e) {
            plugin.getComponentLogger().warn("<red>Ошибка чтения wa_migrations: " + e.getMessage());
        }
        return applied;
    }

    private void recordMigration(String id) {
        db.execute("INSERT INTO wa_migrations (id, applied_at) VALUES (?, ?)",
                id, System.currentTimeMillis());
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Предустановленные миграции
    // ═══════════════════════════════════════════════════════════════

    /** Миграция 001 — основные таблицы (wa_players, wa_artifact_data, wa_collection). */
    public static Migration initialTables() {
        return new Migration("001_initial", "Создание основных таблиц",
                "CREATE TABLE IF NOT EXISTS wa_players (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "name VARCHAR(16), " +
                        "last_seen BIGINT, " +
                        "total_found INT DEFAULT 0, " +
                        "total_crafted INT DEFAULT 0, " +
                        "total_upgraded INT DEFAULT 0, " +
                        "elite_kills INT DEFAULT 0, " +
                        "dungeons_cleared INT DEFAULT 0, " +
                        "fishing_caught INT DEFAULT 0, " +
                        "boss_kills INT DEFAULT 0)",
                "CREATE TABLE IF NOT EXISTS wa_artifact_data (" +
                        "id VARCHAR(64), " +
                        "owner_uuid VARCHAR(36), " +
                        "level INT DEFAULT 1, " +
                        "xp BIGINT DEFAULT 0, " +
                        "kills INT DEFAULT 0, " +
                        "slot INT DEFAULT -1, " +
                        "PRIMARY KEY (id, owner_uuid))",
                "CREATE TABLE IF NOT EXISTS wa_collection (" +
                        "player_uuid VARCHAR(36), " +
                        "artifact_id VARCHAR(64), " +
                        "found_date BIGINT, " +
                        "PRIMARY KEY (player_uuid, artifact_id))"
        );
    }

    /** Миграция 002 — таблица достижений. */
    public static Migration achievementsTable() {
        return new Migration("002_achievements", "Таблица достижений",
                "CREATE TABLE IF NOT EXISTS wa_achievements (" +
                        "player_uuid VARCHAR(36), " +
                        "achievement_id VARCHAR(64), " +
                        "progress INT DEFAULT 0, " +
                        "completed BOOLEAN DEFAULT FALSE, " +
                        "completed_date BIGINT, " +
                        "PRIMARY KEY (player_uuid, achievement_id))"
        );
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Record
    // ═══════════════════════════════════════════════════════════════

    public record Migration(@NotNull String id, @NotNull String description, @NotNull String... statements) {}
}
