package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChatInputManager implements Listener {

    private final WastelandArtifacts plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final Map<UUID, Consumer<String>> pendingInputs = new ConcurrentHashMap<>();

    public ChatInputManager(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void requestInput(Player player, String prompt, Consumer<String> callback) {
        player.sendMessage(plugin.getConfigManager().getLang("prefix") + " " + prompt);
            player.sendMessage(mm.deserialize("<gray>[<green>Введите в чат<gray>] <white>Напишите текст <gray>(или <red>отмена<gray>)"));
            pendingInputs.put(player.getUniqueId(), callback);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Consumer<String> callback = pendingInputs.remove(player.getUniqueId());
        if (callback == null) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();

        if (message.equalsIgnoreCase("отмена") || message.equalsIgnoreCase("cancel")) {
            player.sendMessage(mm.deserialize("<red>Ввод отменён"));
            return;
        }

        callback.accept(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pendingInputs.remove(event.getPlayer().getUniqueId());
    }

    public boolean hasPendingInput(Player player) {
        return pendingInputs.containsKey(player.getUniqueId());
    }

    public void cancelInput(Player player) {
        pendingInputs.remove(player.getUniqueId());
    }
}
