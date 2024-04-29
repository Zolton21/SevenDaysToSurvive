package net.zolton21.sevendaystosurvive;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(SevenDaysToSurvive.MOD_ID)
public class SevenDaysToSurvive{
    public static final String MOD_ID = "sevendaystosurvive";


    public SevenDaysToSurvive() {
        IEventBus eventBus =FMLJavaModLoadingContext.get().getModEventBus();


        eventBus.addListener(this::setup);
        //eventBus.addListener(this::onWorldTick);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;

        /*if (!world.isRemote && event.phase == TickEvent.Phase.END) {
            long totalTime = world.getDayTime();
            long day = totalTime / 24000;
            System.out.println("Current Day Number: " + day);
        }*/
    }
}
