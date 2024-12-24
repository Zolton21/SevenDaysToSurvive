package net.zolton21.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
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
    private boolean sevenDaysToSurvive$canReachTarget;
    @Unique
    private Path sevenDaysToSurvive$pathToNextBlockPos;


    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.sevenDaysToSurvive$executingCustomGoal = false;
        this.sevenDaysToSurvive$canReachTarget = false;
    }

    @Inject(method = "addBehaviourGoals()V", at = @At("TAIL"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(5, new SearchAndGoToPlayerGoal(this, 1.0));

        this.sevenDaysToSurvive$blockBreakingSpeedModifier = 1.0f + new Random().nextFloat();
        System.out.println("blockBreakingSpeedModifier: " + this.sevenDaysToSurvive$blockBreakingSpeedModifier);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if(this.getNavigation() instanceof GroundPathNavigation groundPathNavigator) {
            if (this.getTarget() == null) {
                if (!this.sevenDaysToSurvive$executingCustomGoal) {
                    if(this.tickCount % 60 == 0) {
                        this.sevenDaysToSurvive$findReachableTarget();
                    }
                }
            }else{
                if(this.sevenDaysToSurvive$modGoalTarget != this.getTarget()){
                    this.sevenDaysToSurvive$modGoalTarget = this.getTarget();
                }
            }
            if (this.sevenDaysToSurvive$modGoalTarget != null) {
                if(this.tickCount % 60 == 0) {
                    if(!this.sevenDaysToSurvive$getModGoalTarget().isAlive() || this.sevenDaysToSurvive$getModGoalTarget().isSpectator() || ((Player)this.sevenDaysToSurvive$getModGoalTarget()).isCreative()){
                        this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
                    }else {
                        this.sevenDaysToSurvive$canReachTarget();
                        if (this.sevenDaysToSurvive$canReachTarget) {
                            this.setTarget(this.sevenDaysToSurvive$modGoalTarget);
                        }
                        this.sevenDaysToSurvive$findCustomPath();
                        System.out.println("Zombie blockpos: " + this.blockPosition());
                        System.out.println("nextBlockPos: " + this.sevenDaysToSurvive$getNextBlockPos());
                        this.sevenDaysToSurvive$pathToNextBlockPos = this.getNavigation().createPath(this.sevenDaysToSurvive$getNextBlockPos(), 0);
                    }
                }
            }

            if (this.tickCount % 500 == 0 && this.sevenDaysToSurvive$modGoalTarget != null) {
                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 11");
                if (this.distanceTo(this.sevenDaysToSurvive$modGoalTarget) > 50) {
                    //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 12");
                    this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
                }
            }
        }
    }

    public void sevenDaysToSurvive$canReachTarget(){
        LivingEntity target = this.sevenDaysToSurvive$modGoalTarget;
        if(target != null) {
            Path path = this.getNavigation().createPath(target.blockPosition(), 0);
            if(path != null) {
                this.sevenDaysToSurvive$canReachTarget = path.canReach();
            }
        }
        this.sevenDaysToSurvive$canReachTarget = false;
    }

    public void sevenDaysToSurvive$customGoalStarted(){
        this.sevenDaysToSurvive$executingCustomGoal = true;
    }

    public void sevenDaysToSurvive$customGoalFinished(){
        this.sevenDaysToSurvive$executingCustomGoal = false;
        this.sevenDaysToSurvive$canReachTarget = false;
        this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
    }

    public void sevenDaysToSurvive$findReachableTarget(){
        this.sevenDaysToSurvive$modGoalTarget = ModUtils.getNearestSurvivalPlayer(this, 60);
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

                System.out.println("target y: " + targetYPos + " mob y: " + this.getBlockY());
                System.out.println("||x1|-|x2||+||z1|-|z2||: " + (Math.abs(Math.abs(this.getBlockX()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockX())) + Math.abs(Math.abs(this.getBlockZ()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockZ()))));
                System.out.println("||y1|-|y2||: " + Math.abs(Math.abs(this.getBlockY()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockY())));
                if(Math.abs(Math.abs(this.getBlockX()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockX())) + Math.abs(Math.abs(this.getBlockZ()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockZ())) < Math.abs(Math.abs(this.getBlockY()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockY()))){
                    if(y < targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                    } else if (y > targetYPos) {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                    } else {
                        this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
                    }
                }
                else {
                    if(Math.abs(Math.abs(this.getBlockX()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockX())) + Math.abs(Math.abs(this.getBlockZ()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockZ())) == Math.abs(Math.abs(this.getBlockY()) - Math.abs(this.sevenDaysToSurvive$modGoalTarget.getBlockY()))) {
                        if (this.getBlockY() < this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                            y++;
                        } else if (this.getBlockY() > this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                            y--;
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

    public void setSevenDaysToSurvive$nextBlockPos(BlockPos newNextBlockPos){
        this.sevenDaysToSurvive$nextBlockPos = newNextBlockPos;
    }

    public LivingEntity sevenDaysToSurvive$getModGoalTarget(){
        return this.sevenDaysToSurvive$modGoalTarget;
    }

    public void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos(){
        this.sevenDaysToSurvive$modGoalTarget = null;
        this.sevenDaysToSurvive$nextBlockPos = null;
    }

    public boolean SevenDaysToSurvive$getCanReachTarget(){
        return this.sevenDaysToSurvive$canReachTarget;
    }
}
