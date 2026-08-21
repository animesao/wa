package me.darkcube.wa.resourcepack;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.config.MainConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import fi.iki.elonen.NanoHTTPD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ResourcePackManager {

    private final WastelandArtifacts plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final ModelGenerator modelGenerator;
    private PackHttpServer httpServer;
    private File packFile;
    private byte[] packHash;

    public ResourcePackManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.modelGenerator = new ModelGenerator(plugin);
    }

    public void start() {
        MainConfig.ResourcePackConfig config = plugin.getConfigManager().getMainConfig().resourcePack;
        if (!config.autoHost) return;

        buildPack();
        startHttpServer(config.hostPort);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop();
            httpServer = null;
        }
    }

    public void buildPack() {
        try {
            // Создаём временную директорию для RP
            File rpDir = new File(plugin.getDataFolder(), "resource-pack-gen");
            if (rpDir.exists()) {
                deleteDirectory(rpDir);
            }
            rpDir.mkdirs();

            // Генерируем модели
            modelGenerator.generateAll(rpDir);

            // Копируем ручные модели из resource-pack/ если есть
            File manualRp = new File(plugin.getDataFolder().getParentFile().getParentFile(), "resource-pack");
            if (manualRp.exists()) {
                copyDirectory(manualRp, rpDir);
            }

            // Создаём ZIP
            packFile = new File(plugin.getDataFolder(), "resource-pack.zip");
            zipDirectory(rpDir, packFile);

            packHash = generateHash(packFile);
            plugin.getComponentLogger().info("<green>Ресурс-пак собран: " + packFile.getName() +
                    " (" + packFile.length() / 1024 + " KB)");

            // Удаляем временную директорию
            deleteDirectory(rpDir);

        } catch (Exception e) {
            plugin.getComponentLogger().warn("<red>Ошибка сборки ресурс-пака: " + e.getMessage());
        }
    }

    public void sendToPlayer(@NotNull Player player) {
        if (packHash == null) {
            buildPack();
        }
        MainConfig.ResourcePackConfig config = plugin.getConfigManager().getMainConfig().resourcePack;
        String url = "http://" + getServerIp() + ":" + config.hostPort + "/pack";
        player.setResourcePack(url, packHash,
                miniMessage.deserialize(config.prompt),
                config.force
        );
    }

    public void sendToAll() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendToPlayer(player);
            count++;
        }
        plugin.getComponentLogger().info("<green>Ресурс-пак отправлен " + count + " игрокам");
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  HTTP-сервер на NanoHTTPD
    // ═══════════════════════════════════════════════════════════════

    private void startHttpServer(int port) {
        httpServer = new PackHttpServer(port);
        try {
            httpServer.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            plugin.getComponentLogger().info("<green>HTTP-сервер RP запущен на порту " + port);
        } catch (IOException e) {
            plugin.getComponentLogger().warn("<red>Не удалось запустить HTTP-сервер на порту " + port + ": " + e.getMessage());
        }
    }

    private class PackHttpServer extends NanoHTTPD {

        PackHttpServer(int port) {
            super(port);
        }

        @Override
        public Response serve(IHTTPSession session) {
            String uri = session.getUri();

            if (!"GET".equals(session.getMethod().name()) || !"/pack".equals(uri)) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Not Found");
            }

            if (packFile == null || !packFile.exists()) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHTTPD.MIME_PLAINTEXT, "Pack not built");
            }

            // Стриминг ZIP-файла — не загружаем весь файл в память
            try {
                FileInputStream fis = new FileInputStream(packFile);
                Response response = newChunkedResponse(Response.Status.OK, "application/zip", fis);
                return response;
            } catch (FileNotFoundException e) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "File not found");
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Утилиты
    // ═══════════════════════════════════════════════════════════════

    private byte[] generateHash(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (InputStream is = Files.newInputStream(file.toPath())) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) > 0) {
                    digest.update(buffer, 0, read);
                }
            }
            return digest.digest();
        } catch (NoSuchAlgorithmException | IOException e) {
            return new byte[20];
        }
    }

    private String getServerIp() {
        String ip = Bukkit.getIp();
        if (ip == null || ip.isEmpty() || "0.0.0.0".equals(ip)) {
            ip = "localhost";
        }
        return ip;
    }

    private void zipDirectory(File sourceDir, File zipFile) throws IOException {
        try (java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] files = sourceDir.listFiles();
            if (files == null) return;
            for (File file : files) {
                if (file.isDirectory()) {
                    addDirToZip(zos, file, file.getName());
                } else {
                    addFileToZip(zos, file, file.getName());
                }
            }
        }
    }

    private void addDirToZip(java.util.zip.ZipOutputStream zos, File dir, String baseName) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                addDirToZip(zos, file, baseName + "/" + file.getName());
            } else {
                addFileToZip(zos, file, baseName + "/" + file.getName());
            }
        }
    }

    private void addFileToZip(java.util.zip.ZipOutputStream zos, File file, String entryName) throws IOException {
        java.util.zip.ZipEntry entry = new java.util.zip.ZipEntry(entryName.replace("\\", "/"));
        zos.putNextEntry(entry);
        Files.copy(file.toPath(), zos);
        zos.closeEntry();
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    private void copyDirectory(File source, File target) throws IOException {
        File[] files = source.listFiles();
        if (files == null) return;
        for (File file : files) {
            String name = file.getName();
            File dest = new File(target, name);
            if (file.isDirectory()) {
                dest.mkdirs();
                copyDirectory(file, dest);
            } else if (!dest.exists()) {
                Files.copy(file.toPath(), dest.toPath());
            }
        }
    }
}
