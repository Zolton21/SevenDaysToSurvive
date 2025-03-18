package net.zolton21.sevendaystosurvive.fabric.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModFabricEvents {
    private static final Map<ServerPlayer, Level> lastKnownDimensions = new HashMap<>();

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockState state = world.getBlockState(hitResult.getBlockPos());
            if (state.getBlock() instanceof BedBlock) {
                if (ZombieUtils.isOblivionNight(world)) {
                    player.displayClientMessage(Component.translatable("message.sevendaystosurvive.oblivion.night.sleep.problem"), true);
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Level currentDimension = player.level();
                Level lastDimension = lastKnownDimensions.getOrDefault(player, currentDimension);

                if (currentDimension != lastDimension) {
                    onPlayerChangeDimension(player, lastDimension, currentDimension);
                    lastKnownDimensions.put(player, currentDimension);
                }
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LocalPlayer player = client.player;
            if(player != null && ZombieUtils.isOblivionNight(player.level())){
                long time = player.level().getDayTime();
                if(time >= 12000 && time <= 12200) {
                    Component text = Component.translatable("message.sevendaystosurvive.oblivion.night.beginning").withStyle(ChatFormatting.DARK_RED);
                    player.displayClientMessage(text, true);
                }
            }
        });
    }

    private static void onPlayerChangeDimension(ServerPlayer player, Level from, Level to) {
        int range = ZombieUtils.isOblivionNight(player.level()) ? CommonConfig.PLAYER_DETECTION_RANGE_OBLIVION_NIGHT : CommonConfig.PLAYER_DETECTION_RANGE;

        AABB AABBrange = new AABB(-range, -range, -range, range, range, range);
        List<Monster> monsters = player.level().getEntitiesOfClass(Monster.class, AABBrange);

        for (Monster monster : monsters) {
            if (monster instanceof Zombie zombie) {
                ((IZombieHelper) zombie).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
            }
        }
    }
}
