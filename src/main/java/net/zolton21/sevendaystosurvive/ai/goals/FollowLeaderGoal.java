package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import java.util.EnumSet;

public class FollowLeaderGoal extends Goal {
    protected final double speedModifier;
    private final PathfinderMob mob;
    private long lastCanUseCheck;
    private int ticksUntilNextAttack;


    public FollowLeaderGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if (ZombieUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            return false;
        }

        if (((IZombieHelper)this.mob).getSevenDaysToSurvive$leader() != null){
            return true;
        }

        return false;
    }

    public boolean canContinueToUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            return false;
        }

        if (ZombieUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            return false;
        }

        if (((IZombieHelper)this.mob).getSevenDaysToSurvive$leader() != null){
            return true;
        }

        return false;
    }

    public void tick() {
        Zombie leader = ((IZombieHelper) this.mob).getSevenDaysToSurvive$leader();
        LivingEntity target = ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget();
        if(leader != null) {
            if(this.mob.distanceTo(leader) <= 3){
                this.mob.getNavigation().stop();
            }else{
                this.mob.getNavigation().moveTo(leader, this.speedModifier);
            }
        }

        if(target != null) {
            double d0 = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(target, d0);
        }
    }

    public void start() {
        System.out.println("Start executing FollowLeaderGoal");
        this.mob.getNavigation().stop();
    }

    public void stop() {
        System.out.println("Stop executing FollowLeaderGoal");
        this.mob.getNavigation().stop();
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
}
