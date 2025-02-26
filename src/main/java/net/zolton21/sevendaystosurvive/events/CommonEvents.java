package net.zolton21.sevendaystosurvive.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.config.Config;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (ZombieUtils.isOblivionNight(player.level())) {
            event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
            player.displayClientMessage(Component.translatable("message.sevendaystosurvive.oblivion.night.sleep.problem"), true);
        }
    }

    @SubscribeEvent
    public static void onEntityChangeDimension(EntityTravelToDimensionEvent event){
        int range = ZombieUtils.isOblivionNight(event.getEntity().level()) ? Config.Server.PLAYER_DETECTION_RANGE_OBLIVION_NIGHT.get() : Config.Server.PLAYER_DETECTION_RANGE.get();
        AABB AABBrange = new AABB(range, range, range, -range, -range, -range);

        List<Monster> monsters = new ArrayList<>();
        monsters.addAll(event.getEntity().level().getEntitiesOfClass(Zombie.class, AABBrange));
        monsters.addAll(event.getEntity().level().getEntitiesOfClass(Husk.class, AABBrange));

        for(Monster monster : monsters){
            if(monster instanceof Zombie zombie){
                ((IZombieHelper)zombie).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(ZombieUtils.isOblivionNight(event.player.level())) {
            if (event.player.level().isClientSide()) {
                long time = event.player.level().getDayTime();
                if(time >= 12000 && time <= 12200){
                    Component text = Component.translatable("message.sevendaystosurvive.oblivion.night.beginning").withStyle(ChatFormatting.DARK_RED);
                    event.player.displayClientMessage(text, true);
                }
            }
        }
    }
}
