package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.altar.AltarConfig;
import me.darkcube.wa.altar.AltarManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class AltarCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public AltarCommand(WastelandArtifacts plugin) {
        super("altar");
        this.plugin = plugin;
        setDescription("Управление алтарями");
        setUsage("/altar <list|info|preview|build|schematic>");
        setAliases(List.of("alt"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize(plugin.msg("altar.players-only")));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        AltarManager altarManager = plugin.getAltarManager();

        return switch (args[0].toLowerCase()) {
            case "list" -> handleList(player, altarManager);
            case "info" -> handleInfo(player, altarManager, args);
            case "preview" -> handlePreview(player, altarManager, args);
            case "build" -> {
                if (!player.hasPermission("wastelandartifacts.admin.altar")) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.no-permission-preview")));
                    yield true;
                }
                yield handleBuild(player, altarManager, args);
            }
            case "schematic" -> {
                if (!player.hasPermission("wastelandartifacts.admin.altar")) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.no-permission")));
                    yield true;
                }
                yield handleSchematic(player, altarManager, args);
            }
            default -> {
                sendHelp(player);
                yield true;
            }
        };
    }

    private void sendHelp(Player player) {
        player.sendMessage(mm.deserialize(plugin.msg("altar.help-title")));
        player.sendMessage(mm.deserialize(plugin.msg("altar.help.list")));
        player.sendMessage(mm.deserialize(plugin.msg("altar.help.info")));
        player.sendMessage(mm.deserialize(plugin.msg("altar.help.preview")));
        if (player.hasPermission("wastelandartifacts.admin.altar")) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.help.build")));
            player.sendMessage(mm.deserialize(plugin.msg("altar.help.schematic")));
        } else {
            player.sendMessage(mm.deserialize(plugin.msg("altar.help.preview-hint")));
        }
    }

    private boolean handleList(Player player, AltarManager mgr) {
        Map<String, AltarConfig.AltarTier> tiers = mgr.getAllTiers();
        if (tiers.isEmpty()) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.list-empty")));
            return true;
        }
        player.sendMessage(mm.deserialize(plugin.msg("altar.list-title")));
        for (var entry : tiers.entrySet()) {
            AltarConfig.AltarTier t = entry.getValue();
            if (!t.enabled) continue;
            player.sendMessage(mm.deserialize(
                    plugin.msg("altar.list-entry", t.displayName, entry.getKey(), t.tier,
                            t.recipes != null ? t.recipes.size() : 0)
            ));
        }
        return true;
    }

    private boolean handleInfo(Player player, AltarManager mgr, String[] args) {
        if (args.length < 2) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.usage.info")));
            return true;
        }
        AltarConfig.AltarTier tier = mgr.getTier(args[1]);
        if (tier == null) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.not-found", args[1])));
            return true;
        }
        player.sendMessage(mm.deserialize(plugin.msg("altar.info-title", tier.displayName)));
        player.sendMessage(mm.deserialize(plugin.msg("altar.info.tier", tier.tier)));
        player.sendMessage(mm.deserialize(plugin.msg("altar.info.description", tier.description)));
        player.sendMessage(mm.deserialize(plugin.msg("altar.info.activator", tier.activatorBlock.name())));
        player.sendMessage(mm.deserialize(plugin.msg("altar.info.cooldown", tier.globalCooldown)));
        player.sendMessage(mm.deserialize(plugin.msg("altar.info.structures",
                tier.structures != null ? tier.structures.size() : 0)));

        if (tier.recipes != null && !tier.recipes.isEmpty()) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.info.recipes-title")));
            for (var recipe : tier.recipes) {
                var art = plugin.getArtifactRegistry().get(recipe.result);
                String name = art != null ? art.getDisplayName() : recipe.result;
                StringBuilder line = new StringBuilder("  <gray>• " + name + ": ");
                for (var ing : recipe.ingredients) {
                    line.append(ing.type.name()).append(" x").append(ing.amount).append(" ");
                }
                player.sendMessage(mm.deserialize(line.toString()));
            }
        }
        return true;
    }

    private boolean handlePreview(Player player, AltarManager mgr, String[] args) {
        if (args.length < 2) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.usage.preview")));
            return true;
        }

        if ("stop".equals(args[1])) {
            mgr.getPreview().stopPreview(player);
            player.sendMessage(mm.deserialize(plugin.msg("altar.preview-stopped")));
            return true;
        }

        AltarConfig.AltarTier tier = mgr.getTier(args[1]);
        if (tier == null) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.not-found", args[1])));
            return true;
        }

        int variant = args.length >= 3 ? tryParse(args[2], 0) : 0;
        mgr.getPreview().showPreview(player, tier, variant);
        return true;
    }

    private boolean handleBuild(Player player, AltarManager mgr, String[] args) {
        if (args.length < 2) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.usage.build")));
            return true;
        }
        AltarConfig.AltarTier tier = mgr.getTier(args[1]);
        if (tier == null) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.not-found", args[1])));
            return true;
        }
        int variant = args.length >= 3 ? tryParse(args[2], 0) : 0;
        boolean creative = player.hasPermission("wastelandartifacts.creative");
        mgr.getPreview().buildAltar(player, tier, variant, !creative);
        return true;
    }

    private boolean handleSchematic(Player player, AltarManager mgr, String[] args) {
        if (args.length < 2) {
            player.sendMessage(mm.deserialize(plugin.msg("altar.usage.schematic")));
            return true;
        }

        return switch (args[1].toLowerCase()) {
            case "save" -> {
                if (args.length < 3) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.usage.schematic-save")));
                    yield true;
                }
                mgr.getSchematic().saveAltar(player.getWorld(), args[2],
                        player.getLocation().add(-5, -1, -5),
                        player.getLocation().add(5, 5, 5));
                yield true;
            }
            case "paste" -> {
                if (args.length < 3) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.usage.schematic-paste")));
                    yield true;
                }
                mgr.getSchematic().pasteAltar(player.getLocation(), args[2]);
                yield true;
            }
            case "list" -> {
                List<String> list = mgr.getSchematic().listAltars();
                if (list.isEmpty()) {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.schematic-list-empty")));
                } else {
                    player.sendMessage(mm.deserialize(plugin.msg("altar.schematic-list", String.join(", ", list))));
                }
                yield true;
            }
            default -> {
                player.sendMessage(mm.deserialize(plugin.msg("altar.usage.schematic")));
                yield true;
            }
        };
    }

    private int tryParse(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("list", "info", "preview", "build", "schematic"));
        } else if (args.length == 2 && ("info".equals(args[0]) || "preview".equals(args[0]) || "build".equals(args[0]))) {
            completions.addAll(plugin.getAltarManager().getAllTiers().keySet());
        } else if (args.length == 2 && "schematic".equals(args[0])) {
            completions.addAll(List.of("save", "paste", "list"));
        } else if (args.length == 3 && "schematic".equals(args[0]) && "paste".equals(args[1])) {
            completions.addAll(plugin.getAltarManager().getSchematic().listAltars());
        }
        return completions;
    }
}
