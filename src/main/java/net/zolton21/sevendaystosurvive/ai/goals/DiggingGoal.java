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
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private CreatureEntity mob;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private int tickCounter;
    private boolean isBreakingBlock;
    private int breakBlockTick;
    private BlockPos breakBlockBlockPos;
    private int blockBreakTime;

    public DiggingGoal(CreatureEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.blockBreakTime = 60;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute() {
        if(this.isStandingOnBlock()) {
            this.findReachableTarget();
            if (this.playerTarget == null) {
                return false;
            }

            this.findCustomPath();

            if ((int)this.mob.getPosX() == this.nextBlockPos.getX() && (int)this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                if(this.mob.getPosY() < this.nextBlockPos.getY()) {
                    if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                        return true;
                    }
                } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                    if (!this.mob.world.getBlockState(this.nextBlockPos).isAir()) {
                        return true;
                    }
                }
            }else {
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                Path pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                if (pathToTarget != null && !pathToTarget.reachesTarget()) {
                    if (this.pathToNextBlockPos != null) {
                        double nextPosY = this.nextBlockPos.getY();
                        double mobY = this.mob.getPosY();
                        if (nextPosY == mobY) {
                            if (!this.mob.world.getBlockState(this.nextBlockPos).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                                return true;
                            }
                        } else if (nextPosY > mobY) {
                            if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isAir()) {
                                return true;
                            } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                if (!this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isAir()) {
                                    return true;
                                }
                            }
                        } else if (nextPosY < mobY) {
                            if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isAir() || !this.mob.world.getBlockState(this.nextBlockPos).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        if(this.tickCounter % 200 == 0) {
            this.findReachableTarget();
        }
        if(this.playerTarget != null) {
            if ((int)this.mob.getPosX() == this.nextBlockPos.getX() && (int)this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                if(this.mob.getPosY() < this.nextBlockPos.getY()) {
                    if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                        return true;
                    }
                } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                    if (!this.mob.world.getBlockState(this.nextBlockPos).isAir()) {
                        return true;
                    }
                }
            }else {
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                if (this.pathToNextBlockPos != null) {
                    double nextPosY = this.nextBlockPos.getY();
                    double mobY = this.mob.getPosY();
                    if (nextPosY == mobY) {
                        if (!this.mob.world.getBlockState(this.nextBlockPos).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                            return true;
                        }
                    } else if (nextPosY > mobY) {
                        if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isAir()) {
                            return true;
                        } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                            if (!this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isAir()) {
                                return true;
                            }
                        }
                    } else if (nextPosY < mobY) {
                        if (!this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isAir() || !this.mob.world.getBlockState(this.nextBlockPos).isAir() || !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()) {
                            return true;
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
        }
        return false;
    }

    public void startExecuting(){
        System.out.println("start executing DiggingGoal");
        System.out.println("current blockpos: " + this.mob.getPosition());
        System.out.println("nextBlockPos: " + this.nextBlockPos);
        this.tickCounter = 0;
        this.isBreakingBlock = false;

        this.findCustomPath();
        //GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();

        //this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
        //this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    public void resetTask(){
        this.mob.getNavigator().clearPath();
        if(this.breakBlockBlockPos != null) {
            this.mob.world.sendBlockBreakProgress(this.mob.getEntityId(), this.breakBlockBlockPos, -1);
        }
        System.out.println("stop executing DiggingGoal");
    }

    public void tick(){
        this.tickCounter++;

        if(this.isBreakingBlock){
            if(this.mob.world.getBlockState(this.breakBlockBlockPos).isAir()){
                this.mob.getNavigator().setSpeed(this.speedModifier);
                this.isBreakingBlock = false;
            }else {
                this.mob.world.sendBlockBreakProgress(this.mob.getEntityId(), this.breakBlockBlockPos, (int) ((float) (this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / (float) this.blockBreakTime * 10.0F));
                this.mob.getNavigator().setSpeed(0);
                this.faceTarget(this.breakBlockBlockPos);
                if ((this.breakBlockTick - this.tickCounter) % 5 == 0) {
                    this.mob.swingArm(this.mob.getActiveHand());
                }
                if (this.tickCounter == this.breakBlockTick) {
                    this.breakBlock(this.breakBlockBlockPos);
                    this.mob.getNavigator().setSpeed(this.speedModifier);
                    this.isBreakingBlock = false;
                }
            }
        }

        if(this.tickCounter % 200 == 0){
            this.findReachableTarget();
            if(this.playerTarget != null) {
                this.findCustomPath();
            }
        }

        if(!this.isBreakingBlock && this.tickCounter % 30 == 0){
            if(this.playerTarget != null && this.isStandingOnBlock()){
                if(this.nextBlockPos.getX() == (int)this.mob.getPosX() && this.nextBlockPos.getZ() == (int)this.mob.getPosZ()){
                    if(this.mob.getPosY() > this.nextBlockPos.getY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }
                    } else if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if(!this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0, 1, 0));
                        }
                    }
                }else {
                    if(this.nextBlockPos.getY() == this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }
                    }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,2,0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,2,0));
                        }
                    }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                        if(!this.mob.world.getBlockState(this.nextBlockPos).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(!this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(!this.mob.world.getBlockState(this.mob.getPosition().add(0,2,0)).isAir()){
                            this.startBreakingBlock(this.tickCounter, this.mob.getPosition().add(0,2,0));
                        }
                    }
                }
            }
        }
    }

    public void moveTowardsTarget(BlockPos blockPos) {
        //System.out.println("moveTowards run");
        this.mob.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
    }

    private void startBreakingBlock(int currentTick, BlockPos blockPos){
        this.isBreakingBlock = true;
        this.breakBlockTick = currentTick + this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
    }

    private void breakBlock(BlockPos blockPos){
        System.out.println("Breaking block");
        this.mob.world.removeBlock(blockPos, true);
        this.mob.getEntity().world.playSound(null, blockPos, this.mob.world.getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getEntity().getSoundCategory(), 1.0F, 1.0F);
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
        System.out.println("Mob BlockPos: " + this.mob.getPosition());
        System.out.println("NextBlockPos: " + this.nextBlockPos);
        if(this.pathToNextBlockPos != null && this.pathToNextBlockPos.reachesTarget()) {
            System.out.println("reaches target");
            this.mob.getNavigator().setPath(this.pathToNextBlockPos, 0);
        }
    }

    public void faceTarget(BlockPos blockPos){
        /*double deltaX = this.nextBlockPos.getX() - this.mob.getPosX();
        double deltaZ = this.nextBlockPos.getZ() - this.mob.getPosZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.rotationYaw = (float) yaw;*/
        this.mob.getLookController().setLookPosition(Vector3d.copyCentered(blockPos));
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return !this.mob.world.getBlockState(pos).isAir();
    }

    private void findCustomPath(){
        if(this.mob != null && this.playerTarget != null){
            if((int)this.mob.getPosX() == (int)this.playerTarget.getPosX() && (int)this.mob.getPosZ() == (int)this.playerTarget.getPosZ()){
                if (this.mob.getPosY() > this.playerTarget.getPosY()) {
                    this.nextBlockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
                } else if (this.mob.getPosY() < this.playerTarget.getPosY()) {
                    this.nextBlockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() + 1, this.mob.getPosZ());
                }
            }else{
                double y = this.mob.getPosY();
                /*
                if(this.mob.getPosY() < this.playerTarget.getPosY()){
                    y = y + 1;
                } else if (this.mob.getPosY() > this.playerTarget.getPosY()) {
                    y = y - 1;
                }
                 */
                Direction.Axis axis = this.setAxis();
                Direction.AxisDirection axisDirection = this.setAxisDirection(axis);

                if(axis == Direction.Axis.X){
                    if(Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.playerTarget.getPosX())) < Math.abs(Math.abs(this.mob.getPosY()) - Math.abs(this.playerTarget.getPosY()))){
                        this.nextBlockPos = new BlockPos(this.mob.getPosX(), y + 1, this.mob.getPosZ());
                    }else {
                        //____________________
                        if((int)Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.playerTarget.getPosX())) == (int)Math.abs(Math.abs(this.mob.getPosY()) - Math.abs(this.playerTarget.getPosY()))){
                            if (this.mob.getPosY() < this.playerTarget.getPosY()) {
                                y = y + 1;
                            } else if (this.mob.getPosY() > this.playerTarget.getPosY()) {
                                y = y - 1;
                            }
                        }
                        //____________________
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX() + 1, y, this.mob.getPosZ());
                        } else {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX() - 1, y, this.mob.getPosZ());
                        }
                    }
                }else{
                    if(Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.playerTarget.getPosZ())) < Math.abs(Math.abs(this.mob.getPosY()) - Math.abs(this.playerTarget.getPosY()))){
                        this.nextBlockPos = new BlockPos(this.mob.getPosX(), y + 1, this.mob.getPosZ());
                    } else {
                        if (axisDirection == Direction.AxisDirection.POSITIVE) {
                            //____________________
                            if((int)Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.playerTarget.getPosZ())) == (int)Math.abs(Math.abs(this.mob.getPosY()) - Math.abs(this.playerTarget.getPosY()))){
                                if (this.mob.getPosY() < this.playerTarget.getPosY()) {
                                    y = y + 1;
                                } else if (this.mob.getPosY() > this.playerTarget.getPosY()) {
                                    y = y - 1;
                                }
                            }
                            //____________________
                            this.nextBlockPos = new BlockPos(this.mob.getPosX(), y, this.mob.getPosZ() + 1);
                        } else {
                            this.nextBlockPos = new BlockPos(this.mob.getPosX(), y, this.mob.getPosZ() - 1);
                        }
                    }
                }
            }

        }
        //SevenDaysToSurvive.LOGGER.info("Searching new custom path. New nextBlockPos: " + this.nextBlockPos);
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
