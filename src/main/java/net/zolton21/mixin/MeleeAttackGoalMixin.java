package net.zolton21.mixin;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeleeAttackGoal.class)
public abstract class MeleeAttackGoalMixin{

    private CreatureEntity attacker;
    @Unique
    private int sevenDaysToSurvive$tickCounter;
    //private long endJumpTick;
    //boolean isJumping;
    /*private BlockPos targetPos;
    private LivingEntity target;
    private long lastCheckTime;
    private boolean canPenalize = false;
    private int delayCounter;
    private Path path;
    private long tickCounter;*/


    public MeleeAttackGoalMixin(CreatureEntity creatureEntity) {
        super();
        this.attacker = creatureEntity;
    }


    //@Shadow protected abstract double getAttackReachSqr(LivingEntity p_179512_1_);

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void jumpTowardsTarget(CallbackInfo ci){
        if(this.sevenDaysToSurvive$tickCounter % 400 == 0){
            this.attacker.getJumpController().setJumping();
        }
    }

    /*@Inject(method = "tick()V", at = @At("TAIL"))
    public void getTargetPos(CallbackInfo ci){
        if(this.target.world.getDimensionKey() == this.attacker.world.getDimensionKey()){
            this.targetPos = this.target.getPosition();
        }
    }
    */
    @Inject(method = "startExecuting()V", at = @At("TAIL"))
    public void startExecutingAdditions(CallbackInfo ci){
        //this.target = this.attacker.getAttackTarget();
        this.sevenDaysToSurvive$tickCounter = 0;
    }
/*
    @Inject(method = "resetTask()V", at = @At("TAIL"))
    public void resetTaskAdditions(CallbackInfo ci){
        if(this.attacker instanceof ZombieEntity){
            if (this.target != null && this.target.isAlive()){
                ((IZombieGoalFunctions)this.attacker).sevenDaysToSurvive$allowMoveToTargetLastKnowLocationGoal(this.targetPos);
            }
        }
    }*/
}
