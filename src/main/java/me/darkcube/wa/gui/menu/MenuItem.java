package me.darkcube.wa.gui.menu;

import java.util.List;

public class MenuItem {
    public final String id;
    public final String material;
    public final int customModelData;
    public final String displayName;
    public final List<String> lore;
    public final List<Integer> slots;
    public final List<String> actions;
    public final boolean fill;
    public final boolean dynamic;

    public MenuItem(String id, String material, int customModelData, String displayName,
                    List<String> lore, List<Integer> slots, List<String> actions,
                    boolean fill, boolean dynamic) {
        this.id = id;
        this.material = material;
        this.customModelData = customModelData;
        this.displayName = displayName;
        this.lore = lore;
        this.slots = slots;
        this.actions = actions;
        this.fill = fill;
        this.dynamic = dynamic;
    }
}
