package me.darkcube.wa.commands;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.feature.arena.ArenaGUI;
import me.darkcube.wa.feature.arena.BossArenaManager;
import me.darkcube.wa.util.ComponentUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArenaCommand extends Command {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final BossArenaManager arenaManager;
    private final ArenaGUI arenaGUI;

    public ArenaCommand(WastelandArtifacts plugin, BossArenaManager arenaManager, ArenaGUI arenaGUI) {
        super("arena", "Boss arena", "/arena", List.of("bossarena"));
        this.plugin = plugin;
        this.arenaManager = arenaManager;
        this.arenaGUI = arenaGUI;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            ComponentUtil.sendMsg(sender, plugin.msg("arena.players-only"));
            return true;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            arenaManager.startArena(player);
        } else {
            arenaGUI.open(player);
        }
        return true;
    }
}
