package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;
import net.zolton21.sevendaystosurvive.helper.IZombieGoalFunctions;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class MoveToTargetLastKnowLocationGoal extends Goal {

    protected final PathfinderMob mob;
    protected final double speedModifier;
    private BlockPos targetLastPos;
    private long taskCounter;
    private Path path;
    @Nullable
    private List<ChunkPos> chunkPosList;

    public MoveToTargetLastKnowLocationGoal(PathfinderMob creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if(this.mob instanceof Zombie){
            if(((IZombieGoalFunctions) this.mob).sevenDaysToSurvive$canRunMoveToTargetLastKnowLocationGoal()){
                this.targetLastPos = ((IZombieGoalFunctions)this.mob).sevenDaysToSurvive$getTargetLastLocation();
                GroundPathNavigation groundPathNavigator = (GroundPathNavigation) this.mob.getNavigation();
                this.path = groundPathNavigator.createPath(this.targetLastPos, 0);
                return this.path != null && this.path.canReach();
            } else{
            return false;
            }
        }else{
            return false;
        }
    }

    public void tick(){
        ChunkPos chunkPos = new ChunkPos(this.mob.blockPosition());
        if(!this.mob.level().getChunkSource().hasChunk(chunkPos.x, chunkPos.z)){
            ChunkPos cPos = new ChunkPos(this.mob.blockPosition());
            this.chunkPosList.add(cPos);
            ForgeChunkManager.forceChunk((ServerLevel) this.mob.level(), SevenDaysToSurvive.MOD_ID, this.mob, cPos.x, cPos.z, true, true);
        }
        this.taskCounter++;
    }

    public void start() {
        this.taskCounter = 0;
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
    }

    public boolean shouldContinueExecuting() {
        if(this.mob.getTarget() != null || this.taskCounter >= 300 || this.mob.getNavigation().isDone()){
            return false;
        }
        return !this.mob.getNavigation().isDone();
    }

    public void stop(){
        if(this.mob instanceof Zombie){
            ((IZombieGoalFunctions)this.mob).sevenDaysToSurvive$forbidMoveToTargetLastKnowLocationGoal();
            if(!this.mob.getNavigation().isDone()){
                this.mob.getNavigation().stop();
            }
        }
        if(this.chunkPosList != null){
            for(int i = 0; i < this.chunkPosList.size(); i++){
                ChunkPos cPos = this.chunkPosList.get(i);
                ForgeChunkManager.forceChunk((ServerLevel) this.mob.level(), SevenDaysToSurvive.MOD_ID, this.mob, cPos.x, cPos.z, false, true);
            }
        }
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
    }
}
