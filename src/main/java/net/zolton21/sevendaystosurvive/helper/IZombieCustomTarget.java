package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

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
