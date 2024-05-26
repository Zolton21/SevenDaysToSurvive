package net.zolton21.mixin;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends MonsterEntity implements IZombieCustomTarget {
    @Shadow public abstract CreatureAttribute getCreatureAttribute();

    @Shadow public abstract void livingTick();

    @Unique
    private boolean sevenDaysToSurvive$executingCustomGoal;
    @Unique
    private LivingEntity sevenDaysToSurvive$modGoalTarget;
    @Unique
    private BlockPos sevenDaysToSurvive$nextBlockPos;
    @Unique
    private EntityPredicate sevenDaysToSurvive$targetEntitySelector;


    protected ZombieEntityMixin(EntityType<? extends MonsterEntity> p_i48553_1_, World p_i48553_2_) {
        super(p_i48553_1_, p_i48553_2_);
        this.sevenDaysToSurvive$executingCustomGoal = false;
    }

    @Inject(method = "applyEntityAI()V", at = @At("TAIL"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(5, new SearchAndGoToPlayerGoal(this, 1.0));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal(this, PlayerEntity.class, false));
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        if (this.getAttackTarget() == null){
            if (!this.sevenDaysToSurvive$executingCustomGoal) {
                this.sevenDaysToSurvive$findReachableTarget();
                if (this.sevenDaysToSurvive$modGoalTarget != null) {
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.getNavigator();
                    Path path = groundPathNavigator.getPathToPos(this.sevenDaysToSurvive$modGoalTarget.getPosition(), 0);
                    if (path != null) {
                        if (path.reachesTarget()) {
                            this.setAttackTarget(this.sevenDaysToSurvive$modGoalTarget);
                        } else {
                            this.sevenDaysToSurvive$findCustomPath();
                        }
                    } else {
                        this.sevenDaysToSurvive$findCustomPath();
                    }
                }
            } else if (this.sevenDaysToSurvive$modGoalTarget != null) {
                Path path = this.getNavigator().getPathToPos(this.sevenDaysToSurvive$modGoalTarget.getPosition(), 0);
                if (path != null) {
                    if (path.reachesTarget()) {
                        this.setAttackTarget(this.sevenDaysToSurvive$modGoalTarget);
                    }
                }
            }
        }
    }
    
    public void sevenDaysToSurvive$customGoalStarted(){
        this.sevenDaysToSurvive$executingCustomGoal = true;
    }
    
    public void sevenDaysToSurvive$customGoalFinished(){
        this.sevenDaysToSurvive$executingCustomGoal = false;
    }

    public void sevenDaysToSurvive$findReachableTarget(){
        if(this.sevenDaysToSurvive$targetEntitySelector == null) {
            this.sevenDaysToSurvive$targetEntitySelector = (new EntityPredicate()).setDistance(this.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
        }
        this.sevenDaysToSurvive$modGoalTarget = this.world.getClosestPlayer(this.sevenDaysToSurvive$targetEntitySelector, this, this.getPosX(), this.getPosY(), this.getPosZ());
        if(this.sevenDaysToSurvive$modGoalTarget == null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                    this.getPosX() - 50,
                    this.getPosY() - 50,
                    this.getPosZ() - 50,
                    this.getPosX() + 50,
                    this.getPosY() + 50,
                    this.getPosZ() + 50);
            if(this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB) != null) {
                if (!this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB).isSpectator() && !this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB).isCreative() && this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB).isAlive()) {
                    this.sevenDaysToSurvive$modGoalTarget = this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB);
                }
            }
        }
    }

    public void sevenDaysToSurvive$findCustomPath(){
        if(this.sevenDaysToSurvive$modGoalTarget != null){
            if((int)this.getPosX() == (int)this.sevenDaysToSurvive$modGoalTarget.getPosX() && (int)this.getPosZ() == (int)this.sevenDaysToSurvive$modGoalTarget.getPosZ()){
                if (this.getPosY() > (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), this.getPosY() - 1, this.getPosZ());
                } else if (this.getPosY() < (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), this.getPosY() + 1, this.getPosZ());
                }
            }else{
                int y = (int)this.getPosY();
                int targetYPos = (int)this.sevenDaysToSurvive$modGoalTarget.getPosY();

                Direction.Axis axis = this.sevenDaysToSurvive$setAxis();
                Direction.AxisDirection axisDirection = this.sevenDaysToSurvive$setAxisDirection(axis);

                if(axis == Direction.Axis.X){
                    if(Math.abs(Math.abs(this.getPosX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosX())) < Math.abs(Math.abs(this.getPosY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosY()))){
                        if(y < targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y + 1, this.getPosZ());
                        } else if (y > targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y - 1, this.getPosZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y, this.getPosZ());
                        }
                    }else {
                        if((int)Math.abs(Math.abs(this.getPosX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosX())) == (int)Math.abs(Math.abs(this.getPosY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosY()))){
                            if (this.getPosY() < (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                                y = y + 1;
                            } else if (this.getPosY() > (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                                y = y - 1;
                            }
                        }
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX() + 1, y, this.getPosZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX() - 1, y, this.getPosZ());
                        }
                    }
                }else{
                    if(Math.abs(Math.abs(this.getPosZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosZ())) < Math.abs(Math.abs(this.getPosY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosY()))){
                        if(y < targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y + 1, this.getPosZ());
                        } else if (y > targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y - 1, this.getPosZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y, this.getPosZ());
                        }
                    } else {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            if((int)Math.abs(Math.abs(this.getPosZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosZ())) == (int)Math.abs(Math.abs(this.getPosY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosY()))){
                                if (this.getPosY() < (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                                    y = y + 1;
                                } else if (this.getPosY() > (int)this.sevenDaysToSurvive$modGoalTarget.getPosY()) {
                                    y = y - 1;
                                }
                            }
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y, this.getPosZ() + 1);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getPosX(), y, this.getPosZ() - 1);
                        }
                    }
                }
            }

        }
    }

    @Unique
    private Direction.Axis sevenDaysToSurvive$setAxis(){
        Direction.Axis axis;
        if(Math.abs(Math.abs(this.getPosX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosX())) >= Math.abs(Math.abs(this.getPosZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getPosZ()))){
            axis = Direction.Axis.X;
        }else {
            axis = Direction.Axis.Z;
        }
        return axis;
    }

    @Unique
    private Direction.AxisDirection sevenDaysToSurvive$setAxisDirection(Direction.Axis direction){
        Direction.AxisDirection axisDirection;
        if(direction == Direction.Axis.X){
            if((int)this.sevenDaysToSurvive$modGoalTarget.getPosX() - this.getPosX() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }else{
            if((int)this.sevenDaysToSurvive$modGoalTarget.getPosZ() - this.getPosZ() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }
        return axisDirection;
    }

    public BlockPos sevenDaysToSurvive$getNextBlockPos(){
        return this.sevenDaysToSurvive$nextBlockPos;
    }

    public LivingEntity sevenDaysToSurvive$getModGoalTarget(){
        return this.sevenDaysToSurvive$modGoalTarget;
    }

    public void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos(){
        this.sevenDaysToSurvive$modGoalTarget = null;
        this.sevenDaysToSurvive$nextBlockPos = null;
    }
}
