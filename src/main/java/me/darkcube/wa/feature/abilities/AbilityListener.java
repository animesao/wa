package me.darkcube.wa.feature.abilities;

import me.darkcube.wa.WastelandArtifacts;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener {
    private final WastelandArtifacts plugin;
    private final AbilityManager manager;

    public AbilityListener(WastelandArtifacts plugin, AbilityManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null) return;

        var artifact = plugin.getArtifactManager().getArtifactFromItem(item);
        if (artifact == null) return;

        for (var comp : artifact.getComponents()) {
            if (comp.getType().equals("ABILITY")) {
                String abilityId = null;
                try {
                    var field = comp.getClass().getDeclaredField("abilityId");
                    field.setAccessible(true);
                    abilityId = (String) field.get(comp);
                } catch (Exception e) {}
                if (abilityId != null) {
                    Ability ability = manager.getAbility(abilityId);
                    manager.executeAbility(player, ability);
                }
            }
        }
    }
}
