package com.yuseix300.dmzgenerations.age;


public enum LifeStage {
    CHILD(0),
    TEEN(13),
    YOUNG_ADULT(25),
    ADULT(40),
    ELDER(95);

    public final int minAge;

    LifeStage(int minAge) {
        this.minAge = minAge;
    }

    public static LifeStage fromAge(double years) {
        LifeStage result = CHILD;
        for (LifeStage stage : values()) {
            if (years >= stage.minAge) {
                result = stage;
            }
        }
        return result;
    }

    public String translationKey() {
        return "lifestage.dmzgenerations." + name().toLowerCase();
    }
}
