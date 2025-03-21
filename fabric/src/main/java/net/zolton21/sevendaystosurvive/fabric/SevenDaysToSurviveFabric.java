package net.zolton21.sevendaystosurvive.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.fabric.events.ModFabricEvents;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricBlockEntityRegistry;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricBlockRegistry;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricItemRegistry;

public final class SevenDaysToSurviveFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ForgeConfigRegistry.INSTANCE.register(SevenDaysToSurvive.MOD_ID, ModConfig.Type.COMMON, CommonConfig.Server.CONFIG);

        FabricBlockRegistry.register();
        FabricBlockEntityRegistry.register();
        FabricItemRegistry.register();
        ModFabricEvents.register();
        SevenDaysToSurvive.init();
    }
}
