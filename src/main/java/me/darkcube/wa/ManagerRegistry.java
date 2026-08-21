package me.darkcube.wa;

import me.darkcube.wa.altar.AltarManager;
import me.darkcube.wa.api.WastelandArtifactsAPI;
import me.darkcube.wa.artifact.ArtifactManager;
import me.darkcube.wa.artifact.ArtifactRegistry;
import me.darkcube.wa.artifact.component.ComponentRegistry;
import me.darkcube.wa.artifact.rarity.RarityManager;
import me.darkcube.wa.bag.ArtifactBagManager;
import me.darkcube.wa.config.ConfigManager;
import me.darkcube.wa.crafting.CraftingManager;
import me.darkcube.wa.database.DatabaseManager;
import me.darkcube.wa.dungeon.DungeonManager;
import me.darkcube.wa.feature.abilities.AbilityManager;
import me.darkcube.wa.feature.achievements.AchievementManager;
import me.darkcube.wa.feature.collection.CollectionManager;
import me.darkcube.wa.feature.elites.EliteMobManager;
import me.darkcube.wa.feature.sets.SetManager;
import me.darkcube.wa.feature.socket.GemManager;
import me.darkcube.wa.feature.socket.SocketListener;
import me.darkcube.wa.feature.upgrades.UpgradeManager;
import me.darkcube.wa.feature.xp.ArtifactXPManager;
import me.darkcube.wa.feature.fishing.FishingListener;
import me.darkcube.wa.gui.GUIConfig;
import me.darkcube.wa.gui.ChatInputManager;
import me.darkcube.wa.gui.menu.MenuManager;
import me.darkcube.wa.item.CustomItemRegistry;
import me.darkcube.wa.resourcepack.ResourcePackManager;
import me.darkcube.wa.schematic.SchematicManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Централизованное хранилище всех менеджеров плагина.
 * <p>
 * Заменяет 30+ индивидуальных полей в {@link WastelandArtifacts}.
 * Использует type-safe обёртку над {@code Map<Class<?>, Object>}.
 */
public class ManagerRegistry {

    private final Map<Class<?>, Object> managers = new ConcurrentHashMap<>();

    // ═══════════════════════════════════════════════════════════════
    // ║  Core managers (инициализируются в onEnable)
    // ═══════════════════════════════════════════════════════════════

    public void register(@NotNull Class<? extends Object> type, @NotNull Object manager) {
        managers.put(type, manager);
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T get(@NotNull Class<T> type) {
        return (T) managers.get(type);
    }

    public <T> boolean isRegistered(@NotNull Class<T> type) {
        return managers.containsKey(type);
    }

    // ─── Typed accessors ───

    public @NotNull ConfigManager configManager() {
        return Objects.requireNonNull(get(ConfigManager.class), "ConfigManager not registered");
    }

    public @NotNull RarityManager rarityManager() {
        return Objects.requireNonNull(get(RarityManager.class), "RarityManager not registered");
    }

    public @NotNull ArtifactRegistry artifactRegistry() {
        return Objects.requireNonNull(get(ArtifactRegistry.class), "ArtifactRegistry not registered");
    }

    public @NotNull ArtifactManager artifactManager() {
        return Objects.requireNonNull(get(ArtifactManager.class), "ArtifactManager not registered");
    }

    public @NotNull ComponentRegistry componentRegistry() {
        return Objects.requireNonNull(get(ComponentRegistry.class), "ComponentRegistry not registered");
    }

    public @NotNull AltarManager altarManager() {
        return Objects.requireNonNull(get(AltarManager.class), "AltarManager not registered");
    }

    public @NotNull DungeonManager dungeonManager() {
        return Objects.requireNonNull(get(DungeonManager.class), "DungeonManager not registered");
    }

    public @NotNull CraftingManager craftingManager() {
        return Objects.requireNonNull(get(CraftingManager.class), "CraftingManager not registered");
    }

    public @NotNull SchematicManager schematicManager() {
        return Objects.requireNonNull(get(SchematicManager.class), "SchematicManager not registered");
    }

    public @NotNull ResourcePackManager resourcePackManager() {
        return Objects.requireNonNull(get(ResourcePackManager.class), "ResourcePackManager not registered");
    }

    public @NotNull ChatInputManager chatInputManager() {
        return Objects.requireNonNull(get(ChatInputManager.class), "ChatInputManager not registered");
    }

    public @NotNull CustomItemRegistry customItemRegistry() {
        return Objects.requireNonNull(get(CustomItemRegistry.class), "CustomItemRegistry not registered");
    }

    public @NotNull ArtifactBagManager artifactBagManager() {
        return Objects.requireNonNull(get(ArtifactBagManager.class), "ArtifactBagManager not registered");
    }

    public @NotNull WastelandArtifactsAPI api() {
        return Objects.requireNonNull(get(WastelandArtifactsAPI.class), "API not registered");
    }

    public @NotNull GUIConfig guiConfig() {
        return Objects.requireNonNull(get(GUIConfig.class), "GUIConfig not registered");
    }

    public @NotNull MenuManager menuManager() {
        return Objects.requireNonNull(get(MenuManager.class), "MenuManager not registered");
    }

    // ─── Nullable feature managers ───

    public @Nullable DatabaseManager databaseManager() {
        return get(DatabaseManager.class);
    }

    public @Nullable FeatureManager featureManager() {
        return get(FeatureManager.class);
    }

    public @Nullable CollectionManager collectionManager() {
        return get(CollectionManager.class);
    }

    public @Nullable SetManager setManager() {
        return get(SetManager.class);
    }

    public @Nullable AbilityManager abilityManager() {
        return get(AbilityManager.class);
    }

    public @Nullable UpgradeManager upgradeManager() {
        return get(UpgradeManager.class);
    }

    public @Nullable FishingListener fishingListener() {
        return get(FishingListener.class);
    }

    public @Nullable EliteMobManager eliteMobManager() {
        return get(EliteMobManager.class);
    }

    public @Nullable ArtifactXPManager artifactXPManager() {
        return get(ArtifactXPManager.class);
    }

    public @Nullable AchievementManager achievementManager() {
        return get(AchievementManager.class);
    }

    public @Nullable GemManager gemManager() {
        return get(GemManager.class);
    }

    public @Nullable SocketListener socketListener() {
        return get(SocketListener.class);
    }
}
