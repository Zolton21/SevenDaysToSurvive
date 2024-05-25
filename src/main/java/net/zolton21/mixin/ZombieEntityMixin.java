package net.zolton21.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.world.World;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin extends MonsterEntity {

    protected ZombieEntityMixin(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
    }

    @Inject(method = "applyEntityAI()V", at = @At("HEAD"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(5, new SearchAndGoToPlayerGoal(this, 1.0));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "func_234342_eQ_()Lnet/minecraft/entity/ai/attributes/AttributeModifierMap$MutableAttribute;", at = @At("HEAD"), cancellable = true)
    private static void applyModifiedAttributes(CallbackInfoReturnable cir) {
        cir.setReturnValue(MonsterEntity.func_234295_eP_().createMutableAttribute(Attributes.FOLLOW_RANGE, 100.0).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.23000000417232513).createMutableAttribute(Attributes.ATTACK_DAMAGE, 3.0).createMutableAttribute(Attributes.ARMOR, 2.0).createMutableAttribute(Attributes.ZOMBIE_SPAWN_REINFORCEMENTS));
    }
}
