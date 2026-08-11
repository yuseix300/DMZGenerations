package com.yuseix300.dmzgenerations.age;

import net.minecraft.nbt.CompoundTag;


public class AgeData {

    public static final long UNSET_DAY = -1L;

    private double ageYears = AgeSettings.defaultStartAge();
    private long lastGameDay = UNSET_DAY;

    private int generation = 1;

    public double getAgeYears() {
        return ageYears;
    }

    public void setAgeYears(double ageYears) {
        this.ageYears = Math.max(0.0, ageYears);
    }

    public void addAgeYears(double delta) {
        setAgeYears(this.ageYears + delta);
    }

    public long getLastGameDay() {
        return lastGameDay;
    }

    public void setLastGameDay(long lastGameDay) {
        this.lastGameDay = lastGameDay;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = Math.max(1, Math.min(generation, AgeSettings.maxGeneration()));
    }

    public double getGenerationMultiplier() {
        return AgeSettings.generationMultiplier(generation);
    }

    public LifeStage getStage() {
        return LifeStage.fromAge(ageYears);
    }

    public void copyFrom(AgeData other) {
        this.ageYears = other.ageYears;
        this.lastGameDay = other.lastGameDay;
        this.generation = other.generation;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("ageYears", ageYears);
        tag.putLong("lastGameDay", lastGameDay);
        tag.putInt("generation", generation);
        return tag;
    }

    public void load(CompoundTag tag) {
        this.ageYears = tag.contains("ageYears") ? tag.getDouble("ageYears") : AgeSettings.defaultStartAge();
        this.lastGameDay = tag.contains("lastGameDay") ? tag.getLong("lastGameDay") : UNSET_DAY;
        this.generation = tag.contains("generation") ? tag.getInt("generation") : 1;
    }
}
