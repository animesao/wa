package me.darkcube.wa.gui.menu;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.gui.GUIBase;
import me.darkcube.wa.util.ComponentUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuGUI extends GUIBase {

    protected final String menuId;
    protected MenuConfig menuConfig;

    public MenuGUI(WastelandArtifacts plugin, Player player, String menuId) {
        super(plugin, player, menuId);
        this.menuId = menuId;
        this.menuConfig = plugin.getMenuManager() != null ? plugin.getMenuManager().getMenu(menuId) : null;
    }

    @Override
    public void build() {
        clickHandlers.clear();
        inventory.clear();
        renderStaticItems();
        renderDynamic();
    }

    protected void renderStaticItems() {
        if (menuConfig == null) return;
        for (MenuItem item : menuConfig.items) {
            if (item.dynamic) continue;
            if (item.fill) {
                fillBorder(Material.matchMaterial(item.material));
                continue;
            }
            for (int slot : item.slots) {
                ItemStack stack = createItem(item);
                if (stack != null) {
                    if (item.actions.isEmpty()) {
                        setItem(slot, stack, null);
                    } else {
                        List<String> acts = item.actions;
                        setItem(slot, stack, e -> executeActions(acts));
                    }
                }
            }
        }
    }

    protected ItemStack createItem(MenuItem item) {
        Material mat = Material.matchMaterial(item.material);
        if (mat == null) mat = Material.STONE;
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return null;

        if (!item.displayName.isEmpty()) {
            meta.displayName(ComponentUtil.fromMini(replacePlaceholders(item.displayName)));
        }
        if (!item.lore.isEmpty()) {
            List<net.kyori.adventure.text.Component> lore = new ArrayList<>();
            for (String line : item.lore) {
                lore.add(ComponentUtil.fromMini(replacePlaceholders(line)));
            }
            meta.lore(lore);
        }
        if (item.customModelData > 0) {
            meta.setCustomModelData(item.customModelData);
        }
        stack.setItemMeta(meta);
        return stack;
    }

    protected String replacePlaceholders(String s) {
        return s.replace("%player_name%", player.getName())
                .replace("%player_uuid%", player.getUniqueId().toString())
                .replace("%player_world%", player.getWorld().getName());
    }

    protected void executeActions(List<String> actions) {
        for (String action : actions) {
            executeAction(action);
        }
    }

    protected void executeAction(String action) {
        if (action == null || action.isEmpty()) return;

        String lower = action.toLowerCase().trim();

        if (lower.startsWith("player:")) {
            String cmd = action.substring(7).trim();
            player.performCommand(cmd);
        } else if (lower.startsWith("console:")) {
            String cmd = action.substring(8).trim();
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),
                    replacePlaceholders(cmd));
        } else if (lower.startsWith("sound:")) {
            String[] parts = action.substring(6).split(":");
            String soundName = parts[0].trim();
            float vol = parts.length > 1 ? Float.parseFloat(parts[1]) : 1.0f;
            float pitch = parts.length > 2 ? Float.parseFloat(parts[2]) : 1.0f;
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                player.playSound(player.getLocation(), sound, vol, pitch);
            } catch (Exception ignored) {}
        } else if (lower.startsWith("message:")) {
            String msg = action.substring(8).trim();
            player.sendMessage(ComponentUtil.fromMini(replacePlaceholders(msg)));
        } else if (lower.equals("close")) {
            close();
        } else if (lower.startsWith("open:")) {
            String target = action.substring(5).trim();
            MenuGUI targetGUI = createMenu(target);
            if (targetGUI != null) {
                targetGUI.open();
            }
        }
    }

    protected abstract void renderDynamic();

    protected abstract MenuGUI createMenu(String id);
}
