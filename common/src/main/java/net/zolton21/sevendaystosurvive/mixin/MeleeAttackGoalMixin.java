package net.zolton21.sevendaystosurvive.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.TrapDoorBlock;
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
public class MeleeAttackGoalMixin {

    @Shadow
    @Final
    protected PathfinderMob mob;
    @Unique
    private long sevenDaysToSurvive$lastCanUseRun;
    @Unique
    private long sevenDaysToSurvive$lastPathCreation;
    @Unique
    private Path sevenDaysToSurvive$path;

    @Inject(method = "canUse()Z", at = @At("HEAD"), cancellable = true)
    public void canUseAdditions(CallbackInfoReturnable<Boolean> cir) {
        long i = this.mob.level().getGameTime();
        if (i - this.sevenDaysToSurvive$lastCanUseRun > 20L) {
            this.sevenDaysToSurvive$lastCanUseRun = i;
            if (this.mob.getType() == EntityType.ZOMBIE || this.mob.getType() == EntityType.HUSK) {
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
        } else {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canContinueToUse()Z", at = @At("HEAD"), cancellable = true)
    public void canContinueToUseAdditions(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob.getType() == EntityType.ZOMBIE || this.mob.getType() == EntityType.HUSK) {
            long i = this.mob.level().getGameTime();
            if (this.mob instanceof Zombie) {
                if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
                    if (this.mob.getTarget() instanceof ServerPlayer serverPlayer) {


                        BlockPos nextBP = this.sevenDaysToSurvive$findNextBP();
                        if(this.mob.onGround()) {
                            if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock ||
                                    this.mob.level().getBlockState(nextBP.offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock) {
                                cir.setReturnValue(false);
                            }
                        }
                        if (i - this.sevenDaysToSurvive$lastPathCreation > 60L) {
                            this.sevenDaysToSurvive$path = this.mob.getNavigation().createPath(serverPlayer, 0);
                        }
                        if (this.sevenDaysToSurvive$path != null && this.sevenDaysToSurvive$path.canReach()/* && this.sevenDaysToSurvive$path.getTarget().equals(serverPlayer.blockPosition())*/) {
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

    @Unique
    private boolean sevenDaysToSurvive$conditions(ServerPlayer player) {
        long i = this.mob.level().getGameTime();
        BlockPos nextBP = this.sevenDaysToSurvive$findNextBP();
        if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock ||
                this.mob.level().getBlockState(nextBP.offset(0, -1, 0)).getBlock() instanceof TrapDoorBlock) {
            return false;
        }

        if (i - this.sevenDaysToSurvive$lastPathCreation > 20L) {
            this.sevenDaysToSurvive$lastPathCreation = i;
            this.sevenDaysToSurvive$path = this.mob.getNavigation().createPath(player, 0);
        }
        if (this.sevenDaysToSurvive$path != null) {
            if (this.mob.getTarget() != null) {
                BlockPos nextBp = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
                if (nextBp != null) {
                    if (this.mob.level().getBlockState(nextBp).getFluidState().isEmpty() &&
                            this.mob.level().getBlockState(nextBp.offset(0, 1, 0)).getFluidState().isEmpty() &&
                            this.mob.level().getBlockState(nextBp.offset(0, -1, 0)).getFluidState().isEmpty()) {
                        return this.sevenDaysToSurvive$path.canReach();
                    }
                }
            }
        }
        return false;
    }

    @Unique
    private BlockPos sevenDaysToSurvive$findNextBP(){
        return this.mob.blockPosition().relative(this.mob.getDirection());
    }
}
