package net.zolton21.sevendaystosurvive.forge.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.zolton21.sevendaystosurvive.forge.blockentity.ForgeSynapticSealBlockEntity;
import net.zolton21.sevendaystosurvive.forge.registry.ForgeBlockEntityRegistry;
import net.zolton21.sevendaystosurvive.helper.IZombieHelper;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ForgeSynapticSealBlock extends BaseEntityBlock {
    public static final int MIN_SHARD_COUNT = 0;
    public static final int MAX_SHARD_COUNT = 8;
    public static final IntegerProperty ECHO_SHARD_COUNT = IntegerProperty.create("echo_shard_count", 0, MAX_SHARD_COUNT);
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 2);
    List<ServerPlayer> protectedPlayers = new ArrayList<>();
    List<Monster> zombiesWithinRange = new ArrayList<>();

    public ForgeSynapticSealBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(ECHO_SHARD_COUNT, 0).setValue(STATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(STATE).add(ECHO_SHARD_COUNT);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(ECHO_SHARD_COUNT, 0).setValue(STATE, 0);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
        if(pLevel.isClientSide()){
            double radius = 0.5;
            DustParticleOptions particle = new DustParticleOptions(new Vector3f(0.161F, 0.874F, 0.922F), 1.0F);
            int particleCount = pState.getValue(ECHO_SHARD_COUNT) * 5;
            for (int i = 0; i < particleCount; i++) {
                double theta = pRandom.nextDouble() * Math.PI;
                double phi = pRandom.nextDouble() * 2 * Math.PI;
                double x = pPos.getX() + 0.5 + radius * Math.sin(theta) * Math.cos(phi);
                double y = pPos.getY() + 0.5 + radius * Math.cos(theta);
                double z = pPos.getZ() + 0.5 + radius * Math.sin(theta) * Math.sin(phi);

                pLevel.addParticle(particle, x, y, z, 0, 0, 0);
            }
        }
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            ItemStack itemStack = pPlayer.getMainHandItem();
            int value = pState.getValue(ECHO_SHARD_COUNT);
            if(itemStack.getItem() == Items.ECHO_SHARD){
                if(value < MAX_SHARD_COUNT) {
                    itemStack.shrink(1);
                    value++;
                }
            }else if(itemStack.isEmpty() && pPlayer.isCrouching()){
                if(value > MIN_SHARD_COUNT) {
                    pPlayer.addItem(new ItemStack(Items.ECHO_SHARD));
                    value--;
                }
            }
            this.updateAndDisplayState(value, pPlayer, pState, pPos, pLevel);
        }
        return InteractionResult.SUCCESS;
    }

    private void updateAndDisplayState(int shardCountValue, Player pPlayer, BlockState state, BlockPos pPos, Level pLevel){
        ChatFormatting chatFormatting;
        int range;
        if(shardCountValue<MAX_SHARD_COUNT/2) {
            range = 0;
            pLevel.setBlock(pPos, state.setValue(STATE, 0).setValue(ECHO_SHARD_COUNT, shardCountValue), 3);
            chatFormatting = ChatFormatting.RED;
        }
        else if(shardCountValue < MAX_SHARD_COUNT) {
            range = 1;
            pLevel.setBlock(pPos, state.setValue(STATE, 1).setValue(ECHO_SHARD_COUNT, shardCountValue), 3);
            chatFormatting = ChatFormatting.YELLOW;
        }
        else{
            range = 2;
            pLevel.setBlock(pPos, state.setValue(STATE, 2).setValue(ECHO_SHARD_COUNT, shardCountValue), 3);
            chatFormatting = ChatFormatting.GREEN;
        }
        Component text = Component.literal(shardCountValue + "/" + MAX_SHARD_COUNT + "   ").withStyle(chatFormatting);
        Component area = Component.literal((1 + 2 * range) + "x" + (1 + 2 * range)).withStyle(chatFormatting);
        pPlayer.displayClientMessage(Component.translatable("message.sevendaystosurvive.synaptic.seal.charge").append(text).append(Component.translatable("message.sevendaystosurvive.synaptic.seal.safe.area")).append(": ").append(area).append(" ").append(Component.translatable("message.sevendaystosurvive.synaptic.seal.chunks")), true);
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        if(!pLevel.isClientSide()) {
            for (Player player : this.protectedPlayers) {
                if (player != null && player.isAlive()) {
                    PlayerHelper.changePlayerProtectionState((ServerPlayer) player, false);
                }
            }
            for (Monster zombie : this.zombiesWithinRange) {
                if (zombie != null && zombie.isAlive()) {
                    ((IZombieHelper) zombie).sevenDaysToSurvive$setIsWithinSynapticSealActivityRange(false);
                }
            }

            super.destroy(pLevel, pPos, pState);

            ItemStack drop = new ItemStack(Items.ECHO_SHARD, pState.getValue(ECHO_SHARD_COUNT));
            popResource((Level) pLevel, pPos, drop);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ForgeSynapticSealBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()){
            return null;
        }

        return createTickerHelper(pBlockEntityType, ForgeBlockEntityRegistry.SYNAPTIC_SEAL.get(),
                    (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }

    public void updateLists(List<ServerPlayer> list1, List<Monster> list2){
        this.protectedPlayers.clear();
        this.zombiesWithinRange.clear();
        if(!list1.isEmpty()) {
            this.protectedPlayers.addAll(list1);
        }
        if(!list2.isEmpty()) {
            this.zombiesWithinRange.addAll(list2);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return Integer.min(state.getValue(ECHO_SHARD_COUNT) * 2, 15);
    }
}
