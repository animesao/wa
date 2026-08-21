package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

import org.jetbrains.annotations.NotNull;

public class DamageComponent implements ArtifactComponent {

    private double damage;
    private EquipmentSlot slot;

    public DamageComponent() {
        this.damage = 1.0;
        this.slot = EquipmentSlot.HAND;
    }

    public DamageComponent(double damage) {
        this.damage = damage;
        this.slot = EquipmentSlot.HAND;
    }

    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
    public EquipmentSlot getSlot() { return slot; }
    public void setSlot(EquipmentSlot slot) { this.slot = slot; }

    @Override
    public @NotNull String getType() {
        return "DAMAGE";
    }

    private EquipmentSlot resolveSlot(@NotNull ItemStack item) {
        if (slot != EquipmentSlot.HAND) return slot;
        var type = item.getType();
        if (type.name().endsWith("_HELMET") || type.name().endsWith("_HEAD")
                || type == org.bukkit.Material.CARVED_PUMPKIN
                || type == org.bukkit.Material.SKELETON_SKULL
                || type == org.bukkit.Material.WITHER_SKELETON_SKULL
                || type == org.bukkit.Material.ZOMBIE_HEAD
                || type == org.bukkit.Material.CREEPER_HEAD
                || type == org.bukkit.Material.DRAGON_HEAD
                || type == org.bukkit.Material.PLAYER_HEAD) {
            return EquipmentSlot.HEAD;
        }
        if (type.name().endsWith("_CHESTPLATE") || type == org.bukkit.Material.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (type.name().endsWith("_LEGGINGS")) {
            return EquipmentSlot.LEGS;
        }
        if (type.name().endsWith("_BOOTS")) {
            return EquipmentSlot.FEET;
        }
        return EquipmentSlot.HAND;
    }

    @Override
    public void apply(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        var att = org.bukkit.attribute.Attribute.GENERIC_ATTACK_DAMAGE;
        var op = org.bukkit.attribute.AttributeModifier.Operation.ADD_NUMBER;
        double val = damage - 1;
        EquipmentSlotGroup group = toSlotGroup(resolveSlot(item));
        var key = new NamespacedKey("wastelandartifacts", "wa_dmg");
        meta.addAttributeModifier(att, new org.bukkit.attribute.AttributeModifier(
                key, val, op, group));
        item.setItemMeta(meta);
    }

    private static EquipmentSlotGroup toSlotGroup(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS -> EquipmentSlotGroup.LEGS;
            case FEET -> EquipmentSlotGroup.FEET;
            case OFF_HAND -> EquipmentSlotGroup.OFFHAND;
            default -> EquipmentSlotGroup.MAINHAND;
        };
    }

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
