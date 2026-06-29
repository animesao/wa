package me.darkcube.wa.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class ItemNameUtil {

    private static final Map<String, String> RUSSIAN_NAMES = new HashMap<>();

    static {
        RUSSIAN_NAMES.put("DIAMOND_SWORD", "Алмазный Меч");
        RUSSIAN_NAMES.put("BLAZE_ROD", "Огненный Стержень");
        RUSSIAN_NAMES.put("BLAZE_POWDER", "Огненный Порошок");
        RUSSIAN_NAMES.put("MAGMA_CREAM", "Магмовый Крем");
        RUSSIAN_NAMES.put("DIAMOND", "Алмаз");
        RUSSIAN_NAMES.put("NETHERITE_INGOT", "Незеритовый Слиток");
        RUSSIAN_NAMES.put("NETHERITE_SCRAP", "Незеритовый Осколок");
        RUSSIAN_NAMES.put("DIAMOND_BLOCK", "Алмазный Блок");
        RUSSIAN_NAMES.put("NETHERITE_BLOCK", "Незеритовый Блок");
        RUSSIAN_NAMES.put("BOOK", "Книга");
        RUSSIAN_NAMES.put("BONE", "Кость");
        RUSSIAN_NAMES.put("STRING", "Нить");
        RUSSIAN_NAMES.put("PHANTOM_MEMBRANE", "Мембрана Фантома");
        RUSSIAN_NAMES.put("ELYTRA", "Элитры");
        RUSSIAN_NAMES.put("INK_SAC", "Чернильный Мешок");
        RUSSIAN_NAMES.put("IRON_INGOT", "Железный Слиток");
        RUSSIAN_NAMES.put("EMERALD", "Изумруд");
        RUSSIAN_NAMES.put("OBSIDIAN", "Обсидиан");
        RUSSIAN_NAMES.put("CRYING_OBSIDIAN", "Плачущий Обсидиан");
        RUSSIAN_NAMES.put("END_STONE_BRICKS", "Кирпичи Края");
        RUSSIAN_NAMES.put("POLISHED_BLACKSTONE_BRICKS", "Полированные Чернокаменные Кирпичи");
        RUSSIAN_NAMES.put("CHISELED_STONE_BRICKS", "Резные Каменные Кирпичи");
        RUSSIAN_NAMES.put("RESPAWN_ANCHOR", "Якорь Возрождения");
        RUSSIAN_NAMES.put("BEACON", "Маяк");
        RUSSIAN_NAMES.put("END_CRYSTAL", "Кристалл Края");
        RUSSIAN_NAMES.put("DRAGON_EGG", "Яйцо Дракона");
        RUSSIAN_NAMES.put("NETHER_STAR", "Звезда Незера");
        RUSSIAN_NAMES.put("TOTEM_OF_UNDYING", "Тотем Бессмертия");
        RUSSIAN_NAMES.put("ENDER_PEARL", "Жемчуг Края");
        RUSSIAN_NAMES.put("ENDER_EYE", "Око Края");
        RUSSIAN_NAMES.put("GHAST_TEAR", "Слеза Гаста");
        RUSSIAN_NAMES.put("SPIDER_EYE", "Паучий Глаз");
        RUSSIAN_NAMES.put("ROTTEN_FLESH", "Гнилая Плоть");
        RUSSIAN_NAMES.put("WITHER_SKELETON_SKULL", "Череп Скелета-Иссушителя");
        RUSSIAN_NAMES.put("BLAZE_SPAWN_EGG", "Яйцо Призыва Ифрит");
        RUSSIAN_NAMES.put("COAL", "Уголь");
        RUSSIAN_NAMES.put("CHARCOAL", "Древесный Уголь");
        RUSSIAN_NAMES.put("STICK", "Палка");
        RUSSIAN_NAMES.put("BOWL", "Миска");
        RUSSIAN_NAMES.put("GOLD_INGOT", "Золотой Слиток");
        RUSSIAN_NAMES.put("IRON_SWORD", "Железный Меч");
        RUSSIAN_NAMES.put("DIAMOND_AXE", "Алмазный Топор");
        RUSSIAN_NAMES.put("NETHERITE_AXE", "Незеритовый Топор");
        RUSSIAN_NAMES.put("NETHERITE_SWORD", "Незеритовый Меч");
        RUSSIAN_NAMES.put("NETHERITE_CHESTPLATE", "Незеритовый Нагрудник");
        RUSSIAN_NAMES.put("NETHERITE_HELMET", "Незеритовый Шлем");
        RUSSIAN_NAMES.put("NETHERITE_LEGGINGS", "Незеритовые Поножи");
        RUSSIAN_NAMES.put("NETHERITE_BOOTS", "Незеритовые Ботинки");
        RUSSIAN_NAMES.put("DIAMOND_CHESTPLATE", "Алмазный Нагрудник");
        RUSSIAN_NAMES.put("DIAMOND_LEGGINGS", "Алмазные Поножи");
        RUSSIAN_NAMES.put("DIAMOND_BOOTS", "Алмазные Ботинки");
        RUSSIAN_NAMES.put("GOLDEN_HELMET", "Золотой Шлем");
        RUSSIAN_NAMES.put("GOLDEN_CHESTPLATE", "Золотой Нагрудник");
        RUSSIAN_NAMES.put("GOLDEN_LEGGINGS", "Золотые Поножи");
        RUSSIAN_NAMES.put("GOLDEN_BOOTS", "Золотые Ботинки");
        RUSSIAN_NAMES.put("LEATHER_HELMET", "Кожаный Шлем");
        RUSSIAN_NAMES.put("LEATHER_CHESTPLATE", "Кожаный Нагрудник");
        RUSSIAN_NAMES.put("LEATHER_LEGGINGS", "Кожаные Поножи");
        RUSSIAN_NAMES.put("LEATHER_BOOTS", "Кожаные Ботинки");
        RUSSIAN_NAMES.put("CHAINMAIL_HELMET", "Кольчужный Шлем");
        RUSSIAN_NAMES.put("CHAINMAIL_CHESTPLATE", "Кольчужный Нагрудник");
        RUSSIAN_NAMES.put("CHAINMAIL_LEGGINGS", "Кольчужные Поножи");
        RUSSIAN_NAMES.put("CHAINMAIL_BOOTS", "Кольчужные Ботинки");
        RUSSIAN_NAMES.put("SHIELD", "Щит");
        RUSSIAN_NAMES.put("BOW", "Лук");
        RUSSIAN_NAMES.put("ARROW", "Стрела");
        RUSSIAN_NAMES.put("TRIDENT", "Трезубец");
        RUSSIAN_NAMES.put("FLINT_AND_STEEL", "Огниво");
        RUSSIAN_NAMES.put("COMPASS", "Компас");
        RUSSIAN_NAMES.put("CLOCK", "Часы");
        RUSSIAN_NAMES.put("HEART_OF_THE_SEA", "Сердце Моря");
        RUSSIAN_NAMES.put("NAUTILUS_SHELL", "Раковина Наутилуса");
        RUSSIAN_NAMES.put("SCULK_CATALYST", "Катализатор Скulk");
        RUSSIAN_NAMES.put("ECHO_SHARD", "Осколок Эха");
        RUSSIAN_NAMES.put("AMETHYST_SHARD", "Осколок Аметиста");
        RUSSIAN_NAMES.put("LIGHTNING_ROD", "Громоотвод");
        RUSSIAN_NAMES.put("COPPER_BLOCK", "Медный Блок");
        RUSSIAN_NAMES.put("ICE", "Лёд");
        RUSSIAN_NAMES.put("PACKED_ICE", "Уплотнённый Лёд");
        RUSSIAN_NAMES.put("BLUE_ICE", "Синий Лёд");
        RUSSIAN_NAMES.put("PAPER", "Бумага");
        RUSSIAN_NAMES.put("MAP", "Карта");
        RUSSIAN_NAMES.put("WRITTEN_BOOK", "Написанная Книга");
        RUSSIAN_NAMES.put("ENCHANTED_BOOK", "Зачарованная Книга");
        RUSSIAN_NAMES.put("FIRE_CHARGE", "Огненный Заряд");
        RUSSIAN_NAMES.put("PLAYER_HEAD", "Голова Игрока");
        RUSSIAN_NAMES.put("SKELETON_SKULL", "Череп Скелета");
        RUSSIAN_NAMES.put("ZOMBIE_HEAD", "Голова Зомби");
        RUSSIAN_NAMES.put("CREEPER_HEAD", "Голова Крипера");
        RUSSIAN_NAMES.put("DRAGON_HEAD", "Голова Дракона");
        RUSSIAN_NAMES.put("SOUL_SOIL", "Почва Душ");
        RUSSIAN_NAMES.put("SOUL_SAND", "Песок Душ");
        RUSSIAN_NAMES.put("END_STONE", "Камень Края");
        RUSSIAN_NAMES.put("NETHER_WART", "Адский Нарост");
        RUSSIAN_NAMES.put("GLOWSTONE_DUST", "Светокаменная Пыль");
        RUSSIAN_NAMES.put("REDSTONE", "Редстоун");
        RUSSIAN_NAMES.put("GUNPOWDER", "Порох");
        RUSSIAN_NAMES.put("SUGAR", "Сахар");
        RUSSIAN_NAMES.put("LEATHER", "Кожа");
        RUSSIAN_NAMES.put("SADDLE", "Седло");
        RUSSIAN_NAMES.put("FIREWORK_ROCKET", "Фейерверк");
        RUSSIAN_NAMES.put("PRISMARINE_SHARD", "Призмариновый Осколок");
        RUSSIAN_NAMES.put("PRISMARINE_CRYSTALS", "Призмариновый Кристалл");
        RUSSIAN_NAMES.put("SPONGE", "Губка");
        RUSSIAN_NAMES.put("WET_SPONGE", "Мокрая Губка");
        RUSSIAN_NAMES.put("CHORUS_FRUIT", "Плод Хоруса");
        RUSSIAN_NAMES.put("DRAGON_BREATH", "Дыхание Дракона");
        RUSSIAN_NAMES.put("END_CRYSTAL", "Кристалл Края");
        RUSSIAN_NAMES.put("MACE", "Булава");
    }

    public static String getRussianName(Material material) {
        return RUSSIAN_NAMES.getOrDefault(material.name(), formatEnglishName(material.name()));
    }

    public static String getRussianName(ItemStack item) {
        if (item == null) return "Пусто";
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return getRussianName(item.getType());
    }

    public static String getRussianName(String materialName) {
        if (materialName == null) return "Неизвестно";
        String result = RUSSIAN_NAMES.get(materialName.toUpperCase());
        if (result != null) return result;
        return formatEnglishName(materialName);
    }

    private static String formatEnglishName(String name) {
        if (name == null) return "";
        String[] parts = name.toLowerCase().replace("_", " ").split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
        }
        return sb.toString();
    }
}
