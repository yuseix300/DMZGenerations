package com.yuseix300.dmzgenerations.network;

import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeSettings;
import com.yuseix300.dmzgenerations.age.Rebirth;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RebirthC2S {

    public RebirthC2S() {}

    public static void encode(RebirthC2S msg, FriendlyByteBuf buf) {}

    public static RebirthC2S decode(FriendlyByteBuf buf) {
        return new RebirthC2S();
    }

    public static void handle(RebirthC2S msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            Rebirth.Result result = Rebirth.attempt(player);
            player.sendSystemMessage(feedback(result, player));
        });
        ctx.get().setPacketHandled(true);
    }

    private static Component feedback(Rebirth.Result result, ServerPlayer player) {
        return switch (result) {
            case SUCCESS -> {
                int gen = player.getCapability(AgeCapability.INSTANCE).resolve()
                        .map(age -> age.getGeneration()).orElse(1);
                yield Component.translatable("message.dmzgenerations.rebirth.success", gen);
            }
            case NOT_ELDER -> Component.translatable("message.dmzgenerations.rebirth.not_elder");
            case MAX_GENERATION -> Component.translatable("message.dmzgenerations.rebirth.max");
            case LEVEL_TOO_LOW -> {
                int req = player.getCapability(AgeCapability.INSTANCE).resolve()
                        .map(age -> AgeSettings.requiredLevelForGeneration(age.getGeneration() + 1)).orElse(0);
                yield Component.translatable("message.dmzgenerations.rebirth.level", req);
            }
            case NO_CHARACTER -> Component.translatable("message.dmzgenerations.rebirth.no_character");
        };
    }
}
