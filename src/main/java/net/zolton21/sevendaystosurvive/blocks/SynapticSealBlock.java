package net.zolton21.sevendaystosurvive.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import net.zolton21.sevendaystosurvive.registries.ModBlockEntities;
import net.zolton21.sevendaystosurvive.registries.ModItems;
import org.jetbrains.annotations.Nullable;

public class SynapticSealBlock extends BaseEntityBlock {

    public static final int MIN_SYNAPTIC_DUST_COUNT = 0;
    public static final int MAX_SYNAPTIC_DUST_COUNT = 8;
    public static final IntegerProperty SYNAPTIC_DUST_COUNT = IntegerProperty.create("synaptic_dust_count", 0, MAX_SYNAPTIC_DUST_COUNT);
    public static final IntegerProperty STATE = IntegerProperty.create("state", 0, 2);

    //activity radius(in chunks)
    //public static final IntegerProperty RANGE = IntegerProperty.create("range", 0, 32);

    public SynapticSealBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(SYNAPTIC_DUST_COUNT, 0).setValue(STATE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(STATE).add(SYNAPTIC_DUST_COUNT);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return (BlockState)this.defaultBlockState().setValue(SYNAPTIC_DUST_COUNT, 0).setValue(STATE, 0);
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()){
            ItemStack itemStack = pPlayer.getMainHandItem();
            int value = pState.getValue(SYNAPTIC_DUST_COUNT);
            if(itemStack.getItem() == ModItems.SYNAPTIC_DUST.get()){
                if(value < MAX_SYNAPTIC_DUST_COUNT) {
                    itemStack.shrink(1);
                    value++;
                }
            }else if(itemStack.isEmpty() && pPlayer.isCrouching()){
                if(value > MIN_SYNAPTIC_DUST_COUNT) {
                    pPlayer.addItem(new ItemStack(ModItems.SYNAPTIC_DUST.get()));
                    value--;
                }
            }
            this.updateAndDisplayState(value, pPlayer, pState, pPos, pLevel);
        }
        return InteractionResult.SUCCESS;
    }

    private void updateAndDisplayState(int dustCountValue, Player pPlayer, BlockState state, BlockPos pPos, Level pLevel){
        ChatFormatting chatFormatting;
        int range;
        if(dustCountValue<4) {
            range = 0;
            pLevel.setBlock(pPos, state.setValue(STATE, 0).setValue(SYNAPTIC_DUST_COUNT, dustCountValue), 3);
            chatFormatting = ChatFormatting.RED;
        }
        else if(dustCountValue < 8) {
            range = 1;
            pLevel.setBlock(pPos, state.setValue(STATE, 1).setValue(SYNAPTIC_DUST_COUNT, dustCountValue), 3);
            chatFormatting = ChatFormatting.YELLOW;
        }
        else{
            range = 2;
            pLevel.setBlock(pPos, state.setValue(STATE, 2).setValue(SYNAPTIC_DUST_COUNT, dustCountValue), 3);
            chatFormatting = ChatFormatting.GREEN;
        }
        Component text = Component.literal(dustCountValue + "/" + MAX_SYNAPTIC_DUST_COUNT + "   ").withStyle(chatFormatting);
        Component area = Component.literal((1 + 2 * range) + "x" + (1 + 2 * range)).withStyle(chatFormatting);
        pPlayer.displayClientMessage(Component.literal("Charge: ").append(text).append("Safe area: ").append(area).append(" Chunks"), true);
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        super.destroy(pLevel, pPos, pState);

        ItemStack drop = new ItemStack(ModItems.SYNAPTIC_DUST.get(), pState.getValue(SYNAPTIC_DUST_COUNT));
        popResource((Level) pLevel, pPos, drop);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SynapticSealBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()){
            return null;
        }

        return  createTickerHelper(pBlockEntityType, ModBlockEntities.SYNAPTIC_SEAL_BLOCK_ENTITY.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}
