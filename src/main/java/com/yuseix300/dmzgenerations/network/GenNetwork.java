package com.yuseix300.dmzgenerations.network;

import com.dragonminez.common.stats.StatsCapability;
import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class GenNetwork {

    private GenNetwork() {}

    public static SimpleChannel INSTANCE;
    private static int packetId = 0;

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(DMZGenerations.MOD_ID, "network"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        net.messageBuilder(AgeSyncS2C.class, packetId++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(AgeSyncS2C::decode)
                .encoder(AgeSyncS2C::encode)
                .consumerMainThread(AgeSyncS2C::handle)
                .add();

        net.messageBuilder(SetStartAgeC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SetStartAgeC2S::decode)
                .encoder(SetStartAgeC2S::encode)
                .consumerMainThread(SetStartAgeC2S::handle)
                .add();

        net.messageBuilder(RebirthC2S.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(RebirthC2S::decode)
                .encoder(RebirthC2S::encode)
                .consumerMainThread(RebirthC2S::handle)
                .add();

        INSTANCE = net;
    }

    public static void sendToServer(Object msg) {
        if (INSTANCE != null) INSTANCE.sendToServer(msg);
    }

    private static boolean hasCharacter(ServerPlayer player) {
        return player.getCapability(StatsCapability.INSTANCE)
                .map(stats -> stats.getStatus().isHasCreatedCharacter()).orElse(false);
    }

    public static void syncTo(ServerPlayer player) {
        if (INSTANCE == null || !hasCharacter(player)) return;
        player.getCapability(AgeCapability.INSTANCE).ifPresent(age ->
                INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                        new AgeSyncS2C(player.getId(), age.getAgeYears(), age.getGeneration(),
                                AgeSettings.generationMultiplier(age.getGeneration()))));
        player.refreshDimensions();
    }

    public static void syncToObserver(ServerPlayer target, ServerPlayer observer) {
        if (INSTANCE == null || !hasCharacter(target)) return;
        target.getCapability(AgeCapability.INSTANCE).ifPresent(age ->
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> observer),
                        new AgeSyncS2C(target.getId(), age.getAgeYears(), age.getGeneration(),
                                AgeSettings.generationMultiplier(age.getGeneration()))));
    }
}
