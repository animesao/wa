package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.altar.AltarBlockTracker;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlueprintCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public BlueprintCommand(WastelandArtifacts plugin) {
        super("blueprint", "Чертежи артефактов", "/blueprint give <recipeId> [player] [amount]",
                List.of("bp", "blueprints"));
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(mm.deserialize("<gold>/blueprint list <gray>- список чертежей"));
            sender.sendMessage(mm.deserialize("<gold>/blueprint give <id> [player] [amount] <gray>- получить чертёж"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "list" -> handleList(sender);
            case "give" -> handleGive(sender, args);
            default -> {
                sender.sendMessage(mm.deserialize("<red>Неизвестная подкоманда. Используй /blueprint list или /blueprint give"));
                yield true;
            }
        };
    }

    private boolean handleList(CommandSender sender) {
        var recipes = plugin.getAltarManager().getCraftingManager().getAllRecipes();
        if (recipes.isEmpty()) {
            sender.sendMessage(mm.deserialize("<red>Нет зарегистрированных чертежей"));
            return true;
        }
        sender.sendMessage(mm.deserialize("<gold>═══ Чертежи ═══"));
        for (String id : recipes.keySet()) {
            var recipe = recipes.get(id);
            var art = plugin.getArtifactRegistry().get(recipe.getResultId());
            String name = art != null ? art.getDisplayName() : recipe.getResultId();
            sender.sendMessage(mm.deserialize("  <gray>• <white>" + id + " <dark_gray>→ " + name));
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(mm.deserialize("<red>Использование: /blueprint give <recipeId> [player] [amount]"));
            return true;
        }

        String recipeId = args[1];
        var recipe = plugin.getAltarManager().getCraftingManager().getRecipe(recipeId);
        if (recipe == null) {
            sender.sendMessage(mm.deserialize(plugin.msg("admin.recipe-not-found", recipeId)));
            return true;
        }

        var artifact = plugin.getArtifactRegistry().get(recipe.getResultId());
        String name = artifact != null ? artifact.getDisplayName() : recipe.getResultId();
        int amount = args.length >= 4 ? tryParse(args[3], 1) : 1;

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

        ItemStack bp = AltarBlockTracker.createBlueprint(recipeId, name,
                "PAPER", "<gold>📜 Чертёж: " + name, null, 5001);
        bp.setAmount(amount);
        target.getInventory().addItem(bp).forEach((i, leftover) ->
                target.getWorld().dropItem(target.getLocation(), leftover));

        sender.sendMessage(mm.deserialize(plugin.msg("admin.blueprint-given", name)));
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
            completions.addAll(plugin.getAltarManager().getCraftingManager().getAllRecipes().keySet());
        } else if (args.length == 3 && "give".equalsIgnoreCase(args[0])) {
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        }
        return completions;
    }
}
