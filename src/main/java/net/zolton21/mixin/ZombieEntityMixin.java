package net.zolton21.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.MoveToTargetLastKnowLocationGoal;
import net.zolton21.sevendaystosurvive.helper.IZombieGoalFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin extends MonsterEntity implements IZombieGoalFunctions {

    @Unique
    private boolean sevenDaysToSurvive$canMoveToLocation;
    @Unique
    private BlockPos sevenDaysToSurvive$targetLastLocation;

    protected ZombieEntityMixin(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Inject(method = "applyEntityAI()V", at = @At("TAIL"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new MoveToTargetLastKnowLocationGoal(this, 1.0));
        //this.goalSelector.addGoal(3, new (this, 1.0));
        this.goalSelector.addGoal(4, new BuildTowardsTargetGoal(this, 1.0));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, false));
    }

    @Inject(method = "func_234342_eQ_()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", at = @At("HEAD"), cancellable = true)
    private static void applyModifiedAttributes(CallbackInfoReturnable cir) {
        cir.setReturnValue(MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.FOLLOW_RANGE, 70.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23000000417232513).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0).createMutableAttribute(Attributes.ARMOR, 2.0).createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS));
    }

    public void sevenDaysToSurvive$forbidMoveToTargetLastKnowLocationGoal(){
        this.sevenDaysToSurvive$canMoveToLocation = false;
        this.sevenDaysToSurvive$targetLastLocation = null;
    }

    public void sevenDaysToSurvive$allowMoveToTargetLastKnowLocationGoal(BlockPos location){
        this.sevenDaysToSurvive$canMoveToLocation = true;
        this.sevenDaysToSurvive$targetLastLocation = location;
    }

    public BlockPos sevenDaysToSurvive$getTargetLastLocation(){
        return this.sevenDaysToSurvive$targetLastLocation;
    }

    public boolean sevenDaysToSurvive$canRunMoveToTargetLastKnowLocationGoal(){
        return this.sevenDaysToSurvive$canMoveToLocation;
    }
}
