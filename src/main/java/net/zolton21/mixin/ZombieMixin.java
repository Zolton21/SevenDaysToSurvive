package net.zolton21.mixin;


import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
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
        if(((Monster) this.getEntity()).getNavigator() instanceof GroundPathNavigation) {
            if (this.getAttackTarget() == null) {
                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 1");
                if (!this.sevenDaysToSurvive$executingCustomGoal) {
                    //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 2");
                    this.sevenDaysToSurvive$findReachableTarget();
                    if (this.sevenDaysToSurvive$modGoalTarget != null) {
                        //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 3");
                        GroundPathNavigation groundPathNavigator = (GroundPathNavigation) this.getNavigator();
                        Path path = groundPathNavigator.getPathToPos(this.sevenDaysToSurvive$modGoalTarget.getPosition(), 0);
                        if (path != null) {
                            //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 4");
                            if (path.canReach()) {
                                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 5");
                                this.setAttackTarget(this.sevenDaysToSurvive$modGoalTarget);
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
                    Path path = this.getNavigator().getPathToPos(this.sevenDaysToSurvive$modGoalTarget.getPosition(), 0);
                    if (path != null) {
                        //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 9");
                        if (path.canReach()) {
                            //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 10");
                            this.setAttackTarget(this.sevenDaysToSurvive$modGoalTarget);
                        }
                    }
                }
            }
            if (this.ticksExisted % 500 == 0 && this.sevenDaysToSurvive$modGoalTarget != null) {
                //SevenDaysToSurvive.LOGGER.info("Zombie Entity Mixin 11");
                if (this.getDistance(this.sevenDaysToSurvive$modGoalTarget) > 50) {
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
        if(this.sevenDaysToSurvive$targetEntitySelector == null) {
            this.sevenDaysToSurvive$targetEntitySelector = (new EntityPredicate()).setDistance(this.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
        }
        this.sevenDaysToSurvive$modGoalTarget = this.world.getClosestPlayer(this.sevenDaysToSurvive$targetEntitySelector, this, this.getPosX(), this.getPosY(), this.getPosZ());
        if(this.sevenDaysToSurvive$modGoalTarget == null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                    this.getX() - 50,
                    this.getY() - 50,
                    this.getZ() - 50,
                    this.getX() + 50,
                    this.getY() + 50,
                    this.getZ() + 50);
            Player player = this.world.getClosestEntityWithinAABB(PlayerEntity.class, this.sevenDaysToSurvive$targetEntitySelector, null, this.getPosX(), this.getPosY(), this.getPosZ(), axisAlignedBB);
            if(player != null) {
                if (!player.isSpectator() && !player.isCreative() && player.isAlive()) {
                    this.sevenDaysToSurvive$modGoalTarget = player;
                }
            }
        }
    }

    public void sevenDaysToSurvive$runFindCustomPath(){
        this.sevenDaysToSurvive$findCustomPath();
    }

    private void sevenDaysToSurvive$findCustomPath(){
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
                if(this.world.getBlockState(this.sevenDaysToSurvive$nextBlockPos).isSolid() && this.world.getBlockState(this.sevenDaysToSurvive$nextBlockPos).getHarvestLevel() == -1){
                    if(this.world.getBlockState(this.sevenDaysToSurvive$nextBlockPos.add(0, 1, 0)).isSolid()) {
                        if (this.world.getBlockState(this.sevenDaysToSurvive$nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                            this.sevenDaysToSurvive$nextBlockPos = this.sevenDaysToSurvive$nextBlockPos.add(0, 1, 0);
                        } else {
                            this.sevenDaysToSurvive$nextBlockPos = this.getPosition().add(0, 1, 0);
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

    public void sevenDaysToSurvive$setLastExecutingGoal(Goal goal){
        this.sevenDaysToSurvive$lastExecutingGoal = goal;
    }

    public Goal getSevenDaysToSurvive$lastExecutingGoal(){
        return this.sevenDaysToSurvive$lastExecutingGoal;
    }
    //@Shadow public abstract CreatureAttribute getCreatureAttribute();

    //@Shadow public abstract void livingTick();

}
