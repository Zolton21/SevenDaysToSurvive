package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;

public class SearchAndGoToPlayerGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private MonsterEntity mob;
    private BlockPos nextBlockPos;
    private int tickCounter;
    private Path pathToNextBlockPos;
    private BlockPos playerTargetPos;


    public SearchAndGoToPlayerGoal(MonsterEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute() {
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if (this.isStandingOnBlock()) {
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();

                if (this.playerTarget != null && this.playerTarget.isAlive()) {
                    this.playerTargetPos = this.playerTarget.getPosition();
                    ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                    this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                    this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                    if (this.pathToNextBlockPos != null && this.pathToNextBlockPos.reachesTarget()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                            return false;
                        }
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                            return false;
                        }
                        if (this.mob.world.getBlockState(this.nextBlockPos).isSolid()) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        if(this.playerTarget != null && this.playerTarget.isAlive()) {
            if (this.playerTarget != null && this.pathToNextBlockPos != null && this.pathToNextBlockPos.reachesTarget()) {
                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                    return false;
                }
                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                    return false;
                }
                if (this.mob.world.getBlockState(this.nextBlockPos).isSolid()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void tick() {
        this.tickCounter++;
        if (this.tickCounter % 200 == 0) {
            ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget != null) {
                this.playerTargetPos = this.playerTarget.getPosition();
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
            }
        }
        if(this.playerTargetPos != null) {
            this.moveTowardsTarget(this.playerTargetPos);
        }
    }

    public void startExecuting(){
        System.out.println("startExecuting SearchAndGoToPlayerGoal");
        System.out.println("current blockpos: " + this.mob.getPosition());
        System.out.println("nextBlockPos: " + this.nextBlockPos);
        this.tickCounter = 0;
    }

    public void moveTowardsTarget(BlockPos blockPos) {
        //System.out.println("moveTowards run");
        this.mob.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
    }

    public void resetTask(){
        System.out.println("resetTask SearchAndGoToPlayerGoal");
        this.mob.getNavigator().clearPath();
    }

    private Direction.Axis setAxis(){
        Direction.Axis axis;
        if(Math.abs(this.mob.getPosX() - this.playerTarget.getPosX()) >= Math.abs(this.mob.getPosZ() - this.playerTarget.getPosZ())){
            axis = Direction.Axis.X;
        }else {
            axis = Direction.Axis.Z;
        }

        return axis;
    }

    private Direction.AxisDirection setAxisDirection(Direction.Axis direction){
        Direction.AxisDirection axisDirection;
        if(direction == Direction.Axis.X){
            if(this.playerTarget.getPosX() - this.mob.getPosX() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }else{
            if(this.playerTarget.getPosZ() - this.mob.getPosZ() > 0){
                axisDirection = Direction.AxisDirection.POSITIVE;
            }else{
                axisDirection = Direction.AxisDirection.NEGATIVE;
            }
        }

        return axisDirection;
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.world.getBlockState(pos).isSolid();
    }
}
