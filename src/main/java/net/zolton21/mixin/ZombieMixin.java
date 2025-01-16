package net.zolton21.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements IZombieHelper {

    @Unique
    private boolean sevenDaysToSurvive$executingCustomGoal;
    @Unique
    @Nullable
    private LivingEntity sevenDaysToSurvive$modGoalTarget;
    @Unique
    private BlockPos sevenDaysToSurvive$nextBlockPos;
    @Unique
    private float sevenDaysToSurvive$blockBreakingSpeedModifier;
    @Unique
    private Path sevenDaysToSurvive$pathToNextBlockPos;
    @Unique
    private boolean sevenDaysToSurvive$isWithinSynapticSealActivityRange;
    @Unique
    private BlockPos sevenDaysToSurvive$previousBlockPos;

    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.sevenDaysToSurvive$executingCustomGoal = false;
        this.sevenDaysToSurvive$isWithinSynapticSealActivityRange = false;
    }

    @Inject(method = "addBehaviourGoals()V", at = @At("HEAD"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(3, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(3, new SearchAndGoToPlayerGoal(this, 1.0));
        this.sevenDaysToSurvive$blockBreakingSpeedModifier = 1.0f + new Random().nextFloat();
        System.out.println("blockBreakingSpeedModifier: " + this.sevenDaysToSurvive$blockBreakingSpeedModifier);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if(this.isAlive()) {
            if (this.getNavigation() instanceof GroundPathNavigation) {
                if (this.getTarget() == null) {
                    if (!this.sevenDaysToSurvive$executingCustomGoal) {
                        if (this.tickCount % 60 == 0) {
                            this.sevenDaysToSurvive$findReachableTarget();
                        }
                    }
                } else {
                    if (this.sevenDaysToSurvive$getModGoalTarget() != this.getTarget()) {
                        if(this.getTarget() instanceof ServerPlayer) {
                            this.sevenDaysToSurvive$modGoalTarget = this.getTarget();
                        }
                    }
                }
                if (this.sevenDaysToSurvive$getModGoalTarget() != null) {
                    if (!this.sevenDaysToSurvive$getModGoalTarget().isAlive() || this.sevenDaysToSurvive$getModGoalTarget().isSpectator() || ((ServerPlayer) this.sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
                        this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
                    } else {
                        if (this.tickCount % 40 == 0) {
                            if (!ModUtils.mobHasPlayerTargetAndCanReach(this)) {
                                if (this.sevenDaysToSurvive$canReachTarget(this.sevenDaysToSurvive$modGoalTarget)) {
                                    this.setTarget(this.sevenDaysToSurvive$modGoalTarget);
                                }
                            }
                        }
                        if(this.sevenDaysToSurvive$previousBlockPos != this.blockPosition()) {
                            this.sevenDaysToSurvive$previousBlockPos = this.blockPosition();
                            this.sevenDaysToSurvive$findCustomPath();
                            System.out.println("Zombie blockpos: " + this.blockPosition());
                            System.out.println("nextBlockPos: " + this.sevenDaysToSurvive$getNextBlockPos());
                            if(!this.sevenDaysToSurvive$executingCustomGoal) {
                                if (this.sevenDaysToSurvive$getNextBlockPos() != null) {
                                    this.sevenDaysToSurvive$pathToNextBlockPos = this.getNavigation().createPath(this.sevenDaysToSurvive$getNextBlockPos(), 0);
                                }
                            }
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

    @Unique
    private boolean sevenDaysToSurvive$canReachTarget(LivingEntity livingEntity){
        Path path = this.getNavigation().createPath(livingEntity, 0);
        if(path != null) {
            if (livingEntity.blockPosition().equals(path.getTarget())) {
                return path.canReach();
            }
        }
        return false;
    }

    public void sevenDaysToSurvive$customGoalStarted(){
        this.sevenDaysToSurvive$executingCustomGoal = true;
    }

    public void sevenDaysToSurvive$customGoalFinished(){
        this.sevenDaysToSurvive$executingCustomGoal = false;
    }

    @Unique
    public void sevenDaysToSurvive$findReachableTarget(){
        this.sevenDaysToSurvive$modGoalTarget = ModUtils.getNearestUnprotectedSurvivalPlayer(this, 60);
    }

    public void sevenDaysToSurvive$findCustomPath(){
        if(this.sevenDaysToSurvive$modGoalTarget != null){
            if(this.getBlockX() == this.sevenDaysToSurvive$modGoalTarget.getBlockX() && this.getBlockZ() == this.sevenDaysToSurvive$modGoalTarget.getBlockZ()){
                if (this.getBlockY() > this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
                } else if (this.getBlockY() < this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 1, this.getBlockZ());
                }
            }else{
                int y = this.getBlockY();
                int targetYPos = this.sevenDaysToSurvive$modGoalTarget.getBlockY();

                Direction.Axis axis = this.sevenDaysToSurvive$setAxis();
                Direction.AxisDirection axisDirection = this.sevenDaysToSurvive$setAxisDirection(axis);

                double absXZ = Math.abs(Math.abs(this.getBlockX()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockX())) + Math.abs(Math.abs(this.getBlockZ()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockZ()));
                double absY = Math.abs(Math.abs(this.getBlockY()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockY()));

                System.out.println("target y: " + targetYPos + " mob y: " + this.getBlockY());
                System.out.println("||x1|-|x2||+||z1|-|z2||: " + absXZ);
                System.out.println("||y1|-|y2||: " + absY);

                if(absXZ < absY){
                    if(y < targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                    } else if (y > targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                    } else {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
                    }
                }
                else {
                    if(absXZ == absY) {
                        if (this.getBlockY() < targetYPos) {
                            y++;
                            System.out.println("y++");
                        } else if (this.getBlockY() > targetYPos) {
                            y--;
                            System.out.println("y--");
                        }
                    }
                    if(axis == Direction.Axis.X){
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() + 1, y, this.getBlockZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() - 1, y, this.getBlockZ());
                        }
                    }
                    if(axis == Direction.Axis.Z){
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() + 1);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() - 1);
                        }
                    }
                }


                if(this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).isSolid() && this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).getDestroySpeed(level(), this.sevenDaysToSurvive$nextBlockPos) < 0.0F){
                    if(this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos.above(1)).isSolid()) {
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

    public Path sevenDaysToSurvive$getPathToNextBlockPos(){
        return this.sevenDaysToSurvive$pathToNextBlockPos;
    }

    @Unique
    private Direction.Axis sevenDaysToSurvive$setAxis(){
        Direction.Axis axis;
        if(Math.abs(Math.abs(this.getX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getX())) >= Math.abs(Math.abs(this.getZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getZ()))){
            axis = Direction.Axis.X;
        }else {
            axis = Direction.Axis.Z;
        }
        return axis;
    }

    @Unique
    private Direction.AxisDirection sevenDaysToSurvive$setAxisDirection(Direction.Axis direction){
        Direction.AxisDirection axisDirection;
        if(direction == Direction.Axis.X){
            if((int)this.sevenDaysToSurvive$modGoalTarget.getX() - this.getX() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }else{
            if((int)this.sevenDaysToSurvive$modGoalTarget.getZ() - this.getZ() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }
        return axisDirection;
    }

    public float sevenDaysToSurvive$getBlockBreakingSpeedModifier() {
        return sevenDaysToSurvive$blockBreakingSpeedModifier;
    }

    public BlockPos sevenDaysToSurvive$getNextBlockPos(){
        return this.sevenDaysToSurvive$nextBlockPos;
    }

    public LivingEntity sevenDaysToSurvive$getModGoalTarget(){
        return this.sevenDaysToSurvive$modGoalTarget;
    }

    public void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos(){
        this.sevenDaysToSurvive$modGoalTarget = null;
        this.sevenDaysToSurvive$nextBlockPos = null;
    }

    public BlockPos SevenDaysToSurvive$getPreviousBlockPos(){
        return this.sevenDaysToSurvive$previousBlockPos;
    }

    public void sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(boolean isTrue){
        this.sevenDaysToSurvive$isWithinSynapticSealActivityRange = isTrue;
    }

    public boolean sevenDaysToSurvive$getIsWithinSynapticSealActivityRange(){
        return sevenDaysToSurvive$isWithinSynapticSealActivityRange;
    }
}
