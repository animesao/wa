package me.darkcube.wa.gui;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.rarity.Rarity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class ArtifactEditorGUI {

    private final WastelandArtifacts plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ArtifactEditorGUI(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void openCreator(Player player) {
        new EditorSession(plugin, player, null).open();
    }

    public void openEditor(Player player, Artifact artifact) {
        new EditorSession(plugin, player, artifact).open();
    }

    private class EditorSession extends GUIBase {

        private String id;
        private String displayName;
        private Material baseItem;
        private Rarity rarity;
        private float customModelData;
        private final List<String> lore = new ArrayList<>();
        private final List<String> components = new ArrayList<>();
        private final Artifact existing;
        private boolean isNew;

        EditorSession(WastelandArtifacts plugin, Player player, Artifact existing) {
            super(plugin, player, "<dark_gray>Редактор артефактов", 6);
            this.existing = existing;
            this.isNew = (existing == null);

            if (existing != null) {
                this.id = existing.getId();
                this.displayName = existing.getDisplayName();
                this.baseItem = existing.getBaseItem();
                this.rarity = existing.getRarity();
                this.customModelData = existing.getCustomModelData();
                this.lore.addAll(existing.getLore());
                existing.getComponents().forEach(c -> components.add(c.getType()));
            } else {
                this.id = "";
                this.displayName = "<white>Новый артефакт";
                this.baseItem = Material.STICK;
                this.rarity = Rarity.COMMON;
                this.customModelData = 0;
            }
        }

        @Override
        protected void build() {
            clickHandlers.clear();
            inventory.clear();

            // Строка 0: статус
            setItem(4, Material.ANVIL,
                    "<gold>═══ Редактор артефактов ═══",
                    List.of(
                            "<gray>ID: " + (id.isEmpty() ? "<red>не задан" : "<white>" + id),
                            "<gray>Режим: " + (isNew ? "<green>Создание" : "<gold>Редактирование")
                    ),
                    null
            );

            // Строка 1: Основные настройки
            setItem(9, Material.NAME_TAG, "<yellow>Название: " + displayName, null, e -> {
                player.sendMessage(miniMessage.deserialize("<yellow>Введите новое название в чат (MiniMessage)"));
                close();
                // TODO: chat input
            });

            setItem(10, Material.DIAMOND_SWORD, "<yellow>Предмет: " + baseItem.name(),
                    List.of("<gray>Нажмите, чтобы выбрать"), e -> {
                openMaterialSelector(player);
            });

            setItem(11, baseItem, "<yellow>Model Data: " + (int) customModelData,
                    List.of("<dark_gray>ЛКМ +1 | ПКМ -1"), e -> {
                if (e.isLeftClick()) customModelData++;
                else if (e.isRightClick()) customModelData = Math.max(0, customModelData - 1);
                build();
            });

            setItem(12, Material.ENDER_PEARL, "<yellow>Редкость: " + rarity.getDisplayName(),
                    List.of("<gray>Нажмите, чтобы сменить"), e -> {
                Rarity[] values = Rarity.values();
                int next = (rarity.ordinal() + 1) % values.length;
                rarity = values[next];
                build();
            });

            setItem(13, Material.BOOK, "<yellow>Описание (" + lore.size() + " строк)",
                    List.of("<gray>Нажмите, чтобы добавить строку", "<gray>ПКМ чтобы очистить"), e -> {
                if (e.isRightClick()) {
                    lore.clear();
                } else {
                    lore.add("<gray>Новая строка описания");
                }
                build();
            });

            // Строка 2: Компоненты
            setItem(18, Material.BLAZE_POWDER, "<gold>✦ Компоненты (" + components.size() + ")",
                    List.of("<gray>Нажмите, чтобы добавить компонент"), e -> {
                openComponentSelector(player);
            });

            // Отображаем добавленные компоненты
            for (int i = 0; i < Math.min(components.size(), 7); i++) {
                String comp = components.get(i);
                int slot = 19 + i;
                setItem(slot, Material.ENCHANTED_BOOK, "<aqua>" + comp,
                        List.of("<red>ПКМ чтобы удалить"), e -> {
                    if (e.isRightClick()) {
                        components.remove(comp);
                        build();
                    }
                });
            }

            // Строка 5: Сохранить / Отмена
            setItem(49, Material.GREEN_DYE, "<green>Сохранить артефакт",
                    List.of("<gray>Сохранить и выйти"), e -> {
                saveArtifact(player);
                close();
            });

            setItem(50, Material.BARRIER, "<red>Отмена", List.of("<gray>Выйти без сохранения"), e -> {
                close();
            });

            // Заполняем пустые слоты стеклом
            fillBorder(Material.GRAY_STAINED_GLASS_PANE);
        }

        private void saveArtifact(Player player) {
            if (id.isEmpty()) {
                id = "artifact_" + System.currentTimeMillis();
            }

            Artifact artifact = Artifact.builder(id)
                    .displayName(displayName)
                    .lore(lore)
                    .baseItem(baseItem)
                    .customModelData(customModelData)
                    .rarity(rarity)
                    .build();

            plugin.getArtifactRegistry().register(artifact);
            player.sendMessage(miniMessage.deserialize(
                    "<green>Артефакт '" + id + "' сохранён!"
            ));
        }

        private void openMaterialSelector(Player player) {
            new MaterialSelectorGUI(plugin, player, selected -> {
                if (selected != null) {
                    baseItem = selected;
                    build();
                }
            }).open();
        }

        private void openComponentSelector(Player player) {
            new ComponentSelectorGUI(plugin, player, compType -> {
                if (compType != null && !components.contains(compType)) {
                    components.add(compType);
                    build();
                }
            }).open();
        }
    }

    private class MaterialSelectorGUI extends GUIBase {
        private final Consumer<Material> callback;
        private int page = 0;
        private static final int MATERIALS_PER_PAGE = 45;

        MaterialSelectorGUI(WastelandArtifacts plugin, Player player, Consumer<Material> callback) {
            super(plugin, player, "<dark_gray>Выберите предмет", 6);
            this.callback = callback;
        }

        @Override
        protected void build() {
            clickHandlers.clear();
            inventory.clear();

            Material[] materials = Material.values();
            int start = page * MATERIALS_PER_PAGE;
            int end = Math.min(start + MATERIALS_PER_PAGE, materials.length);

            int slot = 0;
            for (int i = start; i < end && slot < 45; i++) {
                Material mat = materials[i];
                if (!mat.isItem()) continue;
                int currentSlot = slot;
                setItem(slot, mat, mat.name().toLowerCase().replace("_", " "),
                        List.of("<gray>Нажмите, чтобы выбрать"), e -> {
                    callback.accept(mat);
                    close();
                });
                slot++;
            }

            // Навигация
            setItem(45, Material.ARROW, "<yellow>← Назад",
                    page > 0 ? List.of() : List.of("<red>Первая страница"),
                    e -> { if (page > 0) { page--; build(); } }
            );

            setItem(49, Material.BARRIER, "<red>Отмена", null, e -> {
                callback.accept(null);
                close();
            });

            setItem(53, Material.ARROW, "<yellow>Вперёд →",
                    end < materials.length ? List.of() : List.of("<red>Последняя страница"),
                    e -> { if (end < materials.length) { page++; build(); } }
            );
        }
    }

    private class ComponentSelectorGUI extends GUIBase {
        private final Consumer<String> callback;

        ComponentSelectorGUI(WastelandArtifacts plugin, Player player, Consumer<String> callback) {
            super(plugin, player, "<dark_gray>Выберите компонент", 6);
            this.callback = callback;
        }

        @Override
        protected void build() {
            clickHandlers.clear();
            inventory.clear();

            String[] compTypes = {
                    "DAMAGE", "FIRE_ASPECT", "ATTRIBUTE", "POTION_EFFECT_ON_EQUIP",
                    "PARTICLE_ON_HIT", "PARTICLE_AMBIENT", "SOUND_ON_HIT", "SOUND_ON_USE",
                    "COOLDOWN", "LIFE_STEAL", "LIGHTNING", "EXPLOSION",
                    "SUMMON", "PROJECTILE", "AOE", "CHARGE"
            };

            int slot = 0;
            for (String type : compTypes) {
                if (slot >= 45) break;
                int currentSlot = slot;
                Material icon = switch (type) {
                    case "DAMAGE" -> Material.IRON_SWORD;
                    case "FIRE_ASPECT" -> Material.BLAZE_POWDER;
                    case "ATTRIBUTE" -> Material.FEATHER;
                    case "POTION_EFFECT_ON_EQUIP" -> Material.POTION;
                    case "PARTICLE_ON_HIT", "PARTICLE_AMBIENT" -> Material.FIREWORK_STAR;
                    case "SOUND_ON_HIT", "SOUND_ON_USE" -> Material.JUKEBOX;
                    case "COOLDOWN" -> Material.CLOCK;
                    case "LIFE_STEAL" -> Material.REDSTONE;
                    case "LIGHTNING" -> Material.ECHO_SHARD;
                    case "EXPLOSION" -> Material.TNT;
                    case "SUMMON" -> Material.ZOMBIE_HEAD;
                    case "PROJECTILE" -> Material.FIRE_CHARGE;
                    case "AOE" -> Material.DRAGON_BREATH;
                    case "CHARGE" -> Material.AMETHYST_SHARD;
                    default -> Material.PAPER;
                };
                setItem(slot, icon, "<aqua>" + type,
                        List.of("<gray>Нажмите, чтобы добавить"), e -> {
                    callback.accept(type);
                    close();
                });
                slot++;
            }

            setItem(49, Material.BARRIER, "<red>Отмена", null, e -> {
                callback.accept(null);
                close();
            });
        }
    }
}
