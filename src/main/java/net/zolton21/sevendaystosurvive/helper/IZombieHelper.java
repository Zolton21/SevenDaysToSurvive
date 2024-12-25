package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.Path;

public interface IZombieHelper {

    BlockPos sevenDaysToSurvive$getNextBlockPos();

    float sevenDaysToSurvive$getBlockBreakingSpeedModifier();

    void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();

    LivingEntity sevenDaysToSurvive$getModGoalTarget();

    void sevenDaysToSurvive$customGoalStarted();

    void sevenDaysToSurvive$customGoalFinished();

    boolean SevenDaysToSurvive$getCanReachTarget();

    Path sevenDaysToSurvive$getPathToNextBlockPos();

    void sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(boolean isTrue);

    boolean sevenDaysToSurvive$getIsWithinSynapticSealActivityRange();

}
