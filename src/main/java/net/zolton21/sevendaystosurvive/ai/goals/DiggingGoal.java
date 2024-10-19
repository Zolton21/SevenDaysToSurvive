package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private final PathfinderMob mob;
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

    public DiggingGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean canUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
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
            ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
            if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                this.nextBlockPos = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos();
                if ((long) this.mob.getPosX() == this.nextBlockPos.getX() && (long) this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                    if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if (this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                            return true;
                        }
                    } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                        if (/*this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()*/!this.mob.level().getBlockState(this.nextBlockPos).getCollisionShape(this.mob.level(), this.nextBlockPos).isEmpty()) {
                            return true;
                        }
                    }
                } else {
                    //System.out.println("ELSE");
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    Path pathToTarget = groundPathNavigation.createPath(this.playerTarget.blockPosition(), 0);
                    this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
                    if (pathToTarget != null && !pathToTarget.canReach()) {
                        //System.out.println("IF1");
                        if (this.pathToNextBlockPos != null) {
                            //
                            if(!this.pathToNextBlockPos.canReach()) {
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
                                    if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1){
                                            return true;
                                        }
                                    }
                                    if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    }
                                } else if (nextPosY > mobY) {
                                    //System.out.println("IF3");
                                    //System.out.println(this.mob.getPosition());
                                    //System.out.println(this.nextBlockPos);
                                    //System.out.println(this.mob.world.getBlockState(this.nextBlockPos));
                                    if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    }
                                    if (this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                        if(this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    } else if (this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                        if (this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                            if(this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getHarvestLevel() != -1) {
                                                return true;
                                            }
                                        }
                                    }
                                } else if (nextPosY < mobY) {
                                    if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    }
                                    if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                            return true;
                                        }
                                    }
                                    if (this.mob.level().getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid()) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, -1, 0)).getHarvestLevel() != -1) {
                                            return true;
                                        }
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

    public boolean canContinueToUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                  //System.out.println("should continue executing return false 1");
                    return false;
                }
            }
        }
        if(this.tickCounter % 200 == 0 && !this.isBreakingBlock){
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if(this.playerTarget != null) {
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
            }
        }
        if(this.playerTarget != null) {
            if ((long)this.mob.getPosX() == this.nextBlockPos.getX() && (long)this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                if(this.mob.getPosY() < this.nextBlockPos.getY()) {
                    if (this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                            return true;
                        }
                    }
                } else if (this.mob.getPosY() > this.nextBlockPos.getY()) {
                    if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                        if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                            return true;
                        }
                    }
                }
            }else {
                GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
                if (this.pathToNextBlockPos != null) {
                    //
                    if(!this.pathToNextBlockPos.canReach()) {
                        //
                        double nextPosY = this.nextBlockPos.getY();
                        double mobY = this.mob.getPosY();
                        if (nextPosY == mobY) {
                            if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                            if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                                if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                        } else if (nextPosY > mobY) {
                            if (this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()) {
                                if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                            if (this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                if(this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getHarvestLevel() != -1) {
                                    return true;
                                }
                            } else if (this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()) {
                                if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                    return true;
                                }
                            } else if (Math.abs(Math.abs(this.mob.getPosX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getPosZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                if (this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getMaterial().isSolid()) {
                                    if (this.mob.level().getBlockState(this.mob.getPosition().add(0, 2, 0)).getHarvestLevel() != -1) {
                                        return true;
                                    }
                                }
                            }
                        } else if (nextPosY < mobY) {
                            if (this.mob.level().getBlockState(this.nextBlockPos.add(0, -1, 0)).getMaterial().isSolid()) {
                                if(this.mob.level().getBlockState(this.nextBlockPos.add(0, -1, 0)).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                            if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                                if(this.mob.level().getBlockState(this.nextBlockPos).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                            if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                                if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getHarvestLevel() != -1) {
                                    return true;
                                }
                            }
                        }
                    }/*else{
                        this.moveTowardsTarget(this.nextBlockPos);
                        return true;
                    }*/
                }
            }

            if(this.isStandingOnBlock()) {
                if(this.mob.getNavigation().getPath() != null) {
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    Path pathToTarget = groundPathNavigation.createPath(this.playerTarget.getPosition(), 0);
                    Path path = this.mob.getNavigation().getPath();
                    if (pathToTarget != null && path != null) {
                        if (pathToTarget.getTarget() != path.getTarget()) {
                            if (pathToTarget.canReach()) {
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

    public void start(){
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
        //GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();

        //this.pathToNextBlockPos = groundPathNavigation.getPathToPos(this.nextBlockPos, 0);
        //this.mob.getNavigation().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    public void stop(){
        //SevenDaysToSurvive.LOGGER.info("stop executing DiggingGoal");
        //System.out.println("stop executing DiggingGoal");
        this.mob.getNavigation().stop();
        if(this.breakBlockBlockPos != null) {
            this.mob.level().sendBlockBreakProgress(this.mob.getEntityId(), this.breakBlockBlockPos, -1);
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
                    this.mob.level().setBlockState(this.placeBlockBlockPos, Blocks.COBBLESTONE.getDefaultState());
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
            if(!this.mob.level().getBlockState(this.breakBlockBlockPos).getMaterial().isSolid()){
                this.mob.getNavigation().setSpeed(this.speedModifier);
                this.isBreakingBlock = false;
            }else {
                this.mob.level().sendBlockBreakProgress(this.mob.getEntityId(), this.breakBlockBlockPos, (int) ((float) (this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / (float) this.blockBreakTime * 10.0F));
                this.mob.getNavigation().setSpeed(0);
                this.faceTarget(this.breakBlockBlockPos);
                if ((this.breakBlockTick - this.tickCounter) % 5 == 0) {
                    this.mob.swingArm(this.mob.getActiveHand());
                }
                if (this.tickCounter == this.breakBlockTick) {
                    this.breakBlock(this.breakBlockBlockPos);
                    this.mob.getNavigation().setSpeed(this.speedModifier);
                    this.isBreakingBlock = false;
                }
            }
        }

        if(!this.isBreakingBlock && this.tickCounter % 30 == 0){
            if(this.playerTarget != null && this.isStandingOnBlock()){
                if(this.nextBlockPos.getX() == (long)this.mob.getPosX() && this.nextBlockPos.getZ() == (long)this.mob.getPosZ()){
                    if(this.mob.getPosY() > this.nextBlockPos.getY()){
                        if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }
                    } else if (this.mob.getPosY() < this.nextBlockPos.getY()) {
                        if(this.mob.level().getBlockState(this.nextBlockPos.add(0, 1, 0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0, 1, 0));
                        }
                    }
                }else {
                    if(this.nextBlockPos.getY() == this.mob.getPosY()){
                        if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.level().getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }
                    }else if(this.nextBlockPos.getY() < this.mob.getPosY()){
                        if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.level().getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(this.mob.level().getBlockState(this.nextBlockPos.add(0,2,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,2,0));
                        }
                    }else if(this.nextBlockPos.getY() > this.mob.getPosY()){
                        if(this.mob.level().getBlockState(this.nextBlockPos).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(this.mob.level().getBlockState(this.nextBlockPos.add(0,1,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.add(0,1,0));
                        }else if(this.mob.level().getBlockState(this.mob.getPosition().add(0,2,0)).getMaterial().isSolid()){
                            this.startBreakingBlock(this.tickCounter, this.mob.getPosition().add(0,2,0));
                        }
                    }
                }
            }
        }

        /*if(!this.isBreakingBlock && !this.mob.world.getBlockState(this.nextBlockPos).isSolid() && !this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid() && (int) this.mob.getPosX() != this.nextBlockPos.getX() && (int) this.mob.getPosZ() != this.nextBlockPos.getZ())
        {
            Path path = ((GroundPathNavigation) this.mob.getNavigation()).getPathToPos(this.nextBlockPos, 0);
            if (path != null && path.canReach()) {
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
        this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
    }

    private void startBreakingBlock(long currentTick, BlockPos blockPos){
        this.isBreakingBlock = true;
        int harvestLevel = this.mob.level().getBlockState(blockPos).getHarvestLevel();
        if(harvestLevel < 3) {
            this.blockBreakTime = 60 + harvestLevel * 20L;
        }else{
            this.blockBreakTime = 740 + harvestLevel * 20L;
        }
        //this.mob.world.getBlockState(blockPos).getHarvestLevel();
        this.breakBlockTick = currentTick + this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
        //this.isMovingTowardsTarget = false;
        if(blockPos.getX() == (long)this.mob.getPosX() && blockPos.getZ() == (long)this.mob.getPosZ()){
            if(blockPos.getY() == (long)this.mob.getPosY() - 1){
                if(!this.mob.level().getBlockState(blockPos.add(0, -1, 0)).getMaterial().isSolid()) {
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

        //System.out.println(this.mob.world.getBlockState(blockPos));
        this.mob.level().destroyBlock(blockPos, true);
        this.mob.level().playSound(null, blockPos, this.mob.level().getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getEntity().getSoundCategory(), 1.0F, 1.0F);
        GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
        this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
        if(this.pathToNextBlockPos != null && this.pathToNextBlockPos.canReach()) {
            this.mob.getNavigation().setPath(this.pathToNextBlockPos, 0);
        }
    }

    public void faceTarget(BlockPos blockPos){
        double deltaX = blockPos.getX() - this.mob.getPosX();
        double deltaZ = blockPos.getZ() - this.mob.getPosZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.rotationYaw = (float) yaw;

        this.mob.getLookController().setLookPosition(Vector3d.copyCentered(blockPos));
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.level().getBlockState(pos).getMaterial().isSolid();
    }

}
