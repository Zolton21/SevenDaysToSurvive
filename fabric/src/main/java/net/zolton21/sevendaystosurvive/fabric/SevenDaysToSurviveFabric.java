package net.zolton21.sevendaystosurvive.fabric;

import net.fabricmc.api.ModInitializer;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.fabric.events.ModFabricEvents;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricBlockEntityRegistry;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricBlockRegistry;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricItemRegistry;

public final class SevenDaysToSurviveFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FabricBlockRegistry.register();
        FabricBlockEntityRegistry.register();
        FabricItemRegistry.register();
        ModFabricEvents.register();
        SevenDaysToSurvive.init();
    }
}
