package me.darkcube.wa.api;

import me.darkcube.wa.WastelandArtifacts;
import me.darkcube.wa.artifact.Artifact;
import me.darkcube.wa.artifact.ArtifactManager;
import me.darkcube.wa.artifact.ArtifactRegistry;
import me.darkcube.wa.artifact.builder.ArtifactBuilder;
import me.darkcube.wa.artifact.component.ArtifactComponent;
import me.darkcube.wa.artifact.component.ComponentRegistry;
import me.darkcube.wa.artifact.rarity.Rarity;
import me.darkcube.wa.artifact.rarity.RarityManager;
import me.darkcube.wa.artifact.trigger.Trigger;
import me.darkcube.wa.artifact.trigger.TriggerContext;
import me.darkcube.wa.artifact.trigger.TriggerType;
import me.darkcube.wa.altar.AltarManager;
import me.darkcube.wa.bag.ArtifactBagManager;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.database.DatabaseManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.feature.abilities.Ability;
import me.darkcube.wa.feature.abilities.AbilityManager;
import me.darkcube.wa.feature.achievements.Achievement;
import me.darkcube.wa.feature.achievements.AchievementManager;
import me.darkcube.wa.feature.collection.CollectionManager;
import me.darkcube.wa.feature.elites.EliteMobManager;
import me.darkcube.wa.feature.sets.ArtifactSet;
import me.darkcube.wa.feature.sets.SetManager;
import me.darkcube.wa.feature.socket.Gem;
import me.darkcube.wa.feature.socket.GemManager;
import me.darkcube.wa.feature.upgrades.UpgradeManager;
import me.darkcube.wa.feature.xp.ArtifactXPManager;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.util.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Главное API для Wasteland Artifacts.
 * <p>
 * Получить инстанс: {@code WastelandArtifacts.getInstance().getApi()}
 * <br>
 * Или: {@code ((WastelandArtifacts) Bukkit.getPluginManager().getPlugin("WastelandArtifacts")).getApi()}
 */
public class WastelandArtifactsAPI {

    private final WastelandArtifacts plugin;

