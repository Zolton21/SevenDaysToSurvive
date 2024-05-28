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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private CreatureEntity mob;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private long tickCounter;
    private boolean isBreakingBlock;
    private long breakBlockTick;
    private BlockPos breakBlockBlockPos;
    private long blockBreakTime;
    private ItemStack offHandHeldItem;
    private boolean shouldPlaceBlock;
    private long placeBlockTick;
    private BlockPos placeBlockBlockPos;
    boolean isMovingTowardsTarget;

    public DiggingGoal(CreatureEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.blockBreakTime = 60;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute() {
        if(this.mob.getAttackTarget() != null && this.mob.getAttackTarget() instanceof PlayerEntity) {
            if(this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0) != null) {
                if (this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0).reachesTarget()) {
                    //System.out.println("should execute return false 1");
                    return false;
                }
            }
        }
        //if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
        if (this.isStandingOnBlock()) {
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget == null) {
                //System.out.println("should execute return false 2");
                return false;
            }
            ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
            if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                this.nextBlockPos = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos();
                if ((long) this.mob.getPosX() == this.nextBlockPos.getX() && (long) this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                    if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                            return true;
                        }
                    } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                            return true;
                        }
                    }
                } else {
                    //System.out.println("ELSE");
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                    Path pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
                    this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                    if (pathToTarget != null && !pathToTarget.reachesTarget()) {
                        //System.out.println("IF1");
                        if (this.pathToNextBlockPos != null) {
                            //
                            if(!this.pathToNextBlockPos.reachesTarget()) {
                                //
                                //System.out.println("IF2");
                                double nextPosY = this.nextBlockPos.getY();
                                double mobY = this.mob.getPosY();
                                //
                                //if(this.mob.world.getBlockState(this.nextBlockPos).isSolid() == false && this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)))
                                //
                                if (nextPosY == mobY) {
                                    //System.out.println("IFFFF");
                                    //System.out.println("mob " + this.mob.getPosition());
                                    //System.out.println("nextblockpos " + this.nextBlockPos);
                                    //System.out.println("nextblockpos material " + this.mob.world.getBlockState(this.nextBlockPos));
                                    if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                        return true;
                                    }
                                } else if (nextPosY > mobY) {
                                    //System.out.println("IF3");
                                    //System.out.println(this.mob.getPosition());
                                    //System.out.println(this.nextBlockPos);
                                    //System.out.println(this.mob.world.getBlockState(this.nextBlockPos));
                                    if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                        return true;
                                    }
                                    if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                        return true;
                                    } else if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                        return true;
                                    } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                        if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                            return true;
                                        }
                                    }
                                } else if (nextPosY < mobY) {
                                    if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                        return true;
                                    }
                                }
                            }/*else{
                                this.moveTowardsTarget(this.nextBlockPos);
                                return true;
                            }*/
                        }
                    }
                }
            }
        }
        //}
        //System.out.println("should execute return false 3");
      //System.out.println("current " + this.mob.getPosition());
      //System.out.println("next " + this.nextBlockPos);*/
        return false;
    }

    public boolean shouldContinueExecuting() {
        if(this.mob.getAttackTarget() != null && this.mob.getAttackTarget() instanceof PlayerEntity) {
            if(this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0) != null) {
                if (this.mob.getNavigator().getPathToPos(this.mob.getAttackTarget().getPosition(), 0).reachesTarget()) {
                  //System.out.println("should continue executing return false 1");
                    return false;
                }
            }
        }
        if(this.tickCounter % 200 == 0 && !this.isBreakingBlock){
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if(this.playerTarget != null) {
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
            }
        }
        if(this.playerTarget != null) {
            if ((long)this.mob.getPosX() == this.nextBlockPos.getX() && (long)this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                if(this.mob.getPosY() < this.nextBlockPos.getY()) {
                    if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                        return true;
                    }
                } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                    if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                        return true;
                    }
                }
            }else {
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                if (this.pathToNextBlockPos != null) {
                    //
                    if(!this.pathToNextBlockPos.reachesTarget()) {
                        //
                        double nextPosY = this.nextBlockPos.getY();
                        double mobY = this.mob.getPosY();
                        if (nextPosY == mobY) {
                            if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                return true;
                            }
                        } else if (nextPosY > mobY) {
                            if (this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                return true;
                            }
                            if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                return true;
                            } else if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                return true;
                            } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                    return true;
                                }
                            }
                        } else if (nextPosY < mobY) {
                            if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid() || this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                return true;
                            }
                        }
                    }/*else{
                        this.moveTowardsTarget(this.nextBlockPos);
                        return true;
                    }*/
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
                              //System.out.println("should continue executing return false 2");
                                return false;
                            }
                        }
                    }
                }
            }
            if(this.shouldPlaceBlock){
                return true;
            }
        }
      //System.out.println("should continue executing return false 3");
        return false;
    }

    public void startExecuting(){
        //SevenDaysToSurvive.LOGGER.info("start executing DiggingGoal");
        //System.out.println("start executing DiggingGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        //System.out.println("current blockpos: " + this.mob.getPosition());
        //System.out.println("nextBlockPos: " + this.nextBlockPos);
        this.tickCounter = 0;
        this.isBreakingBlock = false;
        this.shouldPlaceBlock = false;
        this.placeBlockTick = 0;

        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalStarted();
        //GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();

        //this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
        //this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    public void resetTask(){
        //SevenDaysToSurvive.LOGGER.info("stop executing DiggingGoal");
        //System.out.println("stop executing DiggingGoal");
        this.mob.getNavigator().clearPath();
        if(this.breakBlockBlockPos != null) {
            this.mob.world.sendBlockBreakProgress(this.mob.getEntityId(), this.breakBlockBlockPos, -1);
        }
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
        //System.out.println("stop executing DiggingGoal");
    }

    public void tick(){
        this.tickCounter++;
        
        if(this.shouldPlaceBlock){
            if(this.placeBlockBlockPos != null) {
                if (this.placeBlockTick - 3 == this.tickCounter) {
                    this.mob.swingArm(this.mob.getActiveHand());
                    this.mob.world.setBlockState(this.placeBlockBlockPos, Blocks.COBBLESTONE.getDefaultState());
                }
                if(this.offHandHeldItem != null) {
                    if (this.placeBlockTick == this.tickCounter) {
                        this.mob.setHeldItem(Hand.OFF_HAND, this.offHandHeldItem);
                        this.shouldPlaceBlock = false;
                    }
                }
            }
        }
        if(this.isBreakingBlock){
            if(!this.mob.world.getBlockState(this.breakBlockBlockPos).getMaterial().isSolid()){
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

        if(!this.isBreakingBlock && this.tickCounter % 30 == 0){
            if(this.playerTarget != null && this.isStandingOnBlock()){
                if(this.nextBlockPos.getX() == (long)this.mob.getPosX() && this.nextBlockPos.getZ() == (long)this.mob.getPosZ()){
                    if(this.mob.getPosY() > this.nextBlockPos.getY()){
                        if(this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }
                    } else if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if(this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0, 1, 0));
                        }
                    }
                }else {
                    if(this.nextBlockPos.getY() == this.mob.getPosY()){
                        if(this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }
                    }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                        if(this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(this.mob.world.getBlockState(this.nextBlockPos.add(0,2,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,2,0));
                        }
                    }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                        if(this.mob.world.getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.world.getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(this.mob.world.getBlockState(this.mob.getPosition().add(0,2,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.mob.getPosition().add(0,2,0));
                        }
                    }
                }
            }
        }

        /*if(!this.isBreakingBlock && !this.mob.world.getBlockState(this.nextBlockPos).isSolid() && !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid() && (int) this.mob.getPosX() != this.nextBlockPos.getX() && (int) this.mob.getPosZ() != this.nextBlockPos.getZ())
        {
            Path path = ((GroundPathNavigator) this.mob.getNavigator()).getPathToPos(this.nextBlockPos, 0);
            if (path != null && path.reachesTarget()) {
                this.moveTowardsTarget(this.nextBlockPos);
            }else {
                this.isMovingTowardsTarget = false;
            }
        }else{
            this.isMovingTowardsTarget = false;
        }*/
    }

    public void moveTowardsTarget(BlockPos blockPos) {
        //this.isMovingTowardsTarget = true;
        this.mob.getNavigator().tryMoveToXYZ(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
    }

    private void startBreakingBlock(long currentTick, BlockPos blockPos){
        this.isBreakingBlock = true;
        this.breakBlockTick = currentTick + this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
        //this.isMovingTowardsTarget = false;
        if(blockPos.getX() == (long)this.mob.getPosX() && blockPos.getZ() == (long)this.mob.getPosZ()){
            if(blockPos.getY() == (long)this.mob.getPosY() - 1){
                if(!this.mob.world.getBlockState(blockPos.add(0, -1, 0)).getMaterial().isSolid()) {
                    this.placeBlockBlockPos = blockPos.add(0, -1, 0);
                    this.shouldPlaceBlock = true;
                    this.placeBlockTick = this.breakBlockTick + 5;
                    this.offHandHeldItem = this.mob.getHeldItem(Hand.OFF_HAND);
                    this.mob.setHeldItem(Hand.OFF_HAND, new ItemStack(Items.COBBLESTONE));
                }
            }
        }
    }

    private void breakBlock(BlockPos blockPos){
        this.mob.world.removeBlock(blockPos, true);
        this.mob.getEntity().world.playSound(null, blockPos, this.mob.world.getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getEntity().getSoundCategory(), 1.0F, 1.0F);
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
        if(this.pathToNextBlockPos != null && this.pathToNextBlockPos.reachesTarget()) {
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
        return this.mob.world.getBlockState(pos).getMaterial().isSolid();
    }

}
