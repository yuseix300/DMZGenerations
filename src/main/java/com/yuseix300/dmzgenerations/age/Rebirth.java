package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.hair.CustomHair;
import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.OpenRecustomizeS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.dragonminez.common.stats.character.Character;
import com.dragonminez.common.util.TransformationsHelper;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class Rebirth {

    private Rebirth() {}

    public static volatile boolean IN_PROGRESS = false;

    public enum Result {
        SUCCESS,
        NO_CHARACTER,
        NOT_ELDER,
        MAX_GENERATION,
        LEVEL_TOO_LOW
    }

    public static int nextGeneration(AgeData age) {
        return age.getGeneration() + 1;
    }

    public static Result check(ServerPlayer player) {
        Optional<StatsData> stats = player.getCapability(StatsCapability.INSTANCE).resolve();
        Optional<AgeData> ageOpt = player.getCapability(AgeCapability.INSTANCE).resolve();
        if (stats.isEmpty() || ageOpt.isEmpty()) return Result.NO_CHARACTER;

        StatsData data = stats.get();
        AgeData age = ageOpt.get();
        if (!data.getStatus().isHasCreatedCharacter()) return Result.NO_CHARACTER;
        if (age.getStage() != LifeStage.ELDER) return Result.NOT_ELDER;
        if (age.getGeneration() >= AgeSettings.maxGeneration()) return Result.MAX_GENERATION;
        if (data.getLevel() < AgeSettings.requiredLevelForGeneration(nextGeneration(age))) return Result.LEVEL_TOO_LOW;
        return Result.SUCCESS;
    }

    public static Result attempt(ServerPlayer player) {
        Result eligibility = check(player);
        if (eligibility != Result.SUCCESS) return eligibility;

        player.getCapability(StatsCapability.INSTANCE).ifPresent(data ->
                player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> {
                    int targetGen = nextGeneration(age);
                    boolean keepRace = !AgeSettings.allowRaceChangeOnRebirth();

                    // Snapshot the appearance so we can rebuild the SAME character after the reset.
                    Character c = data.getCharacter();
                    String race = c.getRaceName();
                    String clazz = c.getCharacterClass();
                    String gender = c.getGender();
                    int hairId = c.getHairId();
                    CustomHair hair = c.getHairBase();
                    int bodyType = c.getBodyType();
                    int eyesType = c.getEyesType();
                    int noseType = c.getNoseType();
                    int mouthType = c.getMouthType();
                    int tattooType = c.getTattooType();
                    float boobScale = c.getBoobScale();
                    String headBone = c.getActiveHeadBone();
                    String hairColor = c.getHairColor();
                    String bodyColor = c.getBodyColor();
                    String bodyColor2 = c.getBodyColor2();
                    String bodyColor3 = c.getBodyColor3();
                    String eye1 = c.getEye1Color();
                    String eye2 = c.getEye2Color();
                    String aura = c.getAuraColor();

                    IN_PROGRESS = true;
                    try {
                        data.resetPlayerProgress(player, null, false, false);
                    } finally {
                        IN_PROGRESS = false;
                    }

                    age.setGeneration(targetGen);
                    age.setAgeYears(AgeSettings.defaultStartAge());

                    if (keepRace) {
                        // Rebuild the same character in place → hasCreatedCharacter stays true, so the
                        // race-selection creation flow never opens and the race cannot be changed.
                        data.initializeWithRaceAndClass(race, clazz, gender, hairId, hair,
                                bodyType, eyesType, noseType, mouthType, tattooType, boobScale,
                                headBone, hairColor, bodyColor, bodyColor2, bodyColor3, eye1, eye2, aura);
                        c.setSelectedFormGroup(TransformationsHelper.getGroupWithFirstAvailableForm(data));
                        c.setSelectedForm(TransformationsHelper.getFirstAvailableForm(data));
                        c.setSelectedStackFormGroup(TransformationsHelper.getGroupWithFirstAvailableStackForm(data));
                        c.setSelectedStackForm(TransformationsHelper.getFirstAvailableStackForm(data));
                        player.setHealth(player.getMaxHealth());
                    }
                    // else: hasCreatedCharacter is left false → the full creation (with race selection)
                    // opens so the player re-picks their race (opt-in via allowRaceChangeOnRebirth).

                    AgeBonuses.applyAll(player); // also fires StatsSyncS2C for the reset state
                    player.refreshDimensions();
                    GenNetwork.syncTo(player);

                    // Let the player re-customize their APPEARANCE (hair, colors, tattoos, ...) on rebirth.
                    // The recustomize screen has no race tab, so the race stays locked to the rebuilt one.
                    if (keepRace) {
                        NetworkHandler.sendToPlayer(new OpenRecustomizeS2C(), player);
                    }
                }));
        return Result.SUCCESS;
    }
}