    public WastelandArtifactsAPI(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  АРТЕФАКТЫ — регистрация, поиск, создание предметов
    // ═══════════════════════════════════════════════════════════════

    /** Зарегистрировать новый артефакт в реестре. */
    public void registerArtifact(@NotNull Artifact artifact) {
        plugin.getArtifactRegistry().register(artifact);
    }

    /** Удалить артефакт из реестра по ID. */
    public void unregisterArtifact(@NotNull String id) {
        plugin.getArtifactRegistry().unregister(id);
    }

    /** Получить артефакт по ID. */
    public @Nullable Artifact getArtifact(@NotNull String id) {
        return plugin.getArtifactRegistry().get(id);
    }

    /** Список всех зарегистрированных артефактов. */
    public @NotNull List<Artifact> getAllArtifacts() {
        return plugin.getArtifactRegistry().getAll();
    }

    /** Проверить, существует ли артефакт с таким ID. */
    public boolean hasArtifact(@NotNull String id) {
        return plugin.getArtifactRegistry().exists(id);
    }

    /** Создать ItemStack для артефакта (с компонентами, лором, PDC). */
    public @NotNull ItemStack createItem(@NotNull Artifact artifact) {
        return plugin.getArtifactManager().createItemStack(artifact);
    }

    /** Создать ItemStack по ID артефакта. */
    public @NotNull ItemStack createItem(@NotNull String artifactId) {
        return plugin.getArtifactManager().createItemStack(artifactId);
    }

    /** Получить артефакт из ItemStack (по PDC). */
    public @Nullable Artifact getArtifactFromItem(@NotNull ItemStack item) {
        return plugin.getArtifactManager().getArtifactFromItem(item);
    }

    /** Проверить, является ли ItemStack артефактом. */
    public boolean isArtifact(@NotNull ItemStack item) {
        return plugin.getArtifactManager().isArtifact(item);
    }

    /** Выдать артефакт игроку в инвентарь. */
    public void giveArtifact(@NotNull Player player, @NotNull String artifactId, int amount) {
        plugin.getArtifactManager().giveArtifact(player, artifactId, amount);
    }

    /** Создать билдер для кастомного артефакта программно. */
    public @NotNull ArtifactBuilder builder(@NotNull String id) {
        return Artifact.builder(id);
    }

    /** Получить артефакт-менеджер. */
    public @NotNull ArtifactManager getArtifactManager() {
        return plugin.getArtifactManager();
    }

    /** Получить реестр артефактов. */
    public @NotNull ArtifactRegistry getArtifactRegistry() {
        return plugin.getArtifactRegistry();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  КОМПОНЕНТЫ — кастомные поведения артефактов
    // ═══════════════════════════════════════════════════════════════

    /** Зарегистрировать новый тип компонента. */
    public void registerComponent(@NotNull String id, @NotNull Class<? extends ArtifactComponent> clazz) {
        plugin.getComponentRegistry().register(id, clazz);
    }

    /** Создать инстанс компонента по типу. */
    public @Nullable ArtifactComponent createComponent(@NotNull String type) {
        return plugin.getComponentRegistry().create(type);
    }

    /** Получить реестр компонентов (для регистрации кастомных). */
    public @NotNull ComponentRegistry getComponentRegistry() {
        return plugin.getComponentRegistry();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ТРИГГЕРЫ — события-триггеры на артефактах
    // ═══════════════════════════════════════════════════════════════

    /** Зарегистрировать глобальный триггер на тип события. */
    public void registerTrigger(@NotNull TriggerType type, @NotNull Trigger trigger) {
        plugin.getArtifactRegistry().registerTrigger(type, trigger);
    }

    /** Выполнить все триггеры указанного типа вручную. */
    public void fireTriggers(@NotNull TriggerType type, @NotNull TriggerContext ctx) {
        for (Trigger t : plugin.getArtifactRegistry().getTriggers(type)) {
            t.execute(ctx);
        }
    }

    /** Получить все зарегистрированные триггеры. */
    public @NotNull Map<TriggerType, List<Trigger>> getAllTriggers() {
        return plugin.getArtifactRegistry().getAllTriggers();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  РЕДКОСТИ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер редкостей. */
    public @NotNull RarityManager getRarityManager() {
        return plugin.getRarityManager();
    }

    /** Получить определение редкости по ID. */
    public @NotNull RarityManager.RarityDef getRarityDef(@NotNull String id) {
        return plugin.getRarityManager().get(id);
    }

    /** Получить определение редкости по enum Rarity. */
    public @NotNull RarityManager.RarityDef getRarityDef(@NotNull Rarity rarity) {
        return plugin.getRarityManager().get(rarity);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  СУМКА АРТЕФАКТОВ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер сумки артефактов (может быть null, если фича отключена). */
    public @Nullable ArtifactBagManager getBagManager() {
        return plugin.getArtifactBagManager();
    }

    /** Получить содержимое сумки игрока. */
    public ItemStack[] getPlayerBag(@NotNull Player player) {
        var mgr = plugin.getArtifactBagManager();
        return mgr != null ? mgr.getBag(player) : new ItemStack[0];
    }

    /** Установить предмет в слот сумки игрока. */
    public void setBagSlot(@NotNull Player player, int slot, @Nullable ItemStack item) {
        var mgr = plugin.getArtifactBagManager();
        if (mgr != null) mgr.setSlot(player, slot, item);
    }

    /** Пересчитать эффекты сумки + оффхенд. */
    public void recalcBagEffects(@NotNull Player player) {
        var mgr = plugin.getArtifactBagManager();
        if (mgr != null) mgr.recalcEffects(player);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ПОДЗЕМЕЛЬЯ (DUNGEONS)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер подземелий (может быть null). */
    public @Nullable DungeonManager getDungeonManager() {
        return plugin.getDungeonManager();
    }

    /** Сгенерировать лут из указанного подземелья. */
    public @Nullable List<ItemStack> generateDungeonLoot(@NotNull String dungeonId) {
        var mgr = plugin.getDungeonManager();
        return mgr != null ? mgr.generateLoot(dungeonId) : null;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  АЛТАРИ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер алтарей (может быть null). */
    public @Nullable AltarManager getAltarManager() {
        return plugin.getAltarManager();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  КОЛЛЕКЦИЯ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер коллекции (может быть null). */
    public @Nullable CollectionManager getCollectionManager() {
        return plugin.getCollectionManager();
    }

    /** Отметить артефакт как найденный игроком. */
    public void markArtifactFound(@NotNull Player player, @NotNull String artifactId) {
        var mgr = plugin.getCollectionManager();
        if (mgr != null) mgr.markFound(player, artifactId);
    }

    /** Проверить, находил ли игрок артефакт. */
    public boolean hasFoundArtifact(@NotNull Player player, @NotNull String artifactId) {
        var mgr = plugin.getCollectionManager();
        return mgr != null && mgr.hasFound(player, artifactId);
    }

    /** Количество найденных игроком артефактов. */
    public int getCollectionCount(@NotNull Player player) {
        var mgr = plugin.getCollectionManager();
        return mgr != null ? mgr.getFoundCount(player) : 0;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  УЛУЧШЕНИЯ (UPGRADES)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер улучшений (может быть null). */
    public @Nullable UpgradeManager getUpgradeManager() {
        return plugin.getUpgradeManager();
    }

    /** Уровень артефакта у игрока. */
    public int getArtifactLevel(@NotNull Player player, @NotNull String artifactId) {
        var mgr = plugin.getUpgradeManager();
        return mgr != null ? mgr.getLevel(player, artifactId) : 1;
    }

    /** Установить уровень артефакта игроку. */
    public void setArtifactLevel(@NotNull Player player, @NotNull String artifactId, int level) {
        var mgr = plugin.getUpgradeManager();
        if (mgr != null) mgr.setLevel(player, artifactId, level);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ОПЫТ АРТЕФАКТОВ (XP)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер опыта артефактов (может быть null). */
    public @Nullable ArtifactXPManager getXPManager() {
        return plugin.getArtifactXPManager();
    }

    /** Уровень артефакта по XP-системе. */
    public int getArtifactXPLevel(@NotNull Player player, @NotNull String artifactId) {
        var mgr = plugin.getArtifactXPManager();
        return mgr != null ? mgr.getLevel(player, artifactId) : 1;
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  СЕТЫ (SETS)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер сетов (может быть null). */
    public @Nullable SetManager getSetManager() {
        return plugin.getSetManager();
    }

    /** Зарегистрировать новый сет артефактов. */
    public void registerSet(@NotNull ArtifactSet set) {
        var mgr = plugin.getSetManager();
        if (mgr != null) mgr.registerSet(set);
    }

    /** Активные сеты игрока (set → количество надетых частей). */
    public @NotNull Map<ArtifactSet, Integer> getActiveSets(@NotNull Player player) {
        var mgr = plugin.getSetManager();
        return mgr != null ? mgr.getActiveSets(player) : Map.of();
    }

    /** Применить/пересчитать сет-бонусы игрока. */
    public void applySetBonuses(@NotNull Player player) {
        var mgr = plugin.getSetManager();
        if (mgr != null) mgr.applySetBonuses(player);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  АБИЛКИ (ACTIVE ABILITIES)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер абилок (может быть null). */
    public @Nullable AbilityManager getAbilityManager() {
        return plugin.getAbilityManager();
    }

    /** Получить абилку по ID. */
    public @Nullable Ability getAbility(@NotNull String id) {
        var mgr = plugin.getAbilityManager();
        return mgr != null ? mgr.getAbility(id) : null;
    }

    /** Все зарегистрированные абилки. */
    public @NotNull Map<String, Ability> getAllAbilities() {
        var mgr = plugin.getAbilityManager();
        return mgr != null ? mgr.getAllAbilities() : Map.of();
    }

    /** Проверить кулдаун абилки у игрока. */
    public boolean hasAbilityCooldown(@NotNull Player player, @NotNull String abilityId) {
        var mgr = plugin.getAbilityManager();
        return mgr != null && mgr.hasCooldown(player, abilityId);
    }

    /** Выполнить абилку от имени игрока. */
    public void executeAbility(@NotNull Player player, @NotNull Ability ability) {
        var mgr = plugin.getAbilityManager();
        if (mgr != null) mgr.executeAbility(player, ability);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  САМОЦВЕТЫ (GEMS)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер самоцветов (может быть null). */
    public @Nullable GemManager getGemManager() {
        return plugin.getGemManager();
    }

    /** Получить самоцвет по ID. */
    public @Nullable Gem getGem(@NotNull String id) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.getGem(id) : null;
    }

    /** Все зарегистрированные самоцветы. */
    public @NotNull Collection<Gem> getAllGems() {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.getAll() : List.of();
    }

    /** Проверить, является ли предмет самоцветом. */
    public boolean isGem(@NotNull ItemStack item) {
        var mgr = plugin.getGemManager();
        return mgr != null && mgr.isGem(item);
    }

    /** Создать предмет самоцвета. */
    public @NotNull ItemStack createGemItem(@NotNull Gem gem) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.createGemItem(gem) : ItemStack.empty();
    }

    /** Выдать самоцвет игроку. */
    public void giveGem(@NotNull Player player, @NotNull String gemId, int amount) {
        var mgr = plugin.getGemManager();
        if (mgr != null) mgr.giveGem(player, gemId, amount);
    }

    /** Количество слотов для самоцветов на артефакте. */
    public int getSocketCount(@NotNull ItemStack artifactItem) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.getSocketCount(artifactItem) : 0;
    }

    /** Установить количество слотов для самоцветов. */
    public void setSocketCount(@NotNull ItemStack artifactItem, int count) {
        var mgr = plugin.getGemManager();
        if (mgr != null) mgr.setSocketCount(artifactItem, count);
    }

    /** Самоцветы, вставленные в артефакт (список ID). */
    public @NotNull List<String> getSocketedGems(@NotNull ItemStack artifactItem) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.getSocketedGems(artifactItem) : List.of();
    }

    /** Вставить самоцвет в артефакт (с вызовом GemSocketEvent). */
    public boolean socketGem(@NotNull Player player, @NotNull ItemStack artifactItem, @NotNull String gemId) {
        var mgr = plugin.getGemManager();
        return mgr != null && mgr.socketGem(player, artifactItem, gemId);
    }

    /** @deprecated Используйте {@link #socketGem(Player, ItemStack, String)} для получения событий. */
    @Deprecated
    public boolean socketGem(@NotNull ItemStack artifactItem, @NotNull String gemId) {
        var mgr = plugin.getGemManager();
        return mgr != null && mgr.socketGem(artifactItem, gemId);
    }

    /** Извлечь самоцвет из артефакта по индексу (с вызовом GemUnsocketEvent). */
    public @Nullable String unsocketGem(@NotNull Player player, @NotNull ItemStack artifactItem, int index) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.unsocketGem(player, artifactItem, index) : null;
    }

    /** @deprecated Используйте {@link #unsocketGem(Player, ItemStack, int)} для получения событий. */
    @Deprecated
    public @Nullable String unsocketGem(@NotNull ItemStack artifactItem, int index) {
        var mgr = plugin.getGemManager();
        return mgr != null ? mgr.unsocketGem(artifactItem, index) : null;
    }

    /** Принудительно применить эффекты самоцветов игроку. */
    public void applyGemEffects(@NotNull Player player) {
        var gemMgr = plugin.getGemManager();
        if (gemMgr == null) return;
        var sl = plugin.getSocketListener();
        if (sl != null) sl.applyAllGems(player);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  КАСТОМНЫЕ ПРЕДМЕТЫ
    // ═══════════════════════════════════════════════════════════════

    /** Получить реестр кастомных предметов. */
    public @NotNull CustomItemRegistry getCustomItemRegistry() {
        return plugin.getCustomItemRegistry();
    }

    /** Создать кастомный предмет по ID. */
    public @Nullable ItemStack createCustomItem(@NotNull String id) {
        return plugin.getCustomItemRegistry().create(id);
    }

    /** Создать кастомный предмет по ID с количеством. */
    public @Nullable ItemStack createCustomItem(@NotNull String id, int amount) {
        return plugin.getCustomItemRegistry().create(id, amount);
    }

    /** Проверить, является ли предмет кастомным. */
    public boolean isCustomItem(@NotNull ItemStack item) {
        return plugin.getCustomItemRegistry().isCustomItem(item);
    }

    /** Получить ID кастомного предмета из ItemStack. */
    public @Nullable String getCustomItemId(@NotNull ItemStack item) {
        return plugin.getCustomItemRegistry().getId(item);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ДОСТИЖЕНИЯ (ACHIEVEMENTS)
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер достижений (может быть null). */
    public @Nullable AchievementManager getAchievementManager() {
        return plugin.getAchievementManager();
    }

    /** Все достижения. */
    public @NotNull Collection<Achievement> getAllAchievements() {
        var mgr = plugin.getAchievementManager();
        return mgr != null ? mgr.getAll() : List.of();
    }

    /** Прогресс игрока по достижению. */
    public int getAchievementProgress(@NotNull Player player, @NotNull String achievementId) {
        var mgr = plugin.getAchievementManager();
        return mgr != null ? mgr.getProgress(player, achievementId) : 0;
    }

    /** Завершено ли достижение. */
    public boolean isAchievementCompleted(@NotNull Player player, @NotNull String achievementId) {
        var mgr = plugin.getAchievementManager();
        return mgr != null && mgr.isCompleted(player, achievementId);
    }

    /** Принудительно проверить достижения игрока. */
    public void checkAchievements(@NotNull Player player) {
        var mgr = plugin.getAchievementManager();
        if (mgr != null) mgr.check(player);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ЭЛИТНЫЕ МОБЫ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер элитных мобов (может быть null). */
    public @Nullable EliteMobManager getEliteMobManager() {
        return plugin.getEliteMobManager();
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  БАЗА ДАННЫХ
    // ═══════════════════════════════════════════════════════════════

    /** Получить менеджер БД (может быть null, если БД не настроена). */
    public @Nullable DatabaseManager getDatabaseManager() {
        return plugin.getDatabaseManager();
    }

    /** Выполнить SQL-запрос. */
    public void executeQuery(@NotNull String sql, Object... args) {
        if (plugin.getDatabaseManager() != null) {
            plugin.getDatabaseManager().execute(sql, args);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  КОНФИГИ
    // ═══════════════════════════════════════════════════════════════

    /** Получить конфиг-менеджер. */
    public @NotNull ConfigManager getConfigManager() {
        return plugin.getConfigManager();
    }

    /** Перезагрузить все конфиги. */
    public void reload() {
        plugin.getConfigManager().reloadAll();
    }

    /** Получить локализованную строку. */
    public @NotNull String getMessage(@NotNull String key, Object... args) {
        return plugin.msg(key, args);
    }

    /** Получить локализованную строку для игрока (с учётом его языка). */
    public @NotNull String getMessageFor(@NotNull Player player, @NotNull String key, Object... args) {
        return plugin.msgFor(player, key, args);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ItemBuilder — утилиты
    // ═══════════════════════════════════════════════════════════════

    /** Получить ItemBuilder для артефактов. */
    public @NotNull ItemBuilder getItemBuilder() {
        return plugin.getArtifactManager().getItemBuilder();
    }

    /** Получить ключ PDC по строке. */
    public @NotNull org.bukkit.NamespacedKey getPDCKey(@NotNull String key) {
        return ItemBuilder.getPDCKey(key);
    }

    // ═══════════════════════════════════════════════════════════════
    // ║  ПЛАГИН
    // ═══════════════════════════════════════════════════════════════

    /** Получить инстанс плагина. */
    public @NotNull WastelandArtifacts getPlugin() {
        return plugin;
    }

    /** Версия плагина. */
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }
}
