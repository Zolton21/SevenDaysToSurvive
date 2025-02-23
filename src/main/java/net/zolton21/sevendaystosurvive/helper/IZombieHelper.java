package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.pathfinder.Path;

import java.util.List;

public interface IZombieHelper {
    void setSevenDaysToSurvive$nextBlockPos(BlockPos blockPos);

    BlockPos sevenDaysToSurvive$getNextBlockPos();

    float sevenDaysToSurvive$getBlockBreakingSpeedModifier();

    void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();

    LivingEntity sevenDaysToSurvive$getModGoalTarget();

    void sevenDaysToSurvive$customGoalStarted();

    void sevenDaysToSurvive$customGoalFinished();

    Path sevenDaysToSurvive$getPathToNextBlockPos();

    void sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(boolean isTrue);

    boolean sevenDaysToSurvive$getIsWithinSynapticSealActivityRange();

    void sevenDaysToSurvive$findCustomPath();

    void setSevenDaysToSurvive$placedBlockBlockPos(BlockPos blockPos);

    void setSevenDaysToSurvive$dugNextBlockPos(BlockPos blockPos);

    BlockPos getSevenDaysToSurvive$dugNextBlockPos();

    BlockPos getSevenDaysToSurvive$placedBlockBlockPos();

    Path getSevenDaysToSurvive$pathToTargetEntity();

    void sevenDaysToSurvive$createPathToTargetEntity();

    boolean sevenDaysToSurvive$getMobHasPlayerTargetAndCanReach();
}
