package me.darkcube.wa.artifact.trigger;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Trigger {

    void execute(@NotNull TriggerContext ctx);

    static Trigger of(@NotNull TriggerAction action) {
        return action::execute;
    }

    @FunctionalInterface
    interface TriggerAction {
        void execute(@NotNull TriggerContext ctx);
    }
}
