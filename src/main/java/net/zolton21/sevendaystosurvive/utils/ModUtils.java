package net.zolton21.sevendaystosurvive.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class ModUtils {

    public static boolean HasBlockEntityCollision(Level level, BlockPos blockPos){
        BlockState blockState = level.getBlockState(blockPos);
        return !blockState.getCollisionShape(level, blockPos, CollisionContext.empty()).isEmpty();
    }

    public static Player getNearestSurvivalPlayer(Mob mob, double range){
        ServerLevel serverLevel = (ServerLevel) mob.level();
        Player nearestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for(Player player: serverLevel.players()){
            if (player.distanceTo(mob) < range){
                if(player.isAlive() && !player.isSpectator() && !player.isCreative()){
                    double distance = mob.distanceToSqr(player);
                    if(distance < closestDistance){
                        closestDistance = distance;
                        nearestPlayer = player;
                    }
                }
            }
        }
        return nearestPlayer;
    }
}
