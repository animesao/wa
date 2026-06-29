package me.darkcube.wa.artifact;

import me.darkcube.wa.artifact.trigger.Trigger;
import me.darkcube.wa.artifact.trigger.TriggerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ArtifactRegistry {

    private final Map<String, Artifact> artifacts = new ConcurrentHashMap<>();
    private final Map<TriggerType, List<Trigger>> globalTriggers = new ConcurrentHashMap<>();

    public void register(@NotNull Artifact artifact) {
        artifacts.put(artifact.getId(), artifact);
    }

    public void unregister(@NotNull String id) {
        artifacts.remove(id);
    }

    public @Nullable Artifact get(@NotNull String id) {
        return artifacts.get(id);
    }

    public @NotNull List<Artifact> getAll() {
        return List.copyOf(artifacts.values());
    }

    public boolean exists(@NotNull String id) {
        return artifacts.containsKey(id);
    }

    public int size() {
        return artifacts.size();
    }

    public void clear() {
        artifacts.clear();
    }

    public void registerTrigger(@NotNull TriggerType type, @NotNull Trigger trigger) {
        globalTriggers.computeIfAbsent(type, k -> new ArrayList<>()).add(trigger);
    }

    public @NotNull List<Trigger> getTriggers(@NotNull TriggerType type) {
        return globalTriggers.getOrDefault(type, Collections.emptyList());
    }

    public @NotNull Map<TriggerType, List<Trigger>> getAllTriggers() {
        return Collections.unmodifiableMap(globalTriggers);
    }
}
