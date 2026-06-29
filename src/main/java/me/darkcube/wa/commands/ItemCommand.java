package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
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
            sender.sendMessage(mm.deserialize("<red>Только для игроков!"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(mm.deserialize("<gold>/item encode <gray>- получить Base64 предмета в руке"));
            player.sendMessage(mm.deserialize("<gold>/item decode <base64> <gray>- создать предмет из Base64"));
            return true;
        }

        return switch (args[0].toLowerCase()) {
            case "encode" -> handleEncode(player);
            case "decode" -> handleDecode(player, args);
            default -> {
                player.sendMessage(mm.deserialize("<red>Неизвестная команда. Используй /item encode или /item decode"));
                yield true;
            }
        };
    }

    private boolean handleEncode(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            player.sendMessage(mm.deserialize("<red>Возьми предмет в руку!"));
            return true;
        }
        String b64 = MojangItemCodec.encode(hand);
        player.sendMessage(mm.deserialize("<green>Base64 (Mojang NBT):"));
        player.sendMessage(mm.deserialize("<white>" + b64));
        player.sendMessage(mm.deserialize("<dark_gray>Скопируй строку выше"));
        return true;
    }

    private boolean handleDecode(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(mm.deserialize("<red>Использование: /item decode <base64>"));
            return true;
        }
        String b64 = args[1];
        if (!MojangItemCodec.isValid(b64)) {
            player.sendMessage(mm.deserialize("<red>Невалидная Base64 строка!"));
            return true;
        }
        ItemStack item = MojangItemCodec.decode(b64);
        if (item == null) {
            player.sendMessage(mm.deserialize("<red>Не удалось декодировать предмет!"));
            return true;
        }
        player.getInventory().addItem(item);
        player.sendMessage(mm.deserialize("<green>Предмет создан!"));
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
