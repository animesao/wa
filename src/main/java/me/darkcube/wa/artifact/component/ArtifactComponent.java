package me.darkcube.wa.artifact.component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface ArtifactComponent {

    @NotNull String getType();

    void apply(@NotNull ItemStack item);

    void onEquip(@NotNull Player player);

    void onUnequip(@NotNull Player player);
}
