package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import java.util.EnumSet;

public class DiggingGoal extends Goal {

    private LivingEntity playerTarget;
    protected final double speedModifier;
    private final PathfinderMob mob;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private long tickCounter;
    private boolean isBreakingBlock;
    private long breakBlockTick;
    private BlockPos breakBlockBlockPos;
    private float blockBreakTime;
    private ItemStack offHandHeldItem;
    private boolean shouldPlaceBlock;
    private long placeBlockTick;
    private BlockPos placeBlockBlockPos;

    public DiggingGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET));
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
        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget == null) {
                //System.out.println("should execute return false 2");
                return false;
            }
            ((IZombieHelper)this.mob).sevenDaysToSurvive$findCustomPath();
            if(((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                this.nextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos();
                if ( this.mob.getBlockX() == this.nextBlockPos.getX() &&  this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                    if (this.mob.getY() < this.nextBlockPos.getY()) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            return true;
                        }
                    } else if (this.mob.getY() > this.nextBlockPos.getY()) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
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
                                double mobY = this.mob.getBlockY();
                                //
                                //if(this.mob.world.getBlockState(this.nextBlockPos).isSolid() == false && this.mob.world.getBlockState(this.nextBlockPos.offset(0, 1, 0)))
                                //
                                if (nextPosY == mobY) {
                                    //System.out.println("IFFFF");
                                    //System.out.println("mob " + this.mob.getPosition());
                                    //System.out.println("nextblockpos " + this.nextBlockPos);
                                    //System.out.println("nextblockpos material " + this.mob.world.getBlockState(this.nextBlockPos));
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F){
                                            return true;
                                        }
                                    }
                                    if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                                        if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                                            return true;
                                        }
                                    }
                                } else if (nextPosY > mobY) {
                                    //System.out.println("IF3");
                                    //System.out.println(this.mob.getPosition());
                                    //System.out.println(this.nextBlockPos);
                                    //System.out.println(this.mob.world.getBlockState(this.nextBlockPos));
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
                                            return true;
                                        }
                                    }
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                        if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, 2, 0)).getDestroySpeed(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0)) != -1.0F) {
                                            return true;
                                        }
                                    } else if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                                            return true;
                                        }
                                    } else if (Math.abs(Math.abs(this.mob.getBlockX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getBlockZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                            if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, 2, 0)).getDestroySpeed(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0)) != -1.0F) {
                                                return true;
                                            }
                                        }
                                    }
                                } else if (nextPosY < mobY) {
                                    if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                                        if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
                                            return true;
                                        }
                                    }
                                    if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                                        if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                                            return true;
                                        }
                                    }
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                                        if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, -1, 0)) != -1.0F) {
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

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((Player)((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if(this.tickCounter % 200 == 0 && !this.isBreakingBlock){
            //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if(this.playerTarget != null) {
                ((IZombieHelper)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieHelper)this.mob).sevenDaysToSurvive$getNextBlockPos();
            }
        }
        if(this.playerTarget != null) {
            if (this.mob.getBlockX() == this.nextBlockPos.getX() && this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                if(this.mob.getBlockY() < this.nextBlockPos.getY()) {
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                        if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                            return true;
                        }
                    }
                } else if (this.mob.getBlockY() > this.nextBlockPos.getY()) {
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                        if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
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
                        double mobY = this.mob.getBlockY();
                        if (nextPosY == mobY) {
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
                                    return true;
                                }
                            }
                            if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                                if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                                    return true;
                                }
                            }
                        } else if (nextPosY > mobY) {
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                                if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
                                    return true;
                                }
                            }
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                if(this.mob.level().getBlockState(this.mob.blockPosition().offset(0, 2, 0)).getDestroySpeed(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0)) != -1.0F) {
                                    return true;
                                }
                            } else if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
                                    return true;
                                }
                            } else if (Math.abs(Math.abs(this.mob.getBlockX()) - Math.abs(this.nextBlockPos.getX())) < 2 || Math.abs(Math.abs(this.mob.getBlockZ()) - Math.abs(this.nextBlockPos.getZ())) < 2) {
                                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                    if (this.mob.level().getBlockState(this.mob.blockPosition().offset(0, 2, 0)).getDestroySpeed(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0)) != -1.0F) {
                                        return true;
                                    }
                                }
                            }
                        } else if (nextPosY < mobY) {
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                                if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, -1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, -1, 0)) != -1.0F) {
                                    return true;
                                }
                            }
                            if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                                if(this.mob.level().getBlockState(this.nextBlockPos).getDestroySpeed(this.mob.level(), this.nextBlockPos) != -1.0F) {
                                    return true;
                                }
                            }
                            if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                                if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).getDestroySpeed(this.mob.level(), this.nextBlockPos.offset(0, 1, 0)) != -1.0F) {
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

            if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                if(this.mob.getNavigation().getPath() != null) {
                    GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    Path pathToTarget = groundPathNavigation.createPath(this.playerTarget.blockPosition(), 0);
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
        System.out.println("start executing DiggingGoal");
        //System.out.println("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        //System.out.println("current blockpos: " + this.mob.getPosition());
        //System.out.println("nextBlockPos: " + this.nextBlockPos);
        this.tickCounter = 0;
        this.isBreakingBlock = false;
        this.shouldPlaceBlock = false;
        this.placeBlockTick = 0;

        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalStarted();
        //GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();

        //this.pathToNextBlockPos = groundPathNavigation.getPathToPos(this.nextBlockPos, 0);
        //this.mob.getNavigation().setPath(this.pathToNextBlockPos, this.speedModifier);
    }

    public void stop(){
        System.out.println("stop executing DiggingGoal");
        this.mob.getNavigation().stop();
        if(this.breakBlockBlockPos != null) {
            this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, -1);
        }
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieHelper)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
        //System.out.println("stop executing DiggingGoal");
    }

    public void tick(){
        this.tickCounter++;

        if(this.shouldPlaceBlock){
            if(this.placeBlockBlockPos != null) {
                if (this.placeBlockTick - 3 == this.tickCounter) {
                    this.mob.swing(this.mob.getUsedItemHand());
                    this.mob.level().setBlock(this.placeBlockBlockPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
                }
                if(this.offHandHeldItem != null) {
                    if (this.placeBlockTick == this.tickCounter) {
                        this.mob.setItemInHand(InteractionHand.OFF_HAND, this.offHandHeldItem);
                        this.shouldPlaceBlock = false;
                    }
                }
            }
        }
        if(this.isBreakingBlock){
            if(!ModUtils.HasBlockEntityCollision(this.mob.level(), this.breakBlockBlockPos)){
                this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                this.isBreakingBlock = false;
            }else {
                this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, (int) ((float) (this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / (float) this.blockBreakTime * 10.0F));
                this.mob.getNavigation().setSpeedModifier(0);
                this.faceTarget(this.breakBlockBlockPos);
                if ((this.breakBlockTick - this.tickCounter) % 5 == 0) {
                    this.mob.swing(this.mob.getUsedItemHand());
                }
                if (this.tickCounter == this.breakBlockTick) {
                    this.breakBlock(this.breakBlockBlockPos);
                    this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                    this.isBreakingBlock = false;
                }
            }
        }

        if(!this.isBreakingBlock && this.tickCounter % 30 == 0){
            if(this.playerTarget != null && ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))){ //Check if mob is standing on a block
                if(this.nextBlockPos.getX() == this.mob.getBlockX() && this.nextBlockPos.getZ() == this.mob.getBlockZ()){
                    if(this.mob.getBlockY() > this.nextBlockPos.getY()){
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }
                    } else if (this.mob.getBlockY() < this.nextBlockPos.getY()) {
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0));
                        }
                    }
                }else {
                    if(this.nextBlockPos.getY() == this.mob.getBlockY()){
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0,1,0));
                        }
                    }else if(this.nextBlockPos.getY() < this.mob.getBlockY()){
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0,1,0));
                        }else if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0,2,0));
                        }
                    }else if(this.nextBlockPos.getY() > this.mob.getBlockY()){
                        if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos);
                        }else if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))){
                            this.startBreakingBlock(this.tickCounter, this.nextBlockPos.offset(0,1,0));
                        }else if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))){
                            this.startBreakingBlock(this.tickCounter, this.mob.blockPosition().offset(0,2,0));
                        }
                    }
                }
            }
        }

        /*if(!this.isBreakingBlock && !this.mob.world.getBlockState(this.nextBlockPos).isSolid() && !this.mob.world.getBlockState(this.nextBlockPos.offset(0, 1, 0)).isSolid() && (int) this.mob.getBlockX() != this.nextBlockPos.getX() && (int) this.mob.getBlockZ() != this.nextBlockPos.getZ())
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

        float blockHardness = this.mob.level().getBlockState(blockPos).getDestroySpeed(this.mob.level(), blockPos);
        this.blockBreakTime = blockHardness * 50.0F / ((IZombieHelper) this.mob).sevenDaysToSurvive$getBlockBreakingSpeedModifier();
        //this.blockBreakTime = 60;

        this.breakBlockTick = currentTick + (long) this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
        //this.isMovingTowardsTarget = false;
        if(blockPos.getX() == this.mob.getBlockX() && blockPos.getZ() == this.mob.getBlockZ()){
            if(blockPos.getY() == this.mob.getBlockY() - 1){
                if(!ModUtils.HasBlockEntityCollision(this.mob.level(), blockPos.offset(0, -1, 0))) {
                    this.placeBlockBlockPos = blockPos.offset(0, -1, 0);
                    this.shouldPlaceBlock = true;
                    this.placeBlockTick = this.breakBlockTick + 5;
                    this.offHandHeldItem = this.mob.getItemInHand(InteractionHand.OFF_HAND);
                    this.mob.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.COBBLESTONE));
                }
            }
        }
    }

    private void breakBlock(BlockPos blockPos){

        //System.out.println(this.mob.world.getBlockState(blockPos));
        this.mob.level().destroyBlock(blockPos, true);
        this.mob.level().playSound(null, blockPos, this.mob.level().getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getSoundSource(), 1.0F, 1.0F);
        GroundPathNavigation groundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
        this.pathToNextBlockPos = groundPathNavigation.createPath(this.nextBlockPos, 0);
        if(this.pathToNextBlockPos != null && this.pathToNextBlockPos.canReach()) {
            this.mob.getNavigation().moveTo(this.pathToNextBlockPos, 0);
        }
    }

    public void faceTarget(BlockPos blockPos){
        double deltaX = blockPos.getX() - this.mob.getBlockX();
        double deltaZ = blockPos.getZ() - this.mob.getBlockZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        //this.mob.rotationYaw = (float) yaw;
        this.mob.setYRot((float) yaw);

        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }

}
