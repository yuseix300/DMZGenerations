package com.yuseix300.dmzgenerations.command;

import com.yuseix300.dmzgenerations.DMZGenerations;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DMZGenerations.MOD_ID)
public class GenerationsCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        AgeCommand.register(event.getDispatcher());
    }
}
