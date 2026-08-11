package com.yuseix300.dmzgenerations.network;

import com.yuseix300.dmzgenerations.age.AgeCapability;
import com.yuseix300.dmzgenerations.age.AgeSettings;
import com.yuseix300.dmzgenerations.age.LifeStage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;


public class SetStartAgeC2S {

    private final int stageOrdinal;

    public SetStartAgeC2S(int stageOrdinal) {
        this.stageOrdinal = stageOrdinal;
    }

    public static void encode(SetStartAgeC2S msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.stageOrdinal);
    }

    public static SetStartAgeC2S decode(FriendlyByteBuf buf) {
        return new SetStartAgeC2S(buf.readInt());
    }

    public static void handle(SetStartAgeC2S msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            LifeStage[] stages = LifeStage.values();
            LifeStage stage = stages[Mth.clamp(msg.stageOrdinal, 0, stages.length - 1)];
            double startAge = stage == LifeStage.CHILD ? AgeSettings.defaultStartAge() : stage.minAge;

            player.getCapability(AgeCapability.INSTANCE).ifPresent(age -> age.setAgeYears(startAge));
            com.yuseix300.dmzgenerations.age.AgeBonuses.applyAll(player); // apply the starting stage's bonus
            GenNetwork.syncTo(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
