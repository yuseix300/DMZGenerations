package com.yuseix300.dmzgenerations.network;

import com.yuseix300.dmzgenerations.client.ClientAgeHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AgeSyncS2C {

    private final int entityId;
    private final double ageYears;
    private final int generation;
    private final double multiplier;

    public AgeSyncS2C(int entityId, double ageYears, int generation, double multiplier) {
        this.entityId = entityId;
        this.ageYears = ageYears;
        this.generation = generation;
        this.multiplier = multiplier;
    }

    public static void encode(AgeSyncS2C msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeDouble(msg.ageYears);
        buf.writeInt(msg.generation);
        buf.writeDouble(msg.multiplier);
    }

    public static AgeSyncS2C decode(FriendlyByteBuf buf) {
        return new AgeSyncS2C(buf.readInt(), buf.readDouble(), buf.readInt(), buf.readDouble());
    }

    public static void handle(AgeSyncS2C msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ClientAgeHandler.apply(msg.entityId, msg.ageYears, msg.generation, msg.multiplier)));
        ctx.get().setPacketHandled(true);
    }
}
