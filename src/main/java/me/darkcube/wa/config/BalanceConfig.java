package me.darkcube.wa.config;

import java.util.*;

public class BalanceConfig {

    public StackingConfig stacking = new StackingConfig();
    public BagConfig bag = new BagConfig();
    public OffhandConfig offhand = new OffhandConfig();

    public static class StackingConfig {
        public boolean enabled = true;
        public int maxAmplifier = 4;
        public Map<String, PerEffectConfig> perEffect = new HashMap<>();
    }

    public static class PerEffectConfig {
        public int maxAmplifier = 4;
    }

    public static class BagConfig {
        public int maxSlots = 54;
        public boolean allowSameInBag = true;
        public int effectApplyDelay = 20;
    }

    public static class OffhandConfig {
        public boolean enabled = true;
        public boolean allowStacking = true;
    }
}
