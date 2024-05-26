package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;

import java.util.EnumSet;
import java.util.List;

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
    private int placeBlockTick;
    private boolean isPlacingBlock;
    private boolean shouldMoveToBlockPos;
    private BlockPos placeBlockBlockPos;

    public BuildTowardsTargetGoal(CreatureEntity creature, double speed) {
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
                if (this.playerTarget == null) {
                    return false;
                }

                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();

                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                    return false;
                }

                if ((int) this.mob.getPosX() == this.nextBlockPos.getX() && (int) this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                    if (this.nextBlockPos.getY() > this.mob.getPosY()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                            return false;
                        }
                    } else if (this.nextBlockPos.getY() < this.mob.getPosY()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos).isSolid()) {
                            return false;
                        }
                    }
                }

                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                Path pathToTarget = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                if (pathToTarget != null && !pathToTarget.reachesTarget()) {
                    if (this.pathToNextBlockPos != null) {
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                            double nextPosY = this.nextBlockPos.getY();
                            double mobY = this.mob.getPosY();

                            if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                                return false;
                            }
                            if (nextPosY > mobY) {
                                if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isSolid()) {
                                    return false;
                                }
                            } else if (nextPosY < mobY) {
                                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isSolid()) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shouldContinueExecuting() {
        if(((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if(this.tickCounter % 200 == 0){
                ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findReachableTarget();
                this.playerTarget = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getModGoalTarget();
                if(this.playerTarget != null) {
                    ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
                    this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
                }
            }
            if (this.playerTarget != null) {
                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                    return false;
                }

                if ((int) this.mob.getPosX() == this.nextBlockPos.getX() && (int) this.mob.getPosZ() == this.nextBlockPos.getZ()) {
                    if (this.nextBlockPos.getY() > this.mob.getPosY()) {
                        if (!this.isJumping) {
                            if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                                return false;
                            }
                        }
                    } else if (this.nextBlockPos.getY() < this.mob.getPosY()) {
                        if (this.mob.world.getBlockState(this.nextBlockPos).isSolid()) {
                            return false;
                        }
                    }
                } else {
                    GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                    this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos, 0);
                    if (this.pathToNextBlockPos != null) {
                        if (this.mob.world.getBlockState(this.nextBlockPos.add(0, -1, 0)).isSolid()) {
                            double nextPosY = this.nextBlockPos.getY();
                            double mobY = this.mob.getPosY();

                            if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 1, 0)).isSolid()) {
                                return false;
                            }

                            if (nextPosY > mobY) {
                                if (this.mob.world.getBlockState(this.mob.getPosition().add(0, 2, 0)).isSolid()) {
                                    return false;
                                }
                            } else if (nextPosY < mobY) {
                                if (this.mob.world.getBlockState(this.nextBlockPos.add(0, 2, 0)).isSolid()) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (this.isStandingOnBlock()) {
                    if (this.mob.getNavigator().getPath() != null) {
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
            }
        }
        return false;
    }

    public void tick(){
        this.tickCounter++;

        if(this.mob.getNavigator().noPath()){
            //System.out.println("no Path");
            this.pathToNextBlockPosActive = false;
            ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
            this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();
        }

        if(this.isPlacingBlock){
            this.faceTarget(this.placeBlockBlockPos);
            this.mob.getNavigator().setSpeed(0);
            if(this.tickCounter == this.placeBlockTick) {
                this.placeBlock(this.placeBlockBlockPos, this.shouldMoveToBlockPos);
                this.mob.getNavigator().setSpeed(this.speedModifier);
                this.isPlacingBlock = false;
            }
        }

        if(this.isJumping && this.tickCounter == this.endJumpTick){
            this.placeBlock(new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ()), false);
            this.isJumping = false;
        }

        if (!this.isPlacingBlock && this.tickCounter % 20 == 0) {
            if (this.playerTarget != null) {
                if (!this.isStandingOnBlock()){
                    if(Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getPosY()) < 2) {
                        if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) < 3 ||
                                Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) < 3) {
                        this.startPlacingBlock(this.tickCounter, this.mob.getPosition().add(0, -1, 0), false);
                        }
                    }
                } else{
                    if(this.mob.world.getBlockState(this.mob.getPosition()).isSolid()){
                        this.mobJump(this.tickCounter);
                    }
                    if (this.mob.getPosition().getX() == this.nextBlockPos.getX() && this.mob.getPosition().getY() < this.nextBlockPos.getY() && this.mob.getPosition().getZ() == this.nextBlockPos.getZ()) {
                        boolean canPlaceBlock = true;
                        BlockPos blockPos;
                        for (int i = 0; i < 3; i++) {
                            blockPos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() + i, this.mob.getPosZ());
                            if (this.mob.world.getBlockState(blockPos).isSolid()) {
                                canPlaceBlock = false;
                                break;
                            }
                        }
                        if (canPlaceBlock) {
                            this.mobJump(this.tickCounter);
                        }
                    } else {
                        if (Math.abs(Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getPosY())) < 2) {
                            if (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) < 3 ||
                                    Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) < 3) {
                                BlockPos blockPos = new BlockPos(this.nextBlockPos.add(0, -1, 0));
                                if (!this.mob.world.getBlockState(blockPos).isSolid()) {
                                    this.startPlacingBlock(this.tickCounter ,blockPos, true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void startPlacingBlock(int currentTick, BlockPos blockPos, boolean shouldMove){
        this.isPlacingBlock = true;
        this.placeBlockTick = currentTick + 20;
        this.placeBlockBlockPos = blockPos;
        this.shouldMoveToBlockPos = shouldMove;
    }

    public void faceTarget(BlockPos blockPos){
        /*double deltaX = this.nextBlockPos.getX() - this.mob.getPosX();
        double deltaZ = this.nextBlockPos.getZ() - this.mob.getPosZ();
        double yaw = Math.atan2(deltaZ, deltaX);yaw = Math.toDegrees(yaw) - 90.0;
        this.mob.rotationYaw = (float) yaw;*/
        this.mob.getLookController().setLookPosition(Vector3d.copyCentered(blockPos));
    }

    private void mobJump(int currentTick){
        this.isJumping = true;
        double x = this.mob.getPosX();
        double y = this.mob.getPosY() + 3;
        double z = this.mob.getPosZ();
        if(this.mob.world.getBlockState(new BlockPos(x, y, z)).isSolid()){
            this.endJumpTick = currentTick + 4;
        }else {
            this.endJumpTick = currentTick + 6;
        }
        //this.mob.getJumpController().setJumping();
        AxisAlignedBB bb = new AxisAlignedBB(this.mob.getPosition());
        List<MonsterEntity> monsterEntities = this.mob.world.getEntitiesWithinAABB(MonsterEntity.class, bb);
        if(!monsterEntities.isEmpty()){
            for(MonsterEntity monsterEntity: monsterEntities){
                monsterEntity.getJumpController().setJumping();
            }
        }
    }

    private boolean isStandingOnBlock(){
        BlockPos pos = new BlockPos(this.mob.getPosX(), this.mob.getPosY() - 1, this.mob.getPosZ());
        return this.mob.world.getBlockState(pos).isSolid();
    }

    private void placeBlock(BlockPos blockPos, boolean shouldMove){
        //SevenDaysToSurvive.LOGGER.info("Placing block at" + blockPos);
        if(!this.mob.world.getBlockState(blockPos).isSolid()) {
            this.mob.world.setBlockState(blockPos, Blocks.COBBLESTONE.getDefaultState());
            this.mob.swingArm(this.mob.getActiveHand());
            this.mob.getEntity().world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PLACE, this.mob.getEntity().getSoundCategory(), 1.0F, 1.0F);
        }
        if(shouldMove) {
            GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
            this.pathToNextBlockPos = groundPathNavigator.getPathToPos(new BlockPos(this.nextBlockPos.add(0, 1, 0)), 0);
            if (!this.pathToNextBlockPosActive) {
                this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
                //this.pathToNextBlockPosActive = true;
            }
        }
    }

    public void startExecuting() {
        System.out.println("start executing BuildForwardGoal");
        System.out.println("current blockpos: " + this.mob.getPosition());
        System.out.println("nextBlockPos: " + this.nextBlockPos);
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
        this.isJumping = false;
        this.isPlacingBlock = false;
        this.pathToNextBlockPosActive = true;
        this.heldItem = this.mob.getHeldItem(Hand.MAIN_HAND);
        this.mob.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));

        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$findCustomPath();
        this.nextBlockPos = ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$getNextBlockPos();

        if (this.nextBlockPos != null) {
            if(Math.abs(Math.abs(this.nextBlockPos.getY()) - Math.abs(this.mob.getPosY())) >= 2
                    && (Math.abs(Math.abs(this.nextBlockPos.getX()) - Math.abs(this.nextBlockPos.getX())) >= 2
                    && Math.abs(Math.abs(this.nextBlockPos.getZ()) - Math.abs(this.nextBlockPos.getZ())) >= 2)){

                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();

                this.pathToNextBlockPos = groundPathNavigator.getPathToPos(this.nextBlockPos.add(0, 1, 0), 0);
                this.mob.getNavigator().setPath(this.pathToNextBlockPos, this.speedModifier);
            }
        }
    }

    public void resetTask(){
        this.mob.setHeldItem(Hand.MAIN_HAND, this.heldItem);
        this.mob.getNavigator().clearPath();
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$customGoalFinished();
        System.out.println("stop executing BuildForwardGoal");
    }
    
}
