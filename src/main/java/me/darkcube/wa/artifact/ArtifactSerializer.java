package me.darkcube.wa.artifact;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.component.components.*;
import me.darkcube.wa.artifact.rarity.Rarity;
import me.darkcube.wa.crafting.ArtifactRecipe;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class ArtifactSerializer {

    private final WastelandArtifacts plugin;
    private final Map<String, Function<JsonNode, ArtifactComponent>> componentDeserializers = new HashMap<>();

    public ArtifactSerializer(WastelandArtifacts plugin) {
        this.plugin = plugin;
        registerComponentDeserializers();
    }

    private void registerComponentDeserializers() {
        componentDeserializers.put("DAMAGE", this::deserializeDamage);
        componentDeserializers.put("FIRE_ASPECT", this::deserializeFireAspect);
        componentDeserializers.put("ATTRIBUTE", this::deserializeAttribute);
        componentDeserializers.put("POTION_EFFECT_ON_EQUIP", this::deserializePotionEffectOnEquip);
        componentDeserializers.put("POTION_EFFECT_ON_HIT", this::deserializePotionEffectOnHit);
        componentDeserializers.put("POTION_EFFECT_AURA", this::deserializePotionEffectAura);
        componentDeserializers.put("PARTICLE_ON_HIT", this::deserializeParticleOnHit);
        componentDeserializers.put("PARTICLE_AMBIENT", this::deserializeParticleAmbient);
        componentDeserializers.put("SOUND_ON_HIT", this::deserializeSoundOnHit);
        componentDeserializers.put("SOUND_ON_USE", this::deserializeSoundOnUse);
        componentDeserializers.put("COOLDOWN", this::deserializeCooldown);
        componentDeserializers.put("LIFE_STEAL", this::deserializeLifeSteal);
        componentDeserializers.put("LIGHTNING", this::deserializeLightning);
        componentDeserializers.put("EXPLOSION", this::deserializeExplosion);
        componentDeserializers.put("SUMMON", this::deserializeSummon);
        componentDeserializers.put("PROJECTILE", this::deserializeProjectile);
        componentDeserializers.put("AOE", this::deserializeAoe);
        componentDeserializers.put("CHARGE", this::deserializeCharge);
        componentDeserializers.put("COMMAND", this::deserializeCommand);
        componentDeserializers.put("SOULBIND", this::deserializeSoulbind);
    }

    /** Позволяет другим плагинам регистрировать кастомные десериализаторы компонентов. */
    public void registerComponentDeserializer(String type, Function<JsonNode, ArtifactComponent> deserializer) {
        componentDeserializers.put(type.toUpperCase(), deserializer);
    }

    public @NotNull List<Artifact> deserializeAll(@NotNull File file) throws IOException {
        ObjectMapper mapper = plugin.getConfigManager().getYamlMapper();
        JsonNode root = mapper.readTree(file);
        JsonNode artifactsNode = root.get("artifacts");
        if (artifactsNode == null || !artifactsNode.isArray()) {
            return Collections.emptyList();
        }

        List<Artifact> result = new ArrayList<>();
        for (JsonNode node : artifactsNode) {
            try {
                result.add(deserializeOne(node));
            } catch (Exception e) {
                plugin.getComponentLogger().warn("<red>Ошибка загрузки артефакта в " + file.getName() + ": " + e.getMessage());
            }
        }
        return result;
    }

    private @NotNull Artifact deserializeOne(@NotNull JsonNode node) {
        String id = node.get("id").asText();
        String displayName = node.get("displayName").asText();
        Material baseItem = Material.matchMaterial(node.get("baseItem").asText());
        if (baseItem == null) baseItem = Material.STICK;

        String rarityName = node.get("rarity").asText().toUpperCase();
        Rarity rarity;
        try {
            rarity = Rarity.valueOf(rarityName);
        } catch (IllegalArgumentException e) {
            var customRarity = plugin.getRarityManager().getByName(rarityName);
            rarity = customRarity != null ? Rarity.valueOf(customRarity.id()) : Rarity.COMMON;
        }
        float customModelData = node.has("customModelData") ? (float) node.get("customModelData").asDouble() : 0;
        boolean unbreakable = node.has("unbreakable") && node.get("unbreakable").asBoolean();
        int maxStackSize = node.has("maxStackSize") ? node.get("maxStackSize").asInt() : 1;
        String skinTexture = node.has("skinTexture") ? node.get("skinTexture").asText() : null;

        List<String> lore = new ArrayList<>();
        if (node.has("lore") && node.get("lore").isArray()) {
            node.get("lore").forEach(l -> lore.add(l.asText()));
        }

        List<ArtifactComponent> components = new ArrayList<>();
        if (node.has("components") && node.get("components").isArray()) {
            for (JsonNode compNode : node.get("components")) {
                ArtifactComponent component = deserializeComponent(compNode);
                if (component != null) components.add(component);
            }
        }

        ArtifactRecipe recipe = null;
        if (node.has("recipe")) {
            recipe = deserializeRecipe(node.get("recipe"));
        }

        var builder = Artifact.builder(id)
                .displayName(displayName)
                .lore(lore)
                .baseItem(baseItem)
                .customModelData(customModelData)
                .rarity(rarity)
                .unbreakable(unbreakable)
                .maxStackSize(maxStackSize)
                .skinTexture(skinTexture)
                .components(components.toArray(new ArtifactComponent[0]))
                .recipe(recipe);

        if (node.has("extraData") && node.get("extraData").isObject()) {
            var extra = node.get("extraData");
            extra.fieldNames().forEachRemaining(key -> {
                var val = extra.get(key);
                if (val.isInt()) builder.extraData(key, val.asInt());
                else if (val.isDouble()) builder.extraData(key, val.asDouble());
                else if (val.isBoolean()) builder.extraData(key, val.asBoolean());
                else if (val.isTextual()) builder.extraData(key, val.asText());
            });
        }

        return builder.build();
    }

    private ArtifactComponent deserializeComponent(JsonNode node) {
        String type = node.get("type").asText().toUpperCase();
        Function<JsonNode, ArtifactComponent> deserializer = componentDeserializers.get(type);
        if (deserializer != null) {
            return deserializer.apply(node);
        }
        plugin.getComponentLogger().warn("<yellow>Неизвестный компонент: " + type);
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Индивидуальные десериализаторы компонентов
    // ═══════════════════════════════════════════════════════════════

    private ArtifactComponent deserializeDamage(JsonNode node) {
        DamageComponent c = new DamageComponent();
        c.setDamage(node.has("damage") ? node.get("damage").asDouble() : 1.0);
        if (node.has("slot")) trySetSlot(c, node);
        return c;
    }

    private ArtifactComponent deserializeFireAspect(JsonNode node) {
        FireAspectComponent c = new FireAspectComponent();
        c.setLevel(node.has("level") ? node.get("level").asInt() : 1);
        return c;
    }

    private ArtifactComponent deserializeAttribute(JsonNode node) {
        AttributeComponent c = new AttributeComponent();
        if (node.has("attribute")) c.setAttribute(Attribute.valueOf(node.get("attribute").asText()));
        c.setAmount(node.has("amount") ? node.get("amount").asDouble() : 0.1);
        if (node.has("operation")) c.setOperation(AttributeModifier.Operation.valueOf(node.get("operation").asText()));
        if (node.has("slot")) trySetSlot(c, node);
        return c;
    }

    private ArtifactComponent deserializePotionEffectOnEquip(JsonNode node) {
        PotionEffectOnEquipComponent c = new PotionEffectOnEquipComponent();
        if (node.has("effect")) {
            PotionEffectType effect = PotionEffectType.getByName(node.get("effect").asText());
            if (effect != null) c.setEffect(effect);
        }
        c.setAmplifier(node.has("amplifier") ? node.get("amplifier").asInt() : 0);
        c.setAmbient(node.has("ambient") && node.get("ambient").asBoolean());
        return c;
    }

    private ArtifactComponent deserializePotionEffectOnHit(JsonNode node) {
        PotionEffectOnHitComponent c = new PotionEffectOnHitComponent();
        if (node.has("effect")) {
            PotionEffectType effect = PotionEffectType.getByName(node.get("effect").asText());
            if (effect != null) c.setEffect(effect);
        }
        c.setDuration(node.has("duration") ? node.get("duration").asInt() : 100);
        c.setAmplifier(node.has("amplifier") ? node.get("amplifier").asInt() : 1);
        return c;
    }

    private ArtifactComponent deserializePotionEffectAura(JsonNode node) {
        PotionEffectAuraComponent c = new PotionEffectAuraComponent();
        if (node.has("effect")) {
            PotionEffectType effect = PotionEffectType.getByName(node.get("effect").asText());
            if (effect != null) c.setEffect(effect);
        }
        c.setAmplifier(node.has("amplifier") ? node.get("amplifier").asInt() : 2);
        c.setRadius(node.has("radius") ? node.get("radius").asDouble() : 6.0);
        return c;
    }

    private ArtifactComponent deserializeParticleOnHit(JsonNode node) {
        ParticleOnHitComponent c = new ParticleOnHitComponent();
        if (node.has("particle")) {
            try { c.setParticle(Particle.valueOf(node.get("particle").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        c.setCount(node.has("count") ? node.get("count").asInt() : 10);
        return c;
    }

    private ArtifactComponent deserializeParticleAmbient(JsonNode node) {
        ParticleAmbientComponent c = new ParticleAmbientComponent();
        if (node.has("particle")) {
            try { c.setParticle(Particle.valueOf(node.get("particle").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        c.setCount(node.has("count") ? node.get("count").asInt() : 2);
        return c;
    }

    private ArtifactComponent deserializeSoundOnHit(JsonNode node) {
        SoundOnHitComponent c = new SoundOnHitComponent();
        if (node.has("sound")) {
            try { c.setSound(Sound.valueOf(node.get("sound").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        return c;
    }

    private ArtifactComponent deserializeSoundOnUse(JsonNode node) {
        SoundOnUseComponent c = new SoundOnUseComponent();
        if (node.has("sound")) {
            try { c.setSound(Sound.valueOf(node.get("sound").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        return c;
    }

    private ArtifactComponent deserializeCooldown(JsonNode node) {
        CooldownComponent c = new CooldownComponent();
        c.setSeconds(node.has("seconds") ? node.get("seconds").asInt() : 5);
        return c;
    }

    private ArtifactComponent deserializeLifeSteal(JsonNode node) {
        LifeStealComponent c = new LifeStealComponent();
        double pct = node.has("percentage") ? node.get("percentage").asDouble()
                : node.has("multiplier") ? node.get("multiplier").asDouble() : 0.1;
        c.setPercentage(pct);
        return c;
    }

    private ArtifactComponent deserializeLightning(JsonNode node) {
        LightningComponent c = new LightningComponent();
        c.setChance(node.has("chance") ? node.get("chance").asDouble() : 1.0);
        c.setDamage(!node.has("safe") || !node.get("safe").asBoolean());
        return c;
    }

    private ArtifactComponent deserializeExplosion(JsonNode node) {
        ExplosionComponent c = new ExplosionComponent();
        c.setPower(node.has("power") ? (float) node.get("power").asDouble() : 3.0f);
        c.setSafe(node.has("safe") && node.get("safe").asBoolean());
        return c;
    }

    private ArtifactComponent deserializeSummon(JsonNode node) {
        SummonComponent c = new SummonComponent();
        if (node.has("entityType")) {
            try { c.setEntityType(EntityType.valueOf(node.get("entityType").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        c.setAmount(node.has("amount") ? node.get("amount").asInt() : 1);
        c.setDuration(node.has("duration") ? node.get("duration").asInt() : 200);
        c.setWithEquipment(node.has("withEquipment") && node.get("withEquipment").asBoolean());
        return c;
    }

    private ArtifactComponent deserializeProjectile(JsonNode node) {
        ProjectileComponent c = new ProjectileComponent();
        if (node.has("projectileType")) {
            try { c.setProjectileType(EntityType.valueOf(node.get("projectileType").asText())); }
            catch (IllegalArgumentException ignored) {}
        }
        c.setSpeed(node.has("speed") ? node.get("speed").asDouble() : 1.5);
        return c;
    }

    private ArtifactComponent deserializeAoe(JsonNode node) {
        AoeComponent c = new AoeComponent();
        c.setRadius(node.has("radius") ? node.get("radius").asDouble() : 5.0);
        c.setDamage(node.has("damage") ? node.get("damage").asDouble() : 0);
        if (node.has("effect")) {
            PotionEffectType effect = PotionEffectType.getByName(node.get("effect").asText());
            if (effect != null) c.setEffect(effect);
        }
        c.setEffectAmplifier(node.has("amplifier") ? node.get("amplifier").asInt() : 0);
        c.setEffectDuration(node.has("duration") ? node.get("duration").asInt() : 0);
        return c;
    }

    private ArtifactComponent deserializeCharge(JsonNode node) {
        ChargeComponent c = new ChargeComponent();
        c.setMaxCharges(node.has("maxCharges") ? node.get("maxCharges").asInt() : 5);
        c.setConsumeOnUse(!node.has("consumeOnUse") || node.get("consumeOnUse").asBoolean());
        c.setDestroyWhenEmpty(!node.has("destroyWhenEmpty") || node.get("destroyWhenEmpty").asBoolean());
        return c;
    }

    private ArtifactComponent deserializeCommand(JsonNode node) {
        CommandComponent c = new CommandComponent();
        c.setCommand(node.has("command") ? node.get("command").asText() : "say triggered");
        c.setAsPlayer(!node.has("asPlayer") || node.get("asPlayer").asBoolean());
        return c;
    }

    private ArtifactComponent deserializeSoulbind(JsonNode node) {
        return new SoulbindComponent();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  Хелперы
    // ═══════════════════════════════════════════════════════════════

    private void trySetSlot(DamageComponent c, JsonNode node) {
        try { c.setSlot(EquipmentSlot.valueOf(node.get("slot").asText())); }
        catch (IllegalArgumentException ignored) {}
    }

    private void trySetSlot(AttributeComponent c, JsonNode node) {
        try { c.setSlot(EquipmentSlot.valueOf(node.get("slot").asText())); }
        catch (IllegalArgumentException ignored) {}
    }

    private ArtifactRecipe deserializeRecipe(JsonNode node) {
        String type = node.has("type") ? node.get("type").asText() : "shaped";
        Map<String, ArtifactRecipe.IngredientDef> ingredients = new LinkedHashMap<>();
        if (node.has("ingredients")) {
            var fields = node.get("ingredients").fields();
            while (fields.hasNext()) {
                var e = fields.next();
                String key = e.getKey();
                JsonNode val = e.getValue();
                if (val.isTextual()) {
                    // Простая строка: "DIAMOND" или "artifact:fire_sword"
                    ingredients.put(key, new ArtifactRecipe.IngredientDef(val.asText()));
                } else if (val.isObject()) {
                    // Объект: { type: "DIAMOND", name: "...", lore: [...], customModelData: 123 }
                    String matType = val.has("type") ? val.get("type").asText() : "STONE";
                    String name = val.has("name") ? val.get("name").asText() : null;
                    List<String> lore = null;
                    if (val.has("lore") && val.get("lore").isArray()) {
                        List<String> loreFinal = new ArrayList<>();
                        val.get("lore").forEach(l -> loreFinal.add(l.asText()));
                        lore = loreFinal;
                    }
                    int cmd = val.has("customModelData") ? val.get("customModelData").asInt() : 0;
                    ingredients.put(key, new ArtifactRecipe.IngredientDef(matType, name, lore, cmd));
                }
            }
        }
        List<String> pattern = new ArrayList<>();
        if (node.has("pattern") && node.get("pattern").isArray()) {
            node.get("pattern").forEach(p -> pattern.add(p.asText()));
        }
        int resultAmount = node.has("result") && node.get("result").has("amount")
                ? node.get("result").get("amount").asInt() : 1;

        return new ArtifactRecipe(type, pattern, ingredients, resultAmount);
    }

    public void serialize(@NotNull Artifact artifact, @NotNull File file) throws IOException {
        ObjectMapper mapper = plugin.getConfigManager().getYamlMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode artifactsArray = root.putArray("artifacts");
        ObjectNode node = artifactsArray.addObject();

        node.put("id", artifact.getId());
        node.put("displayName", artifact.getDisplayName());
        node.put("baseItem", artifact.getBaseItem().name());
        node.put("customModelData", artifact.getCustomModelData());
        node.put("rarity", artifact.getRarity().name());
        node.put("unbreakable", artifact.isUnbreakable());
        node.put("maxStackSize", artifact.getMaxStackSize());

        if (!artifact.getLore().isEmpty()) {
            ArrayNode loreArray = node.putArray("lore");
            artifact.getLore().forEach(loreArray::add);
        }

        if (!artifact.getComponents().isEmpty()) {
            ArrayNode compArray = node.putArray("components");
            for (ArtifactComponent comp : artifact.getComponents()) {
                // Для простоты сохраняем только тип
                ObjectNode compNode = compArray.addObject();
                compNode.put("type", comp.getType());
            }
        }

        mapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
    }
}
