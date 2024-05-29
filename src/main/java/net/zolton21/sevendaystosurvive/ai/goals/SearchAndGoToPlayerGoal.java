package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private MonsterEntity mob;
    private BlockPos nextBlockPos;
    private long tickCounter;
    private Path pathToNextBlockPos;
    private BlockPos playerTargetPos;


    public SearchAndGoToPlayerGoal(MonsterEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute() {
        if(this.mob.getAttackTarget() != null && this.mob.getAttackTarget() instanceof PlayerEntity) {
            if(this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0) != null) {
                if (this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0).reachesTarget()) {
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
                    this.playerTargetPos = this.playerTarget.getPosition();
                    ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$findCustomPath();
                    this.nextBlockPos = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos();
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                    this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                    if (this.pathToNextBlockPos != null){
                        if (this.pathToNextBlockPos.reachesTarget()) {
                            if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid()) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 2");
                                //System.out.println("should execute return false 2");
                                return false;
                            }
                            if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                               //SevendaysToSurvive.LOGGER.info("should execute return false 3");
                                //System.out.println("should execute return false 3");
                                return false;
                            }
                            if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
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

    public boolean shouldContinueExecuting() {
        if(this.mob.getAttackTarget() != null && this.mob.getAttackTarget() instanceof PlayerEntity) {
            if(this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0) != null) {
                if (this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0).reachesTarget()) {
                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 1");
                    return false;
                }
            }
        }
        if(this.playerTarget != null && this.playerTarget.isAlive()) {
            if (this.pathToNextBlockPos != null) {
                if (this.pathToNextBlockPos.reachesTarget()) {
                    if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid()) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 2");
                        return false;
                    }
                    if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 3");
                        return false;
                    }
                    if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
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
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
            }
        }
        if(this.playerTarget != null) {
            this.moveTowardsPlayer();
        }
        if(this.tickCounter > 400){
            this.tickCounter = 0;
        }
    }

    public void startExecuting(){
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
                    this.mob.getNavigator().tryMoveToEntityLiving(this.playerTarget, this.speedModifier);
                }
            } else {
                this.mob.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
            }
        }
    }

    public void resetTask(){
        //SevenDaysToSurvive.LOGGER.info("stop executing searchAndGoToPlayerGoal");
        //System.out.println("resetTask SearchAndGoToPlayerGoal");
        this.mob.getNavigator().clearPath();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.world.getBlockState(pos).getMaterial().isSolid();
    }
}
