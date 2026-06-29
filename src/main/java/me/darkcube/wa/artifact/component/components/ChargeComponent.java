package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ChargeComponent implements ArtifactComponent {

    private int maxCharges;
    private boolean consumeOnUse;
    private boolean destroyWhenEmpty;

    public ChargeComponent() {
        this.maxCharges = 5;
        this.consumeOnUse = true;
        this.destroyWhenEmpty = true;
    }

    public int getMaxCharges() { return maxCharges; }
    public void setMaxCharges(int maxCharges) { this.maxCharges = maxCharges; }
    public boolean isConsumeOnUse() { return consumeOnUse; }
    public void setConsumeOnUse(boolean consumeOnUse) { this.consumeOnUse = consumeOnUse; }
    public boolean isDestroyWhenEmpty() { return destroyWhenEmpty; }
    public void setDestroyWhenEmpty(boolean destroyWhenEmpty) { this.destroyWhenEmpty = destroyWhenEmpty; }

    public int getCharges(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) return maxCharges;
        Integer charges = meta.getPersistentDataContainer().get(
                new org.bukkit.NamespacedKey("wastelandartifacts", "wa_charges"),
                PersistentDataType.INTEGER
        );
        return charges != null ? charges : maxCharges;
    }

    public boolean useCharge(ItemStack item, Player player) {
        int charges = getCharges(item) - 1;
        if (charges <= 0) {
            if (destroyWhenEmpty) {
                item.setAmount(0);
            }
            return false;
        }
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey("wastelandartifacts", "wa_charges"),
                    PersistentDataType.INTEGER, charges
            );
            item.setItemMeta(meta);
        }
        return true;
    }

    @Override
    public @NotNull String getType() { return "CHARGE"; }

    @Override
    public void apply(@NotNull ItemStack item) {
        var meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(
                    new org.bukkit.NamespacedKey("wastelandartifacts", "wa_charges"),
                    PersistentDataType.INTEGER, maxCharges
            );
            item.setItemMeta(meta);
        }
    }

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
