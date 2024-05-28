package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.entity.CreatureEntity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.helper.IZombieCustomTarget;
import net.zolton21.sevendaystosurvive.helper.IZombieGoalFunctions;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class MoveToTargetLastKnowLocationGoal extends Goal {

    protected final CreatureEntity mob;
    protected final double speedModifier;
    private BlockPos targetLastPos;
    private long taskCounter;
    private Path path;
    @Nullable
    private List<ChunkPos> chunkPosList;

    public MoveToTargetLastKnowLocationGoal(CreatureEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean shouldExecute() {
        if(this.mob instanceof ZombieEntity){
            if(((IZombieGoalFunctions) this.mob).sevenDaysToSurvive$canRunMoveToTargetLastKnowLocationGoal()){
                this.targetLastPos = ((IZombieGoalFunctions)this.mob).sevenDaysToSurvive$getTargetLastLocation();
                GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
                this.path = groundPathNavigator.getPathToPos(this.targetLastPos, 0);
                return this.path != null && this.path.reachesTarget();
            } else{
            return false;
            }
        }else{
            return false;
        }
    }

    public void tick(){
        if(!this.mob.world.getChunkProvider().isChunkLoaded(mob) && this.mob.world != null){
            ChunkPos cPos = new ChunkPos(this.mob.getPosition());
            this.chunkPosList.add(cPos);
            ForgeChunkManager.forceChunk((ServerWorld) this.mob.world, SevenDaysToSurvive.MOD_ID, this.mob, cPos.x, cPos.z, true, true);
        }
        this.taskCounter++;
    }

    public void startExecuting() {
        this.taskCounter = 0;
        this.mob.getNavigator().setPath(this.path, this.speedModifier);
    }

    public boolean shouldContinueExecuting() {
        if(this.mob.getAttackTarget() != null || this.taskCounter >= 300 || this.mob.getNavigator().noPath()){
            return false;
        }
        return !this.mob.getNavigator().noPath();
    }

    public void resetTask(){
        if(this.mob instanceof ZombieEntity){
            ((IZombieGoalFunctions)this.mob).sevenDaysToSurvive$forbidMoveToTargetLastKnowLocationGoal();
            if(!this.mob.getNavigator().noPath()){
                this.mob.getNavigator().clearPath();
            }
        }
        if(this.chunkPosList != null){
            for(int i = 0; i < this.chunkPosList.size(); i++){
                ChunkPos cPos = this.chunkPosList.get(i);
                ForgeChunkManager.forceChunk((ServerWorld) this.mob.world, SevenDaysToSurvive.MOD_ID, this.mob, cPos.x, cPos.z, false, true);
            }
        }
        ((IZombieCustomTarget)this.mob).sevenDaysToSurvive$setLastExecutingGoal(this);
    }
}
