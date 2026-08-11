package com.yuseix300.dmzgenerations.client;

import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.age.LifeStage;
import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

public final class ClientAgeData {

    private ClientAgeData() {}

    private record Entry(double ageYears, int generation, double multiplier) {}

    private static final Map<Integer, Entry> ENTRIES = new HashMap<>();

    public static void set(int entityId, double ageYears, int generation, double multiplier) {
        ENTRIES.put(entityId, new Entry(ageYears, generation, multiplier));
    }

    public static void remove(int entityId) {
        ENTRIES.remove(entityId);
    }

    public static void clear() {
        ENTRIES.clear();
    }

    private static Entry local() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player == null ? null : ENTRIES.get(mc.player.getId());
    }

    public static boolean hasData() {
        return local() != null;
    }

    public static int getAgeYears() {
        Entry e = local();
        return e == null ? 0 : (int) Math.floor(e.ageYears);
    }

    public static int getGeneration() {
        Entry e = local();
        return e == null ? 1 : e.generation;
    }

    public static double getGenerationMultiplier() {
        Entry e = local();
        return e == null ? 1.0 : e.multiplier;
    }

    public static LifeStage getStage() {
        Entry e = local();
        return LifeStage.fromAge(e == null ? 0 : e.ageYears);
    }


    public static boolean has(int entityId) {
        return ENTRIES.containsKey(entityId);
    }

    public static float scaleOf(int entityId) {
        Entry e = ENTRIES.get(entityId);
        if (e == null) return 1.0f;
        return GenerationsConfig.get().modelScaleForAge(e.ageYears);
    }

    public static LifeStage stageOf(int entityId) {
        Entry e = ENTRIES.get(entityId);
        return e == null ? null : LifeStage.fromAge(e.ageYears);
    }

    public static double ageOf(int entityId) {
        Entry e = ENTRIES.get(entityId);
        return e == null ? -1.0 : e.ageYears;
    }
}
