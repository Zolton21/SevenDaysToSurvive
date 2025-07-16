package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected final double speedModifier;
    private final PathfinderMob mob;
    private int ticksUntilNextAttack;
    @Nullable
    private Path pathToPlayer;
    private long lastCanUseCheck;
    private boolean isMoving;
    private int notMovingTickCounter;
    private BlockPos mobBP;
    private boolean ranOnce;
    private LivingEntity modGoalTarget;

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

        if(CommonConfig.Server.ZOMBIES_BUILD_AND_DIG_ONLY_ON_OBLIVION_NIGHT.get()){
            if(!ZombieUtils.isOblivionNight(this.mob.level())){
                return false;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                return false;
            }
        }

        if (((IZombieHelper)this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if(this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock) {
                return false;
            }

            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                return false;
            }
            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()){
                return false;
            }
            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()){
                return false;
            }

            if (ZombieUtils.isMobStandingOnAFullBlock(this.mob) || ZombieUtils.hasAFullBlockCollision(this.mob, ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
                        if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                            if(this.mob.blockPosition().getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                                if(!ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                                    return false;
                                }
                            }else{
                                return false;
                            }
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                            return false;
                        }
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                            return false;
                        }
                        if (this.mob.blockPosition().getY() < ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos().getY()){
                            if(ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))){
                                return false;
                            }
                        }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if(CommonConfig.Server.ZOMBIES_BUILD_AND_DIG_ONLY_ON_OBLIVION_NIGHT.get()){
            if(!ZombieUtils.isOblivionNight(this.mob.level())){
                return false;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                return false;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            double attackDistance = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());
            double d0 = this.getAttackReachSqr(((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget());
            if (attackDistance <= d0){
                return true;
            }
        }

        if (((IZombieHelper)this.mob).sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive()) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if(this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock) {
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()){
                    return false;
                }
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()){
                    return false;
                }
            }
        }

        if(this.mob.getNavigation().getPath() != null){
            if(this.mob.getNavigation().isStuck()){
                return false;
            }else{
                if(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                    if (this.mob.getBlockY() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                        if (!this.mob.getNavigation().isDone()) {
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
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                    return false;
                }
                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                    return false;
                }
                if (this.mob.blockPosition().getY() < ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                    if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void tick() {
        if(this.pathToPlayer != ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity()){
            this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
            this.ranOnce = false;
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
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
        this.mob.getNavigation().stop();
        this.ranOnce = false;
        this.pathToPlayer = ((IZombieHelper)this.mob).getSevenDaysToSurvive$pathToTargetEntity();
        this.isMoving = true;
        this.notMovingTickCounter = 0;
        this.mobBP = this.mob.blockPosition();
        if(this.mob instanceof Zombie zombie){
            zombie.setAggressive(true);
        }
    }


    public void moveTowardsPlayer() {
        if((((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos() != null && !this.mob.level().getBlockState(((IZombieHelper)this.mob).getSevenDaysToSurvive$placedBlockBlockPos()).isAir()) ||
                (((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos() != null && this.mob.level().getBlockState(((IZombieHelper) this.mob).getSevenDaysToSurvive$dugNextBlockPos()).isAir())){
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
                    this.mob.getNavigation().moveTo(this.modGoalTarget, this.speedModifier);
                }else {
                    this.runOnce();
                }
            }else{
                this.mob.getNavigation().moveTo(this.modGoalTarget, this.speedModifier);
            }
        }

    }

    private void runOnce(){
        if(!this.ranOnce) {
            this.mob.getNavigation().moveTo(this.pathToPlayer, this.speedModifier);
            this.ranOnce = true;
        }
    }

    public void stop() {
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
    }
}