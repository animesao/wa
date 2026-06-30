package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.altar.AltarBlockTracker;
import me.darkcube.wa.gui.AdminItemsGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public AdminCommand(WastelandArtifacts plugin) {
        super("waadmin");
        this.plugin = plugin;
        setDescription("Админ-команды WastelandArtifacts");
        setUsage("/waadmin <rp|debug>");
        setAliases(List.of("waa"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("wastelandartifacts.admin")) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.no-permission")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(miniMessage.deserialize("<gold>/waadmin gui - GUI всех предметов"));
            sender.sendMessage(miniMessage.deserialize("<gold>/waadmin rp build - собрать RP"));
            sender.sendMessage(miniMessage.deserialize("<gold>/waadmin rp send - раздать RP"));
            sender.sendMessage(miniMessage.deserialize("<gold>/waadmin debug - режим отладки"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "rp" -> handleRP(sender, args);
            case "blueprint" -> handleBlueprint(sender, args);
            case "customitem" -> handleCustomItem(sender, args);
            case "debug" -> handleDebug(sender);
            case "gui" -> handleGUI(sender);
            default -> {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.unknown-subcommand")));
                yield true;
            }
        };
    }

    private boolean handleRP(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.usage.rp")));
            return true;
        }
        return switch (args[1].toLowerCase()) {
            case "build" -> {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.rp-building")));
                plugin.getResourcePackManager().buildPack();
                sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.rp-built")));
                yield true;
            }
            case "send" -> {
                sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.rp-sending")));
                plugin.getResourcePackManager().sendToAll();
                yield true;
            }
            default -> {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.usage.rp")));
                yield true;
            }
        };
    }

    private boolean handleBlueprint(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.players-only")));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.usage.blueprint")));
            return true;
        }
        var recipe = plugin.getAltarManager().getCraftingManager().getRecipe(args[1]);
        if (recipe == null) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.recipe-not-found", args[1])));
            return true;
        }
        var artifact = plugin.getArtifactRegistry().get(recipe.getResultId());
        String name = artifact != null ? artifact.getDisplayName() : recipe.getResultId();
        // Ищем настройки чертежа В ТОМ ЖЕ рецепте
        String matName = "PAPER";
        String bpName = "<gold>📜 Чертёж: " + name;
        List<String> bpLore = null;
        int bpCmd = 5001;
        for (var tier : plugin.getAltarManager().getAllTiers().values()) {
            if (tier.recipes == null) continue;
            for (var re : tier.recipes) {
                String fullId = null;
                for (var cmEntry : plugin.getAltarManager().getCraftingManager().getAllRecipes().entrySet()) {
                    if (cmEntry.getKey().endsWith(re.id) && re.result.equals(recipe.getResultId())) {
                        fullId = cmEntry.getKey();
                        break;
                    }
                }
                if (fullId != null && fullId.equals(args[1])) {
                    if (re.blueprintMaterial != null) matName = re.blueprintMaterial;
                    if (re.blueprintName != null) bpName = re.blueprintName;
                    if (re.blueprintLore != null) bpLore = re.blueprintLore;
                    if (re.blueprintCustomModelData > 0) bpCmd = re.blueprintCustomModelData;
                    break;
                }
            }
        }
        player.getInventory().addItem(AltarBlockTracker.createBlueprint(args[1], name, matName, bpName, bpLore, bpCmd));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.blueprint-given", name)));
        return true;
    }

    private boolean handleCustomItem(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.players-only")));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.usage.customitem")));
            return true;
        }
        int amount = args.length >= 3 ? tryParse(args[2], 1) : 1;
        ItemStack item = plugin.getCustomItemRegistry().create(args[1], amount);
        if (item == null) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.customitem-not-found", args[1])));
            return true;
        }
        // Берём displayName из предмета, если есть
        String displayName = args[1];
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(item.getItemMeta().displayName());
        }
        player.getInventory().addItem(item);
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.customitem-given", displayName, amount)));
        return true;
    }

    private int tryParse(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    private boolean handleDebug(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug-title")));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug.artifacts", plugin.getArtifactRegistry().size())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug.components", plugin.getComponentRegistry().getFactories().size())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug.dungeons", plugin.getDungeonManager().getAllConfigs().size())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug.schematics", plugin.getSchematicManager().listSchematics().size())));
        sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.debug.resourcepack",
                plugin.getResourcePackManager() != null ? "активен" : "неактивен")));
        return true;
    }

    private boolean handleGUI(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(miniMessage.deserialize(plugin.msg("admin.players-only")));
            return true;
        }
        new AdminItemsGUI(plugin, player).open();
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("rp", "blueprint", "customitem", "debug", "gui"));
        } else if (args.length == 2 && "rp".equals(args[0])) {
            completions.addAll(List.of("build", "send"));
        } else if (args.length == 2 && "blueprint".equals(args[0])) {
            completions.addAll(plugin.getAltarManager().getCraftingManager().getAllRecipes().keySet());
        } else if (args.length == 2 && "customitem".equals(args[0])) {
            completions.addAll(plugin.getCustomItemRegistry().getAll().keySet());
        }
        return completions;
    }
}
