package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

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
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null){
            if(PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget())){
                return false;
            }
        }

        if(ModUtils.mobHasPlayerTargetAndCanReach(this.mob)){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if(!this.mob.level().getBlockState(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()){
                return false;
            }

            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                //this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                    this.playerTargetPos = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().blockPosition();
                    this.nextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
                    this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
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
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null){
            if(PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget())){
                return false;
            }
        }

        if(ModUtils.mobHasPlayerTargetAndCanReach(this.mob)){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
            if(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    return false;
                }
            }

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
        if(this.tickCounter % 10 == 0){
            ((IZombieHelper)this.mob).sevenDaysToSurvive$findCustomPath();
        }
        if (this.tickCounter % 200 == 0) {
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            if (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
                this.playerTargetPos = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().blockPosition();
                this.nextBlockPos = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
                this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
            }
        }
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
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
                if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                    this.mob.getNavigation().moveTo(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget(), this.speedModifier);
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
    }
}
