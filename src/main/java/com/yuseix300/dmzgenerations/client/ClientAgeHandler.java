package com.yuseix300.dmzgenerations.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;

public final class ClientAgeHandler {

    private ClientAgeHandler() {}

    public static void apply(int entityId, double ageYears, int generation, double multiplier) {
        ClientAgeData.set(entityId, ageYears, generation, multiplier);

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            Entity entity = mc.level.getEntity(entityId);
            if (entity != null) entity.refreshDimensions();
        }
    }
}
