package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.bag.ArtifactBagGUI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BagCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public BagCommand(WastelandArtifacts plugin) {
        super("bag");
        this.plugin = plugin;
        setDescription("Открыть сумку артефактов");
        setUsage("/bag");
        setAliases(List.of("artifacts", "artbag"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(mm.deserialize(plugin.msg("bag.players-only")));
            return true;
        }
        if (!player.hasPermission("wastelandartifacts.player.bag")) {
            player.sendMessage(mm.deserialize(plugin.msg("bag.no-permission")));
            return true;
        }

        // Проверяем наличие артефакта "Сумка Пустоши"
        boolean hasBag = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && plugin.getArtifactManager().isArtifact(item)) {
                String artId = plugin.getArtifactManager().getArtifactId(item);
                if ("wasteland_bag".equals(artId)) {
                    hasBag = true;
                    break;
                }
            }
        }

        if (!hasBag) {
            player.sendMessage(mm.deserialize(plugin.msg("bag.no-bag")));
            player.sendMessage(mm.deserialize(plugin.msg("bag.craft-hint")));
            player.sendMessage(mm.deserialize(plugin.msg("bag.recipe-hint")));
            return true;
        }

        new ArtifactBagGUI(plugin, player).open();
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias,
                                              @NotNull String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
