package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected final double speedModifier;
    private final PathfinderMob mob;
    private int ticksUntilNextAttack;
    private ItemStack heldItem;
    @Nullable
    private Path pathToPlayer;
    private long lastCanUseCheck;
    private boolean isMoving;
    private int notMovingTickCounter;
    private BlockPos mobBP;
    private boolean runnedOnce;
    private Path pathToNextBlockPos;
    private LivingEntity modGoalTarget;
    private long runOnceTick;
    private long tickCounter;

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

        if (((IZombieHelper)this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            System.out.println("SearchAndGo canUse false3");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            System.out.println("SearchAndGo canUse false 4");
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            System.out.println("SearchAndGo canUse false 5");
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

            if (ZombieUtils.isMobStandingOnAFullBlock(this.mob) || ZombieUtils.hasAFullBlockCollision(this.mob, ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                        if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                            if(this.mob.blockPosition().getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                                if(!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                                    System.out.println("SearchAndGo canUse false5.1");
                                    return false;
                                }
                            }else{
                                System.out.println("SearchAndGo canUse false5.2");
                                return false;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                            System.out.println("SearchAndGo canUse false6");
                            return false;
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                            System.out.println("SearchAndGo canUse false7");
                            return false;
                        }
                        if (this.mob.blockPosition().getY() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY()){
                            if(ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))){
                                System.out.println("SearchAndGo canUse false8");
                                return false;
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

        if (((IZombieHelper)this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            System.out.println("SearchAndGo canContinueToUse false3");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            System.out.println("SearchAndGo cancel 2.1");
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            System.out.println("SearchAndGo cancel 2.2");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    System.out.println("SearchAndGo cancel false4.1");
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()){
                    System.out.println("SearchAndGo cancel false4.2");
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()){
                    System.out.println("SearchAndGo cancel false4.3");
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
                if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                    if (this.mob.blockPosition().getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) {
                            if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                                System.out.println("SearchAndGo canContinueToUse false5.1");
                                return false;
                            }
                        }
                    } else {
                        System.out.println("SearchAndGo canContinueToUse false5.2");
                        System.out.println("mob blockpos: " + this.mob.blockPosition());
                        System.out.println("next blockpos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
                        return false;
                    }
                }
                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                    System.out.println("SearchAndGo canContinueToUse false6");
                    return false;
                }
                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                    System.out.println("SearchAndGo canContinueToUse false7");
                    return false;
                }
                if (this.mob.blockPosition().getY() < ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                    if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
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
        if(this.pathToPlayer != ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity()){
            this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
            this.runnedOnce = false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            this.moveTowardsPlayer();

            double d0 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget(), d0);
        }

        if(this.isMoving){
            this.notMovingTickCounter = 0;
            if(this.mobBP == this.mob.blockPosition()){
                this.isMoving = false;
            }
        }else {
            this.notMovingTickCounter++;
            if(this.notMovingTickCounter >= 60) {
                ((IZombieHelper) this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
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
        this.runnedOnce = false;
        this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
        this.heldItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.YELLOW_WOOL));
        this.isMoving = true;
        this.notMovingTickCounter = 0;
        this.mobBP = this.mob.blockPosition();
        if(this.mob instanceof Zombie zombie){
            zombie.setAggressive(true);
        }
    }


    public void moveTowardsPlayer() {
        System.out.println("moveTowards run");
        if((((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos() != null && !this.mob.level().getBlockState(((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos()).isAir()) ||
                (((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos() != null && this.mob.level().getBlockState(((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos()).isAir())){
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
                if(this.mob.getNavigation().getPath() != null) {
                    if (this.mob.getNavigation().getPath().isDone()) {
                        double distance = this.mob.distanceToSqr(path.getTarget().getCenter());
                        if (distance > 0.15) {
                            System.out.println("Push mob to complete path");
                            Vec3 multiplication = nextBP.getCenter().subtract(this.mob.blockPosition().getCenter()).normalize().multiply(0.1, 0.1, 0.1);
                            this.mob.setDeltaMovement(multiplication);
                            this.mob.getLookControl().setLookAt(nextBP.getCenter());
                        } else {
                            ((IZombieHelper) this.mob).setSevenDaysToSurvive$dugNextBlockPos(null);
                            ((IZombieHelper) this.mob).setSevenDaysToSurvive$placedBlockBlockPos(null);
                        }
                    }
                }
            }

        }else{
            this.modGoalTarget = ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget();
            if(this.pathToPlayer != null && this.notMovingTickCounter <= 40) {
                if(this.pathToPlayer.canReach()){
                    System.out.println("move if2");
                    this.mob.getNavigation().moveTo(this.modGoalTarget, this.speedModifier);
                }else {
                    System.out.println("move if3");
                    this.runOnce();
                }
            }else{
                System.out.println("move if4");
                this.mob.getNavigation().moveTo(this.modGoalTarget, this.speedModifier);
            }
        }

    }

    private void runOnce(){
        if(!this.runnedOnce) {
            this.mob.getNavigation().moveTo(this.pathToPlayer, this.speedModifier);
            this.runnedOnce = true;
        }
    }

    public void stop() {
        System.out.println("stop executing SearchAndGoToPlayerGoal");
        this.mob.getNavigation().stop();

        if(this.mob instanceof Zombie zombie){
            zombie.setAggressive(false);
        }

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