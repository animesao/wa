package me.darkcube.wa.feature.sets;

import java.util.List;

public class ArtifactSet {
    private final String id;
    private final String displayName;
    private final List<String> artifacts;
    private final List<SetBonus> bonuses;

    public ArtifactSet(String id, String displayName, List<String> artifacts, List<SetBonus> bonuses) {
        this.id = id;
        this.displayName = displayName;
        this.artifacts = artifacts;
        this.bonuses = bonuses;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public List<String> getArtifacts() { return artifacts; }
    public List<SetBonus> getBonuses() { return bonuses; }

    public static class SetBonus {
        private final int piecesRequired;
        private final String description;
        private final List<String> effects;

        public SetBonus(int piecesRequired, String description, List<String> effects) {
            this.piecesRequired = piecesRequired;
            this.description = description;
            this.effects = effects;
        }

        public int getPiecesRequired() { return piecesRequired; }
        public String getDescription() { return description; }
        public List<String> getEffects() { return effects; }
    }
}
