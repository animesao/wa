package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PotionEffectOnHitComponent implements ArtifactComponent {

    private PotionEffectType effect;
    private int duration;
    private int amplifier;

    public PotionEffectOnHitComponent() {
        this.effect = PotionEffectType.POISON;
        this.duration = 100;
        this.amplifier = 1;
    }

    public void applyToTarget(@NotNull LivingEntity target) {
        if (effect == null) return;
        target.addPotionEffect(new PotionEffect(effect, duration, amplifier));
    }

    public PotionEffectType getEffect() { return effect; }
    public void setEffect(PotionEffectType effect) { this.effect = effect; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getAmplifier() { return amplifier; }
    public void setAmplifier(int amplifier) { this.amplifier = amplifier; }

    @Override
    public @NotNull String getType() { return "POTION_EFFECT_ON_HIT"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
