package com.yuseix300.dmzgenerations.events;

import com.dragonminez.common.events.DMZEvent;
import com.dragonminez.common.stats.StatsCapability;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeData;
import com.yuseix300.dmzgenerations.age.GenerationsConfig;
import com.yuseix300.dmzgenerations.age.LifeStage;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = DMZGenerations.MOD_ID)
public class StagePerksEvents {

    @SubscribeEvent
    public static void onTpGain(DMZEvent.TPGainEvent event) {
        LifeStage stage = stageOf(event.getPlayer());
        if (stage == null) return;

        double multiplier = GenerationsConfig.get().trainGain(stage);
        if ((stage == LifeStage.CHILD || stage == LifeStage.TEEN) && nearElder(event.getPlayer())) {
            multiplier += GenerationsConfig.get().elderMentorTrainBonus;
        }
        event.setTpGain((int) Math.round(event.getTpGain() * multiplier));
    }

    @SubscribeEvent
    public static void onEnergyRegen(DMZEvent.EnergyRegenEvent event) {
        LifeStage stage = stageOf(event.getPlayer());
        if (stage == LifeStage.TEEN) event.setAmount(event.getAmount() * GenerationsConfig.get().teenKiRegenMultiplier);
        else if (stage == LifeStage.ELDER) event.setAmount(event.getAmount() * GenerationsConfig.get().elderKiRegenMultiplier);
    }

    @SubscribeEvent
    public static void onStaminaRegen(DMZEvent.StaminaRegenEvent event) {
        LifeStage stage = stageOf(event.getPlayer());
        if (stage == LifeStage.TEEN) event.setAmount(event.getAmount() * GenerationsConfig.get().teenStaminaRegenMultiplier);
        else if (stage == LifeStage.YOUNG_ADULT) event.setAmount(event.getAmount() * GenerationsConfig.get().youngAdultStaminaRegenMultiplier);
    }

    @SubscribeEvent
    public static void onCritChance(DMZEvent.CritChanceEvent event) {
        if (stageOf(event.getPlayer()) == LifeStage.ADULT) {
            event.setChance(event.getChance() + GenerationsConfig.get().adultCritBonus);
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && stageOf(player) == LifeStage.CHILD) {
            event.setDamageMultiplier(event.getDamageMultiplier() * (float) GenerationsConfig.get().childFallDamageMultiplier);
        }
    }

    private static LifeStage stageOf(Player player) {
        boolean hasCharacter = player.getCapability(StatsCapability.INSTANCE)
                .map(stats -> stats.getStatus().isHasCreatedCharacter()).orElse(false);
        if (!hasCharacter) return null;
        return player.getCapability(AgeCapability.INSTANCE).map(AgeData::getStage).orElse(null);
    }

    private static boolean nearElder(Player player) {
        double radius = GenerationsConfig.get().elderMentorRadius;
        List<Player> elders = player.level().getEntitiesOfClass(Player.class,
                player.getBoundingBox().inflate(radius),
                other -> other != player && stageOf(other) == LifeStage.ELDER);
        return !elders.isEmpty();
    }
}
