package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CooldownComponent implements ArtifactComponent {

    private int seconds;

    public CooldownComponent() {
        this.seconds = 5;
    }

    public CooldownComponent(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() { return seconds; }
    public void setSeconds(int seconds) { this.seconds = seconds; }

    public boolean hasCooldown(Player player) {
        return player.getCooldown(player.getInventory().getItemInMainHand().getType()) > 0;
    }

    public void applyCooldown(Player player) {
        player.setCooldown(player.getInventory().getItemInMainHand().getType(), seconds * 20);
    }

    @Override
    public @NotNull String getType() { return "COOLDOWN"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
