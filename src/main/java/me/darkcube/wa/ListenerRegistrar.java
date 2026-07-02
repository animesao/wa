package me.darkcube.wa;

import me.darkcube.wa.altar.AltarBlockListener;
import me.darkcube.wa.altar.AltarListener;
import me.darkcube.wa.bag.ArtifactBagListener;
import me.darkcube.wa.dungeon.MobLootListener;
import me.darkcube.wa.listener.*;
import org.bukkit.plugin.PluginManager;

public class ListenerRegistrar {

    private final WastelandArtifacts plugin;

    public ListenerRegistrar(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new ArtifactListener(plugin), plugin);
        pm.registerEvents(new CraftingListener(plugin), plugin);
        pm.registerEvents(new DungeonListener(plugin), plugin);
        pm.registerEvents(plugin.getChatInputManager(), plugin);
        pm.registerEvents(new AltarListener(plugin), plugin);
        pm.registerEvents(new AltarBlockListener(plugin), plugin);
        pm.registerEvents(new ArmorListener(plugin), plugin);
        pm.registerEvents(new MobLootListener(plugin), plugin);
        pm.registerEvents(new ArtifactBagListener(plugin), plugin);
        pm.registerEvents(new CraftingProtectionListener(plugin), plugin);
        pm.registerEvents(new CustomItemBlockListener(plugin), plugin);
        pm.registerEvents(new SoulbindListener(plugin), plugin);

        if (plugin.getAchievementManager() != null) {
            pm.registerEvents(new me.darkcube.wa.feature.achievements.AchievementListener(plugin, plugin.getAchievementManager()), plugin);
        }

        var featureManager = plugin.getFeatureManager();
        if (featureManager != null) {
            if (featureManager.isEnabled("sockets") && plugin.getGemManager() != null) {
                var socketListener = new me.darkcube.wa.feature.socket.SocketListener(plugin, plugin.getGemManager());
                pm.registerEvents(socketListener, plugin);
                socketListener.start();
                plugin.setSocketListener(socketListener);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    for (var p : plugin.getServer().getOnlinePlayers()) {
                        socketListener.applyAllGems(p);
                    }
                });
            }
            if (featureManager.isEnabled("abilities") && plugin.getAbilityManager() != null) {
                pm.registerEvents(new me.darkcube.wa.feature.abilities.AbilityListener(plugin, plugin.getAbilityManager()), plugin);
            }
            if (featureManager.isEnabled("fishing") && plugin.getFishingListener() != null) {
                pm.registerEvents(plugin.getFishingListener(), plugin);
            }
            if (featureManager.isEnabled("elites") && plugin.getEliteMobManager() != null) {
                pm.registerEvents(new me.darkcube.wa.feature.elites.EliteMobListener(plugin, plugin.getEliteMobManager()), plugin);
            }
            if (featureManager.isEnabled("xp") && plugin.getArtifactXPManager() != null) {
                pm.registerEvents(new me.darkcube.wa.feature.xp.XPListener(plugin, plugin.getArtifactXPManager()), plugin);
            }
        }
    }
}
