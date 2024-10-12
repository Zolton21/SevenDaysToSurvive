package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public interface IZombieCustomTarget {

    BlockPos sevenDaysToSurvive$getNextBlockPos();

    void sevenDaysToSurvive$runFindCustomPath();

    void sevenDaysToSurvive$findReachableTarget();

    void sevenDaysToSurvive$resetModGoalTargetAndNextBlockPos();

    LivingEntity sevenDaysToSurvive$getModGoalTarget();

    void sevenDaysToSurvive$customGoalStarted();

    void sevenDaysToSurvive$customGoalFinished();

    void sevenDaysToSurvive$setLastExecutingGoal(Goal goal);

    Goal getSevenDaysToSurvive$lastExecutingGoal();
}
