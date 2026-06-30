package me.darkcube.wa.altar;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class AltarData {

    private final String world;
    private final int x, y, z;
    private final ItemStack[] slots;
    private long lastActivity;

    public AltarData(Location loc, int maxSlots) {
        this.world = loc.getWorld().getName();
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.slots = new ItemStack[maxSlots];
        this.lastActivity = System.currentTimeMillis();
    }

    public AltarData(String world, int x, int y, int z, int maxSlots) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.slots = new ItemStack[maxSlots];
        this.lastActivity = System.currentTimeMillis();
    }

    public String getLocationKey() {
        return world + ":" + x + ":" + y + ":" + z;
    }

    public String getWorld() { return world; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public long getLastActivity() { return lastActivity; }
    public void setLastActivity(long t) { this.lastActivity = t; }

    public ItemStack getSlot(int slot) {
        if (slot < 0 || slot >= slots.length) return null;
        return slots[slot];
    }

    public void setSlot(int slot, ItemStack item) {
        if (slot < 0 || slot >= slots.length) return;
        slots[slot] = (item != null && item.getType().isAir()) ? null : item;
        lastActivity = System.currentTimeMillis();
    }

    public ItemStack removeSlot(int slot) {
        ItemStack old = slots[slot];
        slots[slot] = null;
        lastActivity = System.currentTimeMillis();
        return old;
    }

    public void clear() {
        Arrays.fill(slots, null);
        lastActivity = System.currentTimeMillis();
    }

    public int countItems() {
        int c = 0;
        for (ItemStack s : slots) if (s != null) c++;
        return c;
    }

    public ItemStack[] getAllSlots() {
        return Arrays.copyOf(slots, slots.length);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("world", world);
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("time", lastActivity);
        List<String> items = new ArrayList<>();
        for (ItemStack slot : slots) {
            items.add(itemToBase64(slot));
        }
        map.put("slots", items);
        return map;
    }

    @SuppressWarnings("unchecked")
    public static AltarData deserialize(Map<String, Object> map, int maxSlots) {
        if (map == null) return null;
        Object worldObj = map.get("world");
        Object xObj = map.get("x");
        Object yObj = map.get("y");
        Object zObj = map.get("z");
        if (!(worldObj instanceof String) || !(xObj instanceof Number)
                || !(yObj instanceof Number) || !(zObj instanceof Number)) {
            return null;
        }
        AltarData data = new AltarData(
                (String) worldObj,
                ((Number) xObj).intValue(),
                ((Number) yObj).intValue(),
                ((Number) zObj).intValue(),
                maxSlots
        );
        data.lastActivity = map.containsKey("time") ? ((Number) map.get("time")).longValue() : System.currentTimeMillis();
        Object slotsObj = map.get("slots");
        if (slotsObj instanceof List) {
            List<String> items = (List<String>) slotsObj;
            for (int i = 0; i < Math.min(items.size(), data.slots.length); i++) {
                data.slots[i] = itemFromBase64(items.get(i));
            }
        }
        return data;
    }

    private static String itemToBase64(ItemStack item) {
        if (item == null) return "";
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(bos);
            oos.writeObject(item);
            oos.close();
            return Base64Coder.encodeLines(bos.toByteArray());
        } catch (IOException e) {
            return "";
        }
    }

    private static ItemStack itemFromBase64(String data) {
        if (data == null || data.isEmpty()) return null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream ois = new BukkitObjectInputStream(bis);
            ItemStack item = (ItemStack) ois.readObject();
            ois.close();
            return item;
        } catch (Exception e) {
            return null;
        }
    }
}
