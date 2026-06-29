package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DungeonCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public DungeonCommand(WastelandArtifacts plugin) {
        super("dungeon");
        this.plugin = plugin;
        setDescription("Управление подземельями");
        setUsage("/dungeon <scan|paste|info> [args]");
        setAliases(List.of("dg"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("wastelandartifacts.admin")) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.no-permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.help.scan")));
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.help.paste")));
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.help.info")));
            if (sender.hasPermission("wastelandartifacts.admin")) {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.help.loot")));
            }
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "scan" -> handleScan(sender, args);
            case "paste" -> handlePaste(sender, args);
            case "loot" -> {
                if (!sender.hasPermission("wastelandartifacts.admin")) {
                    sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.no-permission")));
                    yield true;
                }
                yield handleLoot(sender);
            }
            case "info" -> handleInfo(sender);
            default -> {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.unknown-subcommand")));
                yield true;
            }
        };
    }

    private boolean handleScan(CommandSender sender, String[] args) {
        sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.scanning")));
        if (args.length >= 2) {
            var world = org.bukkit.Bukkit.getWorld(args[1]);
            if (world != null) {
                plugin.getDungeonManager().scanWorld(world);
                sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.world-scanned", world.getName())));
            } else {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.world-not-found", args[1])));
            }
        } else {
            plugin.getDungeonManager().scanAllWorlds();
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.all-scanned")));
        }
        return true;
    }

    private boolean handlePaste(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.usage.paste")));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.players-only")));
            return true;
        }

        boolean result = plugin.getSchematicManager().paste(args[1], player.getLocation());
        if (result) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.schematic-placed", args[1])));
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.schematic-failed", args[1])));
        }
        return true;
    }

    private boolean handleLoot(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.players-only")));
            return true;
        }
        new me.darkcube.wa.dungeon.DungeonLootGUI(plugin, player).open();
        return true;
    }

    private boolean handleInfo(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize(plugin.msg("dungeon.info-title")));
        sender.sendMessage(miniMessage.deserialize(
                plugin.msg("dungeon.info.configs", plugin.getDungeonManager().getAllConfigs().size())
        ));
        sender.sendMessage(miniMessage.deserialize(
                plugin.msg("dungeon.info.schematics", plugin.getSchematicManager().listSchematics().size())
        ));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("scan", "paste", "info"));
        } else if (args.length == 2 && "scan".equals(args[0])) {
            plugin.getServer().getWorlds().forEach(w -> completions.add(w.getName()));
        } else if (args.length == 2 && "paste".equals(args[0])) {
            completions.addAll(plugin.getSchematicManager().listSchematics());
        }
        return completions;
    }
}
