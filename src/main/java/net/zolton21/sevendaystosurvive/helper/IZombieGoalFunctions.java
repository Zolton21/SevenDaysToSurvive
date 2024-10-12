package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.core.BlockPos;

public interface IZombieGoalFunctions {

    void sevenDaysToSurvive$forbidMoveToTargetLastKnowLocationGoal();

    void sevenDaysToSurvive$allowMoveToTargetLastKnowLocationGoal(BlockPos location);

    BlockPos sevenDaysToSurvive$getTargetLastLocation();

    boolean sevenDaysToSurvive$canRunMoveToTargetLastKnowLocationGoal();
}
