package net.zolton21.sevendaystosurvive.ai.goals;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;

import java.util.EnumSet;
import java.util.List;

public class BuildTowardsTargetGoal extends Goal {
    protected final double speedModifier;
    private final PathfinderMob mob;
    private Path pathToNextBlockPos;
    private int tickCounter;
    private int endJumpTick;
    private boolean isJumping;
    private ItemStack heldItem;
    private int placeBlockTick;
    private boolean isPlacingBlock;
    private BlockPos placeBlockBlockPos;
    private long lastCanUseCheck;

    public BuildTowardsTargetGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
    }

    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            System.out.println("can use BuildingGoal false 0.1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                System.out.println("can use BuildingGoal false 0.2");
                return false;
            }
        }

        if (ZombieUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            System.out.println("can use BuildingGoal false 1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            System.out.println("BuildingGoal canUse false 2.1");
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            System.out.println("BuildingGoal canUse false 2.2");
            return false;
        }

        if(((IZombieHelper)this.mob).getSevenDaysToSurvive$leader() != null){
            System.out.println("can use BuildingGoal false (Zombie Has A Leader)");
            return false;
        }

        if (this.mob.level().getBlockState(this.mob.blockPosition()).getFluidState().isEmpty()) {
            if (!this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getFluidState().isEmpty()) {
                System.out.println("can use BuildingGoal true 0.1");
                return true;
            }
        }

        if (ZombieUtils.isMobStandingOnAFullBlock(this.mob)) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()) {
                    System.out.println("can use BuildingGoal true liquid nextBlockPos 0, -1, 0");
                    return true;
                }

                if (this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    System.out.println("can use BuildingGoal false (nextblockposition isn't Fluid)");
                    if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).isAir()) {
                        System.out.println("can use BuildingGoal false (nextblockposition isn't air)");
                        return false;
                    }
                }

                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)) &&
                        !ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)) &&
                        !ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()) &&
                        this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                    System.out.println("can use BuildingGoal false 3");
                    return false;
                }
                if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() - this.mob.getBlockY()) < 3) {
                    if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() - this.mob.getBlockX()) < 3 || Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() - this.mob.getBlockZ()) < 3) {
                        if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty() || !this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()) {
                            System.out.println("can use BuildingGoal true 1");
                            return true;
                        }
                    }
                }
                if (this.mob.getBlockX() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() && this.mob.getBlockZ() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()) {
                    if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() > this.mob.getBlockY()) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                            System.out.println("can use BuildingGoal false 4");
                            return false;
                        }
                    } else if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() < this.mob.getBlockY()) {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                            System.out.println("can use BuildingGoal false 5");
                            return false;
                        }
                    }
                }
                this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
                if (this.pathToNextBlockPos != null) {
                    if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                        System.out.println("can use if 134");
                        double nextPosY = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY();
                        double mobY = this.mob.getBlockY();
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                            System.out.println("can use BuildingGoal false 6");
                            return false;
                        }
                        if (nextPosY > mobY) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                System.out.println("can use BuildingGoal false 7");
                                return false;
                            }
                        } else if (nextPosY < mobY) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 2, 0))) {
                                System.out.println("can use BuildingGoal false 8");
                                return false;
                            }
                        }
                    } else if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -2, 0))) {
                        System.out.println("can use if 153");
                        if (this.mob.getY() >= ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY()) {
                            System.out.println("can use if 155");
                            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() != this.mob.getBlockX() || ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() != this.mob.getBlockZ()) {
                                System.out.println("can use BuildingGoal false 9");
                                return false;
                            }
                        }
                    }
                    /*if(ModUtils.HasBlockEntityCollision(this.mob.level(), this.pathToNextBlockPos.getTarget())){
                        System.out.println("can use BuildingGoal false 10");
                        return false;
                    }*/
                }
                System.out.println("can use BuildingGoal true 2");
                return true;
            }
        }
        System.out.println("can use BuildingGoal false 11");
        return false;
    }

    public boolean canContinueToUse() {
        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getIsWithinSynapticSealActivityRange()) {
            System.out.println("cancel 0.1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
            if (PlayerHelper.isPlayerProtected((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget())) {
                System.out.println("cancel 0.2");
                return false;
            }
        }

        if (ZombieUtils.mobHasPlayerTargetAndCanReach(this.mob)) {
            System.out.println("cancel 1");
            return false;
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() == null) {
            System.out.println("BuildingGoal cancel 2.1");
            return false;
        } else if (!((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget().isAlive() || (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isSpectator() || ((ServerPlayer) ((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget()).isCreative()) {
            System.out.println("BuildingGoal cancel 2.2");
            return false;
        }

        if(((IZombieHelper)this.mob).getSevenDaysToSurvive$leader() != null){
            System.out.println("can use BuildingGoal cancel (Zombie Has A Leader)");
            return false;
        }

        if (this.mob.level().getBlockState(this.mob.blockPosition()).getFluidState().isEmpty()) {
            if (!this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getFluidState().isEmpty()) {
                System.out.println("can use BuildingGoal true 0.1");
                return true;
            }
        }

        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                    if (!this.isJumping) {
                        if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)).getFluidState().isEmpty()) {
                            System.out.println("canContinueToUse true liquid nextBlockPos 0, -1, 0");
                            return true;
                        }

                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0)) && !ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)) && !ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()) && this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                            System.out.println("cancel 4");
                            return false;
                        }
                    }

                    if (this.mob.getBlockX() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() && this.mob.getBlockZ() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()) {
                        if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() > this.mob.getBlockY()) {
                            if (!this.isJumping) {
                                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                                    System.out.println("cancel 5");
                                    return false;
                                }
                            }
                        } else if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() < this.mob.getBlockY()) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos())) {
                                if (!this.isJumping) {
                                    System.out.println("cancel 6");
                                    return false;
                                }
                            }
                        }
                    } else {
                        this.pathToNextBlockPos = ((IZombieHelper) this.mob).sevenDaysToSurvive$getPathToNextBlockPos();
                        if (this.pathToNextBlockPos != null) {
                            if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0))) {
                                double nextPosY = ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY();
                                double mobY = this.mob.getBlockY();
                                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0))) {
                                    System.out.println("cancel 7");
                                    return false;
                                }
                                if (nextPosY > mobY) {
                                    if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition().offset(0, 2, 0))) {
                                        System.out.println("cancel 8");
                                        return false;
                                    }
                                } else if (nextPosY < mobY) {
                                    if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 2, 0))) {
                                        System.out.println("cancel 9");
                                        return false;
                                    }
                                }
                            }
                        }
                    }

                    if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() - this.mob.getBlockY()) < 3) {
                        if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() - this.mob.getBlockX()) < 3 || Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() - this.mob.getBlockZ()) < 3) {
                            if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty() || !this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()) {
                                return true;
                            }
                        }
                    }

                    return true;
                }
            }
        }
        System.out.println("cancel 12");
        return false;
    }

    public void tick() {
        this.tickCounter++;

        if (this.isPlacingBlock) {
            this.faceTarget(this.placeBlockBlockPos);
            this.mob.getNavigation().setSpeedModifier(0);
            if (this.tickCounter == this.placeBlockTick) {
                this.placeBlock(this.placeBlockBlockPos);
                this.mob.getNavigation().setSpeedModifier(this.speedModifier);
                this.isPlacingBlock = false;
                ((IZombieHelper) this.mob).setSevenDaysToSurvive$nextBlockPos(this.placeBlockBlockPos.offset(0, 1, 0));
            }
        }

        if (this.isJumping && this.tickCounter == this.endJumpTick) {
            this.placeBlock(new BlockPos(this.mob.getBlockX(), this.mob.getBlockY() - 1, this.mob.getBlockZ()));
            this.isJumping = false;
        }

        if (!this.isPlacingBlock && this.tickCounter % 10 == 0) {
            System.out.println("if0");
            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getModGoalTarget() != null) {
                System.out.println("if0.2");
                if (this.mob.level().getBlockState(this.mob.blockPosition()).getFluidState().isEmpty()) {
                    if (!this.mob.level().getBlockState(this.mob.blockPosition().offset(0, -1, 0)).getFluidState().isEmpty()) {
                        this.startPlacingBlock(this.mob.blockPosition().offset(0, -1, 0));
                    }
                }
                if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos() != null) {
                    System.out.println("if0.3");
                    if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                        System.out.println("if1");
                        if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() - this.mob.getBlockX()) < 3 || Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() - this.mob.getBlockZ()) < 3) {
                            System.out.println("Start placing block 1");
                            this.startPlacingBlock(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
                        }
                    } else if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)).getFluidState().isEmpty()) {
                        System.out.println("if2");
                        if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() - this.mob.getBlockX()) < 3 || Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() - this.mob.getBlockZ()) < 3) {
                            System.out.println("Start placing block 2");
                            this.startPlacingBlock(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0));
                        }
                    } else {
                        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), this.mob.blockPosition())) {
                            this.mobJump(this.tickCounter);
                        }
                        if (this.mob.blockPosition().getX() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() && this.mob.blockPosition().getY() < ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() && this.mob.blockPosition().getZ() == ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()) {
                            boolean canPlaceBlock = true;
                            BlockPos blockPos;
                            for (int i = 0; i < 3; i++) {
                                blockPos = new BlockPos(this.mob.getBlockX(), this.mob.getBlockY() + i, this.mob.getBlockZ());
                                if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
                                    canPlaceBlock = false;
                                    break;
                                }
                            }
                            if (canPlaceBlock) {
                                this.mobJump(this.tickCounter);
                            }

                            if (((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() == this.mob.getBlockX() && ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() == this.mob.getBlockZ()) {
                                double distance = this.mob.distanceToSqr(this.mob.blockPosition().getCenter());
                                if (distance > 0.15) {
                                    System.out.println("Move to block center (Building goal)");
                                    double currentX = this.mob.getX();
                                    double currentZ = this.mob.getZ();
                                    double offsetX = Math.floor(currentX) + 0.5 - currentX;
                                    double offsetZ = Math.floor(currentZ) + 0.5 - currentZ;
                                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(offsetX * 0.1, 0, offsetZ * 0.1));
                                }
                            }

                        } else {
                            if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getY() - this.mob.getBlockY()) < 2) {
                                System.out.println("if3");
                                if (Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX() - ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getX()) < 3 ||
                                        Math.abs(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ() - ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().getZ()) < 3) {
                                    System.out.println("if4");
                                    BlockPos blockPos = new BlockPos(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, -1, 0));
                                    System.out.println(this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()));
                                    System.out.println(this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty());
                                    if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
                                        System.out.println("Start placing block 3");
                                        this.startPlacingBlock(blockPos);
                                    } else if (!this.mob.level().getBlockState(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos()).getFluidState().isEmpty()) {
                                        System.out.println("Start placing block 4");
                                        this.startPlacingBlock(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
                                    } /*else if (this.diagonalBlockPos != null) {
                                        if (!this.mob.level().getBlockState(this.diagonalBlockPos).getFluidState().isEmpty()) {
                                            System.out.println("Start placing block 5");
                                            this.startPlacingBlock(this.diagonalBlockPos, true);
                                        } else if (!this.mob.level().getBlockState(this.diagonalBlockPos.offset(0, -1, 0)).getFluidState().isEmpty()) {
                                            System.out.println("Start placing block 6");
                                            this.startPlacingBlock(this.diagonalBlockPos.offset(0, -1, 0), true);
                                        }
                                    }*/
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void startPlacingBlock(BlockPos blockPos) {
        this.isPlacingBlock = true;
        this.placeBlockTick = this.tickCounter + 10;
        this.placeBlockBlockPos = blockPos;
    }

    public void faceTarget(BlockPos blockPos) {
        this.mob.getLookControl().setLookAt(Vec3.atCenterOf(blockPos));
    }

    private void mobJump(int currentTick) {

        this.isJumping = true;
        int x = this.mob.getBlockX();
        int y = this.mob.getBlockY() + 3;
        int z = this.mob.getBlockZ();
        if (ZombieUtils.HasBlockEntityCollision(this.mob.level(), new BlockPos(x, y, z))) {
            this.endJumpTick = currentTick + 2;
        } else {
            this.endJumpTick = currentTick + 4;
        }
        //this.mob.getJumpController().setJumping();
        AABB aabb = new AABB(this.mob.blockPosition());
        List<Monster> monsterEntities = this.mob.level().getEntitiesOfClass(Monster.class, aabb);
        if (!monsterEntities.isEmpty()) {
            for (Monster monsterEntity : monsterEntities) {
                monsterEntity.getJumpControl().jump();
            }
        }
    }

    private void placeBlock(BlockPos blockPos) {
        System.out.println("placing block");
        System.out.println("mob blockPos: " + this.mob.blockPosition());
        System.out.println("next blockPos: " + ((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos());
        System.out.println("place block blockPos: " + blockPos);
        ((IZombieHelper) this.mob).setSevenDaysToSurvive$placedBlockBlockPos(blockPos);
        if (!ZombieUtils.HasBlockEntityCollision(this.mob.level(), blockPos)) {
            this.mob.level().setBlock(blockPos, Blocks.COBBLESTONE.defaultBlockState(), 3);
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.level().playSound(null, blockPos, SoundEvents.STONE_PLACE, this.mob.getSoundSource(), 1.0F, 1.0F);
        }
        GroundPathNavigation GroundPathNavigation = (GroundPathNavigation) this.mob.getNavigation();
        this.pathToNextBlockPos = GroundPathNavigation.createPath(new BlockPos(((IZombieHelper) this.mob).sevenDaysToSurvive$getNextBlockPos().offset(0, 1, 0)), 0);
    }

    public void start() {
        System.out.println("start executing BuildForwardGoal");
        this.mob.getNavigation().stop();

        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalStarted();
        this.tickCounter = 0;
        this.isJumping = false;
        this.isPlacingBlock = false;
        this.heldItem = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.COBBLESTONE));
    }

    public void stop() {
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, this.heldItem);
        ((IZombieHelper) this.mob).sevenDaysToSurvive$customGoalFinished();
        System.out.println("stop executing BuildForwardGoal");
    }

}
