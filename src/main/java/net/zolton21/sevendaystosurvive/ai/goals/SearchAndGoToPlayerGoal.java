package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
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
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
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
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if (this.isStandingOnBlock()) {
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (this.playerTarget != null && this.playerTarget.isAlive()) {
                    this.playerTargetPos = this.playerTarget.blockPosition();
                    ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$runFindCustomPath();
                    this.nextBlockPos = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos();
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
                    if (this.pathToNextBlockPos != null){
                        if (this.pathToNextBlockPos.canReach()) {
                            if (!this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getMaterial().isSolid()) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 2");
                                //System.out.println("should execute return false 2");
                                return false;
                            }
                            if (this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getMaterial().isSolid()) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 3");
                                //System.out.println("should execute return false 3");
                                return false;
                            }
                            if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
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
            if(this.mob.getNavigation().createPath(this.mob.getTarget().getPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().getPosition(), 0).reachesTarget()) {
                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 1");
                    return false;
                }
            }
        }
        if(this.playerTarget != null && this.playerTarget.isAlive()) {
            if (this.pathToNextBlockPos != null) {
                if (this.pathToNextBlockPos.canReach()) {
                    if (!this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getMaterial().isSolid()) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 2");
                        return false;
                    }
                    if (this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getMaterial().isSolid()) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 3");
                        return false;
                    }
                    if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
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
            this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget != null) {
                this.playerTargetPos = this.playerTarget.getPosition();
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
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
        //System.out.println("start executing searchAndGoToPlayerGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        //SevenDaysToSurvive.LOGGER.info("start executing searchAndGoToPlayerGoal");
       //SevendaysToSurvive.LOGGER.info("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalStarted();
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
        //SevenDaysToSurvive.LOGGER.info("stop executing searchAndGoToPlayerGoal");
        //System.out.println("resetTask SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getBlockX(), this.mob.getBlockY() - 1, this.mob.getBlockZ());
        return this.mob.level().getBlockState(pos).getMaterial().isSolid();
    }
}
