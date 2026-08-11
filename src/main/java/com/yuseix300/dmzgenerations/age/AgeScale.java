package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.stats.StatsCapability;
import net.minecraft.world.entity.player.Player;

public final class AgeScale {

    private AgeScale() {}

    public static float forPlayer(Player player) {
        if (!GenerationsConfig.get().modelScalingEnabled) return 1.0f;

        if (player.level().isClientSide) {
            return com.yuseix300.dmzgenerations.client.ClientAgeData.scaleOf(player.getId());
        }

        boolean hasCharacter = player.getCapability(StatsCapability.INSTANCE)
                .map(stats -> stats.getStatus().isHasCreatedCharacter()).orElse(false);
        if (!hasCharacter) return 1.0f;
        return player.getCapability(AgeCapability.INSTANCE)
                .map(age -> GenerationsConfig.get().modelScaleForAge(age.getAgeYears())).orElse(1.0f);
    }
}
