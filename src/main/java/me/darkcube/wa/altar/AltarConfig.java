package me.darkcube.wa.altar;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.darkcube.wa.WastelandArtifacts.getInstance;

public class AltarConfig {

    public Map<String, AltarTier> altars = new LinkedHashMap<>();
    public AltarSettings settings = new AltarSettings();

    public static class AltarTier {
        public boolean enabled = true;
        public String displayName = "<gold>Алтарь";
        public int tier = 1;
        public String description = "";
        public Material activatorBlock = Material.CHISELED_STONE_BRICKS;
        public List<AltarStructure> structures = new ArrayList<>();
        public List<RecipeEntry> recipes = new ArrayList<>();
        public AltarEffects effects = new AltarEffects();
        public int globalCooldown = 10;
    }

    public static class AltarStructure {
        public List<String> layers = new ArrayList<>();
        public Map<String, String> mapping = new LinkedHashMap<>();
    }

    public static class AltarEffects {
        public EffectConfig activate = new EffectConfig(Particle.ENCHANTED_HIT, 30, Sound.BLOCK_END_PORTAL_FRAME_FILL);
        public EffectConfig craft = new EffectConfig(Particle.PORTAL, 50, Sound.ENTITY_ILLUSIONER_CAST_SPELL);
        public EffectConfig fail = new EffectConfig(Particle.POOF, 10, Sound.ENTITY_GENERIC_EXTINGUISH_FIRE);
    }

    public static class EffectConfig {
        public Particle particle;
        public int count;
        public Sound sound;
        public EffectConfig() {}
        public EffectConfig(Particle particle, int count, Sound sound) {
            this.particle = particle; this.count = count; this.sound = sound;
        }
    }

    public static class RecipeEntry {
        public String id;
        public String result;
        public int experience;
        public int cooldown;
        public CatalystEntry catalyst;
        public List<IngredientEntry> ingredients = new ArrayList<>();
        // Настройки чертежа для этого рецепта
        public String blueprintMaterial = "PAPER";
        public String blueprintName;
        public List<String> blueprintLore;
        public int blueprintCustomModelData = 5001;
        // Настройки крафта чертежа в верстаке
        public WorkbenchRecipe workbench;
    }

    public static class WorkbenchRecipe {
        public List<String> pattern = new ArrayList<>();
        public Map<String, String> ingredients = new LinkedHashMap<>();
    }

    public static class CatalystEntry {
        public Material item;
        public boolean consume = true;
        public String name;          // MiniMessage display name (optional)
        public List<String> lore;    // MiniMessage lore lines (optional)
        public Integer customModelData;

        public transient ItemStack template;

        public ItemStack buildTemplate() {
            if (template != null) return template;
            if (item == null) return null;
            ItemStack tpl = new ItemStack(item);
            ItemMeta meta = tpl.getItemMeta();
            if (name != null && !name.isEmpty()) {
                meta.displayName(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream().map(l -> net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(l)).toList());
            }
            if (customModelData != null && customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            tpl.setItemMeta(meta);
            this.template = tpl;
            return tpl;
        }
    }

    public static class IngredientEntry {
        public Material type;
        public int amount = 1;
        public int slot;
        public String name;          // MiniMessage display name (optional)
        public List<String> lore;    // MiniMessage lore lines (optional)
        public Integer customModelData;

        public transient ItemStack itemTemplate;

        public ItemStack buildTemplate() {
            if (itemTemplate != null) return itemTemplate;
            if (type == null) return null;

            ItemStack tpl = new ItemStack(type);
            ItemMeta meta = tpl.getItemMeta();
            if (name != null && !name.isEmpty()) {
                meta.displayName(MiniMessage.miniMessage().deserialize(name));
            }
            if (lore != null && !lore.isEmpty()) {
                meta.lore(lore.stream()
                        .map(l -> MiniMessage.miniMessage().deserialize(l))
                        .toList());
            }
            if (customModelData != null && customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }
            tpl.setItemMeta(meta);
            this.itemTemplate = tpl;
            return tpl;
        }
    }

    public static class AltarSettings {
        public int maxAltarsPerChunk = 1;
        public boolean allowInAllWorlds = true;
        public List<String> worlds = new ArrayList<>();
        public boolean particlesEnabled = true;
        public boolean soundsEnabled = true;
        public String titleGUI = "<dark_gray>Алтарь Артефактов";
        public int guiRows = 6;
        public double previewMaxDistance = 10.0;
        public int maxSlots = 16;
    }
}
