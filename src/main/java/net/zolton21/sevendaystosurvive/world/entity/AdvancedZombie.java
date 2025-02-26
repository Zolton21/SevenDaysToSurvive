package net.zolton21.sevendaystosurvive.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import net.zolton21.sevendaystosurvive.config.Config;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import javax.annotation.Nullable;
import java.util.Random;

public class AdvancedZombie extends Zombie {

    private boolean executingCustomGoal;
    private boolean isWithinSynapticSealActivityRange;
    private int ticksUntilNextPathRecalculation;
    private float blockBreakingSpeedModifier;
    private boolean mobHasPlayerTargetAndCanReach;
    private long ticksUntilNextCustomPathSearch;
    @Nullable
    private Path pathToTargetEntity;
    @Nullable
    private LivingEntity modGoalTarget;
    @Nullable
    private BlockPos nextBlockPos;
    @Nullable
    private Path pathToNextBlockPos;
    @Nullable
    private BlockPos previousBlockPos;
    @Nullable
    private BlockPos placedBlockBlockPos;
    @Nullable
    private BlockPos dugNextBlockPos;
    @Nullable
    private BlockPos breakingBlockBP;
    
    public AdvancedZombie(EntityType<? extends Zombie> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.executingCustomGoal = false;
        this.isWithinSynapticSealActivityRange = false;
        this.ticksUntilNextPathRecalculation = 0;
        this.mobHasPlayerTargetAndCanReach = false;
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(3, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(3, new SearchAndGoToPlayerGoal(this, 1.0));
        this.blockBreakingSpeedModifier = Math.round((1.0f + new Random().nextFloat()) * 10) / 10.0f;
        super.addBehaviourGoals();
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.ticksUntilNextPathRecalculation--;
            if (this.getNavigation() instanceof GroundPathNavigation) {
                if (this.getTarget() == null) {
                    if (!this.executingCustomGoal) {
                        if (this.tickCount % 60 == 0) {
                            this.findReachableTarget();
                        }
                    }
                } else {
                    if (this.getModGoalTarget() != this.getTarget()) {
                        if (this.getTarget() instanceof ServerPlayer) {
                            this.mobHasPlayerTargetAndCanReach = ZombieUtils.mobHasPlayerTargetAndCanReach(this);
                            this.modGoalTarget = this.getTarget();
                        }
                    }
                }
                if (this.getModGoalTarget() != null) {
                    if (!this.getModGoalTarget().isAlive() || this.getModGoalTarget().isSpectator() || ((ServerPlayer) this.getModGoalTarget()).isCreative()) {
                        this.resetModGoalTargetAndNextBlockPos();
                    } else {
                        if (this.pathToTargetEntity == null || this.ticksUntilNextPathRecalculation <= 0) {
                            this.createPathToTargetEntity();
                        }
                        long i = this.level().getGameTime();
                        if (this.tickCount % 40 == 0) {
                            if (!this.mobHasPlayerTargetAndCanReach) {
                                if (this.canReachTarget(this.modGoalTarget)) {
                                    this.setTarget(this.modGoalTarget);
                                }
                            }
                        }
                        if (this.previousBlockPos != this.blockPosition()) {
                            this.findCustomPath();
                            System.out.println("Zombie blockpos: " + this.blockPosition());
                            System.out.println("nextBlockPos: " + this.getNextBlockPos());
                            if (!this.executingCustomGoal) {
                                if (this.getNextBlockPos() != null) {
                                    this.pathToNextBlockPos = this.getNavigation().createPath(this.getNextBlockPos(), 0);
                                }
                            }
                        } else if (i - this.ticksUntilNextCustomPathSearch > 60) {
                            this.ticksUntilNextCustomPathSearch = i;
                            this.findCustomPath();
                        }
                    }
                }

                if (this.tickCount % 500 == 0 && this.modGoalTarget != null) {
                    this.findReachableTarget();
                }
            }
        }
        super.tick();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("BlockBreakingSpeedModifier")) {
            this.blockBreakingSpeedModifier = tag.getFloat("BlockBreakingSpeedModifier");
        }
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("BlockBreakingSpeedModifier", this.blockBreakingSpeedModifier);
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void checkDespawn() {
        if (this.modGoalTarget == null || this.level().getDifficulty() == Difficulty.PEACEFUL) {
            super.checkDespawn();
        }
    }

