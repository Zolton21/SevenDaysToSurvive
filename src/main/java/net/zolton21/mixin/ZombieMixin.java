package net.zolton21.mixin;


import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.zolton21.sevendaystosurvive.ai.goals.BuildTowardsTargetGoal;
import net.zolton21.sevendaystosurvive.ai.goals.DiggingGoal;
import net.zolton21.sevendaystosurvive.ai.goals.SearchAndGoToPlayerGoal;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster implements IZombieCustomTarget {

    @Unique
    private boolean sevenDaysToSurvive$executingCustomGoal;
    @Unique
    private LivingEntity sevenDaysToSurvive$modGoalTarget;
    @Unique
    private BlockPos sevenDaysToSurvive$nextBlockPos;
    @Unique
    private EntityPredicate sevenDaysToSurvive$targetEntitySelector;
    @Unique
    private Goal sevenDaysToSurvive$lastExecutingGoal;
    @Unique
    private TargetingConditions targetingConditions;


    protected ZombieMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.sevenDaysToSurvive$executingCustomGoal = false;
    }

    @Inject(method = "addBehaviourGoals()V", at = @At("TAIL"))
    public void applyCustomAI(CallbackInfo ci){
        this.goalSelector.addGoal(3, new DiggingGoal(this, 1.0));
        this.goalSelector.addGoal(4, new BuildTowardsTargetGoal(this, 1.0));
        this.goalSelector.addGoal(5, new SearchAndGoToPlayerGoal(this, 1.0));
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tickInject(CallbackInfo ci) {
        //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin tick");
        if(this.getNavigation() instanceof GroundPathNavigation) {
            if (this.getTarget() == null) {
                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 1");
                if (!this.sevenDaysToSurvive$executingCustomGoal) {
                    //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 2");
                    this.sevenDaysToSurvive$findReachableTarget();
                    if (this.sevenDaysToSurvive$modGoalTarget != null) {
                        //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 3");
                        GroundPathNavigation groundPathNavigator = (GroundPathNavigation) this.getNavigation();
                        Path path = groundPathNavigator.createPath(this.sevenDaysToSurvive$modGoalTarget.blockPosition(), 0);
                        if (path != null) {
                            //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 4");
                            if (path.canReach()) {
                                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 5");
                                this.setTarget(this.sevenDaysToSurvive$modGoalTarget);
                            } else {
                                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 6");
                                this.sevenDaysToSurvive$findCustomPath();
                            }
                        } else {
                            //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 7");
                            this.sevenDaysToSurvive$findCustomPath();
                        }
                    }
                } else if (this.sevenDaysToSurvive$modGoalTarget != null) {
                    //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 8");
                    Path path = this.getNavigation().createPath(this.sevenDaysToSurvive$modGoalTarget.blockPosition(), 0);
                    if (path != null) {
                        //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 9");
                        if (path.canReach()) {
                            //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 10");
                            this.setTarget(this.sevenDaysToSurvive$modGoalTarget);
                        }
                    }
                }
            }
            if (this.tickCount % 500 == 0 && this.sevenDaysToSurvive$modGoalTarget != null) {
                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 11");
                if (this.distanceTo(this.sevenDaysToSurvive$modGoalTarget) > 50) {
                    //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 12");
                    this.sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
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
        this.sevenDaysToSurvive$modGoalTarget = this.level().getNearestPlayer(this, 60);
    }

    public void sevenDaysToSurvive$runFindCustomPath(){
        this.sevenDaysToSurvive$findCustomPath();
    }

    private void sevenDaysToSurvive$findCustomPath(){
        if(this.sevenDaysToSurvive$modGoalTarget != null){
            if((int)this.getBlockX() == (int)this.sevenDaysToSurvive$modGoalTarget.getBlockX() && (int)this.getBlockZ() == (int)this.sevenDaysToSurvive$modGoalTarget.getBlockZ()){
                if (this.getBlockY() > (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 1, this.getBlockZ());
                } else if (this.getBlockY() < (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                    this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 1, this.getBlockZ());
                }
            }else{
                int y = (int)this.getBlockY();
                int targetYPos = (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY();

                Direction.Axis axis = this.sevenDaysToSurvive$setAxis();
                Direction.AxisDirection axisDirection = this.sevenDaysToSurvive$setAxisDirection(axis);

                if(axis == Direction.Axis.X){
                    if(Math.abs(Math.abs(this.getBlockX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockX())) < Math.abs(Math.abs(this.getBlockY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()))){
                        if(y < targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                        } else if (y > targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
                        }
                    }else {
                        if((int)Math.abs(Math.abs(this.getBlockX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockX())) == (int)Math.abs(Math.abs(this.getBlockY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()))){
                            if (this.getBlockY() < (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                                y = y + 1;
                            } else if (this.getBlockY() > (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                                y = y - 1;
                            }
                        }
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() + 1, y, this.getBlockZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX() - 1, y, this.getBlockZ());
                        }
                    }
                }else{
                    if(Math.abs(Math.abs(this.getBlockZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockZ())) < Math.abs(Math.abs(this.getBlockY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()))){
                        if(y < targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y + 1, this.getBlockZ());
                        } else if (y > targetYPos) {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y - 1, this.getBlockZ());
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ());
                        }
                    } else {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            if((int)Math.abs(Math.abs(this.getBlockZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockZ())) == (int)Math.abs(Math.abs(this.getBlockY()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()))){
                                if (this.getBlockY() < (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                                    y = y + 1;
                                } else if (this.getBlockY() > (int)this.sevenDaysToSurvive$modGoalTarget.getBlockY()) {
                                    y = y - 1;
                                }
                            }
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() + 1);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = new BlockPos(this.getBlockX(), y, this.getBlockZ() - 1);
                        }
                    }
                }
                if(this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).isSolid() && this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).getDestroySpeed(level(), this.sevenDaysToSurvive$nextBlockPos) < 0.0F){
                    if(this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos.above(1)).isSolid()) {
                        if (this.level().getBlockState(this.sevenDaysToSurvive$nextBlockPos).getDestroySpeed(level(), this.sevenDaysToSurvive$nextBlockPos) >= 0.0F) {
                            this.sevenDaysToSurvive$nextBlockPos = this.sevenDaysToSurvive$nextBlockPos.above(1);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = this.blockPosition().offset(0, 1, 0);
                        }
                    }
                }
            }

        }
        //System.out.println("next blockpos " + this.sevenDaysToSurvive$nextBlockPos);
    }

    @Unique
    private Direction.Axis sevenDaysToSurvive$setAxis(){
        Direction.Axis axis;
        if(Math.abs(Math.abs(this.getX()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getX())) >= Math.abs(Math.abs(this.getZ()) - Math.abs((int)this.sevenDaysToSurvive$modGoalTarget.getZ()))){
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
            if((int)this.sevenDaysToSurvive$modGoalTarget.getX() - this.getX() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }else{
            if((int)this.sevenDaysToSurvive$modGoalTarget.getZ() - this.getZ() > 0){
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

    public void sevenDaysToSurvive$setLastExecutingGoal(Goal goal){
        this.sevenDaysToSurvive$lastExecutingGoal = goal;
    }

    public Goal getSevenDaysToSurvive$lastExecutingGoal(){
        return this.sevenDaysToSurvive$lastExecutingGoal;
    }
    //@Shadow public abstract CreatureAttribute getCreatureAttribute();

    //@Shadow public abstract void livingTick();

}
