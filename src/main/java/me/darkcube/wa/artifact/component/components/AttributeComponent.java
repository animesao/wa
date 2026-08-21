package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AttributeComponent implements ArtifactComponent {

    private Attribute attribute;
    private double amount;
    private AttributeModifier.Operation operation;
    private EquipmentSlot slot;

    public AttributeComponent() {
        this.attribute = Attribute.GENERIC_MOVEMENT_SPEED;
        this.amount = 0.1;
        this.operation = AttributeModifier.Operation.ADD_NUMBER;
        this.slot = EquipmentSlot.HAND;
    }

    public AttributeComponent(Attribute attribute, double amount, AttributeModifier.Operation operation) {
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.slot = EquipmentSlot.HAND;
    }

    public Attribute getAttribute() { return attribute; }
    public void setAttribute(Attribute attribute) { this.attribute = attribute; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public AttributeModifier.Operation getOperation() { return operation; }
    public void setOperation(AttributeModifier.Operation operation) { this.operation = operation; }
    public EquipmentSlot getSlot() { return slot; }
    public void setSlot(EquipmentSlot slot) { this.slot = slot; }

    @Override
    public @NotNull String getType() { return "ATTRIBUTE"; }

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
        if (type == org.bukkit.Material.SHIELD) {
            return EquipmentSlot.OFF_HAND;
        }
        return EquipmentSlot.HAND;
    }

    @Override
    public void apply(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        EquipmentSlotGroup group = toSlotGroup(resolveSlot(item));
        var key = new NamespacedKey("wastelandartifacts", "wa_" + attribute.name().toLowerCase());
        meta.addAttributeModifier(attribute, new AttributeModifier(
                key, amount, operation, group
        ));
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
