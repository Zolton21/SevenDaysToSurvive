package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
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
    private BlockPos previousBlockPos;
    private Path pathToPlayer;

    public SearchAndGoToPlayerGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if(!ModUtils.isMobStandingOnAFullBlock(this.mob)){
            System.out.println("SearchAndGo canUse false0");
            return false;
        }

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

            if (ModUtils.isMobStandingOnAFullBlock(this.mob) || ModUtils.hasAFullBlockCollision(this.mob, ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                    if (((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos() != null) {
                        if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                            if(this.mob.blockPosition().getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                                if(!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                                    System.out.println("SearchAndGo canUse false5.1");
                                    return false;
                                }
                            }else{
                                System.out.println("SearchAndGo canUse false5.2");
                                return false;
                            }
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

        if(this.mob.getNavigation().getPath() != null){
            if(this.mob.getNavigation().isStuck()){
                System.out.println("SearchAndGo canContinueToUse false3.1 isStuck");
                return false;
            }else{
                if(this.mob.getBlockY() >= ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                    if (!this.mob.getNavigation().isDone()) {
                        System.out.println("SearchAndGo canContinueToUse true path isn't done");
                        return true;
                    }
                }
            }
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
                    if(this.mob.blockPosition().getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) {
                            if (!ModUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                                System.out.println("SearchAndGo canContinueToUse false5.1");
                                return false;
                            }
                        }
                    }else{
                        System.out.println("SearchAndGo canContinueToUse false5.2");
                        System.out.println("mob blockpos: " + this.mob.blockPosition());
                        System.out.println("next blockpos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
                        return false;
                    }
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
        if(this.previousBlockPos != null) {
            this.isMoving = this.mob.blockPosition() != this.previousBlockPos;
        }
        this.previousBlockPos = ((IZombieHelper) this.mob).SevenDaysToSurvive$getPreviousBlockPos();

        if(!this.isMoving){
            this.notMovingTickCounter++;
        }else{
            this.notMovingTickCounter = 0;
        }
        this.tickCounter++;
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.moveTowardsPlayer();
        }
        if (this.tickCounter > 200) {
            this.pathToPlayer = this.mob.getNavigation().createPath(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), 0);
            this.tickCounter = 0;
        }
    }

    public void start() {
        System.out.println("start executing searchAndGoToPlayerGoal");
        this.isMoving = false;
        this.notMovingTickCounter = 0;
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
        this.pathToPlayer = this.mob.getNavigation().createPath(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), 0);
    }

    public void moveTowardsPlayer() {
        System.out.println("moveTowards run");
        if(this.notMovingTickCounter <= 60 && (this.mob.getBlockY() >= ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY())) {
            System.out.println("move to player");
            if (this.pathToPlayer == null) {
                this.pathToPlayer = this.mob.getNavigation().createPath(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), 0);
            }else{
                this.mob.getNavigation().moveTo(this.pathToPlayer, this.speedModifier);
            }
        }else{//move to the center of nextBP
            System.out.println("move to nextBP");
            BlockPos nextBP = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
            Vec3 multiplication = nextBP.getCenter().subtract(this.mob.blockPosition().getCenter()).normalize().multiply(0.2, 0.2, 0.2);
            this.mob.setDeltaMovement(multiplication);

            if(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
                this.mob.getLookControl().setLookAt(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), 30.0F, 30.0F);
            }
        }
    }

    public void stop() {
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalFinished();
    }
}