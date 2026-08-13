package com.yuseix300.dmzgenerations.age;

import com.dragonminez.common.stats.StatsCapability;
import com.dragonminez.common.stats.StatsData;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.network.GenNetwork;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

    private static final ResourceKey<Level> TIME_CHAMBER = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath("dragonminez", "time_chamber"));

    private static final Map<UUID, Integer> achaqueCooldown = new HashMap<>();

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
                AgeBonuses.applyAll(serverPlayer);
            });
            serverPlayer.refreshDimensions();
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
        serverPlayer.getCapability(INSTANCE).ifPresent(age -> {
            tickAge(serverPlayer, age);
            tickAchaques(serverPlayer, age);
        });
    }

    private static void tickAge(ServerPlayer player, AgeData age) {
        StatsData stats = statsOf(player);
        if (stats == null || !stats.getStatus().isHasCreatedCharacter()) return;
        // No aging while dead / in the Otherworld with a halo.
        if (!stats.getStatus().isAlive() || stats.getStatus().isForceHalo()) return;

        long today = currentDay(player);
        if (today < 0) return;
        if (age.getLastGameDay() == AgeData.UNSET_DAY) {
            age.setLastGameDay(today);
            return;
        }

        double delta = 0.0;
        if (today > age.getLastGameDay()) {
            delta += (double) (today - age.getLastGameDay()) / AgeSettings.daysPerYear();
            age.setLastGameDay(today);
        }
        // The Time Chamber accelerates aging, tick by tick.
        if (player.level().dimension().equals(TIME_CHAMBER)) {
            delta += GenerationsConfig.get().htcAgingMultiplier
                    / (AgeSettings.daysPerYear() * (double) TICKS_PER_DAY);
        }
        if (delta <= 0.0) return;

        applyAgeDelta(player, age, delta);
    }

    /** Adds an age delta (natural aging / HTC) and fires the growth messages + effects. */
    private static void applyAgeDelta(ServerPlayer player, AgeData age, double delta) {
        int oldShownAge = (int) Math.floor(age.getAgeYears());
        LifeStage before = age.getStage();

        age.addAgeYears(delta);

        // Nothing visible crossed yet (e.g. a sub-year HTC tick) — don't spam effects.
        if ((int) Math.floor(age.getAgeYears()) == oldShownAge && age.getStage() == before) return;

        applyAgeChangeEffects(player, age, oldShownAge, before);
    }

    /**
     * Re-applies bonuses, syncs, and plays the birthday / stage-up feedback (messages, XP sound,
     * totem particles) for an age change from {@code oldShownAge}/{@code before}. Shared by natural
     * aging and the {@code /dmzgen} commands so setting the age fires the same effects.
     */
    public static void applyAgeChangeEffects(ServerPlayer player, AgeData age, int oldShownAge, LifeStage before) {
        int newShownAge = (int) Math.floor(age.getAgeYears());
        LifeStage after = age.getStage();
        boolean advanced = after.ordinal() > before.ordinal(); // moved to a LATER life stage
        boolean birthday = newShownAge > oldShownAge;

        AgeBonuses.applyAll(player);

        if (advanced) {
            player.sendSystemMessage(Component.translatable("message.dmzgenerations.stage_up",
                    Component.translatable(after.translationKey()), newShownAge));
        } else if (birthday) {
            player.sendSystemMessage(Component.translatable("message.dmzgenerations.grew",
                    newShownAge, Component.translatable(after.translationKey())));
        }

        if (GenerationsConfig.get().growthEffectsEnabled && (advanced || birthday)) {
            // XP "ding" on a birthday; a burst of totem particles when you enter a new life stage.
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 1.0f, 1.0f);
            if (advanced && player.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(), player.getY() + player.getBbHeight() * 0.6, player.getZ(),
                        40, 0.5, 0.6, 0.5, 0.25);
            }
        }

        // Recompute the server-side hitbox so it matches the new age scale (fires EntityEvent.Size).
        player.refreshDimensions();
        GenNetwork.syncTo(player);
    }

    /** Elder ailments: periodic small damage that gets more frequent the older you are. */
    private static void tickAchaques(ServerPlayer player, AgeData age) {
        GenerationsConfig cfg = GenerationsConfig.get();
        UUID id = player.getUUID();
        if (!cfg.elderAchaquesEnabled || age.getStage() != LifeStage.ELDER) {
            achaqueCooldown.remove(id);
            return;
        }
        StatsData stats = statsOf(player);
        if (stats == null || !stats.getStatus().isHasCreatedCharacter()) return;
        if (!stats.getStatus().isAlive() || stats.getStatus().isForceHalo()) return;

        int ticks = achaqueCooldown.getOrDefault(id, -1);
        if (ticks > 0) {
            achaqueCooldown.put(id, ticks - 1);
            return;
        }
        if (ticks == 0) doAchaque(player, cfg);
        achaqueCooldown.put(id, cfg.elderAchaqueInterval(age.getAgeYears()) * 20);
    }

    private static void doAchaque(ServerPlayer player, GenerationsConfig cfg) {
        float dmg = (float) cfg.elderAchaqueDamage;
        if (!cfg.elderAchaqueLethal) {
            dmg = Math.min(dmg, Math.max(0.0f, player.getHealth() - 2.0f)); // never kills you
        }
        if (dmg > 0.0f) player.hurt(player.damageSources().magic(), dmg);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 0.7f, 0.6f);
        if (player.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    player.getX(), player.getY() + player.getBbHeight() * 0.6, player.getZ(),
                    6, 0.3, 0.4, 0.3, 0.02);
        }
        player.sendSystemMessage(Component.translatable("message.dmzgenerations.achaque"));
    }

    private static StatsData statsOf(Player player) {
        return player.getCapability(StatsCapability.INSTANCE).resolve().orElse(null);
    }

    private static long currentDay(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) return -1L;
        return server.overworld().getDayTime() / TICKS_PER_DAY;
    }
}
