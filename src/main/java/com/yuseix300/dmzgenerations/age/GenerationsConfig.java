package com.yuseix300.dmzgenerations.age;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yuseix300.dmzgenerations.DMZGenerations;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;


public class GenerationsConfig {

    public int configVersion = 1;
    public int daysPerYear = 7;
    public int defaultStartAge = 1;
    public double bonusPerGeneration = 0.2;
    public int maxGeneration = 5;
    public int levelPerGeneration = 2000;
    public boolean allowRaceChangeOnRebirth = false;

    public double statMultChild = 0.75;
    public double statMultTeen = 0.85;
    public double statMultYoungAdult = 1.0;
    public double statMultAdult = 1.2;
    public double statMultElder = 1.0;

    public double stageMultiplier(LifeStage stage) {
        return switch (stage) {
            case CHILD -> statMultChild;
            case TEEN -> statMultTeen;
            case YOUNG_ADULT -> statMultYoungAdult;
            case ADULT -> statMultAdult;
            case ELDER -> statMultElder;
        };
    }

    public double trainGainChild = 1.6;
    public double trainGainTeen = 1.35;
    public double trainGainYoungAdult = 1.1;
    public double trainGainAdult = 1.0;
    public double trainGainElder = 0.85;
    public double childSpeedMultiplier = 1.30;
    public double childFallDamageMultiplier = 0.5;
    public double teenKiRegenMultiplier = 1.5;
    public double teenStaminaRegenMultiplier = 1.5;
    public double youngAdultStaminaRegenMultiplier = 1.3;
    public double elderKiRegenMultiplier = 1.5;
    public double adultCritBonus = 0.15;
    public double elderMentorTrainBonus = 0.25;
    public double elderMentorRadius = 8.0;

    public double trainGain(LifeStage stage) {
        return switch (stage) {
            case CHILD -> trainGainChild;
            case TEEN -> trainGainTeen;
            case YOUNG_ADULT -> trainGainYoungAdult;
            case ADULT -> trainGainAdult;
            case ELDER -> trainGainElder;
        };
    }

    public double speedMultiplier(LifeStage stage) {
        return stage == LifeStage.CHILD ? childSpeedMultiplier : 1.0;
    }

    public boolean modelScalingEnabled = true;
    public double modelScaleChild = 0.65;
    public double modelScaleTeen = 0.80;
    public double modelScaleYoungAdult = 1.0;
    public double modelScaleAdult = 1.0;
    public double modelScaleElder = 0.90;

    public double chibiHeadScale = 0.9;
    public double chibiFullUntilAge = 8.0;

    public float chibiStrength(double age) {
        double fadeEnd = LifeStage.TEEN.minAge;
        if (age <= chibiFullUntilAge) return 1.0f;
        if (age >= fadeEnd) return 0.0f;
        return 1.0f - (float) ((age - chibiFullUntilAge) / (fadeEnd - chibiFullUntilAge));
    }
    public double modelScale(LifeStage stage) {
        return switch (stage) {
            case CHILD -> modelScaleChild;
            case TEEN -> modelScaleTeen;
            case YOUNG_ADULT -> modelScaleYoungAdult;
            case ADULT -> modelScaleAdult;
            case ELDER -> modelScaleElder;
        };
    }

    public float modelScaleForAge(double ageYears) {
        double[] ages = {LifeStage.CHILD.minAge, LifeStage.TEEN.minAge, LifeStage.YOUNG_ADULT.minAge,
                LifeStage.ADULT.minAge, LifeStage.ELDER.minAge};
        double[] scales = {modelScaleChild, modelScaleTeen, modelScaleYoungAdult, modelScaleAdult, modelScaleElder};

        if (ageYears <= ages[0]) return (float) scales[0];
        for (int i = 1; i < ages.length; i++) {
            if (ageYears < ages[i]) {
                double t = (ageYears - ages[i - 1]) / (ages[i] - ages[i - 1]);
                return (float) (scales[i - 1] + (scales[i] - scales[i - 1]) * t);
            }
        }
        return (float) scales[scales.length - 1];
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static GenerationsConfig INSTANCE = new GenerationsConfig();

    public static GenerationsConfig get() {
        return INSTANCE;
    }

    public double generationMultiplier(int generation) {
        int cap = Math.max(1, maxGeneration);
        int g = Math.max(1, Math.min(generation, cap));
        return 1.0 + (g - 1) * bonusPerGeneration;
    }

    public static void load() {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve(DMZGenerations.MOD_ID);
            Files.createDirectories(dir);
            Path file = dir.resolve("generations.json");

            if (Files.exists(file)) {
                try (Reader reader = Files.newBufferedReader(file)) {
                    GenerationsConfig loaded = GSON.fromJson(reader, GenerationsConfig.class);
                    if (loaded != null) INSTANCE = loaded;
                }
            }
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception e) {
            DMZGenerations.LOGGER.error("[{}] Failed to load generations.json, using defaults.", DMZGenerations.MOD_ID, e);
        }
    }
}
