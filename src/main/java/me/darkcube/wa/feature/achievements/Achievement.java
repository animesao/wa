package me.darkcube.wa.feature.achievements;

import java.util.List;

public class Achievement {
    private final String id;
    private final String displayName;
    private final String description;
    private final String icon;
    private final String category;
    private final String type;
    private final int target;
    private final List<String> rewards;
    private final String dataKey;

    public Achievement(String id, String displayName, String description, String icon,
                       String category, String type, int target, List<String> rewards, String dataKey) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.type = type;
        this.target = target;
        this.rewards = rewards;
        this.dataKey = dataKey;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getCategory() { return category; }
    public String getType() { return type; }
    public int getTarget() { return target; }
    public List<String> getRewards() { return rewards; }
    public String getDataKey() { return dataKey; }
}
