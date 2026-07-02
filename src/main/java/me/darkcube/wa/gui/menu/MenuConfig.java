package me.darkcube.wa.gui.menu;

import java.util.List;

public class MenuConfig {
    public final String id;
    public final String title;
    public final int rows;
    public final List<MenuItem> items;
    public final boolean enabled;

    public MenuConfig(String id, String title, int rows, List<MenuItem> items, boolean enabled) {
        this.id = id;
        this.title = title;
        this.rows = rows;
        this.items = items;
        this.enabled = enabled;
    }
}
