package net.zolton21.sevendaystosurvive.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.UUID;

public class ZombieUtils {
    public static final AttributeModifier speedBoostModifier = new AttributeModifier(UUID.fromString("e3689d2f-12c9-4af2-94bb-db025908da17"), "SpeedBoostModifier", 0.13F, AttributeModifier.Operation.ADDITION);

    public static boolean isBlockBreakable(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        ResourceLocation BlockId = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(blockState.getBlock());
        if (blockState.getDestroySpeed(level, pos) == -1.0F) {
            return false;
        }
        if (BlockId != null) {
            if (CommonConfig.Server.UNBREAKABLE_BLOCKS_LIST.get().contains(BlockId.toString())) {
                return false;
            }
        }
        return true;
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
                    if (player.level().dimension() == mob.level().dimension()) {
                        if (player.distanceTo(mob) <= range) {
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

    public static boolean isOblivionNight(Level level) {
        if(level instanceof ServerLevel serverLevel) {
            long time = serverLevel.getDayTime() % 24000;
            long daysPassed = serverLevel.getDayTime() / 24000 + 1;

            if (daysPassed % CommonConfig.Server.OBLIVION_NIGHT_FREQUENCY.get() == 0) {
                if (12010 < time && time < 23991) {
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack @NotNull [] getWeaponAndArmorSet() {
        ItemStack[][] armorSets = new ItemStack[][]{
                {
                        new ItemStack(Items.LEATHER_HELMET),
                        new ItemStack(Items.LEATHER_CHESTPLATE),
                        new ItemStack(Items.LEATHER_LEGGINGS),
                        new ItemStack(Items.LEATHER_BOOTS),
                        new ItemStack(Items.STONE_SWORD)
                },
                {
                        new ItemStack(Items.CHAINMAIL_HELMET),
                        new ItemStack(Items.CHAINMAIL_CHESTPLATE),
                        new ItemStack(Items.CHAINMAIL_LEGGINGS),
                        new ItemStack(Items.CHAINMAIL_BOOTS),
                        new ItemStack(Items.IRON_SWORD)
                },
                {
                        new ItemStack(Items.IRON_HELMET),
                        new ItemStack(Items.IRON_CHESTPLATE),
                        new ItemStack(Items.IRON_LEGGINGS),
                        new ItemStack(Items.IRON_BOOTS),
                        new ItemStack(Items.IRON_SWORD)
                }
        };
        Random random = new Random();
        return armorSets[random.nextInt(armorSets.length)];
    }
}
