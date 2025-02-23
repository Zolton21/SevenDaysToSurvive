package net.zolton21.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.FollowLeaderGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements IZombieHelper {

    @Shadow
    protected abstract void registerGoals();

    @Shadow public abstract int getExperienceReward();

    @Shadow public abstract void tick();

    @Unique
    private boolean sevenDaysToSurvive$executingCustomGoal;
    @Unique
    @Nullable
    private Path sevenDaysToSurvive$pathToTargetEntity;
    @Unique
    @Nullable
    private LivingEntity sevenDaysToSurvive$modGoalTarget;
    @Unique
    @Nullable
    private BlockPos sevenDaysToSurvive$nextBlockPos;
    @Unique
    private float sevenDaysToSurvive$blockBreakingSpeedModifier;
    @Unique
    @Nullable
    private Path sevenDaysToSurvive$pathToNextBlockPos;
    @Unique
    private boolean sevenDaysToSurvive$isWithinSynapticSealActivityRange;
    @Unique
    @Nullable
    private BlockPos sevenDaysToSurvive$previousBlockPos;
    @Unique
    @Nullable
    private BlockPos sevenDaysToSurvive$placedBlockBlockPos;
    @Unique
    @Nullable
    private BlockPos sevenDaysToSurvive$dugNextBlockPos;
    @Unique
    private int sevenDaysToSurvive$ticksUntilNextPathRecalculation;
    @Unique
    private boolean sevenDaysToSurvive$isLeader;
    @Unique
    private long sevenDaysToSurvive$lastLeaderCheck;
    @Unique
    @Nullable
    private Zombie sevenDaysToSurvive$leader;
    @Unique
    private List<Zombie> sevenDaysToSurvive$group = new ArrayList<>();

    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.sevenDaysToSurvive$executingCustomGoal = false;
        this.sevenDaysToSurvive$isWithinSynapticSealActivityRange = false;
        this.sevenDaysToSurvive$ticksUntilNextPathRecalculation = 0;
    }

    @Inject(method = "addBehaviourGoals()V", at = @At("HEAD"))
    public void applyCustomAI(CallbackInfo ci) {
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            this.goalSelector.addGoal(3, new FollowLeaderGoal(this, 1.0));
            this.goalSelector.addGoal(3, new SearchAndGoToPlayerGoal(this, 1.0));
            this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
            this.goalSelector.addGoal(3, new BuildTowardsTargetGoal(this, 1.0));
            this.sevenDaysToSurvive$blockBreakingSpeedModifier = Math.round((1.0f + new Random().nextFloat()) * 10) / 10.0f;
            System.out.println("blockBreakingSpeedModifier: " + this.sevenDaysToSurvive$blockBreakingSpeedModifier);
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            this.sevenDaysToSurvive$additionalTickLogic();
        }
    }

    @Unique
    private void sevenDaysToSurvive$additionalTickLogic(){
        if (this.isAlive()) {
            if (this.sevenDaysToSurvive$isLeader) {
                this.setItemInHand(InteractionHand.OFF_HAND, Items.LIME_WOOL.getDefaultInstance());
            } else {
                this.setItemInHand(InteractionHand.OFF_HAND, Items.RED_WOOL.getDefaultInstance());
            }

            if (!this.sevenDaysToSurvive$group.isEmpty()) {//zombie is a leader of a group
                this.sevenDaysToSurvive$isLeader = true;
                this.setItemSlot(EquipmentSlot.HEAD, Items.GOLDEN_HELMET.getDefaultInstance());
            } else {//if zombie isn't leader of a group
                this.setItemSlot(EquipmentSlot.HEAD, Items.AIR.getDefaultInstance());
                long i = this.tickCount;
                if (this.onGround()) {
                    if (this.sevenDaysToSurvive$leader == null) {
                        if (i - this.sevenDaysToSurvive$lastLeaderCheck > 20L) {
                            this.sevenDaysToSurvive$lastLeaderCheck = i;
                            ZombieUtils.searchReachableZombieLeader(this);
                        }
                    } else {
                        if (i - this.sevenDaysToSurvive$lastLeaderCheck > 300L) {
                            this.sevenDaysToSurvive$lastLeaderCheck = i;
                            if (!ZombieUtils.isLeaderWithingRange(this)) {
                                Zombie zombie = this.getSevenDaysToSurvive$leader();
                                if (zombie != null) {
                                    ((IZombieHelper) zombie).sevenDaysToSurvive$removeZombieFromGroup((Zombie) (Object) this);
                                }
                                this.sevenDaysToSurvive$isLeader = true;
                            }
                        }
                    }
                }
            }

            this.sevenDaysToSurvive$ticksUntilNextPathRecalculation--;
            if (this.getNavigation() instanceof GroundPathNavigation) {
                if (this.getTarget() == null) {
                    if (!this.sevenDaysToSurvive$executingCustomGoal) {
                        if (this.tickCount % 60 == 0) {
                            this.sevenDaysToSurvive$findReachableTarget();
                        }
                    }
                } else {
                    if (this.sevenDaysToSurvive$getModGoalTarget() != this.getTarget()) {
                        if (this.getTarget() instanceof ServerPlayer) {
                            this.sevenDaysToSurvive$modGoalTarget = this.getTarget();
                        }
                    }
                }
                if (this.sevenDaysToSurvive$getModGoalTarget() != null) {
                    if (!this.sevenDaysToSurvive$getModGoalTarget().isAlive() || this.sevenDaysToSurvive$getModGoalTarget().isSpectator() || ((ServerPlayer) this.sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
                        this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
                    } else {
                        if (this.sevenDaysToSurvive$pathToTargetEntity == null || this.sevenDaysToSurvive$ticksUntilNextPathRecalculation <= 0) {
                            this.sevenDaysToSurvive$createPathToTargetEntity();
                        }

                        if (this.tickCount % 40 == 0) {
                            if (!ZombieUtils.mobHasPlayerTargetAndCanReach(this)) {
                                if (this.sevenDaysToSurvive$canReachTarget(this.sevenDaysToSurvive$modGoalTarget)) {
                                    this.setTarget(this.sevenDaysToSurvive$modGoalTarget);
                                }
                            }
                        }
                        if (this.sevenDaysToSurvive$previousBlockPos != this.blockPosition()) {
                            this.sevenDaysToSurvive$findCustomPath();
                            System.out.println("Zombie blockpos: " + this.blockPosition());
                            System.out.println("nextBlockPos: " + this.sevenDaysToSurvive$getNextBlockPos());
                            if (!this.sevenDaysToSurvive$executingCustomGoal) {
                                if (this.sevenDaysToSurvive$getNextBlockPos() != null) {
                                    this.sevenDaysToSurvive$pathToNextBlockPos = this.getNavigation().createPath(this.sevenDaysToSurvive$getNextBlockPos(), 0);
                                }
                            }
                        } else if (this.sevenDaysToSurvive$ticksUntilNextPathRecalculation % 60 == 0) {
                            this.sevenDaysToSurvive$findCustomPath();
                            System.out.println("Zombie blockpos: " + this.blockPosition());
                            System.out.println("nextBlockPos: " + this.sevenDaysToSurvive$getNextBlockPos());
                        }
                    }
                }

                if (this.tickCount % 500 == 0 && this.sevenDaysToSurvive$modGoalTarget != null) {
                    if (this.distanceTo(this.sevenDaysToSurvive$modGoalTarget) > 50) {
                        this.sevenDaysToSurvive$findReachableTarget();
                    }
                }
            }
        }
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"))
    private void onLoad(CompoundTag tag, CallbackInfo ci) {
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            if (tag.contains("BlockBreakingSpeedModifier")) {
                this.sevenDaysToSurvive$blockBreakingSpeedModifier = tag.getFloat("BlockBreakingSpeedModifier");
            }
            this.sevenDaysToSurvive$group.clear();
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSave(CompoundTag tag, CallbackInfo ci){
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            tag.putFloat("BlockBreakingSpeedModifier", this.sevenDaysToSurvive$blockBreakingSpeedModifier);
        }
    }

    @Override
    public void checkDespawn() {
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            if (this.sevenDaysToSurvive$modGoalTarget == null && this.sevenDaysToSurvive$group.isEmpty()) {
                super.checkDespawn();
            }
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!((Object) this instanceof Drowned) && !((Object) this instanceof ZombifiedPiglin)) {
            if (this.sevenDaysToSurvive$isLeader) {
                if (!this.sevenDaysToSurvive$group.isEmpty()) {
                    Zombie zombie = this.sevenDaysToSurvive$group.get(0);
                    this.sevenDaysToSurvive$group.remove(zombie);
                    List<Zombie> group_copy = new ArrayList<>(this.sevenDaysToSurvive$group);
                    ((IZombieHelper) zombie).sevenDaysToSurvive$setAsALeader(group_copy);

                }
            } else {
                Zombie zombie = this.getSevenDaysToSurvive$leader();
                if (zombie != null) {
                    ((IZombieHelper) zombie).sevenDaysToSurvive$removeZombieFromGroup((Zombie) (Object) this);
                }
            }
        }
        super.die(pDamageSource);
    }

    @Unique
    private boolean sevenDaysToSurvive$canReachTarget(LivingEntity livingEntity) {
        if(this.level().dimension() == this.sevenDaysToSurvive$modGoalTarget.level().dimension()) {
            Path path = this.getNavigation().createPath(livingEntity, 0);
            if (path != null) {
                if (livingEntity.blockPosition().equals(path.getTarget())) {
                    return path.canReach();
                }
            }
        }
        return false;
    }

    public void sevenDaysToSurvive$customGoalStarted() {
        this.sevenDaysToSurvive$executingCustomGoal = true;
    }

    public void sevenDaysToSurvive$customGoalFinished() {
        this.sevenDaysToSurvive$executingCustomGoal = false;
        this.sevenDaysToSurvive$createPathToTargetEntity();
    }

    @Unique
    public void sevenDaysToSurvive$findReachableTarget() {
        if(this.sevenDaysToSurvive$isLeader) {
            this.sevenDaysToSurvive$modGoalTarget = ZombieUtils.getNearestUnprotectedSurvivalPlayer(this, 60);
        }
    }

    public void sevenDaysToSurvive$findCustomPath() {
        if (this.sevenDaysToSurvive$modGoalTarget != null) {
            this.sevenDaysToSurvive$previousBlockPos = this.blockPosition();
            if (this.getBlockX() == this.sevenDaysToSurvive$modGoalTarget.getBlockX() && this.getBlockZ() == this.sevenDaysToSurvive$modGoalTarget.getBlockZ()) {
                if (this.getBlockY() > this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
                } else if (this.getBlockY() < this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 1, this.getBlockZ());
                }
            } else {
                int y = this.getBlockY();
                int targetYPos = this.sevenDaysToSurvive$modGoalTarget.getBlockY();

                Direction.Axis axis = this.sevenDaysToSurvive$setAxis();
                Direction.AxisDirection axisDirection = this.sevenDaysToSurvive$setAxisDirection(axis);

                double absXZ = Math.abs(this.getBlockX() - this.sevenDaysToSurvive$modGoalTarget.getBlockX()) + Math.abs(this.getBlockZ() - this.sevenDaysToSurvive$modGoalTarget.getBlockZ());
                double absY = Math.abs(this.getBlockY() - this.sevenDaysToSurvive$modGoalTarget.getBlockY()) + 1;

                System.out.println("mob pos: " + this.blockPosition());
                System.out.println("target pos: " + this.sevenDaysToSurvive$getModGoalTarget().blockPosition());
                System.out.println("|x1-x2|+|z1-z2|: " + absXZ);
                System.out.println("|y1-y2|: " + absY);

                if (absXZ < absY) {
                    if (y < targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                    } else if (y > targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                    } else {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
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
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() + 1, y, this.getBlockZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() - 1, y, this.getBlockZ());
                        }
                    }
                    if (axis == Direction.Axis.Z) {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() + 1);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() - 1);
                        }
                    }
                }

                if (this.sevenDaysToSurvive$nextBlockPos != null) {
                    if (this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).isSolid() && this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).getDestroySpeed(level(), this.sevenDaysToSurvive$nextBlockPos) < 0.0F) {
                        if (this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos.above(1)).isSolid()) {
                            if (this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).getDestroySpeed(level(), this.sevenDaysToSurvive$nextBlockPos) >= 0.0F) {
                                this.sevenDaysToSurvive$nextBlockPos = this.sevenDaysToSurvive$nextBlockPos.above(1);
                            } else {
                                this.sevenDaysToSurvive$nextBlockPos = this.blockPosition().offset(0, 1, 0);
                            }
                        }
                    }
                }
            }

        }
    }

    public Path sevenDaysToSurvive$getPathToNextBlockPos() {
        return this.sevenDaysToSurvive$pathToNextBlockPos;
    }

    @Unique
    private Direction.Axis sevenDaysToSurvive$setAxis() {
        Direction.Axis axis;
        if (Math.abs(this.getX() - (int) this.sevenDaysToSurvive$modGoalTarget.getX()) >= Math.abs(this.getZ() - (int) this.sevenDaysToSurvive$modGoalTarget.getZ())) {
            axis = Direction.Axis.X;
        } else {
            axis = Direction.Axis.Z;
        }
        return axis;
    }

    @Unique
    private Direction.AxisDirection sevenDaysToSurvive$setAxisDirection(Direction.Axis direction) {
        Direction.AxisDirection axisDirection;
        if (direction == Direction.Axis.X) {
            if ((int) this.sevenDaysToSurvive$modGoalTarget.getX() - this.getX() > 0) {
                axisDirection = Direction.AxisDirection.POSITIVE;
            } else {
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        } else {
            if ((int) this.sevenDaysToSurvive$modGoalTarget.getZ() - this.getZ() > 0) {
                axisDirection = Direction.AxisDirection.POSITIVE;
            } else {
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }
        return axisDirection;
    }

    public float sevenDaysToSurvive$getBlockBreakingSpeedModifier() {
        return sevenDaysToSurvive$blockBreakingSpeedModifier;
    }

    public BlockPos sevenDaysToSurvive$getNextBlockPos() {
        return this.sevenDaysToSurvive$nextBlockPos;
    }

    public LivingEntity sevenDaysToSurvive$getModGoalTarget() {
        return this.sevenDaysToSurvive$modGoalTarget;
    }

    public void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos() {
        System.out.println("Reset Mod Goal Target");
        this.sevenDaysToSurvive$modGoalTarget = null;
        this.sevenDaysToSurvive$nextBlockPos = null;
        this.sevenDaysToSurvive$previousBlockPos = null;
        this.sevenDaysToSurvive$dugNextBlockPos = null;
        this.sevenDaysToSurvive$placedBlockBlockPos = null;
        this.sevenDaysToSurvive$pathToTargetEntity = null;
        this.sevenDaysToSurvive$pathToNextBlockPos = null;
    }

    public void sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(boolean isTrue) {
        this.sevenDaysToSurvive$isWithinSynapticSealActivityRange = isTrue;
    }

    public boolean sevenDaysToSurvive$getIsWithinSynapticSealActivityRange() {
        return sevenDaysToSurvive$isWithinSynapticSealActivityRange;
    }

    public void setSevenDaysToSurvive$nextBlockPos(BlockPos blockPos) {
        this.sevenDaysToSurvive$nextBlockPos = blockPos;
    }

    public void setSevenDaysToSurvive$placedBlockBlockPos(BlockPos blockPos) {
        this.sevenDaysToSurvive$placedBlockBlockPos = blockPos;
    }

    public void setSevenDaysToSurvive$dugNextBlockPos(BlockPos blockPos) {
        this.sevenDaysToSurvive$dugNextBlockPos = blockPos;
    }

    @Nullable
    public BlockPos getSevenDaysToSurvive$placedBlockBlockPos() {
        return this.sevenDaysToSurvive$placedBlockBlockPos;
    }

    @Nullable
    public BlockPos getSevenDaysToSurvive$dugNextBlockPos() {
        return this.sevenDaysToSurvive$dugNextBlockPos;
    }

    @Nullable
    public Path getSevenDaysToSurvive$pathToTargetEntity() {
        return this.sevenDaysToSurvive$pathToTargetEntity;
    }

    public void sevenDaysToSurvive$createPathToTargetEntity() {
        System.out.println("Recalculate Path");
        if (this.sevenDaysToSurvive$getModGoalTarget() != null) {
            this.sevenDaysToSurvive$pathToTargetEntity = this.getNavigation().createPath(sevenDaysToSurvive$getModGoalTarget(), 0);
        }
        this.sevenDaysToSurvive$ticksUntilNextPathRecalculation = 300;
    }

    public boolean sevenDaysToSurvive$isLeader() {
        return this.sevenDaysToSurvive$isLeader;
    }

    public void sevenDaysToSurvive$setLeaderForZombie(@Nullable Zombie zombie){
        this.sevenDaysToSurvive$leader = zombie;
        this.sevenDaysToSurvive$isLeader = false;
    }

    public void sevenDaysToSurvive$addZombieToGroup(Zombie zombie) {
        this.sevenDaysToSurvive$group.add(zombie);
        ((IZombieHelper) zombie).sevenDaysToSurvive$setLeaderForZombie((Zombie) (Object) this);
    }

    public void sevenDaysToSurvive$removeZombieFromGroup(Zombie zombie) {
        this.sevenDaysToSurvive$group.remove(zombie);
        ((IZombieHelper) zombie).sevenDaysToSurvive$setLeaderForZombie(null);
    }

    public void sevenDaysToSurvive$setAsALeader(List<Zombie> zombieList){
        this.sevenDaysToSurvive$isLeader = true;
        this.sevenDaysToSurvive$group.clear();
        this.sevenDaysToSurvive$group.addAll(zombieList);
        for(Zombie zombie: zombieList){
            ((IZombieHelper) zombie).sevenDaysToSurvive$setLeaderForZombie((Zombie) (Object) this);
        }
    }

    public void sevenDaysToSurvive$strayAlone(){
        this.sevenDaysToSurvive$isLeader = true;
    }

    @Nullable
    public Zombie getSevenDaysToSurvive$leader(){
        return this.sevenDaysToSurvive$leader;
    }
}