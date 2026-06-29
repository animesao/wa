package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SoundOnHitComponent implements ArtifactComponent {

    private Sound sound;
    private float volume;
    private float pitch;

    public SoundOnHitComponent() {
        this.sound = Sound.ENTITY_BLAZE_HURT;
        this.volume = 1.0f;
        this.pitch = 1.0f;
    }

    public SoundOnHitComponent(Sound sound) {
        this.sound = sound;
        this.volume = 1.0f;
        this.pitch = 1.0f;
    }

    public Sound getSound() { return sound; }
    public void setSound(Sound sound) { this.sound = sound; }

    public void play(Location location) {
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    @Override
    public @NotNull String getType() { return "SOUND_ON_HIT"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
