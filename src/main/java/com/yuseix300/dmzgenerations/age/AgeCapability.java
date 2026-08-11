package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.stats.StatsCapability;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = DMZGenerations.MOD_ID)
public class AgeCapability {

    public static final Capability<AgeData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    private static final long TICKS_PER_DAY = 24000L;

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(AgeData.class);
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            if (!player.getCapability(INSTANCE).isPresent()) {
                event.addCapability(AgeProvider.ID, new AgeProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().reviveCaps();
        event.getOriginal().getCapability(INSTANCE).ifPresent(oldData ->
                event.getEntity().getCapability(INSTANCE).ifPresent(newData -> newData.copyFrom(oldData)));
        event.getOriginal().invalidateCaps();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            long today = currentDay(serverPlayer);
            serverPlayer.getCapability(INSTANCE).ifPresent(age -> {
                if (today >= 0) age.setLastGameDay(today);
                // Re-apply so the generation + life-stage bonuses match the stored state + config.
                AgeBonuses.applyAll(serverPlayer);
            });
            GenNetwork.syncTo(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) GenNetwork.syncTo(serverPlayer);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) GenNetwork.syncTo(serverPlayer);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && event.getEntity() instanceof ServerPlayer observer) {
            GenNetwork.syncToObserver(target, observer);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;
        serverPlayer.getCapability(INSTANCE).ifPresent(age -> tickAge(serverPlayer, age));
    }

    private static void tickAge(ServerPlayer player, AgeData age) {
        if (!hasDmzCharacter(player)) return;

        long today = currentDay(player);
        if (today < 0) return;

        if (age.getLastGameDay() == AgeData.UNSET_DAY) {
            age.setLastGameDay(today);
            return;
        }
        if (today <= age.getLastGameDay()) return;

        long daysPassed = today - age.getLastGameDay();
        age.setLastGameDay(today);

        int oldShownAge = (int) Math.floor(age.getAgeYears());
        LifeStage before = age.getStage();

        age.addAgeYears((double) daysPassed / AgeSettings.daysPerYear());

        int newShownAge = (int) Math.floor(age.getAgeYears());
        LifeStage after = age.getStage();

        if (after != before) {
            AgeBonuses.applyAll(player);
            player.sendSystemMessage(Component.translatable(
                    "message.dmzgenerations.stage_up",
                    Component.translatable(after.translationKey()),
                    newShownAge));
        } else if (newShownAge > oldShownAge) {
            player.sendSystemMessage(Component.translatable(
                    "message.dmzgenerations.grew",
                    newShownAge,
                    Component.translatable(after.translationKey())));
        }

        GenNetwork.syncTo(player);
    }

    private static boolean hasDmzCharacter(Player player) {
        return player.getCapability(StatsCapability.INSTANCE)
                .map(stats -> stats.getStatus().isHasCreatedCharacter())
                .orElse(false);
    }

    private static long currentDay(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return -1L;
        return server.overworld().getDayTime() / TICKS_PER_DAY;
    }
}
