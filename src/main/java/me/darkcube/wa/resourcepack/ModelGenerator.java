package me.darkcube.wa.resourcepack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ModelGenerator {

    private final WastelandArtifacts plugin;
    private final ObjectMapper mapper = new ObjectMapper();

    private static final Set<Material> HANDHELD_ITEMS = Set.of(
            Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            Material.TRIDENT, Material.MACE, Material.BOW, Material.CROSSBOW,
            Material.SHIELD, Material.FISHING_ROD, Material.CARROT_ON_A_STICK,
            Material.WARPED_FUNGUS_ON_A_STICK
    );

    public ModelGenerator(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void generateAll(@NotNull File rpDir) throws IOException {
        File modelsDir = new File(rpDir, "assets/minecraft/models/item");
        File texturesDir = new File(rpDir, "assets/minecraft/textures/item");
        modelsDir.mkdirs();
        texturesDir.mkdirs();

        // Для каждого материала создаём override-файл
        Map<Material, List<Artifact>> byMaterial = new HashMap<>();
        for (Artifact artifact : plugin.getArtifactRegistry().getAll()) {
            if (artifact.getCustomModelData() > 0) {
                byMaterial.computeIfAbsent(artifact.getBaseItem(), k -> new ArrayList<>()).add(artifact);
            }
        }

        for (var entry : byMaterial.entrySet()) {
            Material material = entry.getKey();
            List<Artifact> artifacts = entry.getValue();
            String materialName = material.name().toLowerCase();

            // Создаём модель-оверрайд для материала
            File modelFile = new File(modelsDir, materialName + ".json");
            if (!modelFile.exists()) {
                // Копируем оригинальную ванильную модель как базу
                createBaseModel(modelFile, materialName, isHandheld(material));
            }

            // Добавляем overrides для каждого артефакта
            addOverrides(modelFile, artifacts);

            // Создаём индивидуальные модели артефактов
            for (Artifact artifact : artifacts) {
                String artifactModelName = "artifact_" + artifact.getId();
                createArtifactModel(new File(modelsDir, artifactModelName + ".json"),
                        artifactModelName, isHandheld(artifact.getBaseItem()));
            }
        }

        // Создаём pack.mcmeta
        File packMcmeta = new File(rpDir, "pack.mcmeta");
        if (!packMcmeta.exists()) {
            ObjectNode meta = mapper.createObjectNode();
            ObjectNode pack = meta.putObject("pack");
            pack.put("pack_format", 41);
            pack.put("description", "§6Wasteland Artifacts §7- §eКастомные артефакты");
            mapper.writerWithDefaultPrettyPrinter().writeValue(packMcmeta, meta);
        }

        // Создаём заглушки текстур (если нет)
        for (Artifact artifact : plugin.getArtifactRegistry().getAll()) {
            File textureFile = new File(texturesDir, "artifact_" + artifact.getId() + ".png");
            if (!textureFile.exists()) {
                createPlaceholderTexture(textureFile, artifact);
            }
        }
    }

    private void createBaseModel(@NotNull File file, @NotNull String materialName, boolean handheld) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.put("parent", handheld ? "minecraft:item/handheld" : "minecraft:item/generated");
        ObjectNode textures = root.putObject("textures");
        textures.put("layer0", "minecraft:item/" + materialName);
        root.putArray("overrides");
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    private void addOverrides(@NotNull File baseModelFile, @NotNull List<Artifact> artifacts) throws IOException {
        ObjectNode root = (ObjectNode) mapper.readTree(baseModelFile);
        ArrayNode overrides = root.has("overrides") ? (ArrayNode) root.get("overrides") : root.putArray("overrides");

        for (Artifact artifact : artifacts) {
            String artifactModelName = "artifact_" + artifact.getId();
            ObjectNode override = overrides.addObject();
            ObjectNode predicate = override.putObject("predicate");
            predicate.put("custom_model_data", artifact.getCustomModelData());
            override.put("model", "minecraft:item/" + artifactModelName);
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(baseModelFile, root);
    }

    private void createArtifactModel(@NotNull File file, @NotNull String modelName, boolean handheld) throws IOException {
        ObjectNode root = mapper.createObjectNode();
        root.put("parent", handheld ? "minecraft:item/handheld" : "minecraft:item/generated");
        ObjectNode textures = root.putObject("textures");
        textures.put("layer0", "minecraft:item/" + modelName);
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }

    private void createPlaceholderTexture(@NotNull File file, @NotNull Artifact artifact) throws IOException {
        // Создаём 16x16 PNG заглушку с цветом редкости
        int size = 16;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = img.createGraphics();

        // Цвет фона на основе редкости
        java.awt.Color baseColor = switch (artifact.getRarity()) {
            case COMMON -> new java.awt.Color(128, 128, 128);
            case UNCOMMON -> new java.awt.Color(85, 255, 85);
            case RARE -> new java.awt.Color(85, 255, 255);
            case EPIC -> new java.awt.Color(255, 85, 255);
            case LEGENDARY -> new java.awt.Color(255, 170, 0);
            case MYTHIC -> new java.awt.Color(170, 0, 0);
            case UNKNOWN -> new java.awt.Color(0, 170, 170);
            case VOID -> new java.awt.Color(0, 0, 0);
        };

        // Рисуем простую иконку
        g.setColor(baseColor);
        g.fillRect(0, 0, size, size);
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(0, 0, size - 1, size - 1);

        // Буква артефакта
        g.setColor(java.awt.Color.WHITE);
        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 8));
        String letter = artifact.getId().substring(0, 1).toUpperCase();
        g.drawString(letter, 4, 12);

        g.dispose();
        javax.imageio.ImageIO.write(img, "png", file);
    }

    private boolean isHandheld(Material material) {
        return HANDHELD_ITEMS.contains(material);
    }
}
