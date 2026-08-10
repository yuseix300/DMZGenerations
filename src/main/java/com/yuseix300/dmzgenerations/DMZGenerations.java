package com.yuseix300.dmzgenerations;

import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main entry point for DMZ: Generations, an addon for DragonMineZ.
 *
 * <p>Registration follows the same deferred-register pattern DragonMineZ uses:
 * create your {@code DeferredRegister}s, register them to the mod event bus here,
 * and wire any setup from {@link #commonSetup(FMLCommonSetupEvent)}.</p>
 */
@Mod(DMZGenerations.MOD_ID)
public class DMZGenerations {

    public static final String MOD_ID = "dmzgenerations";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DMZGenerations() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // TODO: register your DeferredRegisters (items, blocks, entities, ...) here.
        // Example:
        //   MainItems.ITEMS.register(modEventBus);
        //   MainBlocks.BLOCKS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        LOGGER.info("[{}] Addon constructed - DragonMineZ required.", MOD_ID);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // TODO: deferred, thread-safe setup (network channels, capability wiring, etc.).
            LOGGER.info("[{}] Common setup complete.", MOD_ID);
        });
    }
}
