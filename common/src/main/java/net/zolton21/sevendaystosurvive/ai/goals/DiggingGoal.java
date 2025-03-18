package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    protected final double speedModifier;
    private final PathfinderMob mob;
    private ItemStack heldItem;
    @Nullable
    private BlockPos nextBlockPos;
    private int tickCounter;
    private boolean isBreakingBlock;
    private int breakBlockTick;
    private BlockPos breakBlockBlockPos;
    private float blockBreakTime;
    private ItemStack offHandHeldItem;
    private boolean shouldPlaceBlock;
    private int placeBlockTick;
    private BlockPos placeBlockBlockPos;
    private long lastCanUseCheck;


    public DiggingGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                return false;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) {
            this.nextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
            if (this.nextBlockPos != null) {
                if (this.mob.level().getBlockState(this.nextBlockPos).getFluidState().isEmpty()) {
                    if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                        return true;
                    }
                    if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                        return true;
                    }
                    if (this.mob.getBlockX() == this.nextBlockPos.getX() && this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                        if (this.mob.getY() < this.nextBlockPos.getY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                return true;
                            }
                        } else if (this.mob.getY() > this.nextBlockPos.getY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                return true;
                            }
                        }
                    } else {
                        double nextPosY = this.nextBlockPos.getY();
                        double mobY = this.mob.getBlockY();
                        if (nextPosY == mobY) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                    return true;
                                }
                            }
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                    return true;
                                }
                            }
                        } else if (nextPosY > mobY) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                    return true;
                                }
                            }
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                    return true;
                                }
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                    return true;
                                }
                            } else if (Math.abs(this.mob.getBlockX() - this.nextBlockPos.getX()) < 2 || Math.abs(this.mob.getBlockZ() - this.nextBlockPos.getZ()) < 2) {
                                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                    if (ZombieUtils.isBlockBreakable(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                        return true;
                                    }
                                }
                            }
                        } else {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                    return true;
                                }
                            }
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                    return true;
                                }
                            }
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!ZombieUtils.isMobStandingOnAFullBlock(this.mob) && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (this.mob.level().getBlockState(this.mob.blockPosition()).getFluidState().isEmpty() && this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getFluidState().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                return false;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.nextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
            if (this.nextBlockPos != null) {
                if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                    return true;
                }
                if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                    return true;
                }

                if (this.mob.getBlockX() == this.nextBlockPos.getX() && this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                    if (this.mob.getBlockY() < this.nextBlockPos.getY()) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                return true;
                            }
                        }
                    } else if (this.mob.getBlockY() > this.nextBlockPos.getY()) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                return true;
                            }
                        }
                    }
                } else {
                    double nextPosY = this.nextBlockPos.getY();
                    double mobY = this.mob.getBlockY();
                    if (nextPosY == mobY) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                return true;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                return true;
                            }
                        }
                    } else if (nextPosY > mobY) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                return true;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                return true;
                            }
                        } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                return true;
                            }
                        } else if (Math.abs(this.mob.getBlockX() - this.nextBlockPos.getX()) < 2 || Math.abs(this.mob.getBlockZ() - this.nextBlockPos.getZ()) < 2) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                if (ZombieUtils.isBlockBreakable(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                    return true;
                                }
                            }
                        }
                    } else {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos)) {
                                return true;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                return true;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                            if (ZombieUtils.isBlockBreakable(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                return true;
                            }
                        }
                    }
                }

                if (this.shouldPlaceBlock) {
                    return true;
                }
            }
            if (!ZombieUtils.isMobStandingOnAFullBlock(this.mob)) {
                if (this.mob.level().getBlockState(this.mob.blockPosition()).getFluidState().isEmpty() && this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getFluidState().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void start() {
        this.mob.getNavigation().stop();
        this.tickCounter = 0;
        this.isBreakingBlock = false;
        this.shouldPlaceBlock = false;
        this.placeBlockTick = 0;
        this.heldItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
    }

    public void stop() {

        if (this.breakBlockBlockPos != null) {
            this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, -1);
        }

        if (this.nextBlockPos != null) {
            if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos) && !ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                double mobY = this.mob.blockPosition().getY();
                double nextBPY = this.nextBlockPos.getY();
                if (mobY == nextBPY) {
                    ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(this.nextBlockPos);
                } else if (nextBPY > mobY) {
                    if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                        ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(this.nextBlockPos);
                    }
                } else {
                    if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                        ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(this.nextBlockPos);
                    }
                }
            }
        }
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.heldItem);
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalFinished();
    }

    public void tick() {
        this.tickCounter++;

        if (this.shouldPlaceBlock) {
            if (this.placeBlockBlockPos != null) {
                if (this.placeBlockTick - 3 == this.tickCounter) {
                    this.mob.swing(this.mob.getUsedItemHand());
                    this.mob.level().setBlock(this.placeBlockBlockPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                }
                if (this.placeBlockTick == this.tickCounter) {
                    this.shouldPlaceBlock = false;
                    if (this.offHandHeldItem != null) {
                        this.mob.setItemInHand(InteractionHand.OFF_HAND, this.offHandHeldItem);
                    }
                }
            }
        }
        if (this.isBreakingBlock) {
            if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.breakBlockBlockPos) && !ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.breakBlockBlockPos.offset(0, -1, 0))) {
                this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                this.isBreakingBlock = false;
            } else {
                this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, (int) ((this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / this.blockBreakTime * 10.0F));
                this.mob.getNavigation().setSpeedModifier(0);
                this.faceTarget(this.breakBlockBlockPos);
                if ((this.breakBlockTick - this.tickCounter) % 5 == 0) {
                    this.mob.swing(this.mob.getUsedItemHand());
                }
                if (this.tickCounter == this.breakBlockTick) {
                    this.breakBlock(this.breakBlockBlockPos);
                    this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                    this.isBreakingBlock = false;
                }
            }
        }

        if (!this.isBreakingBlock && this.tickCounter % 10 == 0) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {

                if (!ZombieUtils.isMobStandingOnAFullBlock(this.mob)) {
                    if (!ZombieUtils.hasAFullBlockCollision(this.mob, this.mob.blockPosition())) {
                        startBreakingBlock(this.tickCounter, this.mob.blockPosition());
                    } else if (!ZombieUtils.hasAFullBlockCollision(this.mob, this.mob.blockPosition().offset(0, -1, 0))) {
                        startBreakingBlock(this.tickCounter, this.mob.blockPosition().offset(0, -1, 0));
                    }
                }

                if (this.nextBlockPos != null) {
                    if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                        this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, -1, 0));
                    }
                    if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock){
                        this.startBreakingBlock(this.tickCounter, this.mob.blockPosition().offset(0, -1, 0));
                    }

                    if (this.nextBlockPos.getX() == this.mob.getBlockX() && this.nextBlockPos.getZ() == this.mob.getBlockZ()) {
                        if (this.mob.getBlockY() > this.nextBlockPos.getY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                            }
                        } else if (this.mob.getBlockY() < this.nextBlockPos.getY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0));
                            }
                        }
                    } else {
                        if (this.nextBlockPos.getY() == this.mob.getBlockY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0));
                            }
                        } else if (this.nextBlockPos.getY() < this.mob.getBlockY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0));
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 2, 0));
                            }
                        } else if (this.nextBlockPos.getY() > this.mob.getBlockY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0));
                            } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                this.startBreakingBlock(this.tickCounter, this.mob.blockPosition().offset(0, 2, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private void startBreakingBlock(int currentTick, BlockPos blockPos) {
        ((IZombieHelper) this.mob).sevenDaysToSurvive$setBreakingBlockBP(blockPos);
        this.isBreakingBlock = true;

        float blockHardness = this.mob.level().getBlockState(blockPos).getDestroySpeed(this.mob.level(), blockPos);
        this.blockBreakTime = blockHardness * 50.0F / ((IZombieHelper) this.mob).sevenDaysToSurvive$getBlockBreakingSpeedModifier();

        if (this.blockBreakTime == 0)
            this.blockBreakTime = 1;

        this.breakBlockTick = currentTick + (int) this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
        if (blockPos.getX() == this.mob.getBlockX() && blockPos.getZ() == this.mob.getBlockZ()) {
            double distance = this.mob.distanceToSqr(this.mob.blockPosition().getCenter());
            if (distance > 0.15) {
                double currentX = this.mob.getX();
                double currentZ = this.mob.getZ();
                double offsetX = Math.floor(currentX) + 0.5 - currentX;
                double offsetZ = Math.floor(currentZ) + 0.5 - currentZ;
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(offsetX * 0.1, 0, offsetZ * 0.1));
            }

            if (blockPos.getY() == this.mob.getBlockY() - 1) {
                if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), blockPos.offset(0, -1, 0))) {
                    this.placeBlockBlockPos = blockPos.offset(0, -1, 0);
                    this.shouldPlaceBlock = true;
                    this.placeBlockTick = this.breakBlockTick + 5;
                    this.offHandHeldItem = this.mob.getItemInHand(InteractionHand.OFF_HAND);
                    this.mob.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.COBBLESTONE));
                }
            }
        }
    }

    private void breakBlock(BlockPos blockPos) {
        this.mob.level().destroyBlock(blockPos, true);
        this.mob.level().playSound(null, blockPos, this.mob.level().getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getSoundSource(), 1.0F, 1.0F);
    }

    public void faceTarget(BlockPos blockPos) {
        double deltaX = blockPos.getX() - this.mob.getBlockX();
        double deltaZ = blockPos.getZ() - this.mob.getBlockZ();
        double yaw = Math.atan2(deltaZ, deltaX);
        yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.setYRot((float) yaw);

        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }
}
