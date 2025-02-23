package net.zolton21.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
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
    @Unique
    private long sevenDaysToSurvive$targetHurtTick;
    @Unique
    private long sevenDaysToSurvive$lastCanUseRun;
    @Unique
    private long sevenDaysToSurvive$lastPathCreation;
    @Unique
    private Path sevenDaysToSurvive$path;

    @Inject(method = "canUse()Z", at = @At("HEAD"), cancellable = true)
    public void canUseAdditions(CallbackInfoReturnable<Boolean> cir){
        long i = this.mob.level().getGameTime();
        if (i - this.sevenDaysToSurvive$lastCanUseRun < 20L) {

        }else {
            this.sevenDaysToSurvive$lastCanUseRun = i;
            if (!(this.mob instanceof Drowned) && !(this.mob instanceof ZombifiedPiglin)) {
                if (this.mob instanceof Zombie) {
                    if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
                        if (this.mob.getTarget() instanceof ServerPlayer player) {
                            if (player.isAlive() && !player.isSpectator() && !player.isCreative()) {
                                if (!this.sevenDaysToSurvive$conditions(player)) {
                                    cir.setReturnValue(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "canContinueToUse()Z", at = @At("HEAD"), cancellable = true)
    public void canContinueToUseAdditions(CallbackInfoReturnable<Boolean> cir){
        if (!(this.mob instanceof Drowned) && !(this.mob instanceof ZombifiedPiglin)) {
            long i = this.mob.level().getGameTime();
            if (this.mob instanceof Zombie) {
                if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
                    LivingEntity target = this.mob.getTarget();
                    if (target instanceof ServerPlayer) {
                        if (((ServerPlayer) target).isHurt()) {
                            this.sevenDaysToSurvive$targetHurtTick = i;
                        }
                        if (i - this.sevenDaysToSurvive$targetHurtTick > 20L) {
                            if (this.ticksUntilNextPathRecalculation <= 0) {
                                Path path = this.mob.getNavigation().createPath(target, 0);
                                if (path != null && path.canReach() && path.getTarget().equals(target.blockPosition())) {
                                    BlockPos nextBp = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
                                    if (nextBp != null) {
                                        if (!this.mob.level().getBlockState(nextBp).getFluidState().isEmpty()) {
                                            cir.setReturnValue(false);
                                        }
                                        if (!this.mob.level().getBlockState(nextBp.offset(0, 1, 0)).getFluidState().isEmpty()) {
                                            cir.setReturnValue(false);
                                        }
                                        if (!this.mob.level().getBlockState(nextBp.offset(0, -1, 0)).getFluidState().isEmpty()) {
                                            cir.setReturnValue(false);
                                        }
                                    }
                                } else {
                                    cir.setReturnValue(false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Unique
    private boolean sevenDaysToSurvive$conditions(ServerPlayer player){
        long i = this.mob.level().getGameTime();
        if(i - this.sevenDaysToSurvive$lastPathCreation > 20L) {
            this.sevenDaysToSurvive$lastPathCreation = i;
            this.sevenDaysToSurvive$path = this.mob.getNavigation().createPath(player, 0);
        }
        if (this.sevenDaysToSurvive$path != null) {
            if(this.mob.getTarget() != null) {
                if (this.sevenDaysToSurvive$path.getTarget().equals(this.mob.getTarget().blockPosition())) {
                    BlockPos nextBp = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
                    if(nextBp != null) {
                        if (this.mob.level().getBlockState(nextBp).getFluidState().isEmpty() &&
                                this.mob.level().getBlockState(nextBp.offset(0, 1, 0)).getFluidState().isEmpty() &&
                                this.mob.level().getBlockState(nextBp.offset(0, -1, 0)).getFluidState().isEmpty()) {
                            return this.sevenDaysToSurvive$path.canReach();
                        }
                    }
                }
            }
        }
        return false;
    }
}
