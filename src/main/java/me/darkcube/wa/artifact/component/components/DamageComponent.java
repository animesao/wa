package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

public class DamageComponent implements ArtifactComponent {

    private double damage;

    public DamageComponent() {
        this.damage = 1.0;
    }

    public DamageComponent(double damage) {
        this.damage = damage;
    }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }

    @Override
    public @NotNull String getType() {
        return "DAMAGE";
    }

    @Override
    @SuppressWarnings("deprecation")
    public void apply(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        meta.addAttributeModifier(
                org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE,
                new org.bukkit.attribute.AttributeModifier(
                        java.util.UUID.randomUUID(),
                        "artifact_damage",
                        damage - 1,
                        org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER,
                        org.bukkit.inventory.EquipmentSlot.HAND
                )
        );
        item.setItemMeta(meta);
    }

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
