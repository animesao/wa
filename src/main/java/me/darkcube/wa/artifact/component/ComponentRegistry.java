package me.darkcube.wa.artifact.component;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.component.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ComponentRegistry {

    private final WastelandArtifacts plugin;
    private final Map<String, Supplier<ArtifactComponent>> componentFactories = new HashMap<>();
    private final Map<String, Class<? extends ArtifactComponent>> componentClasses = new HashMap<>();

    public ComponentRegistry(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerDefaults() {
        register("DAMAGE", DamageComponent::new, DamageComponent.class);
        register("FIRE_ASPECT", FireAspectComponent::new, FireAspectComponent.class);
        register("ATTRIBUTE", AttributeComponent::new, AttributeComponent.class);
        register("POTION_EFFECT_ON_EQUIP", PotionEffectOnEquipComponent::new, PotionEffectOnEquipComponent.class);
        register("PARTICLE_ON_HIT", ParticleOnHitComponent::new, ParticleOnHitComponent.class);
        register("PARTICLE_AMBIENT", ParticleAmbientComponent::new, ParticleAmbientComponent.class);
        register("SOUND_ON_HIT", SoundOnHitComponent::new, SoundOnHitComponent.class);
        register("SOUND_ON_USE", SoundOnUseComponent::new, SoundOnUseComponent.class);
        register("COOLDOWN", CooldownComponent::new, CooldownComponent.class);
        register("LIFE_STEAL", LifeStealComponent::new, LifeStealComponent.class);
        register("LIGHTNING", LightningComponent::new, LightningComponent.class);
        register("EXPLOSION", ExplosionComponent::new, ExplosionComponent.class);
        register("SUMMON", SummonComponent::new, SummonComponent.class);
        register("PROJECTILE", ProjectileComponent::new, ProjectileComponent.class);
        register("AOE", AoeComponent::new, AoeComponent.class);
        register("CHARGE", ChargeComponent::new, ChargeComponent.class);
        register("COMMAND", CommandComponent::new, CommandComponent.class);
    }

    public void register(@NotNull String id, @NotNull Supplier<ArtifactComponent> factory, @NotNull Class<? extends ArtifactComponent> clazz) {
        componentFactories.put(id.toUpperCase(), factory);
        componentClasses.put(id.toUpperCase(), clazz);
    }

    public void register(@NotNull String id, @NotNull Class<? extends ArtifactComponent> clazz) {
        componentClasses.put(id.toUpperCase(), clazz);
    }

    public @Nullable ArtifactComponent create(@NotNull String type) {
        Supplier<ArtifactComponent> factory = componentFactories.get(type.toUpperCase());
        return factory != null ? factory.get() : null;
    }

    public @Nullable Class<? extends ArtifactComponent> getClass(@NotNull String type) {
        return componentClasses.get(type.toUpperCase());
    }

    public boolean exists(@NotNull String type) {
        return componentFactories.containsKey(type.toUpperCase());
    }

    public @NotNull Map<String, Supplier<ArtifactComponent>> getFactories() {
        return Map.copyOf(componentFactories);
    }
}
