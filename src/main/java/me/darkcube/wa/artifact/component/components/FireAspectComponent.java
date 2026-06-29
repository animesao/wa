package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FireAspectComponent implements ArtifactComponent {

    private int level;

    public FireAspectComponent() {
        this.level = 1;
    }

    public FireAspectComponent(int level) {
        this.level = level;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    @Override
    public @NotNull String getType() { return "FIRE_ASPECT"; }

    @Override
    public void apply(@NotNull ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, level);
    }

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
