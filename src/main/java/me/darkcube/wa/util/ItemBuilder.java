package me.darkcube.wa.util;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.trigger.TriggerContext;
import me.darkcube.wa.artifact.trigger.TriggerType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Base64;

public class ItemBuilder {

    private static final NamespacedKey ARTIFACT_KEY = new NamespacedKey("wastelandartifacts", "artifact_id");
    private static final NamespacedKey ARTIFACT_DATA_KEY = new NamespacedKey("wastelandartifacts", "artifact_data");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final WastelandArtifacts plugin;

    public ItemBuilder(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public @NotNull ItemStack build(@NotNull Artifact artifact) {
        ItemStack item = new ItemStack(artifact.getBaseItem(), 1);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        Component displayName = MINI_MESSAGE.deserialize(
                artifact.getRarity().getDisplayName() + " " + artifact.getDisplayName()
        );
        meta.displayName(displayName);

        List<Component> lore = new ArrayList<>();
        lore.add(MINI_MESSAGE.deserialize(""));
        lore.add(MINI_MESSAGE.deserialize(
                "<gray>Редкость: " + artifact.getRarity().getDisplayName()
        ));
        lore.add(MINI_MESSAGE.deserialize(""));

        for (String line : artifact.getLore()) {
            lore.add(MINI_MESSAGE.deserialize(line));
        }

        if (!artifact.getComponents().isEmpty()) {
            lore.add(MINI_MESSAGE.deserialize(""));
            lore.add(MINI_MESSAGE.deserialize("<dark_gray>✦ Свойства:"));
            for (ArtifactComponent comp : artifact.getComponents()) {
                lore.add(MINI_MESSAGE.deserialize("  <gray>• " + formatComponentType(comp.getType())));
            }
        }

        meta.lore(lore);

        if (artifact.getCustomModelData() > 0) {
            meta.setCustomModelData((int) artifact.getCustomModelData());
        }

        meta.setMaxStackSize(artifact.getMaxStackSize());

        if (artifact.isUnbreakable()) {
            meta.setUnbreakable(true);
        }

        if (artifact.getSkinTexture() != null && !artifact.getSkinTexture().isEmpty()
                && item.getType() == Material.PLAYER_HEAD && meta instanceof SkullMeta skullMeta) {
            applySkinTexture(skullMeta, artifact.getSkinTexture());
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(ARTIFACT_KEY, PersistentDataType.STRING, artifact.getId());

        item.setItemMeta(meta);

        for (ArtifactComponent component : artifact.getComponents()) {
            component.apply(item);
        }

        return item;
    }

    public static @NotNull NamespacedKey getPDCKey(String key) {
        return new NamespacedKey("wastelandartifacts", key);
    }

    public void applyTriggers(@NotNull Artifact artifact, @NotNull Player player,
                               @NotNull ItemStack item, @NotNull TriggerType type,
                               @Nullable Event event, @Nullable Entity target) {
        TriggerContext ctx = new TriggerContext(player, artifact, item, type, event, target);
        for (var trigger : artifact.getTriggers()) {
            trigger.execute(ctx);
        }
    }

    private void applySkinTexture(SkullMeta skullMeta, String textureBase64) {
        try {
            // Используем рефлексию для доступа к GameProfile (Mojang internal)
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");
            Class<?> propertyMapClass = Class.forName("com.mojang.authlib.properties.PropertyMap");

            Object profile = gameProfileClass.getConstructor(UUID.class, String.class)
                    .newInstance(UUID.randomUUID(), null);
            Object property = propertyClass.getConstructor(String.class, String.class)
                    .newInstance("textures", textureBase64);

            Method getProperties = gameProfileClass.getMethod("getProperties");
            Object propertyMap = getProperties.invoke(profile);
            Method put = propertyMapClass.getMethod("put", Object.class, Object.class);
            put.invoke(propertyMap, "textures", property);

            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (Exception ignored) {}
    }

    private String formatComponentType(String type) {
        return switch (type) {
            case "DAMAGE" -> "Урон";
            case "FIRE_ASPECT" -> "Заговор огня";
            case "ATTRIBUTE" -> "Атрибут";
            case "POTION_EFFECT_ON_EQUIP" -> "Эффект при надевании";
            case "PARTICLE_ON_HIT" -> "Частицы при ударе";
            case "PARTICLE_AMBIENT" -> "Окружающие частицы";
            case "SOUND_ON_HIT" -> "Звук при ударе";
            case "SOUND_ON_USE" -> "Звук при использовании";
            case "COOLDOWN" -> "Перезарядка";
            case "LIFE_STEAL" -> "Вампиризм";
            case "LIGHTNING" -> "Молния";
            case "EXPLOSION" -> "Взрыв";
            case "SUMMON" -> "Призыв";
            case "PROJECTILE" -> "Снаряд";
            case "AOE" -> "Область действия";
            case "CHARGE" -> "Заряды";
            default -> type;
        };
    }
}
