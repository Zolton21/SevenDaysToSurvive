package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected final double speedModifier;
    private final PathfinderMob mob;
    private int ticksUntilNextAttack;
    private int tickCounter;
    private ItemStack heldItem;
    @Nullable
    private Path pathToPlayer;
    private long lastCanUseCheck;
    private boolean isMoving;
    private int notMovingTickCounter;
    private BlockPos mobBP;

    public SearchAndGoToPlayerGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET, Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
        }

        /*if(!ModUtils.isMobStandingOnAFullBlock(this.mob)){
            System.out.println("SearchAndGo canUse false0");
            return false;
        }*/

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
                System.out.println("SearchAndGo canUse false4.1");
                return false;
            }
            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()){
                System.out.println("SearchAndGo canUse false4.2");
                return false;
            }
            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()){
                System.out.println("SearchAndGo canUse false4.3");
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

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            double attackDistance = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());
            double d0 = this.getAttackReachSqr(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());
            if (attackDistance <= d0){
                System.out.println("SearchAndGo canContinueToUse true1");
                return true;
            }
        }

        if (ModUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            System.out.println("SearchAndGo canContinueToUse false3");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    System.out.println("SearchAndGo canUse false4.1");
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()){
                    System.out.println("SearchAndGo canUse false4.2");
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()){
                    System.out.println("SearchAndGo canUse false4.3");
                    return false;
                }
            }
        }

        if(this.mob.getNavigation().getPath() != null){
            if(this.mob.getNavigation().isStuck()){
                System.out.println("SearchAndGo canContinueToUse false isStuck");
                return false;
            }else{
                if(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                    if (this.mob.getBlockY() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                        if (!this.mob.getNavigation().isDone()) {
                            System.out.println("SearchAndGo canContinueToUse true path isn't done");
                            return true;
                        }
                    }
                }
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
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
            System.out.println("SearchAndGo canContinueToUse true2");
            return true;
        }
        System.out.println("SearchAndGo canContinueToUse false9");
        return false;
    }

    public void tick() {
        this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
        this.tickCounter++;

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.moveTowardsPlayer();

            double d0 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), d0);
        }
        if (this.tickCounter >= 200) {
            this.tickCounter = 0;
        }

        if(this.isMoving){
            this.notMovingTickCounter = 0;
            if(this.mobBP == this.mob.blockPosition()){
                this.isMoving = false;
            }
        }else {
            this.notMovingTickCounter++;
            if(this.notMovingTickCounter >= 30){
                //this.mob.getJumpControl().jump();
                ((IZombieHelper) this.mob).sevenDaysToSurvive$createPathToTargetEntity();
                this.isMoving = true;
            }
            if(this.notMovingTickCounter >= 300) {
                this.stop();
            }
            if(this.mobBP != this.mob.blockPosition()){
                this.isMoving = true;
            }
        }
        this.mobBP = this.mob.blockPosition();
    }

    private void checkAndPerformAttack(LivingEntity target, double pDistToEnemySqr) {
        double d0 = this.getAttackReachSqr(target);
        if (pDistToEnemySqr <= d0){
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if(this.ticksUntilNextAttack <= 0) {
                this.ticksUntilNextAttack = this.adjustedTickDelay(20);
                this.mob.swing(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(target);
            }
        }

    }

    private double getAttackReachSqr(LivingEntity pAttackTarget) {
        return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
    }

    public void start() {
        System.out.println("start executing searchAndGoToPlayerGoal");
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
        this.mob.getNavigation().stop();
        this.tickCounter = 0;
        this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
        this.heldItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.YELLOW_WOOL));
        this.isMoving = true;
        this.notMovingTickCounter = 0;
        this.mobBP = this.mob.blockPosition();
    }


    public void moveTowardsPlayer() {
        System.out.println("moveTowards run");
        if(((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos() != null ||
        ((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos() != null){
            System.out.println("move if1");
            BlockPos nextBP;

            if(((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos() != null) {
                nextBP = ((IZombieHelper) this.mob).getSevenDaysToSurvive$placedBlockBlockPos().offset(0, 1, 0);
            }else{
                nextBP = ((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos();
            }

            Path path = this.mob.getNavigation().createPath(nextBP, 0);
            if(path != null) {
                this.mob.getNavigation().moveTo(path, this.speedModifier);
                if(this.mob.getNavigation().getPath().isDone()){
                    double distance = this.mob.distanceToSqr(path.getTarget().getCenter());
                    if(distance > 0.15){
                        System.out.println("Push mob to complete path");
                        Vec3 multiplication = nextBP.getCenter().subtract(this.mob.blockPosition().getCenter()).normalize().multiply(0.1, 0.1, 0.1);
                        this.mob.setDeltaMovement(multiplication);
                        this.mob.getLookControl().setLookAt(nextBP.getCenter());
                    }else{
                        ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(null);
                        ((IZombieHelper) this.mob).setSevenDaysToSurvive$placedBlockBlockPos(null);
                    }
                }
            }

        }else{
            System.out.println("move if2");
            if(this.pathToPlayer != null) {
                this.mob.getNavigation().moveTo(this.pathToPlayer, this.speedModifier);
            }
        }

    }

    public void stop() {
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalFinished();

        if(((IZombieHelper) this.mob).getSevenDaysToSurvive$placedBlockBlockPos() != null){
            ((IZombieHelper) this.mob).setSevenDaysToSurvive$placedBlockBlockPos(null);
        }
        if(((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos() != null){
            ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(null);
        }
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.heldItem);
    }
}