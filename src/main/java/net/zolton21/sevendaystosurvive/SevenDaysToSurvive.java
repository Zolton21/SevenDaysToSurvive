package net.zolton21.sevendaystosurvive;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.zolton21.sevendaystosurvive.config.Config;
import net.zolton21.sevendaystosurvive.registries.BlockEntityRegistry;
import net.zolton21.sevendaystosurvive.registries.BlockRegistry;
import net.zolton21.sevendaystosurvive.registries.ItemRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SevenDaysToSurvive.MOD_ID)
public class SevenDaysToSurvive{
    public static final String MOD_ID = "sevendaystosurvive";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SevenDaysToSurvive() {
        IEventBus eventBus =FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(eventBus);
        BlockRegistry.register(eventBus);
        BlockEntityRegistry.register(eventBus);

        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.Server.CONFIG);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }



}
