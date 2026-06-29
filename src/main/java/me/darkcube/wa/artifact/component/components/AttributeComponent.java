package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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

    @Override
    @SuppressWarnings("deprecation")
    public void apply(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return;
        meta.addAttributeModifier(attribute, new AttributeModifier(
                UUID.randomUUID(), "wa_" + attribute.name(), amount, operation, slot
        ));
        item.setItemMeta(meta);
    }

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
