package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected final double speedModifier;
    private final PathfinderMob mob;
    private long tickCounter;
    private boolean isMoving;
    private int notMovingTickCounter;


    public SearchAndGoToPlayerGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            System.out.println("SearchAndGo canUse false1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                System.out.println("SearchAndGo canUse false2");
                return false;
            }
        }

        if (ModUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            System.out.println("SearchAndGo canUse false3");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                System.out.println("SearchAndGo canUse false4");
                return false;
            }

            if (ModUtils.isMobStandingOnAFullBlock(this.mob) || ModUtils.hasAFullBlockCollision(this.mob, ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) { //Check if mob is standing on a block
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                //this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                    if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos() != null) {
                        if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                            System.out.println("SearchAndGo canUse false5");
                            return false;
                        }
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                            System.out.println("SearchAndGo canUse false6");
                            return false;
                        }
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                            System.out.println("SearchAndGo canUse false7");
                            return false;
                        }
                        if (this.mob.blockPosition().getY() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY()){
                            if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))){
                                System.out.println("SearchAndGo canUse false8");
                                return false;
                            }
                        }
                    }
                    System.out.println("SearchAndGo canUse true");
                    return true;
                }
            }
        }
        System.out.println("SearchAndGo canUse false8");
        return false;
    }

    public boolean canContinueToUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            System.out.println("SearchAndGo canContinueToUse false1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                System.out.println("SearchAndGo canContinueToUse false2");
                return false;
            }
        }

        if (ModUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            System.out.println("SearchAndGo canContinueToUse false3");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    System.out.println("SearchAndGo canContinueToUse false4");
                    return false;
                }
            }

            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos() != null) {
                if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                    System.out.println("SearchAndGo canContinueToUse false5");
                    return false;
                }
                if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                    System.out.println("SearchAndGo canContinueToUse false6");
                    return false;
                }
                if (ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                    System.out.println("SearchAndGo canContinueToUse false7");
                    return false;
                }
                if (this.mob.blockPosition().getY() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY()){
                    if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))){
                        System.out.println("SearchAndGo canContinueToUse false8");
                        return false;
                    }
                }
            }
            System.out.println("SearchAndGo canContinueToUse true");
            return true;
        }
        System.out.println("SearchAndGo canContinueToUse false9");
        return false;
    }

    public void tick() {
        this.isMoving = this.mob.blockPosition() != ((IZombieHelper) this.mob).SevenDaysToSurvive$getPreviousBlockPos();

        if(!this.isMoving){
            this.notMovingTickCounter++;
        }else{
            this.notMovingTickCounter = 0;
        }

        this.tickCounter++;
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.moveTowardsPlayer();
        }
        if (this.tickCounter > 100) {
            this.tickCounter = 0;
        }
    }

    public void start() {
        System.out.println("start executing searchAndGoToPlayerGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
        //SevenDaysToSurvive.LOGGER.info("start executing searchAndGoToPlayerGoal");
        //SevendaysToSurvive.LOGGER.info("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
        this.isMoving = false;
        this.notMovingTickCounter = 0;
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
    }

    public void moveTowardsPlayer() {
        System.out.println("moveTowards run");
        Path path = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
        if(this.notMovingTickCounter <= 20) {
            if (path != null) {
                this.mob.getNavigation().moveTo(path, this.speedModifier);
            }
        }else{
            BlockPos nextBP = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
            this.mob.getLookControl().setLookAt(nextBP.getCenter().add(0, 1, 0));

            if(nextBP.getX() == this.mob.getBlockX()){
                if(nextBP.getZ() > this.mob.getBlockZ()){
                    this.mob.setYRot(0);
                }else{
                    this.mob.setYRot(180);
                    this.mob.setYRot(-180);
                }
            }else{
                if(nextBP.getX() > this.mob.getBlockX()){
                    this.mob.setYRot(-90);
                }else{
                    this.mob.setYRot(90);
                }
            }

            Vec3 multiplication = nextBP.getCenter().subtract(this.mob.blockPosition().getCenter()).normalize().multiply(0.25, 0.25, 0.25);
            this.mob.setDeltaMovement(multiplication);
        }
    }

    public void stop() {
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalFinished();
    }
}