package net.zolton21.sevendaystosurvive.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.zolton21.sevendaystosurvive.config.Config;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;

import java.util.List;

public class ZombieUtils {
    public static boolean isBlockBreakable(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        ResourceLocation BlockId = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(blockState.getBlock());
        if (blockState.getDestroySpeed(level, pos) == -1.0F) {
            return false;
        }
        if (BlockId != null && Config.Server.UNBREAKABLE_BLOCKS_LIST.get().contains(BlockId.toString())) {
            return false;
        }
        return true;
    }

    /*public static boolean searchForAnotherLeaderInRange(Monster mob){
        if(mob.isAlive()){
            int radius = 10;
            AABB aabb = new AABB(mob.getX() - radius, mob.getY() - radius, mob.getZ() - radius, mob.getX() + radius, mob.getY() + radius, mob.getZ() + radius);
            List<Zombie> zombieList = mob.level().getEntitiesOfClass(Zombie.class, aabb);
            for (Zombie zombie : zombieList) {
                if(((IZombieHelper)zombie).sevenDaysToSurvive$isLeader()){
                    if(zombie.isAlive()){
                        if()
                    }
                }
            }
        }
    }*/

    public static boolean isLeaderWithingRange(Monster mob){
        if(mob.isAlive()) {
            Zombie leader = ((IZombieHelper) mob).getSevenDaysToSurvive$leader();
            if (leader.isAlive()) {
                if (mob.distanceTo(leader) < 10) {
                    Path path = mob.getNavigation().createPath(leader, 0);
                    if(path != null){
                        if(path.canReach()){
                            if(path.getTarget().equals(leader.blockPosition())){
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void searchReachableZombieLeader(Monster mob) {
        System.out.println("searchReachableZombieLeader");
        if (mob.isAlive()) {
            int radius = 10;
            AABB aabb = new AABB(mob.getX() - radius, mob.getY() - radius, mob.getZ() - radius, mob.getX() + radius, mob.getY() + radius, mob.getZ() + radius);
            List<Zombie> zombieList = mob.level().getEntitiesOfClass(Zombie.class, aabb);
            for (Zombie zombie : zombieList) {
                if (zombie.isAlive()) {
                    if (!zombie.equals(mob)) {
                        if (((IZombieHelper) zombie).sevenDaysToSurvive$isLeader()) {
                            Path path = mob.getNavigation().createPath(zombie, 0);
                            if (path != null) {
                                if (path.canReach()) {
                                    if (path.getTarget().equals(zombie.blockPosition())) {
                                        ((IZombieHelper) zombie).sevenDaysToSurvive$addZombieToGroup((Zombie) mob);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        ((IZombieHelper) mob).sevenDaysToSurvive$strayAlone();
    }

    public static boolean mobHasPlayerTargetAndCanReach(Mob mob) {
        if (mob.getTarget() != null && mob.getTarget() instanceof ServerPlayer player) {
            Path path = mob.getNavigation().createPath(player.blockPosition(), 0);
            if (path != null) {
                if (path.getTarget() == player.blockPosition()) {
                    return path.canReach();
                }
            }
        }
        return false;
    }

    public static boolean isMobStandingOnAFullBlock(Mob mob) {  //no slabs, fences, etc.
        if (!ZombieUtils.hasAFullBlockCollision(mob, mob.blockPosition())) {
            return false;
        } else if (!ZombieUtils.hasAFullBlockCollision(mob, mob.blockPosition().offset(0, -1, 0))) {
            return false;
        }
        return true;
    }

    public static boolean hasAFullBlockCollision(Mob mob, BlockPos blockPos) {
        BlockState blockState = mob.level().getBlockState(blockPos);
        if (blockState.isAir()) {
            return true;
        } else {
            VoxelShape collisionShape = blockState.getCollisionShape(mob.level(), blockPos);
            if (!collisionShape.isEmpty() && collisionShape.bounds().maxY == 1.0) {
                return true;
            }
            return false;
        }
    }

    public static boolean HasBlockEntityCollision(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        return !blockState.getCollisionShape(level, blockPos, CollisionContext.empty()).isEmpty();
    }

    public static ServerPlayer getNearestUnprotectedSurvivalPlayer(Mob mob, double range) {

        ServerPlayer nearestPlayer = null;

        if (mob.getServer() != null) {
            ServerLevel serverLevel = mob.getServer().getLevel(mob.level().dimension());
            double closestDistance = Double.MAX_VALUE;

            if (serverLevel != null) {
                for (ServerPlayer player : serverLevel.players()) {
                    if (player.level().dimension() != mob.level().dimension()) {
                        if (player.distanceTo(mob) < range) {
                            if (player.isAlive() && !player.isSpectator() && !player.isCreative() && !PlayerHelper.isPlayerProtected((ServerPlayer) player)) {
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
        }
        return nearestPlayer;
    }
}
