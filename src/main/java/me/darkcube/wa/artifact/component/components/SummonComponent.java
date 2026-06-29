package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SummonComponent implements ArtifactComponent {

    private EntityType entityType;
    private int amount;
    private int duration;
    private boolean withEquipment;

    public SummonComponent() {
        this.entityType = EntityType.SKELETON;
        this.amount = 1;
        this.duration = 200;
        this.withEquipment = true;
    }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public boolean isWithEquipment() { return withEquipment; }
    public void setWithEquipment(boolean withEquipment) { this.withEquipment = withEquipment; }

    public List<LivingEntity> summon(Location location, Player owner) {
        List<LivingEntity> summoned = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Location spawnLoc = location.clone().add(
                    Math.random() * 3 - 1.5,
                    0,
                    Math.random() * 3 - 1.5
            );
            Entity entity = location.getWorld().spawnEntity(spawnLoc, entityType);
            if (entity instanceof LivingEntity living) {
                if (withEquipment && living instanceof Mob mob) {
                    mob.setRemoveWhenFarAway(false);
                }
                summoned.add(living);
            }
        }
        if (!summoned.isEmpty() && duration > 0) {
            org.bukkit.Bukkit.getScheduler().runTaskLater(
                    org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(SummonComponent.class),
                    () -> summoned.forEach(LivingEntity::remove),
                    duration * 20L
            );
        }
        return summoned;
    }

    @Override
    public @NotNull String getType() { return "SUMMON"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
