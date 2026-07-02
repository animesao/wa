package me.darkcube.wa;

import me.darkcube.wa.commands.*;

public class CommandRegistrar {

    private final WastelandArtifacts plugin;

    public CommandRegistrar(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        var cmdMap = plugin.getServer().getCommandMap();
        cmdMap.register("wastelandartifacts", new ArtifactCommand(plugin));
        cmdMap.register("wastelandartifacts", new AdminCommand(plugin));
        cmdMap.register("wastelandartifacts", new DungeonCommand(plugin));
        cmdMap.register("wastelandartifacts", new AltarCommand(plugin));
        cmdMap.register("wastelandartifacts", new ItemCommand(plugin));
        cmdMap.register("wastelandartifacts", new CustomItemCommand(plugin));
        cmdMap.register("wastelandartifacts", new BlueprintCommand(plugin));

        if (plugin.getGemManager() != null) {
            cmdMap.register("wastelandartifacts", new GemCommand(plugin));
        }
    }
}
