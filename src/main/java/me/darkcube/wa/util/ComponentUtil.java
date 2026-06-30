package me.darkcube.wa.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentUtil {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private static final Component EMPTY = Component.empty();

    public static @NotNull Component fromMini(@Nullable String mini) {
        if (mini == null || mini.isBlank()) return EMPTY;
        return MINI_MESSAGE.deserialize(mini);
    }

    public static @NotNull String toMini(@NotNull Component component) {
        return MINI_MESSAGE.serialize(component);
    }

    public static @NotNull String toLegacy(@NotNull Component component) {
        return LEGACY.serialize(component);
    }

    public static @NotNull List<Component> fromMiniList(@NotNull List<String> lines) {
        return lines.stream().map(MINI_MESSAGE::deserialize).collect(Collectors.toList());
    }

    public static @NotNull Component gradient(@NotNull String text, @NotNull TextColor from, @NotNull TextColor to) {
        return MINI_MESSAGE.deserialize("<gradient:" + from.asHexString() + ":" + to.asHexString() + ">" + text);
    }

    public static @NotNull Component colored(@NotNull String text, @NotNull TextColor color) {
        return MINI_MESSAGE.deserialize("<color:" + color.asHexString() + ">" + text);
    }

    public static @NotNull Component format(@NotNull String format, Object... args) {
        String formatted = String.format(format, args);
        if (formatted == null || formatted.isBlank()) return EMPTY;
        return MINI_MESSAGE.deserialize(formatted);
    }

    public static @NotNull Component fromLang(@Nullable String msg) {
        if (msg == null || msg.isBlank()) return EMPTY;
        return MINI_MESSAGE.deserialize(msg);
    }
}
