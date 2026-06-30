package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.feature.collection.CollectionGUI;
import me.darkcube.wa.feature.collection.CollectionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CollectionCommand extends Command {

    private final WastelandArtifacts plugin;
    private final CollectionGUI collectionGUI;
    private final CollectionManager collectionManager;

    public CollectionCommand(WastelandArtifacts plugin, CollectionGUI collectionGUI, CollectionManager collectionManager) {
        super("artifacts", "Artifact collection", "/artifacts collection", List.of("collection"));
        this.plugin = plugin;
        this.collectionGUI = collectionGUI;
        this.collectionManager = collectionManager;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cТолько для игроков!");
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("collection")) {
            int found = collectionManager.getFoundCount(player);
            int total = collectionManager.getAllArtifactIds().size();
            player.sendMessage("§6Коллекция: §e" + found + "§6/§e" + total);
        } else {
            collectionGUI.open(player);
        }
        return true;
    }
}
