package com.yuseix300.dmzgenerations.events;

import com.yuseix300.dmzgenerations.DMZGenerations;
import com.yuseix300.dmzgenerations.age.AgeScale;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DMZGenerations.MOD_ID)
public class AgeSizeEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof Player player)) return;

        float scale = AgeScale.forPlayer(player);
        if (scale == 1.0f) return;

        event.setNewSize(event.getNewSize().scale(scale));
        event.setNewEyeHeight(event.getNewEyeHeight() * scale);
    }
}