    @SubscribeEvent
    private void onEntityChangeDimension(EntityTravelToDimensionEvent event){
        if(event.getEntity() == this.modGoalTarget){
            this.resetModGoalTargetAndNextBlockPos();
        }
    }

    private boolean canReachTarget(LivingEntity livingEntity) {
        if(this.level().dimension() == this.modGoalTarget.level().dimension()) {
            Path path = this.getNavigation().createPath(livingEntity, 0);
            if (path != null) {
                if (livingEntity.blockPosition().equals(path.getTarget())) {
                    return path.canReach();
                }
            }
        }
        return false;
    }

    public void customGoalStarted() {
        this.executingCustomGoal = true;
    }

    public void customGoalFinished() {
        this.executingCustomGoal = false;
        this.createPathToTargetEntity();
    }

    
    public void findReachableTarget() {
        int range = ZombieUtils.isOblivionNight(this.level()) ? Config.Server.PLAYER_DETECTION_RANGE_OBLIVION_NIGHT.get() : Config.Server.PLAYER_DETECTION_RANGE.get();
        this.modGoalTarget = ZombieUtils.getNearestUnprotectedSurvivalPlayer(this, range);
    }

    public void findCustomPath() {
        if (this.modGoalTarget != null) {
            this.previousBlockPos = this.blockPosition();
            if (this.getBlockX() == this.modGoalTarget.getBlockX() && this.getBlockZ() == this.modGoalTarget.getBlockZ()) {
                if (this.getBlockY() > this.modGoalTarget.getBlockY()) {
                    this.nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
                } else if (this.getBlockY() < this.modGoalTarget.getBlockY()) {
                    this.nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 1, this.getBlockZ());
                }
            } else {
                int y = this.getBlockY();
                int targetYPos = this.modGoalTarget.getBlockY();

                Direction.Axis axis = this.setAxis();
                Direction.AxisDirection axisDirection = this.setAxisDirection(axis);

                double absXZ = Math.abs(this.getBlockX() - this.modGoalTarget.getBlockX()) + Math.abs(this.getBlockZ() - this.modGoalTarget.getBlockZ());
                double absY = Math.abs(this.getBlockY() - this.modGoalTarget.getBlockY()) + 1;

                System.out.println("mob pos: " + this.blockPosition());
                System.out.println("target pos: " + this.getModGoalTarget().blockPosition());
                System.out.println("|x1-x2|+|z1-z2|: " + absXZ);
                System.out.println("|y1-y2|: " + absY);

                if (absXZ < absY) {
                    if (y < targetYPos) {
                        this.nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                    } else if (y > targetYPos) {
                        this.nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                    } else {
                        this.nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
                    }
                } else {
                    if (absXZ == absY) {
                        if (this.getBlockY() < targetYPos) {
                            y++;
                            System.out.println("y++");
                        } else if (this.getBlockY() > targetYPos) {
                            y--;
                            System.out.println("y--");
                        }
                    }
                    if (axis == Direction.Axis.X) {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.nextBlockPos = new BlockPos(this.getBlockX() + 1, y, this.getBlockZ());
                        } else {
                            this.nextBlockPos = new BlockPos(this.getBlockX() - 1, y, this.getBlockZ());
                        }
                    }
                    if (axis == Direction.Axis.Z) {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() + 1);
                        } else {
                            this.nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() - 1);
                        }
                    }
                }

                if (this.nextBlockPos != null) {
                    if (this.level().getBlockState(this.nextBlockPos).isSolid() && this.level().getBlockState(this.nextBlockPos).getDestroySpeed(level(), this.nextBlockPos) < 0.0F) {
                        if (this.level().getBlockState(this.nextBlockPos.above(1)).isSolid()) {
                            if (this.level().getBlockState(this.nextBlockPos).getDestroySpeed(level(), this.nextBlockPos) >= 0.0F) {
                                this.nextBlockPos = this.nextBlockPos.above(1);
                            } else {
                                this.nextBlockPos = this.blockPosition().offset(0, 1, 0);
                            }
                        }
                    }
                }
            }

        }
    }

    public Path getPathToNextBlockPos() {
        return this.pathToNextBlockPos;
    }

    
    private Direction.Axis setAxis() {
        Direction.Axis axis;
        if (Math.abs(this.getX() - (int) this.modGoalTarget.getX()) >= Math.abs(this.getZ() - (int) this.modGoalTarget.getZ())) {
            axis = Direction.Axis.X;
        } else {
            axis = Direction.Axis.Z;
        }
        return axis;
    }

    
    private Direction.AxisDirection setAxisDirection(Direction.Axis direction) {
        Direction.AxisDirection axisDirection;
        if (direction == Direction.Axis.X) {
            if ((int) this.modGoalTarget.getX() - this.getX() > 0) {
                axisDirection = Direction.AxisDirection.POSITIVE;
            } else {
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        } else {
            if ((int) this.modGoalTarget.getZ() - this.getZ() > 0) {
                axisDirection = Direction.AxisDirection.POSITIVE;
            } else {
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }
        return axisDirection;
    }

    public float getBlockBreakingSpeedModifier() {
        return blockBreakingSpeedModifier;
    }

    public BlockPos getNextBlockPos() {
        return this.nextBlockPos;
    }

    public LivingEntity getModGoalTarget() {
        return this.modGoalTarget;
    }

    public void resetModGoalTargetAndNextBlockPos() {
        System.out.println("Reset Mod Goal Target");
        this.modGoalTarget = null;
        this.nextBlockPos = null;
        this.previousBlockPos = null;
        this.dugNextBlockPos = null;
        this.placedBlockBlockPos = null;
        this.pathToTargetEntity = null;
        this.pathToNextBlockPos = null;
        this.ticksUntilNextPathRecalculation = 0;
    }

    public void setIsWithinSynapticSealActivityRange(boolean isTrue) {
        this.isWithinSynapticSealActivityRange = isTrue;
    }

    public boolean getIsWithinSynapticSealActivityRange() {
        return isWithinSynapticSealActivityRange;
    }

    public void setnextBlockPos(BlockPos blockPos) {
        this.nextBlockPos = blockPos;
    }

    public void setplacedBlockBlockPos(BlockPos blockPos) {
        this.placedBlockBlockPos = blockPos;
    }

    public void setdugNextBlockPos(BlockPos blockPos) {
        this.dugNextBlockPos = blockPos;
    }

    @Nullable
    public BlockPos getplacedBlockBlockPos() {
        return this.placedBlockBlockPos;
    }

    @Nullable
    public BlockPos dugNextBlockPos() {
        return this.dugNextBlockPos;
    }

    @Nullable
    public Path pathToTargetEntity() {
        return this.pathToTargetEntity;
    }

    public void createPathToTargetEntity() {
        System.out.println("Recalculate Path");
        if (this.getModGoalTarget() != null) {
            this.pathToTargetEntity = this.getNavigation().createPath(getModGoalTarget(), 0);
        }
        this.ticksUntilNextPathRecalculation = 200;
    }

    
    public boolean getMobHasPlayerTargetAndCanReach(){
        return this.mobHasPlayerTargetAndCanReach;
    }

    public void setBreakingBlockBP(@Nullable BlockPos blockPos){
        this.breakingBlockBP = blockPos;
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if(this.breakingBlockBP != null) {
            if(!this.level().getBlockState(this.breakingBlockBP).isAir()) {
                this.level().destroyBlockProgress(this.getId(), this.breakingBlockBP, -1);
            }
        }
        super.die(pDamageSource);
    }
}
