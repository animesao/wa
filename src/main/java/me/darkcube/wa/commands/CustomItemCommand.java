package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomItemCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public CustomItemCommand(WastelandArtifacts plugin) {
        super("customitem", "Кастомные предметы", "/customitem give <id> [player] [amount]",
                List.of("ci", "customitems"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize("<gold>/customitem list <gray>- список предметов"));
            sender.sendMessage(mm.deserialize("<gold>/customitem give <id> [player] [amount] <gray>- получить предмет"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "list" -> handleList(sender);
            case "give" -> handleGive(sender, args);
            default -> {
                sender.sendMessage(mm.deserialize("<red>Неизвестная подкоманда. Используй /customitem list или /customitem give"));
                yield true;
            }
        };
    }

    private boolean handleList(CommandSender sender) {
        var all = plugin.getCustomItemRegistry().getAll();
        if (all.isEmpty()) {
            sender.sendMessage(mm.deserialize("<red>Нет зарегистрированных кастомных предметов"));
            return true;
        }
        sender.sendMessage(mm.deserialize("<gold>═══ Кастомные предметы ═══"));
        for (String id : all.keySet()) {
            sender.sendMessage(mm.deserialize("  <gray>• <white>" + id));
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Использование: /customitem give <id> [player] [amount]"));
            return true;
        }

        String id = args[1];
        int amount = args.length >= 4 ? tryParse(args[3], 1) : 1;
        ItemStack item = plugin.getCustomItemRegistry().create(id, amount);

        if (item == null) {
            sender.sendMessage(mm.deserialize(plugin.msg("admin.customitem-not-found", id)));
            return true;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(mm.deserialize(plugin.msg("artifact.player-not-found")));
                return true;
            }
            if (!sender.hasPermission("wastelandartifacts.admin")) {
                sender.sendMessage(mm.deserialize(plugin.msg("admin.no-permission")));
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            sender.sendMessage(mm.deserialize("<red>Укажите игрока из консоли"));
            return true;
        }

        target.getInventory().addItem(item).forEach((i, leftover) ->
                target.getWorld().dropItem(target.getLocation(), leftover));

        String displayName = id;
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(item.getItemMeta().displayName());
        }
        sender.sendMessage(mm.deserialize(plugin.msg("admin.customitem-given", displayName, amount)));
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
            completions.addAll(List.of("give", "list"));
        } else if (args.length == 2 && "give".equalsIgnoreCase(args[0])) {
            completions.addAll(plugin.getCustomItemRegistry().getAll().keySet());
        } else if (args.length == 3 && "give".equalsIgnoreCase(args[0])) {
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        }
        return completions;
    }
}
