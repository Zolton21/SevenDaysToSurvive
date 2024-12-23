package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
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
        /*if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                   //System.out.println("should execute return false 1");
                    return false;
                }
            }
        }*/
        if(ModUtils.mobHasPlayerTargetAndCanReach(this.mob)){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((Player)((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
            //this.playerTarget = ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
               //System.out.println("should execute return false 2");
                return false;
            }
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

                    this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();

                    if (this.pathToNextBlockPos != null) {
                        if(!this.pathToNextBlockPos.canReach()) {
                            double nextPosY = this.nextBlockPos.getY();
                            double mobY = this.mob.getBlockY();
                            if (nextPosY == mobY) {
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
                        }
                    }
                }
            }
        }

        if(!ModUtils.isMobStandingOnAFullBlock(this.mob) && ((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null){
           //System.out.println("!ModUtils.IsMobStandingOnAFullBlock(this.mob) is true");
            return true;
        }
      //System.out.println("return false 3");
        return false;
    }

    public boolean canContinueToUse() {
        /*if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                  //System.out.println("should continue executing return false 1");
                    return false;
                }
            }
        }*/
        if(ModUtils.mobHasPlayerTargetAndCanReach(this.mob)){
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        } else if (!((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((Player)((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            return false;
        }

        if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
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
                this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
                if (this.pathToNextBlockPos != null) {
                    if(!this.pathToNextBlockPos.canReach()) {
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
                    }
                }
            }

            if (ModUtils.isMobStandingOnAFullBlock(this.mob) && ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                if (((IZombieHelper) this.mob).SevenDaysToSurvive$getCanReachTarget()) {
                    return false;
                }
            }

            if(this.shouldPlaceBlock){
                return true;
            }

            if(!ModUtils.isMobStandingOnAFullBlock(this.mob)){
              //System.out.println("should continue executing return true");
                return true;
            }
        }
      //System.out.println("should continue executing return false 3");
        return false;
    }

    public void start(){
        System.out.println("start executing DiggingGoal");
        this.tickCounter = 0;
        this.isBreakingBlock = false;
        this.shouldPlaceBlock = false;
        this.placeBlockTick = 0;

        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalStarted();
    }

    public void stop(){
        System.out.println("stop executing DiggingGoal");
        this.mob.getNavigation().stop();
        if(this.breakBlockBlockPos != null) {
            this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, -1);
        }
        ((IZombieHelper)this.mob).sevenDaysToSurvive$customGoalFinished();
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
            if(!ModUtils.HasBlockEntityCollision(this.mob.level(), this.breakBlockBlockPos) && !ModUtils.HasBlockEntityCollision(this.mob.level(), this.breakBlockBlockPos.offset(0, -1, 0))){
                this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                this.isBreakingBlock = false;
                //System.out.println("if1");
            }else {
                //System.out.println("this.blockBreakTime: " + this.blockBreakTime);
                //System.out.println("this.breakBlockTick: " + this.breakBlockTick);
                //System.out.println("this.tickCounter: " + this.tickCounter);
                //System.out.println("destroyBlockProgress: " + (int) ((this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / this.blockBreakTime * 10.0F));
                this.mob.level().destroyBlockProgress(this.mob.getId(), this.breakBlockBlockPos, (int) ((this.blockBreakTime - (this.breakBlockTick - this.tickCounter)) / this.blockBreakTime * 10.0F));
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
            if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null){
               //System.out.println("!ModUtils.IsMobStandingOnAFullBlock(this.mob): " + !ModUtils.IsMobStandingOnAFullBlock(this.mob));
                if(!ModUtils.isMobStandingOnAFullBlock(this.mob)){
                   //System.out.println("start breaking slab/fence");
                    if(!ModUtils.hasAFullBlockCollision(this.mob, this.mob.blockPosition())){
                        startBreakingBlock(this.tickCounter, this.mob.blockPosition());
                    } else if (!ModUtils.hasAFullBlockCollision(this.mob, this.mob.blockPosition().offset(0, -1, 0))) {
                        startBreakingBlock(this.tickCounter, this.mob.blockPosition().offset(0, -1, 0));
                    }
                }
            }

            if(((IZombieHelper)this.mob).sevenDaysToSurvive$getModGoalTarget() != null && ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))){ //Check if mob is standing on a block
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
    }

    public void moveTowardsTarget(BlockPos blockPos) {
        this.mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speedModifier);
    }

    private void startBreakingBlock(long currentTick, BlockPos blockPos){
        this.isBreakingBlock = true;

        float blockHardness = this.mob.level().getBlockState(blockPos).getDestroySpeed(this.mob.level(), blockPos);
       //System.out.println("Block Type: " + this.mob.level().getBlockState(blockPos));
       //System.out.println("BlockHardness: " + this.mob.level().getBlockState(blockPos).getDestroySpeed(this.mob.level(), blockPos));
        this.blockBreakTime = blockHardness * 50.0F / ((IZombieHelper) this.mob).sevenDaysToSurvive$getBlockBreakingSpeedModifier();

        this.breakBlockTick = currentTick + (long) this.blockBreakTime;
        this.breakBlockBlockPos = blockPos;
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
        //System.out.println("Breaking block");
        this.mob.level().destroyBlock(blockPos, true);
        this.mob.level().playSound(null, blockPos, this.mob.level().getBlockState(blockPos).getSoundType().getBreakSound(), this.mob.getSoundSource(), 1.0F, 1.0F);
        this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
        if(this.pathToNextBlockPos != null && this.pathToNextBlockPos.canReach()) {
            this.mob.getNavigation().moveTo(this.pathToNextBlockPos, 0);
        }
    }

    public void faceTarget(BlockPos blockPos){
        double deltaX = blockPos.getX() - this.mob.getBlockX();
        double deltaZ = blockPos.getZ() - this.mob.getBlockZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.setYRot((float) yaw);

        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }

}
