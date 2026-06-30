package me.darkcube.wa.altar;

import me.darkcube.wa.WastelandArtifacts;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AltarHologram {

    private final Map<String, TextDisplay> displays = new HashMap<>();
    private final MiniMessage mm = MiniMessage.miniMessage();
    private final WastelandArtifacts plugin;

    public AltarHologram(WastelandArtifacts plugin) {
        this.plugin = plugin;
    }

    public void update(@NotNull String altarKey, @NotNull Location activatorLoc,
                        @Nullable AltarRecipe recipe, @NotNull ItemStack[] slots,
                        boolean hasCatalyst) {
        remove(altarKey);
        if (recipe == null) return;

        Location hololoc = activatorLoc.clone().add(0.5, 3.5, 0.5);

        var art = plugin.getArtifactRegistry().get(recipe.getResultId());
        String artName = art != null ? art.getDisplayName() : recipe.getResultId();
        StringBuilder sb = new StringBuilder();
        sb.append("<gold>═══ ").append(artName).append(" ═══\n\n");

        boolean allReady = true;
        for (var ing : recipe.getIngredients()) {
            int slot = ing.getSlot();
            ItemStack slotItem = (slot >= 0 && slot < slots.length) ? slots[slot] : null;
            boolean hasItem = slotItem != null && slotItem.getType() == ing.getType()
                    && slotItem.getAmount() >= ing.getAmount();
            if (!hasItem) allReady = false;

            sb.append(hasItem ? "<green>✅" : "<red>❌");
            sb.append(" <white>").append(getIngredientName(ing));
            int current = (slotItem != null && slotItem.getType() == ing.getType())
                    ? slotItem.getAmount() : 0;
            sb.append(" <gray>x").append(ing.getAmount())
              .append(" <dark_gray>(").append(current).append("/").append(ing.getAmount()).append(")")
              .append(" <gray>[слот ").append(slot).append("]\n");
        }

        if (recipe.getCatalyst() != null) {
            String catName = getCatalystName(recipe.getCatalyst());
            if (hasCatalyst) {
                sb.append("<green>✅ <white>").append(catName).append(" <gray>(катализатор)\n");
            } else {
                allReady = false;
                sb.append("<red>❌ <white>").append(catName)
                  .append(" <gray>(катализатор)\n");
            }
        }

        sb.append("\n");
        if (allReady) {
            sb.append("<green>⚡ ВСЁ ГОТОВО! АВТО-КРАФТ...");
        } else {
            sb.append("<gray>Брось недостающие предметы на алтарь");
        }

        try {
            TextDisplay display = activatorLoc.getWorld().spawn(hololoc, TextDisplay.class, td -> {
                td.text(mm.deserialize(sb.toString()));
                td.setBackgroundColor(Color.fromARGB(80, 0, 0, 0));
                td.setBillboard(Display.Billboard.CENTER);
                td.setSeeThrough(true);
                td.setShadowed(true);
                td.setLineWidth(500);
                td.setPersistent(false);
                td.setGravity(false);
                td.setViewRange(2.0f);
            });
            displays.put(altarKey, display);
        } catch (Exception ignored) {}
    }

    public void remove(@NotNull String altarKey) {
        TextDisplay old = displays.remove(altarKey);
        if (old != null && old.isValid()) old.remove();
    }

    public void removeAll() {
        for (TextDisplay td : displays.values()) {
            if (td.isValid()) td.remove();
        }
        displays.clear();
    }

    private String getIngredientName(AltarRecipe.Ingredient ing) {
        if (ing.getTemplate() != null && ing.getTemplate().hasItemMeta()
                && ing.getTemplate().getItemMeta().hasDisplayName()) {
            return mm.serialize(ing.getTemplate().getItemMeta().displayName());
        }
        for (var def : plugin.getCustomItemRegistry().getAll().entrySet()) {
            if (def.getValue().material == ing.getType()
                    && def.getValue().name != null && !def.getValue().name.isEmpty()) {
                return def.getValue().name;
            }
        }
        return me.darkcube.wa.util.ItemNameUtil.getRussianName(ing.getType());
    }

    private String getCatalystName(AltarRecipe.CatalystConfig cat) {
        if (cat.getTemplate() != null && cat.getTemplate().hasItemMeta()
                && cat.getTemplate().getItemMeta().hasDisplayName()) {
            return mm.serialize(cat.getTemplate().getItemMeta().displayName());
        }
        for (var def : plugin.getCustomItemRegistry().getAll().entrySet()) {
            if (def.getValue().material == cat.getItem()
                    && def.getValue().name != null && !def.getValue().name.isEmpty()) {
                return def.getValue().name;
            }
        }
        return me.darkcube.wa.util.ItemNameUtil.getRussianName(cat.getItem());
    }
}
