package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreativeTabRegistry {
    @SubscribeEvent
    public static void addToExistingCreativeTab(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS){
            event.accept(ItemRegistry.SYNAPTIC_SEAL.get());
        }
    }
}
