package net.zolton21.sevendaystosurvive.events;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.registries.EntityRegistry;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;
import net.zolton21.sevendaystosurvive.world.entity.AdvancedZombie;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    @SubscribeEvent
    public static void onPlayerSleep(PlayerSleepInBedEvent event) {
        Player player = event.getEntity();
        if (ZombieUtils.isOblivionNight(player.level())) {
            event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
            player.displayClientMessage(Component.literal("Can't Sleep During Doomnight"), true);
        }
    }

    /*@SubscribeEvent
    public static void onZombieSpawn(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Monster monster) {
            if (monster.getType() == EntityType.ZOMBIE) {
                Level level = monster.level();
                AdvancedZombie advancedZombie = new AdvancedZombie(EntityRegistry.ADVANCED_ZOMBIE.get(), level);

                advancedZombie.moveTo(monster.getX(), monster.getY(), monster.getZ(), monster.getYRot(), monster.getXRot());
                advancedZombie.setHealth(monster.getHealth());
                if (monster.hasCustomName()) {
                    advancedZombie.setCustomName(monster.getCustomName());
                }
                if (monster.isPersistenceRequired()) {
                    advancedZombie.setPersistenceRequired();
                }
                if (monster.isBaby()) {
                    advancedZombie.setBaby(true);
                }

                level.addFreshEntity(advancedZombie);
                monster.discard();
            }
            if (monster.getType() == EntityType.HUSK) {

            }
        }
    }*/
}
