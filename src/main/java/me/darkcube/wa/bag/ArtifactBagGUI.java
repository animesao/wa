package me.darkcube.wa.bag;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.gui.GUIBase;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ArtifactBagGUI extends GUIBase {

    private final ArtifactBagManager bagManager;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ArtifactBagGUI(WastelandArtifacts plugin, Player player) {
        super(plugin, player, "<dark_gray>🎒 Сумка артефактов", 6);
        this.bagManager = plugin.getArtifactBagManager();
    }

    @Override
    protected void build() {
        clickHandlers.clear();
        inventory.clear();

        ItemStack[] bag = bagManager.getBag(player);

        for (int i = 0; i < 54; i++) {
            if (bag[i] != null && !bag[i].getType().isAir()) {
                inventory.setItem(i, bag[i]);
            } else {
                inventory.setItem(i, makeBg(i));
            }
            final int fi = i;
            clickHandlers.put(i, e -> onSlotClick(e, fi));
        }
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);
    }

    private ItemStack makeBg(int index) {
        ItemStack bg = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta m = bg.getItemMeta();
        m.displayName(mm.deserialize(plugin.msg("bag.gui.slot-name", index + 1)));
        bg.setItemMeta(m);
        return bg;
    }

    @Override
    protected void onSlotClick(InventoryClickEvent event, int slot) {
        event.setCancelled(true);
        ItemStack cursor = event.getCursor();
        ItemStack current = bagManager.getSlot(player, slot);

        if (cursor != null && cursor.getType() != Material.AIR) {
            if (!plugin.getArtifactManager().isArtifact(cursor)) {
                ComponentUtil.sendMsg(player, plugin.msg("bag.gui.artifacts-only"));
                return;
            }
            ItemStack toPlace = cursor.clone();
            toPlace.setAmount(1);
            bagManager.setSlot(player, slot, toPlace);
            inventory.setItem(slot, toPlace);
            if (cursor.getAmount() <= 1) {
                event.getView().setCursor(null);
            } else {
                cursor.setAmount(cursor.getAmount() - 1);
                event.getView().setCursor(cursor);
            }
            ComponentUtil.sendMsg(player, plugin.msg("bag.gui.artifact-placed"));
        } else if (current != null && !current.getType().isAir()) {
            bagManager.setSlot(player, slot, null);
            event.getView().setCursor(current);
            inventory.setItem(slot, makeBg(slot));
            ComponentUtil.sendMsg(player, plugin.msg("bag.gui.artifact-removed"));
        }
    }

    @Override
    protected void onClose() {
        bagManager.saveBag(player);
    }
}
