package me.darkcube.wa.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.config.MainConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private final WastelandArtifacts plugin;
    private HikariDataSource dataSource;
    private DatabaseType type;

    public DatabaseManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public boolean init(MainConfig.DatabaseConfig config) {
        type = "MYSQL".equalsIgnoreCase(config.type) ? DatabaseType.MYSQL : DatabaseType.SQLITE;
        try {
            HikariConfig hikari = new HikariConfig();
            if (type == DatabaseType.SQLITE) {
                File dbFile = new File(plugin.getDataFolder(), "data.db");
                hikari.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
                hikari.setDriverClassName("org.sqlite.JDBC");
                hikari.setMaximumPoolSize(1);
            } else {
                hikari.setJdbcUrl("jdbc:mysql://" + config.host + ":" + config.port + "/" + config.database
                        + "?useSSL=false&characterEncoding=utf8");
                hikari.setUsername(config.user);
                hikari.setPassword(config.password);
                hikari.setDriverClassName("com.mysql.cj.jdbc.Driver");
                hikari.setMaximumPoolSize(config.poolSize > 0 ? config.poolSize : 10);
            }
            dataSource = new HikariDataSource(hikari);
            createTables();
            plugin.getComponentLogger().info("<green>База данных подключена: " + type);
            return true;
        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка подключения к БД: " + e.getMessage());
            return false;
        }
    }

    private void createTables() {
        execute("CREATE TABLE IF NOT EXISTS wa_players (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "name VARCHAR(16), " +
                "last_seen BIGINT, " +
                "total_found INT DEFAULT 0)");

        execute("CREATE TABLE IF NOT EXISTS wa_artifact_data (" +
                "id VARCHAR(64), " +
                "owner_uuid VARCHAR(36), " +
                "level INT DEFAULT 1, " +
                "xp BIGINT DEFAULT 0, " +
                "kills INT DEFAULT 0, " +
                "slot INT DEFAULT -1, " +
                "PRIMARY KEY (id, owner_uuid))");

        execute("CREATE TABLE IF NOT EXISTS wa_collection (" +
                "player_uuid VARCHAR(36), " +
                "artifact_id VARCHAR(64), " +
                "found_date BIGINT, " +
                "PRIMARY KEY (player_uuid, artifact_id))");

        execute("CREATE TABLE IF NOT EXISTS wa_arena_stats (" +
                "player_uuid VARCHAR(36) PRIMARY KEY, " +
                "waves_cleared INT DEFAULT 0, " +
                "bosses_killed INT DEFAULT 0, " +
                "best_time BIGINT DEFAULT 0)");
    }

    public void execute(String sql) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            plugin.getComponentLogger().warn("<red>SQL ошибка: " + e.getMessage());
        }
    }

    public void execute(String sql, Object... args) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            stmt.execute();
        } catch (SQLException e) {
            plugin.getComponentLogger().warn("<red>SQL ошибка: " + e.getMessage());
        }
    }

    public <T> T query(String sql, ResultSetMapper<T> mapper, Object... args) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            var rs = stmt.executeQuery();
            return mapper.map(rs);
        } catch (SQLException e) {
            plugin.getComponentLogger().warn("<red>SQL ошибка: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public DatabaseType getType() {
        return type;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(java.sql.ResultSet rs) throws SQLException;
    }
}
