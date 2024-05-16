package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class BuildTowardsTargetGoal extends Goal {
    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private CreatureEntity mob;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private boolean pathToNextBlockPosActive;
    private int tickCounter;
    private int endJumpTick;
    private boolean isJumping;
    private ItemStack heldItem;

    public BuildTowardsTargetGoal(CreatureEntity creature, double speed) {
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
                return false;
            }
        }

        this.findCustomPath();
        if(this.pathToNextBlockPos != null){
            if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isAir()){
                double nextPosY = this.nextBlockPos.getY();
                double mobY = this.mob.getPosY();

                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()){
                    return false;
                }
                if(nextPosY > mobY){
                    if(!this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isAir()){
                        return false;
                    }
                }else if(nextPosY < mobY){
                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isAir()){
                        return false;
                    }
                }
            }
        }

        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        Path pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
        return pathToTarget != null && !pathToTarget.reachesTarget();
    }

    public void tick(){
        this.tickCounter++;
        if(this.mob.getNavigator().noPath()){
            this.pathToNextBlockPosActive = false;
            this.findCustomPath();
        }
        if(this.isJumping && this.tickCounter == this.endJumpTick){
            this.placeBlock(new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ()));
            this.isJumping = false;
        }

        if (this.tickCounter % 60 == 0) {
            if(this.playerTarget != null && this.isStandingOnBlock()) {
                if (this.mob.getPosition().getX() == this.nextBlockPos.getX() && this.mob.getPosition().getY() == this.nextBlockPos.getY() - 2 && this.mob.getPosition().getZ() == this.nextBlockPos.getZ()) {
                    boolean canPlaceBlock = true;
                    BlockPos blockPos;
                    for (int i = 0; i < 3; i++) {
                        blockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() + i, this.mob.getPosZ());
                        if (!this.mob.world.getBlockState(blockPos).isAir()) {
                            canPlaceBlock = false;
                            break;
                        }
                    }
                    if (canPlaceBlock) {
                        this.mobJump(tickCounter);
                    }
                }
                else {
                    this.faceTarget();
                    BlockPos blockPos = new BlockPos(this.nextBlockPos.add(0, -1, 0));
                    if(this.mob.world.getBlockState(blockPos).isAir()) {
                        this.placeBlock(blockPos);
                    }
                }
            }
        }
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

    private void mobJump(int currentTick){
        this.isJumping = true;
        double x = this.mob.getPosX();
        double y = this.mob.getPosY() + 3;
        double z = this.mob.getPosZ();
        if(!this.mob.world.getBlockState(new BlockPos(x, y, z)).isAir()){
            this.endJumpTick = currentTick + 4;
        }else {
            this.endJumpTick = currentTick + 6;
        }
        this.mob.getJumpController().setJumping();
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.world.getBlockState(pos).isSolid();
    }

    private void placeBlock(BlockPos blockPos){
        this.mob.world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
        this.mob.swingArm(this.mob.getActiveHand());

        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.pathToNextBlockPos = groundPathNavigator.getPathToPos(new BlockPos(this.nextBlockPos.add(0, 1, 0)), 0);
        if(!this.pathToNextBlockPosActive) {
            this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
            this.pathToNextBlockPosActive = true;
        }
    }

    public boolean shouldContinueExecuting() {
        if(this.tickCounter % 40 == 0) {
            this.findReachableTarget();
        }
        if (this.playerTarget != null) {
            if (this.mob.getPosX() == this.playerTarget.getPosX() && this.mob.getPosZ() == this.playerTarget.getPosZ()) {
                if (!this.mob.world.getBlockState(new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 2, this.mob.getPosZ())).isAir()) {
                    return false;
                }
            }

            if(this.pathToNextBlockPos != null){
                if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isAir()){
                    double nextPosY = this.nextBlockPos.getY();
                    double mobY = this.mob.getPosY();

                    if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()){
                        return false;
                    }

                    if(nextPosY > mobY){
                        if(!this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isAir()){
                            return false;
                        }
                    }else if(nextPosY < mobY){
                        if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isAir()){
                            return false;
                        }
                    }
                }
            }

            if(this.isStandingOnBlock()) {
                if(this.mob.getNavigator().getPath() != null) {
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                    Path pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
                    Path path = this.mob.getNavigator().getPath();
                    if (pathToTarget != null && path != null) {
                        if (pathToTarget.getTarget() != path.getTarget()) {
                            if (pathToTarget.reachesTarget()) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    public void startExecuting(){
        System.out.println("start executing BuildForwardGoal");
        this.tickCounter = 0;
        this.isJumping = false;
        this.pathToNextBlockPosActive = false;
        this.heldItem = this.mob.getHeldItem(Hand.MAIN_HAND);
        this.mob.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));

        this.findCustomPath();
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();

        this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos.add(0,1,0), 0);
        this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    private void findCustomPath(){
        if(this.mob != null && this.playerTarget != null){
            if(this.mob.getPosX() == this.playerTarget.getPosX() && this.mob.getPosZ() == this.playerTarget.getPosZ()){
                if(this.mob.getPosY() < this.playerTarget.getPosY()){
                    this.nextBlockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 1, this.mob.getPosZ());
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

    public void resetTask(){
        this.mob.setHeldItem(Hand.MAIN_HAND, this.heldItem);
        this.mob.getNavigator().clearPath();
        System.out.println("stop executing BuildForwardGoal");
    }

    private void findReachableTarget(){
        this.playerTarget = this.mob.world.getClosestPlayer(this.targetEntitySelector, this.mob, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ());
        if(this.playerTarget == null) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                    this.mob.getPosX() - 50,
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
