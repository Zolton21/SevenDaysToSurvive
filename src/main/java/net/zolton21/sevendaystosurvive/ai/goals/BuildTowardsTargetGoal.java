package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;
import net.zolton21.sevendaystosurvive.utils.ModUtils;

import java.util.EnumSet;
import java.util.List;

import static net.minecraft.world.level.block.Blocks.LAVA;

public class BuildTowardsTargetGoal extends Goal {
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private final PathfinderMob mob;
    private Path pathToNextBlockPos;
    private BlockPos nextBlockPos;
    private boolean pathToNextBlockPosActive;
    private long tickCounter;
    private long endJumpTick;
    private boolean isJumping;
    private ItemStack heldItem;
    private long placeBlockTick;
    private boolean isPlacingBlock;
    private boolean shouldMoveToBlockPos;
    private BlockPos placeBlockBlockPos;

    public BuildTowardsTargetGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                   //SevendaysToSurvive.LOGGER.info("should execute return false 1");
                    return false;
                }
            }
        }
        //if (((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
            ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$findReachableTarget();
            this.playerTarget = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getModGoalTarget();
            if (this.playerTarget == null) {
                //SevendaysToSurvive.LOGGER.info("should execute return false 2");
                return false;
            }
            ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$runFindCustomPath();
            if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                this.nextBlockPos = ((IZombieCustomTarget) this.mob).sevenDaysToSurvive$getNextBlockPos();

                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                    //SevendaysToSurvive.LOGGER.info("should execute return false 3");
                    return false;
                }
                if ( this.mob.getBlockX() == this.nextBlockPos.getX() &&  this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                    if (this.nextBlockPos.getY() > this.mob.getBlockY()) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                            //SevendaysToSurvive.LOGGER.info("should execute return false 4");
                            return false;
                        }
                    } else if (this.nextBlockPos.getY() < this.mob.getBlockY()) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            //SevendaysToSurvive.LOGGER.info("should execute return false 5");
                            return false;
                        }
                    }
                }

                GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                Path pathToTarget = GroundPathNavigation.createPath(this.playerTarget.blockPosition(), 0);
                this.pathToNextBlockPos = GroundPathNavigation.createPath(this.nextBlockPos, 0);
                if (pathToTarget != null && !pathToTarget.canReach()) {
                    if (this.pathToNextBlockPos != null) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                            double nextPosY = this.nextBlockPos.getY();
                            double mobY = this.mob.getBlockY();
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                //SevendaysToSurvive.LOGGER.info("should execute return false 6");
                                return false;
                            }
                            if (nextPosY > mobY) {
                                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                    //SevendaysToSurvive.LOGGER.info("should execute return false 7");
                                    return false;
                                }
                            } else if (nextPosY < mobY) {
                                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                    //SevendaysToSurvive.LOGGER.info("should execute return false 8");
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
                if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
                    if (Math.abs(this.nextBlockPos.getY() - this.mob.getBlockY()) < 3) {
                        if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.mob.getBlockX())) < 3 || Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.mob.getBlockZ())) < 3) {
                            if (this.mob.level().getBlockState(this.nextBlockPos).is(LAVA) || this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).is(LAVA)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        //}
       //SevendaysToSurvive.LOGGER.info("should execute return false 9");
        return false;
    }

    public boolean canContinueToUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            if(this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0) != null) {
                if (this.mob.getNavigation().createPath(this.mob.getTarget().blockPosition(), 0).canReach()) {
                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 1");
                    return false;
                }
            }
        }
        //___________________
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            return false;
        }
        //___________________
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if(this.tickCounter % 200 == 0){
                //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
                if(this.playerTarget != null) {
                    ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
                    this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
                }
            }
            if (this.playerTarget != null) {
                if(!this.isJumping) {
                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 2");
                        return false;
                    }
                }

                if ( this.mob.getBlockX() == this.nextBlockPos.getX() &&  this.mob.getBlockZ() == this.nextBlockPos.getZ()) {
                    if (this.nextBlockPos.getY() > this.mob.getBlockY()) {
                        if (!this.isJumping) {
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                               //SevendaysToSurvive.LOGGER.info("should continue executing return false 3");
                                return false;
                            }
                        }
                    } else if (this.nextBlockPos.getY() < this.mob.getBlockY()) {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos)) {
                            if(!this.isJumping) {
                               //SevendaysToSurvive.LOGGER.info("should continue executing return false 4");
                                return false;
                            }
                        }
                    }
                } else {
                    GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                    Path pathToTarget = GroundPathNavigation.createPath(this.playerTarget.blockPosition(), 0);
                    this.pathToNextBlockPos = GroundPathNavigation.createPath(this.nextBlockPos, 0);
                    if (pathToTarget != null && !pathToTarget.canReach()) {
                        if (this.pathToNextBlockPos != null) {
                            if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, -1, 0))) {
                                double nextPosY = this.nextBlockPos.getY();
                                double mobY = this.mob.getBlockY();
                                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 1, 0))) {
                                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 5");
                                    return false;
                                }
                                if (nextPosY > mobY) {
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 6");
                                        return false;
                                    }
                                } else if (nextPosY < mobY) {
                                    if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.nextBlockPos.offset(0, 2, 0))) {
                                       //SevendaysToSurvive.LOGGER.info("should continue executing return false 7");
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }

                if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                    if (this.mob.getNavigation().getPath() != null) {
                        GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
                        Path pathToTarget = GroundPathNavigation.createPath(this.playerTarget.blockPosition(), 0);
                        Path path = this.mob.getNavigation().getPath();
                        if (pathToTarget != null && path != null) {
                            if (pathToTarget.getTarget() != path.getTarget()) {
                                if (pathToTarget.canReach()) {
                                   //SevendaysToSurvive.LOGGER.info("should continue executing return false 8");
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            }
        }
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (Math.abs(this.nextBlockPos.getY() - this.mob.getBlockY()) < 3) {
                if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.mob.getBlockX())) < 3 || Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.mob.getBlockZ())) < 3) {
                    if (this.mob.level().getBlockState(this.nextBlockPos).is(LAVA) || this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).is(LAVA)) {
                        return true;
                    }
                }
            }
        }
       //SevendaysToSurvive.LOGGER.info("should continue executing return false 9");
        return false;
    }

    public void tick(){
        this.tickCounter++;

        if(this.mob.getNavigation().isDone()){
            ////System.out.println("no Path");
            this.pathToNextBlockPosActive = false;
            ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
            this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
        }

        if(this.isPlacingBlock){
            this.faceTarget(this.placeBlockBlockPos);
            this.mob.getNavigation().setSpeedModifier(0);
            if(this.tickCounter == this.placeBlockTick) {
                this.placeBlock(this.placeBlockBlockPos, this.shouldMoveToBlockPos);
                this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                this.isPlacingBlock = false;
            }
        }

        if(this.isJumping && this.tickCounter == this.endJumpTick){
            this.placeBlock(new BlockPos(this.mob.getBlockX(), this.mob.getBlockY() - 1, this.mob.getBlockZ()), false);
            this.isJumping = false;
        }

        if (!this.isPlacingBlock && this.tickCounter % 20 == 0) {
            if (this.playerTarget != null) {
                if (this.mob.level().getBlockState(this.nextBlockPos).is(LAVA)) {
                    if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.mob.getBlockX())) < 3 || Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.mob.getBlockZ())) < 3) {
                        this.startPlacingBlock(this.tickCounter, this.nextBlockPos, false);
                    }
                }else if(this.mob.level().getBlockState(this.nextBlockPos.offset(0, 1, 0)).is(LAVA)){
                    if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.mob.getBlockX())) < 3 || Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.mob.getBlockZ())) < 3) {
                        this.startPlacingBlock(this.tickCounter, this.nextBlockPos.offset(0, 1, 0), false);
                    }
                }else {
                    if (!ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, -1, 0))) { //Check if mob is standing on a block
                        if (Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getBlockY()) < 2) {
                            if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) < 3 ||
                                    Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) < 3) {
                                this.startPlacingBlock(this.tickCounter, this.mob.blockPosition().offset(0, -1, 0), false);
                            }
                        }
                    } else {
                        if (ModUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition())) {
                            this.mobJump(this.tickCounter);
                        }
                        if (this.mob.blockPosition().getX() == this.nextBlockPos.getX() && this.mob.blockPosition().getY() < this.nextBlockPos.getY() && this.mob.blockPosition().getZ() == this.nextBlockPos.getZ()) {
                            boolean canPlaceBlock = true;
                            BlockPos blockPos;
                            for (int i = 0; i < 3; i++) {
                                blockPos = new BlockPos(this.mob.getBlockX(), this.mob.getBlockY() + i, this.mob.getBlockZ());
                                if (ModUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
                                    canPlaceBlock = false;
                                    break;
                                }
                            }
                            if (canPlaceBlock) {
                                this.mobJump(this.tickCounter);
                            }
                        } else {
                            if (Math.abs(Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getBlockY())) < 2) {
                                if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) < 3 ||
                                        Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) < 3) {
                                    BlockPos blockPos = new BlockPos(this.nextBlockPos.offset(0, -1, 0));
                                    if (!ModUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
                                        this.startPlacingBlock(this.tickCounter, blockPos, true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void startPlacingBlock(long currentTick, BlockPos blockPos, boolean shouldMove){
        this.isPlacingBlock = true;
        this.placeBlockTick = currentTick + 20;
        this.placeBlockBlockPos = blockPos;
        this.shouldMoveToBlockPos = shouldMove;
    }

    public void faceTarget(BlockPos blockPos){
        /*double deltaX = this.nextBlockPos.getX() - this.mob.getBlockX();
        double deltaZ = this.nextBlockPos.getZ() - this.mob.getBlockZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.rotationYaw = (float) yaw;*/
        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }

    private void mobJump(long currentTick){

        this.isJumping = true;
        int x = this.mob.getBlockX();
        int y = this.mob.getBlockY() + 3;
        int z = this.mob.getBlockZ();
        if(ModUtils.HasBlockEntityCollision(this.mob.level(), new BlockPos(x, y, z))){
            this.endJumpTick = currentTick + 4;
        }else {
            this.endJumpTick = currentTick + 6;
        }
        //this.mob.getJumpController().setJumping();
        AABB aabb = new AABB(this.mob.blockPosition());
        List<Monster> monsterEntities = this.mob.level().getEntitiesOfClass(Monster.class, aabb);
        if(!monsterEntities.isEmpty()){
            for(Monster monsterEntity: monsterEntities){
                monsterEntity.getJumpControl().jump();
            }
        }
    }

    private void placeBlock(BlockPos blockPos, boolean shouldMove){
        //SevenDaysToSurvive.LOGGER.info("Placing block at" + blockPos);
        if(!ModUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
            this.mob.level().setBlock(blockPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.level().playSound(null, blockPos, SoundEvents.STONE_PLACE, this.mob.getSoundSource(), 1.0F, 1.0F);
        }
        if(shouldMove) {
            GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
            this.pathToNextBlockPos = GroundPathNavigation.createPath(new BlockPos(this.nextBlockPos.offset(0, 1, 0)), 0);
            if (!this.pathToNextBlockPosActive) {
                this.mob.getNavigation().moveTo(this.pathToNextBlockPos, this.speedModifier);
                //this.pathToNextBlockPosActive = true;
            }
        }
    }

    public void start() {
        //SevenDaysToSurvive.LOGGER.info("start executing BuildForwardGoal");
        //System.out.println("start executing BuildForwardGoal");
       //System.out.println("current blockpos: " + this.mob.getPosition());
       //System.out.println("nextBlockPos: " + this.nextBlockPos);*/
       //SevendaysToSurvive.LOGGER.info("start executing BuildForwardGoal");
       //SevendaysToSurvive.LOGGER.info("current blockpos: " + this.mob.getPosition() + "; nextBlockPos: " + this.nextBlockPos);
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
        this.isJumping = false;
        this.isPlacingBlock = false;
        this.pathToNextBlockPosActive = true;
        this.heldItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));

        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$runFindCustomPath();
        this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();

        if (this.nextBlockPos != null) {
            if(Math.abs(Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getBlockY())) >= 2
                    && (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) >= 2
                    && Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) >= 2)){

                GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();

                this.pathToNextBlockPos = GroundPathNavigation.createPath(this.nextBlockPos.offset(0, 1, 0), 0);
                this.mob.getNavigation().moveTo(this.pathToNextBlockPos, this.speedModifier);
            }
        }
    }

    public void stop(){
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.heldItem);
        this.mob.getNavigation().stop();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalFinished();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);

        //SevenDaysToSurvive.LOGGER.info("stop executing BuildForwardGoal");
        //((IZombieCustomTarget)this.mob).sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();
       //SevendaysToSurvive.LOGGER.info("stop executing BuildForwardGoal");
        //System.out.println("stop executing BuildForwardGoal");
    }

}
