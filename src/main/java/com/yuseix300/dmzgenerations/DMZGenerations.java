package com.yuseix300.dmzgenerations;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DMZGenerations.MOD_ID)
public class DMZGenerations {

    public static final String MOD_ID = "dmzgenerations";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DMZGenerations() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        LOGGER.info("[{}] Addon constructed - DragonMineZ required.", MOD_ID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            com.yuseix300.dmzgenerations.age.GenerationsConfig.load();
            com.yuseix300.dmzgenerations.network.GenNetwork.register();
            LOGGER.info("[{}] Common setup complete.", MOD_ID);
        });
    }
}
