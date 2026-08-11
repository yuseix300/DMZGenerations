package com.yuseix300.dmzgenerations.client;

import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.age.LifeStage;

public final class CreationAgePreview {

    private CreationAgePreview() {}

    public static boolean active = false;
    public static int stageIndex = 0;

    public static double previewAge() {
        LifeStage[] stages = LifeStage.values();
        LifeStage stage = stages[Math.max(0, Math.min(stageIndex, stages.length - 1))];
        return stage == LifeStage.CHILD ? GenerationsConfig.get().defaultStartAge : stage.minAge;
    }
}
