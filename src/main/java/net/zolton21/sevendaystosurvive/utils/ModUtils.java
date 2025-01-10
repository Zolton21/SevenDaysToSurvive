package net.zolton21.sevendaystosurvive.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;

import static net.zolton21.sevendaystosurvive.block.SynapticSealBlock.SYNAPTIC_DUST_COUNT;

public class ModUtils {
    public static boolean isPlayerWithinRange(BlockPos pos, BlockState state, ServerPlayer player){
        int chunkActivityRange = state.getValue(SYNAPTIC_DUST_COUNT);

        int blockChunkX = Math.floorDiv(pos.getX(), 16 * chunkActivityRange);
        int blockChunkZ = Math.floorDiv(pos.getZ(), 16 * chunkActivityRange);
        int minChunkX = blockChunkX - 1; // Adjust range as needed
        int maxChunkX = blockChunkX + 1;
        int minChunkZ = blockChunkZ - 1;
        int maxChunkZ = blockChunkZ + 1;

        int playerChunkX = Math.floorDiv(player.getBlockX(), 16);
        int playerChunkZ = Math.floorDiv(player.getBlockZ(), 16);

        return playerChunkX >= minChunkX && playerChunkX <= maxChunkX && playerChunkZ >= minChunkZ && playerChunkZ <= maxChunkZ;
    }

    public static boolean mobHasPlayerTargetAndCanReach(Mob mob){
        if(mob.getTarget() != null && mob.getTarget() instanceof ServerPlayer player){
            Path path = mob.getNavigation().createPath(player.blockPosition(), 0);
            if(path != null) {
                if(path.getTarget() == player.blockPosition()) {
                    return path.canReach();
                }
            }
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

    public static ServerPlayer getNearestUnprotectedSurvivalPlayer(Mob mob, double range){

        ServerPlayer nearestPlayer = null;

        if(mob.getServer() != null) {
            ServerLevel serverLevel = mob.getServer().getLevel(mob.level().dimension());
            double closestDistance = Double.MAX_VALUE;

            if (serverLevel != null) {
                for (ServerPlayer player : serverLevel.players()) {
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
        return nearestPlayer;
    }
}
