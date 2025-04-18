package net.zolton21.sevendaystosurvive.forge.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModForgeEvents {
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
        int range = ZombieUtils.isOblivionNight(event.getEntity().level()) ? CommonConfig.Server.PLAYER_DETECTION_RANGE_OBLIVION_NIGHT.get() : CommonConfig.Server.PLAYER_DETECTION_RANGE.get();
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
    public static void onZombieSpawn(EntityJoinLevelEvent event){
        Entity mobEntity = event.getEntity();
        if(ZombieUtils.isOblivionNight(mobEntity.level())) {
            if (mobEntity.getType() == EntityType.ZOMBIE || mobEntity.getType() == EntityType.HUSK) {
                if(mobEntity instanceof Zombie zombieTypeEntity){
                    if(!zombieTypeEntity.isBaby()) {
                        if (zombieTypeEntity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                            if(zombieTypeEntity.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(ZombieUtils.speedBoostModifier.getId()) == null) {
                                zombieTypeEntity.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(ZombieUtils.speedBoostModifier);

                                ItemStack[] randomSet = ZombieUtils.getWeaponAndArmorSet();

                                zombieTypeEntity.setItemSlot(EquipmentSlot.HEAD, randomSet[0]);
                                zombieTypeEntity.setItemSlot(EquipmentSlot.CHEST, randomSet[1]);
                                zombieTypeEntity.setItemSlot(EquipmentSlot.LEGS, randomSet[2]);
                                zombieTypeEntity.setItemSlot(EquipmentSlot.FEET, randomSet[3]);
                                zombieTypeEntity.setItemSlot(EquipmentSlot.MAINHAND, randomSet[4]);

                                zombieTypeEntity.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
                                zombieTypeEntity.setDropChance(EquipmentSlot.HEAD, 0.0f);
                                zombieTypeEntity.setDropChance(EquipmentSlot.CHEST, 0.0f);
                                zombieTypeEntity.setDropChance(EquipmentSlot.LEGS, 0.0f);
                                zombieTypeEntity.setDropChance(EquipmentSlot.FEET, 0.0f);
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(ZombieUtils.isOblivionNight(event.player.level())) {
            long time = event.player.level().getDayTime() % 24000;
            if(time >= 12000 && time <= 12200) {
                Component text = Component.translatable("message.sevendaystosurvive.oblivion.night.beginning").withStyle(ChatFormatting.DARK_RED);
                event.player.displayClientMessage(text, true);
            }
        }
    }
}
