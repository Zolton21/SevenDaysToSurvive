package net.zolton21.mixin;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.BlockPos;
import net.zolton21.sevendaystosurvive.helper.IZombieGoalFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin{

    private LivingEntity target;
    private CreatureEntity attacker;
    private BlockPos targetPos;

    public MeleeAttackGoalMixin(CreatureEntity creatureEntity) {
        super();
        this.attacker = creatureEntity;
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    public void getTargetPos(CallbackInfo ci){
        if(this.target.world.getDimensionKey() == this.attacker.world.getDimensionKey()){
            this.targetPos = this.target.getPosition();
        }
    }

    @Inject(method = "startExecuting()V", at = @At("TAIL"))
    public void startExecutingAdditions(CallbackInfo ci){
        this.target = this.attacker.getAttackTarget();
    }

    @Inject(method = "resetTask()V", at = @At("TAIL"))
    public void resetTaskAdditions(CallbackInfo ci){
        if(this.attacker instanceof ZombieEntity){
            if (this.target != null && this.target.isAlive()){
                ((IZombieGoalFunctions)this.attacker).sevenDaysToSurvive$allowMoveToTargetLastKnowLocationGoal(this.targetPos);
            }
        }
    }
}
