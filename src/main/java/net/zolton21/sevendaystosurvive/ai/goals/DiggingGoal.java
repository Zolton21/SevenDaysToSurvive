package net.zolton21.sevendaystosurvive.ai.goals;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeBlockState;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class DiggingGoal extends Goal {
    protected EntityPredicate targetEntitySelector;
    private LivingEntity playerTarget;
    protected final double speedModifier;
    private CreatureEntity mob;
    private Path path;
    private double playerTargetYPos;
    private int tickCounter;
    private ItemStack heldItem;

    public DiggingGoal(CreatureEntity creature, double speed) {
        this.mob = creature;
        this.speedModifier = speed;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.targetEntitySelector = (new EntityPredicate()).setDistance(this.mob.getAttributeValue(Attributes.FOLLOW_RANGE)).setCustomPredicate(null);
    }

    public boolean shouldExecute()
    {
        this.findReachableTarget();
        if(this.playerTarget == null){
            return false;
        }
        GroundPathNavigator groundPathNavigator = (GroundPathNavigator) this.mob.getNavigator();
        this.path = groundPathNavigator.getPathToPos(this.playerTarget.getPosition(), 0);
        return this.path != null && !this.path.reachesTarget();
    }

    private void breakBlock(){

    }

    public boolean shouldContinueExecuting(){
        if(this.tickCounter < 100){
            return true;
        }else {
            return this.playerTarget != null && this.path != null && !this.path.reachesTarget();
        }
    }

    public void startExecuting(){
        System.out.println("start executing DiggingGoal");
        this.findReachableTarget();
        this.tickCounter = 0;
        this.heldItem = this.mob.getHeldItem(Hand.MAIN_HAND);
        this.mob.setHeldItem(Hand.MAIN_HAND, new ItemStack(Items.STONE_PICKAXE));
    }

    public void resetTask(){
        this.mob.setHeldItem(Hand.MAIN_HAND, this.heldItem);
        this.mob.getNavigator().clearPath();
        System.out.println("stop executing DiggingGoal");
    }

    private void findReachableTarget(){
        this.playerTarget = this.mob.world.getClosestPlayer(this.targetEntitySelector, this.mob, this.mob.getPosX(), this.mob.getPosY(), this.mob.getPosZ());
        if(this.playerTarget != null) {
            this.playerTargetYPos = this.playerTarget.getPosY();
        }
    }
}
