package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.feature.socket.GemBagGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GemCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public GemCommand(WastelandArtifacts plugin) {
        super("gem", "Самоцветы", "/gem give <id> [player] [amount]",
                List.of("gems", "socket"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize("<gold>/gem open <gray>- открыть гнёзда артефакта в руке"));
            sender.sendMessage(mm.deserialize("<gold>/gem list <gray>- список самоцветов"));
            sender.sendMessage(mm.deserialize("<gold>/gem give <id> [amount] <gray>- получить самоцвет"));
            return true;
        }

        if (plugin.getGemManager() == null) {
            sender.sendMessage(mm.deserialize("<red>Система самоцветов неактивна"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "open" -> handleOpen(sender);
            case "list" -> handleList(sender);
            case "give" -> handleGive(sender, args);
            default -> {
                sender.sendMessage(mm.deserialize("<red>Неизвестная подкоманда. Используй /gem open, /gem list или /gem give"));
                yield true;
            }
        };
    }

    private boolean handleOpen(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize("<red>Только для игроков"));
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR || !plugin.getArtifactManager().isArtifact(item)) {
            sender.sendMessage(mm.deserialize("<red>Возьми артефакт с гнёздами в руку"));
            return true;
        }
        int sockets = plugin.getGemManager().getSocketCount(item);
        if (sockets <= 0) {
            sender.sendMessage(mm.deserialize("<red>У этого артефакта нет гнёзд"));
            return true;
        }
        new GemBagGUI(plugin, player, item).open();
        return true;
    }

    private boolean handleList(CommandSender sender) {
        var all = plugin.getGemManager().getAll();
        if (all.isEmpty()) {
            sender.sendMessage(mm.deserialize("<red>Нет зарегистрированных самоцветов"));
            return true;
        }
        sender.sendMessage(mm.deserialize("<gold>═══ Самоцветы ═══"));
        for (var gem : all) {
            String effects = gem.getEffects().stream()
                    .map(e -> e.getType().equals("POTION") ? "⚗" : "⚡")
                    .collect(java.util.stream.Collectors.joining(" "));
            sender.sendMessage(mm.deserialize("  " + gem.getName()
                    + " <dark_gray>(" + gem.getId() + ")"
                    + " <gray>" + effects));
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Использование: /gem give <id> [amount]"));
            return true;
        }

        String gemId = args[1];
        if (plugin.getGemManager().getGem(gemId) == null) {
            sender.sendMessage(mm.deserialize("<red>Самоцвет '" + gemId + "' не найден"));
            return true;
        }

        int amount = args.length >= 3 ? tryParse(args[2], 1) : 1;

        if (sender instanceof Player player) {
            plugin.getGemManager().giveGem(player, gemId, amount);
            sender.sendMessage(mm.deserialize("<green>Выдан самоцвет " + gemId + " x" + amount));
        } else {
            sender.sendMessage(mm.deserialize("<red>Только для игроков"));
        }
        return true;
    }

    private int tryParse(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("open", "give", "list"));
        } else if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            if (plugin.getGemManager() != null) {
                completions.addAll(plugin.getGemManager().getAll().stream().map(g -> g.getId()).toList());
            }
        } else if (args.length == 3 && "give".equalsIgnoreCase(args[0])) {
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        }
        return completions;
    }
}
