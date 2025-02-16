package net.zolton21.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.pathfinder.Path;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MeleeAttackGoal.class)
public class MeleeAttackGoalMixin{

    @Shadow @Final protected PathfinderMob mob;

    @Shadow private int ticksUntilNextPathRecalculation;

    @Inject(method = "canUse()Z", at = @At("HEAD"), cancellable = true)
    public void canUseAdditions(CallbackInfoReturnable<Boolean> cir){
        if(this.mob.getTarget() instanceof ServerPlayer player){
            if(player.isAlive() && !player.isSpectator() && !player.isCreative()){
                if(!this.sevenDaysToSurvive$conditions(player)){
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "canContinueToUse()Z", at = @At("HEAD"), cancellable = true)
    public void canContinueToUseAdditions(CallbackInfoReturnable<Boolean> cir){
        LivingEntity target = this.mob.getTarget();
        if(target instanceof ServerPlayer player) {
            if (this.ticksUntilNextPathRecalculation <= 0) {
                Path path = this.mob.getNavigation().createPath(target, 0);
                if(path != null && path.canReach() && path.getTarget().equals(target.blockPosition())){
                }else{
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Unique
    private boolean sevenDaysToSurvive$conditions(ServerPlayer player){
        Path path = this.mob.getNavigation().createPath(player, 0);
        if (path != null) {
            if (path.getTarget().equals(this.mob.getTarget().blockPosition())) {
                return path.canReach();
            }
        }
        return false;
    }
}
