package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class PotionEffectOnEquipComponent implements ArtifactComponent {

    private PotionEffectType effect;
    private int amplifier;
    private boolean ambient;
    private boolean showParticles;
    private boolean showIcon;

    public PotionEffectOnEquipComponent() {
        this.effect = PotionEffectType.SPEED;
        this.amplifier = 0;
        this.ambient = true;
        this.showParticles = false;
        this.showIcon = true;
    }

    public PotionEffectOnEquipComponent(PotionEffectType effect, int amplifier) {
        this.effect = effect;
        this.amplifier = amplifier;
        this.ambient = true;
        this.showParticles = false;
        this.showIcon = true;
    }

    public PotionEffectType getEffect() { return effect; }
    public void setEffect(PotionEffectType effect) { this.effect = effect; }
    public int getAmplifier() { return amplifier; }
    public void setAmplifier(int amplifier) { this.amplifier = amplifier; }
    public boolean isAmbient() { return ambient; }
    public void setAmbient(boolean ambient) { this.ambient = ambient; }

    @Override
    public @NotNull String getType() { return "POTION_EFFECT_ON_EQUIP"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {
        player.addPotionEffect(new PotionEffect(effect, Integer.MAX_VALUE, amplifier, ambient, showParticles, showIcon), true);
    }

    @Override
    public void onUnequip(@NotNull Player player) {
        player.removePotionEffect(effect);
    }
}
