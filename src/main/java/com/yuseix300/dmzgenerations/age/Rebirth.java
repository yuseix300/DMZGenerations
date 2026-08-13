package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.OpenRecustomizeS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class Rebirth {

    private Rebirth() {}

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

                    data.resetPlayerProgress(player, null, false, false);

                    age.setGeneration(targetGen);
                    age.setAgeYears(AgeSettings.defaultStartAge());
                    AgeBonuses.applyAll(player);
                    player.refreshDimensions();
                    GenNetwork.syncTo(player);

                    if (AgeSettings.allowRaceChangeOnRebirth()) {
                        NetworkHandler.sendToPlayer(new OpenRecustomizeS2C(), player);
                    }
                }));
        return Result.SUCCESS;
    }
}
