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
    private long tickCounter;


    public SearchAndGoToPlayerGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()){
            System.out.println("Search and go canUse false1");
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null){
            if(PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget())){
                System.out.println("Search and go canUse false2");
                return false;
            }
        }

        if(ModUtils.mobHasPlayerTargetAndCanReach(this.mob)){
            System.out.println("Search and go canUse false3");
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if(!this.mob.level().getBlockState(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()){
                System.out.println("Search and go canUse false4");
                return false;
            }

            if (ModUtils.isMobStandingOnAFullBlock(this.mob) || ModUtils.hasAFullBlockCollision(this.mob, ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) { //Check if mob is standing on a block
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                //this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                    if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos() != null){
                        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos().canReach()) {
                            if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                                System.out.println("Search and go canUse false5");
                                return false;
                            }
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                                System.out.println("Search and go canUse false6");
                                return false;
                            }
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                                System.out.println("Search and go canUse false6");
                                return false;
                            }
                        } else {
                            System.out.println("Search and go canUse false7");
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        System.out.println("Search and go canUse false8");
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

            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos() != null) {
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos().canReach()) {
                    if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 2");
                        return false;
                    }
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 3");
                        return false;
                    }
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
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
        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.moveTowardsPlayer();
        }
        if(this.tickCounter > 200){
            this.tickCounter = 0;
        }
    }

    public void start(){
        System.out.println("start executing searchAndGoToPlayerGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
        //SevenDaysToSurvive.LOGGER.info("start executing searchAndGoToPlayerGoal");
       //SevendaysToSurvive.LOGGER.info("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
    }

    public void moveTowardsPlayer() {
        System.out.println("moveTowards run");
        if(this.tickCounter <= 100) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                this.mob.getNavigation().moveTo(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), this.speedModifier);
            }
        }else{
            int x = 0;
            int z = 0;
            if (this.mob.blockPosition().getX() == ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getX()){
                if(this.mob.blockPosition().getZ() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()){
                    z++;
                }else{
                    z--;
                }
            } else if (this.mob.blockPosition().getZ() == ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()) {
                if(this.mob.blockPosition().getX() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getX()){
                    x++;
                }else{
                    x--;
                }
            }
            if(x != 0 || z != 0){
                this.mob.getNavigation().moveTo(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getX() + x, ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY(), ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() + z, this.speedModifier);
            }else {
                this.mob.getNavigation().moveTo(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ(), this.speedModifier);
            }
        }
    }

    public void stop(){
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalFinished();
    }
}
