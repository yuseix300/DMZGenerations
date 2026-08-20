package com.yuseix300.dmzgenerations.mixin;

import com.dragonminez.common.stats.StatsData;
import com.yuseix300.dmzgenerations.age.AgeBonuses;
import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeSettings;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StatsData.class, remap = false)
public class StatsResetMixin {

    @Inject(method = "resetPlayerProgress", at = @At("TAIL"), remap = false)
    private void dmzgenerations$resetGeneration(ServerPlayer player, Integer keepPercentage,
                                                boolean keepSkills, boolean forceSaiyanTail, CallbackInfo ci) {
        if (keepPercentage != null && keepPercentage > 0) return;
        if (com.yuseix300.dmzgenerations.age.Rebirth.IN_PROGRESS) return;

        player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> {
            age.setGeneration(1);
            age.setAgeYears(AgeSettings.defaultStartAge());
        });
        AgeBonuses.applyAll(player);
        player.refreshDimensions();
        GenNetwork.syncTo(player);
    }
}
