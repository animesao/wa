package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.util.ComponentUtil;
import me.darkcube.wa.util.MojangItemCodec;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ItemCommand(WastelandArtifacts plugin) {
        super("item");
        this.plugin = plugin;
        setDescription("Утилиты для работы с предметами (Mojang Base64)");
        setUsage("/item <encode|decode> [base64]");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ComponentUtil.sendMsg(sender, plugin.msg("item.players-only"));
            return true;
        }

        if (args.length == 0) {
            ComponentUtil.sendMsg(player, plugin.msg("item.help.encode"));
            ComponentUtil.sendMsg(player, plugin.msg("item.help.decode"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "encode" -> handleEncode(player);
            case "decode" -> handleDecode(player, args);
            default -> {
                ComponentUtil.sendMsg(player, plugin.msg("item.unknown-subcommand"));
                yield true;
            }
        };
    }

    private boolean handleEncode(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            ComponentUtil.sendMsg(player, plugin.msg("item.hold-item"));
            return true;
        }
        String b64 = MojangItemCodec.encode(hand);
        ComponentUtil.sendMsg(player, plugin.msg("item.encode-result-title"));
        ComponentUtil.sendMsg(player, plugin.msg("item.encode-result", b64));
        ComponentUtil.sendMsg(player, plugin.msg("item.encode-copy-hint"));
        return true;
    }

    private boolean handleDecode(Player player, String[] args) {
        if (args.length < 2) {
            ComponentUtil.sendMsg(player, plugin.msg("item.usage.decode"));
            return true;
        }
        String b64 = args[1];
        if (!MojangItemCodec.isValid(b64)) {
            ComponentUtil.sendMsg(player, plugin.msg("item.decode-invalid"));
            return true;
        }
        ItemStack item = MojangItemCodec.decode(b64);
        if (item == null) {
            ComponentUtil.sendMsg(player, plugin.msg("item.decode-failed"));
            return true;
        }
        player.getInventory().addItem(item);
        ComponentUtil.sendMsg(player, plugin.msg("item.decode-success"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(List.of("encode", "decode"));
        }
        return completions;
    }
}
