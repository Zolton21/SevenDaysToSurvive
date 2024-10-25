package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    private LivingEntity playerTarget;
    protected final double speedModifier;
    private final PathfinderMob mob;
    private BlockPos nextBlockPos;
    private long tickCounter;
    private Path pathToNextBlockPos;
    private BlockPos playerTargetPos;


    public SearchAndGoToPlayerGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                    //System.out.println("should execute return false 1");
                   //SevendaysToSurvive.LOGGER.info("should execute return false 1");
                    return false;
                }
            }
        }
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (this.playerTarget != null && this.playerTarget.isAlive()) {
                    this.playerTargetPos = this.playerTarget.blockPosition();
                    ((IZombieHelper) this.mob).sevenDaysToSurvive$findCustomPath();
                    this.nextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
                    if (this.pathToNextBlockPos != null){
                        if (this.pathToNextBlockPos.canReach()) {
                            if (!ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 2");
                                //System.out.println("should execute return false 2");
                                return false;
                            }
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 3");
                                //System.out.println("should execute return false 3");
                                return false;
                            }
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 4");
                                //System.out.println("should execute return false 4");
                                return false;
                            }
                        } else {
                           //SevendaysToSurvive.LOGGER.info("should execute return false 5");
                            //System.out.println("should execute return false 5");
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
       //SevendaysToSurvive.LOGGER.info("should execute return false 6");
        //System.out.println("should execute return false 6");
        return false;
    }

    public boolean canContinueToUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 1");
                    return false;
                }
            }
        }
        if(this.playerTarget != null && this.playerTarget.isAlive()) {
            if (this.pathToNextBlockPos != null) {
                if (this.pathToNextBlockPos.canReach()) {
                    if (!ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 2");
                        return false;
                    }
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 3");
                        return false;
                    }
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 4");
                        return false;
                    }
                } else {
                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 5");
                    return false;
                }
            }
            return true;
        }
       //SevendaysToSurvive.LOGGER.info("should continue executing return false 6");
        return false;
    }

    public void tick() {
        this.tickCounter++;
        if (this.tickCounter % 200 == 0) {
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget != null) {
                this.playerTargetPos = this.playerTarget.blockPosition();
                ((IZombieHelper)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
                GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
            }
        }
        if(this.playerTarget != null) {
            this.moveTowardsPlayer();
        }
        if(this.tickCounter > 400){
            this.tickCounter = 0;
        }
    }

    public void start(){
        System.out.println("start executing searchAndGoToPlayerGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        //SevenDaysToSurvive.LOGGER.info("start executing searchAndGoToPlayerGoal");
       //SevendaysToSurvive.LOGGER.info("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
    }

    public void moveTowardsPlayer() {
        //System.out.println("moveTowards run");
        if(this.playerTargetPos != null) {
            BlockPos blockPos = this.playerTargetPos;
            //Goal lastGoal = ((IZombieCustomTarget) this.mob).getSevenDaysToSurvive$lastExecutingGoal();
            if (this.tickCounter > 300) {
                if(this.playerTarget != null && this.playerTarget.isAlive()) {
                    this.mob.getNavigation().moveTo(this.playerTarget, this.speedModifier);
                }
            } else {
                this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
            }
        }
    }

    public void stop(){
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieHelper)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
    }
}
