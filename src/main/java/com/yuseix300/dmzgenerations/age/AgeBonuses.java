package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.network.NetworkHandler;
import com.dragonminez.common.network.S2C.StatsSyncS2C;
import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.character.BonusStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class AgeBonuses {

    private AgeBonuses() {}

    public static final String GENERATION_BONUS = "dmzgenerations_generation";
    public static final String STAGE_BONUS = "dmzgenerations_stage";

    private static final String[] STATS = {"STR", "SKP", "DEF", "STM", "VIT", "PWR", "ENE"};

    private static final UUID SPEED_MODIFIER_ID = UUID.fromString("d3a7f0e2-1b4c-4e8a-9f2d-6c5b4a39e8d1");

    public static void applyAll(ServerPlayer player) {
        player.getCapability(StatsCapability.INSTANCE).ifPresent(data ->
                player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> {
                    BonusStats bonus = data.getBonusStats();
                    setMultiplier(bonus, GENERATION_BONUS, AgeSettings.generationMultiplier(age.getGeneration()));
                    setMultiplier(bonus, STAGE_BONUS, AgeSettings.stageMultiplier(age.getStage()));
                    applySpeed(player, age.getStage());
                    NetworkHandler.sendToTrackingEntityAndSelf(new StatsSyncS2C(player), player);
                }));
    }

    private static void applySpeed(ServerPlayer player, LifeStage stage) {
        AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        attr.removeModifier(SPEED_MODIFIER_ID);
        double multiplier = GenerationsConfig.get().speedMultiplier(stage);
        if (multiplier != 1.0) {
            attr.addTransientModifier(new AttributeModifier(SPEED_MODIFIER_ID, "dmzgen_stage_speed",
                    multiplier - 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void setMultiplier(BonusStats bonus, String name, double multiplier) {
        if (multiplier != 1.0) {
            for (String stat : STATS) bonus.addBonus(stat, name, "*", multiplier, true);
        } else {
            for (String stat : STATS) bonus.removeBonus(stat, name);
        }
    }
}
