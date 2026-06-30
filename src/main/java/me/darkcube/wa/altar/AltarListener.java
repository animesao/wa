package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class AltarListener implements Listener {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final AltarManager altarManager;

    public AltarListener(WastelandArtifacts plugin) {
        this.plugin = plugin;
        this.altarManager = plugin.getAltarManager();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAltarInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        Block block = event.getClickedBlock();
        if (block == null) return;

        AltarConfig.AltarTier tier = altarManager.detectAltar(block);
        if (tier == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (player.isSneaking()) {
            // Shift + ПКМ → забрать предметы
            altarManager.getBlockTracker().collectItems(player, block);
        } else {
            // Обычный ПКМ → информация
            ComponentUtil.sendMsg(player, plugin.msg("altar.interact-info", tier.displayName, tier.description));
            ComponentUtil.sendMsg(player, plugin.msg("altar.howto-drop"));
            ComponentUtil.sendMsg(player, plugin.msg("altar.howto-collect"));
            ComponentUtil.sendMsg(player, plugin.msg("altar.howto-destroy"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        altarManager.getBlockTracker().removeAltar(event.getBlock());
    }
}
