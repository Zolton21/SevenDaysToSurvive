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
    private Path path;
    private double playerTargetYPos;
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
        if(!this.mob.world.getBlockState(new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 2, this.mob.getPosZ())).isAir()){
            //System.out.println("Block isn't air");
            return false;
        }
        this.findReachableTarget();
        if(this.playerTarget == null){
            return false;
        }
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.path = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
        return this.path != null && !this.path.reachesTarget();
    }

    public void tick(){
        this.tickCounter++;
        this.findReachableTarget();
        this.mob.getNavigator().setPath(this.path, this.speedModifier);

        if(this.isJumping && this.tickCounter == this.endJumpTick){
            placeBlock(new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ()));
            this.isJumping = false;
        }

        if(this.playerTarget != null) {
            if (this.tickCounter % 20 == 0) {
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.path = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
            }

            if (this.tickCounter % 40 == 0 && this.isStandingOnBlock()) {
                if(Math.floor(this.playerTarget.getPosX()) == Math.floor(this.mob.getPosX()) && Math.floor(this.playerTarget.getPosZ()) == Math.floor(this.mob.getPosZ())){

                    boolean canPlaceBlock = true;
                    double xPos = this.mob.getPosX();
                    double yPos = this.mob.getPosY();
                    double zPos = this.mob.getPosZ();
                    BlockPos blockPos;

                    for(int i = 0; i < 3; i++){
                        blockPos = new BlockPos(xPos, yPos + i, zPos);
                        if(!this.mob.world.getBlockState(blockPos).isAir()) {
                            canPlaceBlock = false;
                            break;
                        }
                    }
                    System.out.println("canPlaceBlock? " + canPlaceBlock);
                    if(canPlaceBlock){
                        this.mobJump(tickCounter);
                    }

                } else {
                    this.faceTarget();

                    double xPos = this.mob.getPosX();
                    double yPos = this.mob.getPosY();
                    double zPos = this.mob.getPosZ();

                    if (this.mob.getHorizontalFacing().getAxis() == Direction.Axis.X) {
                        if (this.mob.getHorizontalFacing().getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                            xPos++;
                        } else {
                            xPos--;
                        }
                    } else {
                        if (this.mob.getHorizontalFacing().getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                            zPos++;
                        } else {
                            zPos--;
                        }
                    }
                    if (this.playerTargetYPos > this.mob.getPosY()) {

                    } else {
                        yPos--;
                        if (this.playerTargetYPos < this.mob.getPosY()) {
                            yPos--;
                        }
                    }

                    BlockPos blockPos = new BlockPos(xPos, yPos, zPos);

                    if(yPos != this.mob.getPosY() - 1){
                        if(this.mob.world.getBlockState(new BlockPos(xPos, this.mob.getPosY() - 1, zPos)).isAir() && this.mob.world.getBlockState(blockPos).isAir()){
                            placeBlock(blockPos);
                        }
                    } else if (this.mob.world.getBlockState(blockPos).isAir()) {
                        placeBlock(blockPos);
                    }
                }
            }
        }
    }

    public void faceTarget(){
        if(this.playerTarget != null){
            double deltaX = this.playerTarget.getPosX() - this.mob.getPosX();
            double deltaZ = this.playerTarget.getPosZ() - this.mob.getPosZ();
            double yaw = Math.atan2(deltaZ, deltaX);
            yaw = Math.toDegrees(yaw) - 90.0;
            //System.out.println("yaw: " + yaw);
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
    }

    public boolean shouldContinueExecuting(){
        if(this.tickCounter < 100){
            return true;
        }else {
            if(!this.mob.world.getBlockState(new BlockPos(this.mob.getPosX(), this.mob.getPosY()+2, this.mob.getPosZ())).isAir()){
                return false;
            }
            return this.playerTarget != null && this.path != null && !this.path.reachesTarget();
        }
    }

    public void startExecuting(){
        System.out.println("start executing BuildForwardGoal");
        this.findReachableTarget();
        this.tickCounter = 0;
        this.isJumping = false;
        this.heldItem = this.mob.getHeldItem(Hand.MAIN_HAND);
        this.mob.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));
    }

    public void resetTask(){
        this.mob.setHeldItem(Hand.MAIN_HAND, this.heldItem);
        this.mob.getNavigator().clearPath();
        System.out.println("stop executing BuildForwardGoal");
    }

    private void findReachableTarget(){
        //System.out.println("findReachableTarget run");
        this.playerTarget = this.mob.world.getClosestPlayer(this.targetEntitySelector, this.mob, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ());
        if(this.playerTarget == null) {
            double x1 = this.mob.getPosX() - 17;
            double x2 = this.mob.getPosX() + 17;
            double y1 = this.mob.getPosY() - 17;
            double y2 = this.mob.getPosY() + 17;
            double z1 = this.mob.getPosZ() - 17;
            double z2 = this.mob.getPosZ() + 17;
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
            if(this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB) != null) {
                if (!this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isSpectator() && !this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isCreative() && this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB).isAlive()) {
                    this.playerTarget = this.mob.world.getClosestEntityWithinAABB(PlayerEntity.class, this.targetEntitySelector, null, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), axisAlignedBB);
                }
            }
        }

        if(this.playerTarget != null) {
            this.playerTargetYPos = this.playerTarget.getPosY();
        }
        //System.out.println("this.playerTarget" + this.mob.world.isPlayerWithin(this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ(), 35));
        //System.out.println("this.playerTarget" + this.playerTarget);
        //System.out.println("this.playerTargetYPos " + this.playerTargetYPos);
    }
}
