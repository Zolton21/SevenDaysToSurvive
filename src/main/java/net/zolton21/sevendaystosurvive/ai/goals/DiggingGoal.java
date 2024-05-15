package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private CreatureEntity mob;
    private Path pathToTarget;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private int tickCounter;

    public DiggingGoal(CreatureEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute() {
        this.findReachableTarget();
        if(this.playerTarget == null){
            return false;
        }
        if(this.mob.getPosX() == this.playerTarget.getPosX() && this.mob.getPosZ() == this.playerTarget.getPosZ()) {
            if (!this.mob.world.getBlockState(new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 2, this.mob.getPosZ())).isAir()) {
                return true;
            }
        }

        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
        if(this.pathToTarget != null && !this.pathToTarget.reachesTarget()){
            this.findCustomPath();
            if(this.nextBlockPos.getY() == this.mob.getPosY()){
                if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                    return true;
                }
                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                    return true;
                }
            }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                    return true;
                }
                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                    return true;
                }
                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,2,0)).isAir()){
                    return true;
                }
            }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                    return true;
                }
                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                    return true;
                }
                if(!this.mob.world.getBlockState(this.mob.getPosition().add(0,2,0)).isAir()){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean shouldContinueExecuting() {
        if(this.tickCounter % 40 == 0) {
            this.findReachableTarget();
        }
        if(this.playerTarget != null) {
            if(this.mob.getPosX() == this.playerTarget.getPosX() && this.mob.getPosZ() == this.playerTarget.getPosZ()) {
                if (!this.mob.world.getBlockState(new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 2, this.mob.getPosZ())).isAir()) {
                    return true;
                }
            }

            if(this.isStandingOnBlock()) {
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);

            }

            if(this.pathToTarget != null && !this.pathToTarget.reachesTarget()){
                //this.findCustomPath();
                if(this.mob.getNavigator().noPath()){
                    this.findCustomPath();
                }
                if(this.nextBlockPos.getY() == this.mob.getPosY()){
                    if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                        return true;
                    }
                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                        return true;
                    }
                }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                    if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                        return true;
                    }
                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                        return true;
                    }
                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,2,0)).isAir()){
                        return true;
                    }
                }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                    if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                        return true;
                    }
                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                        return true;
                    }
                    if(!this.mob.world.getBlockState(this.mob.getPosition().add(0,2,0)).isAir()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void startExecuting(){
        System.out.println("start executing DiggingGoal");
        this.tickCounter = 0;

        this.findCustomPath();
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();

        this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos.add(0,1,0), 0);
        this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    public void resetTask(){
        this.mob.getNavigator().clearPath();
        System.out.println("stop executing DiggingGoal");
    }

    public void tick(){
        this.tickCounter++;
        if(this.tickCounter % 100 == 0){
            if(this.playerTarget != null && this.isStandingOnBlock()){
                this.faceTarget();

                if(this.nextBlockPos.getX() == this.mob.getPosX() && this.nextBlockPos.getZ() == this.mob.getPosZ()){
                    if(this.mob.getPosY() > this.nextBlockPos.getY()){
                        if(!this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isAir()){
                            this.breakBlock(this.mob.getPosition().add(0, 2, 0));
                        }
                    } else if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if(!this.mob.world.getBlockState(this.mob.getPosition().add(0, -1, 0)).isAir()){
                            this.breakBlock(this.mob.getPosition().add(0, -1, 0));
                        }
                    }
                }else {
                    if(this.nextBlockPos.getY() == this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.breakBlock(this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.breakBlock(this.nextBlockPos.add(0,1,0));
                        }
                    }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.breakBlock(this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.breakBlock(this.nextBlockPos.add(0,1,0));
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,2,0)).isAir()){
                            this.breakBlock(this.nextBlockPos.add(0,2,0));
                        }
                    }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.breakBlock(this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.breakBlock(this.nextBlockPos.add(0,1,0));
                        }else if(!this.mob.world.getBlockState(this.mob.getPosition().add(0,2,0)).isAir()){
                            this.breakBlock(this.mob.getPosition().add(0,2,0));
                        }
                    }
                }
            }
        }
    }

    private void breakBlock(BlockPos blockPos){
        System.out.println("Breaking block");
        this.mob.world.removeBlock(blockPos, true);
    }

    public void faceTarget(){
        if(this.playerTarget != null){
            double deltaX = this.nextBlockPos.getX() - this.mob.getPosX();
            double deltaZ = this.nextBlockPos.getZ() - this.mob.getPosZ();
            double yaw = Math.atan2(deltaZ, deltaX);
            yaw = Math.toDegrees(yaw) - 90.0;
            this.mob.rotationYaw = (float) yaw;
        }
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.world.getBlockState(pos).isSolid();
    }

    private void findCustomPath(){
        if(this.mob != null && this.playerTarget != null){
            if(this.mob.getPosX() == this.playerTarget.getPosX() && this.mob.getPosZ() == this.playerTarget.getPosZ()){
                if(this.mob.getPosY() < this.playerTarget.getPosY()){
                    this.nextBlockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 1, this.mob.getPosZ());
                } else if(this.mob.getPosY() > this.playerTarget.getPosY()){
                    this.nextBlockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
                }
            }else{
                double y = this.mob.getPosY();
                if(this.mob.getPosY() < this.playerTarget.getPosY()){
                    y = y + 1;
                } else if (this.mob.getPosY() > this.playerTarget.getPosY()) {
                    y = y - 1;
                }
                Direction.Axis axis = this.setAxis();
                Direction.AxisDirection axisDirection = this.setAxisDirection(axis);

                if(axis == Direction.Axis.X){
                    if(Math.abs(this.mob.getPosX() - this.playerTarget.getPosX()) < Math.abs(this.mob.getPosY() - this.playerTarget.getPosY())){
                        this.nextBlockPos = new BlockPos(this.mob.getPosX(), y + 1, this.mob.getPosZ());
                    }else {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX() + 1, y, this.mob.getPosZ());
                        } else {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX() - 1, y, this.mob.getPosZ());
                        }
                    }
                }else{
                    if(Math.abs(this.mob.getPosZ() - this.playerTarget.getPosZ()) < Math.abs(this.mob.getPosY() - this.playerTarget.getPosY())){
                        this.nextBlockPos = new BlockPos(this.mob.getPosX(), y + 1, this.mob.getPosZ());
                    } else {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX(), y, this.mob.getPosZ() + 1);
                        } else {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX(), y, this.mob.getPosZ() - 1);
                        }
                    }
                }
            }

        }
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

    private void findReachableTarget(){
        this.playerTarget = this.mob.world.getClosestPlayer(this.targetEntitySelector, this.mob, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ());
        if(this.playerTarget == null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(this.mob.getPosX() - 50,
                    this.mob.getPosY() - 50,
                    this.mob.getPosZ() - 50,
                    this.mob.getPosX() + 50,
                    this.mob.getPosY() + 50,
                    this.mob.getPosZ() + 50);
            if(this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB) != null) {
                if (!this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isSpectator() && !this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isCreative() && this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isAlive()) {
                    this.playerTarget = this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB);
                }
            }
        }

    }
}
