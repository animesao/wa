package me.darkcube.wa.artifact.rarity;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public enum Rarity {
    COMMON("<gray>Обычный", TextColor.color(0x808080)),
    UNCOMMON("<green>Необычный", TextColor.color(0x55FF55)),
    RARE("<aqua>Редкий", TextColor.color(0x55FFFF)),
    EPIC("<light_purple>Эпический", TextColor.color(0xFF55FF)),
    LEGENDARY("<gold>Легендарный", TextColor.color(0xFFAA00)),
    MYTHIC("<dark_red>Мифический", TextColor.color(0xAA0000)),
    UNKNOWN("<dark_aqua>Неизвестный", TextColor.color(0x00AAAA)),
    VOID("<black>Пустота", TextColor.color(0x000000));

    private final String displayName;
    private final TextColor color;

    Rarity(String displayName, TextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TextColor getColor() {
        return color;
    }

    public TextDecoration getDecoration() {
        return this == LEGENDARY || this == MYTHIC || this == UNKNOWN
                ? TextDecoration.BOLD
                : TextDecoration.ITALIC;
    }
}
