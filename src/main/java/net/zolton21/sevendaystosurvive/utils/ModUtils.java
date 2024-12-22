package net.zolton21.sevendaystosurvive.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ModUtils {

    public static boolean mobHasPlayerTargetAndCanReach(Mob mob){
        if(mob.getTarget() != null && mob.getTarget() instanceof Player player){
            Path path = mob.getNavigation().createPath(player.blockPosition(), 0);
            if(path != null)
                return path.canReach();
        }
        return false;
    }

    public static boolean isMobStandingOnAFullBlock(Mob mob){  //no slabs, fences, etc.
        if(!ModUtils.hasAFullBlockCollision(mob, mob.blockPosition())){
            return false;
        }else if(!ModUtils.hasAFullBlockCollision(mob, mob.blockPosition().offset(0, -1, 0))){
            return false;
        }
        return true;
    }

    public static boolean hasAFullBlockCollision(Mob mob, BlockPos blockPos){
        BlockState blockState = mob.level().getBlockState(blockPos);
        if(blockState.isAir()){
            return true;
        }else{
            VoxelShape collisionShape = blockState.getCollisionShape(mob.level(), blockPos);
            //System.out.println("blockState: " + blockState);
            if(!collisionShape.isEmpty()) {
                //System.out.println("collisionShape: " + collisionShape.bounds().maxY);
            }
            if(!collisionShape.isEmpty() && collisionShape.bounds().maxY == 1.0){
                return true;
            }
            return false;
        }
    }

    public static boolean HasBlockEntityCollision(Level level, BlockPos blockPos){
        BlockState blockState = level.getBlockState(blockPos);
        return !blockState.getCollisionShape(level, blockPos, CollisionContext.empty()).isEmpty();
    }

    public static Player getNearestSurvivalPlayer(Mob mob, double range){

        Player nearestPlayer = null;

        if(mob.getServer() != null) {
            ServerLevel serverLevel = mob.getServer().getLevel(mob.level().dimension());
            double closestDistance = Double.MAX_VALUE;

            if (serverLevel != null) {
                for (Player player : serverLevel.players()) {
                    if (player.distanceTo(mob) < range) {
                        if (player.isAlive() && !player.isSpectator() && !player.isCreative()) {
                            double distance = mob.distanceToSqr(player);
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                nearestPlayer = player;
                            }
                        }
                    }
                }
            }
        }
        return nearestPlayer;
    }
}
