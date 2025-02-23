package net.zolton21.sevendaystosurvive.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event){
        Player player = event.getEntity();
        if(player.level() instanceof ServerLevel serverLevel){
            long time = serverLevel.getDayTime() % 24000;
            long daysPassed = serverLevel.getDayTime() / 24000;
            System.out.println("time: " + time);
            System.out.println("daysPassed: " + daysPassed);
            if(daysPassed % 7 == 0){
                if(12010 < time && time < 23991){
                    event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
                    player.displayClientMessage(Component.literal("Can't Sleep During Doomnight"), true);
                }
            }
        }
    }
}
