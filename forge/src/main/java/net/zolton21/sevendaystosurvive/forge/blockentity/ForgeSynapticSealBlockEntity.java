package net.zolton21.sevendaystosurvive.forge.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.zolton21.sevendaystosurvive.config.CommonConfig;
import net.zolton21.sevendaystosurvive.forge.block.ForgeSynapticSealBlock;
import net.zolton21.sevendaystosurvive.forge.registry.ForgeBlockEntityRegistry;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.utils.ZombieUtils;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static net.zolton21.sevendaystosurvive.forge.block.ForgeSynapticSealBlock.STATE;


public class ForgeSynapticSealBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    List<ServerPlayer> protectedPlayers = new CopyOnWriteArrayList<>();
    List<Monster> zombiesWithinRange = new CopyOnWriteArrayList<>();

    public ForgeSynapticSealBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ForgeBlockEntityRegistry.SYNAPTIC_SEAL.get(), pPos, pBlockState);
    }

    public List<Monster> zombiesWithinRange(BlockPos blockPos, BlockState blockState, ServerLevel world) {
        int activityRange = blockState.getValue(STATE);
        int blockChunkX = Math.floorDiv(blockPos.getX(), 16);
        int blockChunkZ = Math.floorDiv(blockPos.getZ(), 16);

        int minChunkX = blockChunkX - activityRange;
        int maxChunkX = blockChunkX + activityRange;
        int minChunkZ = blockChunkZ - activityRange;
        int maxChunkZ = blockChunkZ + activityRange;

        int minWorldX = minChunkX * 16;
        int maxWorldX = (maxChunkX + 1) * 16 - 1;
        int minWorldZ = minChunkZ * 16;
        int maxWorldZ = (maxChunkZ + 1) * 16 - 1;

        AABB range = new AABB(minWorldX, world.getMinBuildHeight(), minWorldZ,
                maxWorldX, world.getMaxBuildHeight(), maxWorldZ);
        List<Monster> monsters = new ArrayList<>();
        monsters.addAll(world.getEntitiesOfClass(Zombie.class, range));
        monsters.addAll(world.getEntitiesOfClass(Husk.class, range));

        return monsters;
    }

    public boolean isEntityWithinRange(BlockPos blockPos, BlockState blockState, ServerPlayer player){
        int state = blockState.getValue(STATE);
        int activityRange = 0;
        int blockChunkX = Math.floorDiv(blockPos.getX(), 16);
        int blockChunkZ = Math.floorDiv(blockPos.getZ(), 16);
        if(state == 1){
            activityRange = 1;
        }
        if(state == 2){
            activityRange = 2;
        }
        int minChunkX = blockChunkX - activityRange;
        int maxChunkX = blockChunkX + activityRange;
        int minChunkZ = blockChunkZ - activityRange;
        int maxChunkZ = blockChunkZ + activityRange;

        int playerChunkX = Math.floorDiv(player.getBlockX(), 16);
        int playerChunkZ = Math.floorDiv(player.getBlockZ(), 16);
        return playerChunkX >= minChunkX && playerChunkX <= maxChunkX && playerChunkZ >= minChunkZ && playerChunkZ <= maxChunkZ;
    }

    public void tick(Level pLevel1, BlockPos pPos, BlockState pState1) {
        if(ZombieUtils.isOblivionNight(this.getLevel()) && !CommonConfig.Server.OBLIVION_NIGHT_SYNAPTIC_SEAL_WORKS.get()) {

        }else{

            for (Player player : pLevel1.players()) {
                if (player != null) {
                    ServerPlayer serverPlayer = (ServerPlayer) player;
                    if (this.isEntityWithinRange(pPos, pState1, serverPlayer)) {
                        if (serverPlayer.isAlive()) {
                            if (this.protectedPlayers.stream().noneMatch(p -> p.equals(serverPlayer))) {
                                this.protectedPlayers.add(serverPlayer);
                                PlayerHelper.changePlayerProtectionState(serverPlayer, true);
                            }
                        }
                    } else {
                        if (this.protectedPlayers.stream().anyMatch(p -> p.equals(serverPlayer))) {
                            if (serverPlayer.isAlive()) {
                                this.protectedPlayers.remove(serverPlayer);
                                PlayerHelper.changePlayerProtectionState(serverPlayer, false);
                            }
                        }
                    }
                }
            }

            for (Monster zombie : zombiesWithinRange(pPos, pState1, (ServerLevel) pLevel1)) {
                if (zombie != null) {
                    if(zombie.getType() == EntityType.ZOMBIE || zombie.getType() == EntityType.HUSK) {
                        if (this.zombiesWithinRange.stream().noneMatch(z -> z.equals(zombie))) {
                            if (zombie.isAlive()) {
                                this.zombiesWithinRange.add(zombie);
                                ((IZombieHelper) zombie).sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(true);
                            }
                        }
                    }
                }
            }

            for (Monster zombie : this.zombiesWithinRange) {
                if (zombie != null) {
                    if(zombie.getType() == EntityType.ZOMBIE || zombie.getType() == EntityType.HUSK) {
                        if (zombiesWithinRange(pPos, pState1, (ServerLevel) pLevel1).stream().noneMatch(z -> z.equals(zombie))) {
                            if (zombie.isAlive()) {
                                this.zombiesWithinRange.remove(zombie);
                                ((IZombieHelper) zombie).sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(false);
                            }
                        }
                    }
                }
            }

            Block block = this.getBlockState().getBlock();
            if (block instanceof ForgeSynapticSealBlock) {
                ((ForgeSynapticSealBlock) block).updateLists(this.protectedPlayers, this.zombiesWithinRange);
            }
        }
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<ForgeSynapticSealBlockEntity> synapticSealBlockEntityAnimationState) {
        synapticSealBlockEntityAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.synaptic_seal.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
