package me.darkcube.wa.artifact.component.components;

import me.darkcube.wa.artifact.component.ArtifactComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CommandComponent implements ArtifactComponent {

    private String command;
    private boolean asPlayer;

    public CommandComponent() {
        this.command = "say triggered";
        this.asPlayer = true;
    }

    public CommandComponent(String command, boolean asPlayer) {
        this.command = command;
        this.asPlayer = asPlayer;
    }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    public boolean isAsPlayer() { return asPlayer; }
    public void setAsPlayer(boolean asPlayer) { this.asPlayer = asPlayer; }

    public void execute(Player player) {
        String cmd = command.replace("%player%", player.getName());
        if (asPlayer) {
            player.performCommand(cmd);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

    @Override
    public @NotNull String getType() { return "COMMAND"; }

    @Override
    public void apply(@NotNull ItemStack item) {}

    @Override
    public void onEquip(@NotNull Player player) {}

    @Override
    public void onUnequip(@NotNull Player player) {}
}
