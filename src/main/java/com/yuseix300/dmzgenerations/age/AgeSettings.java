package com.yuseix300.dmzgenerations.age;


public final class AgeSettings {

    private AgeSettings() {}

    public static int daysPerYear() {
        return Math.max(1, GenerationsConfig.get().daysPerYear);
    }

    public static int defaultStartAge() {
        return GenerationsConfig.get().defaultStartAge;
    }

    public static int maxGeneration() {
        return GenerationsConfig.get().maxGeneration;
    }

    public static double bonusPerGeneration() {
        return GenerationsConfig.get().bonusPerGeneration;
    }

    public static double generationMultiplier(int generation) {
        return GenerationsConfig.get().generationMultiplier(generation);
    }

    public static double stageMultiplier(LifeStage stage) {
        return GenerationsConfig.get().stageMultiplier(stage);
    }

    public static boolean allowRaceChangeOnRebirth() {
        return GenerationsConfig.get().allowRaceChangeOnRebirth;
    }

    public static int requiredLevelForGeneration(int targetGeneration) {
        return Math.max(0, GenerationsConfig.get().levelPerGeneration) * Math.max(0, targetGeneration - 1);
    }
}
