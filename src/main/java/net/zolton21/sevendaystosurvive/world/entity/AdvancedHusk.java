package net.zolton21.sevendaystosurvive.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class AdvancedHusk extends AdvancedZombie{
    public AdvancedHusk(EntityType<? extends Husk> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static boolean checkHuskSpawnRules(EntityType<Husk> pHusk, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return checkMonsterSpawnRules(pHusk, pLevel, pSpawnType, pPos, pRandom) && (pSpawnType == MobSpawnType.SPAWNER || pLevel.canSeeSky(pPos));
    }

    protected boolean isSunSensitive() {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.HUSK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.HUSK_STEP;
    }

    public boolean doHurtTarget(Entity pEntity) {
        boolean $$1 = super.doHurtTarget(pEntity);
        if ($$1 && this.getMainHandItem().isEmpty() && pEntity instanceof LivingEntity) {
            float $$2 = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            ((LivingEntity)pEntity).addEffect(new MobEffectInstance(MobEffects.HUNGER, 140 * (int)$$2), this);
        }

        return $$1;
    }

    protected boolean convertsInWater() {
        return true;
    }

    protected void doUnderWaterConversion() {
        this.convertToZombieType(EntityType.ZOMBIE);
        if (!this.isSilent()) {
            this.level().levelEvent((Player)null, 1041, this.blockPosition(), 0);
        }

    }

    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }
}
