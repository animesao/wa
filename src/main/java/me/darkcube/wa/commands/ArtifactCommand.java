package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.rarity.Rarity;
import me.darkcube.wa.gui.ArtifactEditorGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ArtifactCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ArtifactCommand(WastelandArtifacts plugin) {
        super("artifact");
        this.plugin = plugin;
        setDescription("Управление артефактами");
        setUsage("/artifact <give|list|info|reload|create|edit> [args]");
        setAliases(List.of("art", "wa"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.help.list")));
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.help.info")));
            if (sender.hasPermission("wastelandartifacts.admin.blueprint")) {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.help.give")));
                sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.help.reload")));
                sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.help.edit")));
            }
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "give" -> {
                if (!sender.hasPermission("wastelandartifacts.admin.blueprint")) {
                    sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.no-permission")));
                    yield true;
                }
                yield handleGive(sender, args);
            }
            case "reload" -> {
                if (!sender.hasPermission("wastelandartifacts.admin")) {
                    sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.no-permission")));
                    yield true;
                }
                yield handleReload(sender);
            }
            case "create", "edit" -> {
                if (!sender.hasPermission("wastelandartifacts.admin")) {
                    sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.no-permission")));
                    yield true;
                }
                yield handleEdit(sender, args);
            }
            case "list" -> handleList(sender, args);
            case "info" -> handleInfo(sender, args);
            default -> {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.unknown-subcommand")));
                yield true;
            }
        };
    }

    private boolean checkPlayerPerm(CommandSender sender) {
        if (!sender.hasPermission("wastelandartifacts.player.artifact")) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.no-permission")));
            return false;
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.usage.give")));
            return true;
        }

        String artifactId = args[1];
        Artifact artifact = plugin.getArtifactRegistry().get(artifactId);
        if (artifact == null) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.not-found", artifactId)));
            return true;
        }

        Player target;
        int amount = 1;

        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.player-not-found")));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.specify-player")));
            return true;
        }

        if (args.length >= 4) {
            try { amount = Integer.parseInt(args[3]); }
            catch (NumberFormatException e) { amount = 1; }
        }

        plugin.getArtifactManager().giveArtifact(target, artifactId, amount);
        sender.sendMessage(miniMessage.deserialize(
                plugin.msg("artifact.given", artifact.getDisplayName(), target.getName())
        ));
        return true;
    }

    private boolean handleList(CommandSender sender, String[] args) {
        List<Artifact> all = plugin.getArtifactRegistry().getAll();
        if (all.isEmpty()) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.list-empty")));
            return true;
        }

        int page = 1;
        if (args.length >= 2) {
            try { page = Math.max(1, Integer.parseInt(args[1])); }
            catch (NumberFormatException ignored) {}
        }

        int perPage = 10;
        int totalPages = (all.size() + perPage - 1) / perPage;
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, all.size());

        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.list-title", page, totalPages)));
        for (int i = start; i < end; i++) {
            Artifact a = all.get(i);
            sender.sendMessage(miniMessage.deserialize(
                    plugin.msg("artifact.list-entry", a.getRarity().getColor().asHexString(), a.getId(), a.getDisplayName())
            ));
        }
        return true;
    }

    private boolean handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.usage.info")));
            return true;
        }

        Artifact artifact = plugin.getArtifactRegistry().get(args[1]);
        if (artifact == null) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.not-found", args[1])));
            return true;
        }

        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info-title")));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.id", artifact.getId())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.name", artifact.getDisplayName())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.item", artifact.getBaseItem().name())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.rarity", artifact.getRarity().getDisplayName())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.modeldata", artifact.getCustomModelData())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.components", artifact.getComponents().size())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.info.recipe", artifact.getRecipe() != null ? "Да" : "Нет")));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadAll();
        plugin.getDungeonManager().loadConfigs();
        sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.reloaded")));
        sender.sendMessage(miniMessage.deserialize(
                plugin.msg("artifact.reload-summary", plugin.getArtifactRegistry().size())
        ));
        return true;
    }

    private boolean handleCreate(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.players-only")));
            return true;
        }
        plugin.getArtifactEditorGUI().openCreator(player);
        return true;
    }

    private boolean handleEdit(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.players-only")));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.usage.edit")));
            return true;
        }
        Artifact artifact = plugin.getArtifactRegistry().get(args[1]);
        if (artifact == null) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("artifact.not-found-generic")));
            return true;
        }
        plugin.getArtifactEditorGUI().openEditor(player, artifact);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("give", "list", "info", "reload", "create", "edit"), new ArrayList<>());
        }
        if (args.length == 2 && ("give".equals(args[0]) || "info".equals(args[0]) || "edit".equals(args[0]))) {
            return StringUtil.copyPartialMatches(args[1],
                    plugin.getArtifactRegistry().getAll().stream().map(Artifact::getId).collect(Collectors.toList()),
                    new ArrayList<>()
            );
        }
        if (args.length == 3 && "give".equals(args[0])) {
            return StringUtil.copyPartialMatches(args[2],
                    Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()),
                    new ArrayList<>()
            );
        }
        return Collections.emptyList();
    }
}
