package net.zolton21.sevendaystosurvive;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.zolton21.sevendaystosurvive.items.ModItems;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SevenDaysToSurvive.MOD_ID)
public class SevenDaysToSurvive{
    public static final String MOD_ID = "sevendaystosurvive";

    public static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SevenDaysToSurvive() {
        IEventBus eventBus =FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);

        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event){
        //if(event.getTabKey() == CreativeModeTabs.)
    }


}
