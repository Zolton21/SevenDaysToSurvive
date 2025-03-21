package net.zolton21.sevendaystosurvive.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.forge.registry.ForgeBlockEntityRegistry;
import net.zolton21.sevendaystosurvive.forge.registry.ForgeBlockRegistry;
import net.zolton21.sevendaystosurvive.forge.registry.ForgeItemRegistry;

@Mod(SevenDaysToSurvive.MOD_ID)
public final class SevenDaysToSurviveForge {
    public SevenDaysToSurviveForge() {
        EventBuses.registerModEventBus(SevenDaysToSurvive.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        ForgeBlockRegistry.register();
        ForgeBlockEntityRegistry.register();
        ForgeItemRegistry.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.Server.CONFIG);
        SevenDaysToSurvive.init();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }
}
