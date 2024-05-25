package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public interface IZombieCustomTarget {

    BlockPos sevenDaysToSurvive$getNextBlockPos();

    void sevenDaysToSurvive$findCustomPath();

    void sevenDaysToSurvive$findReachableTarget();

    //void setSevenDaysToSurvive$modGoalTarget(LivingEntity entity);

    LivingEntity sevenDaysToSurvive$getModGoalTarget();

    void sevenDaysToSurvive$customGoalStarted();

    void sevenDaysToSurvive$customGoalFinished();
}
