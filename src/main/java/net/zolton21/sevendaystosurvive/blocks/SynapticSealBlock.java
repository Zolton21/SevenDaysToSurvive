package net.zolton21.sevendaystosurvive.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.zolton21.sevendaystosurvive.registries.ModItems;

public class SynapticSealBlock extends Block{

    public static final int MIN_SYNAPTIC_DUST_COUNT = 0;
    public static final int MAX_SYNAPTIC_DUST_COUNT = 8;
    public static final IntegerProperty SYNAPTIC_DUST_COUNT = IntegerProperty.create("synaptic_dust_count", 0, MAX_SYNAPTIC_DUST_COUNT);

    public SynapticSealBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(SYNAPTIC_DUST_COUNT, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(SYNAPTIC_DUST_COUNT);
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
                    pLevel.setBlock(pPos, pState.setValue(SYNAPTIC_DUST_COUNT, value), 3);
                }
                this.displayState(value, pPlayer);
            }else if(itemStack.isEmpty() && pPlayer.isCrouching()){
                if(value > MIN_SYNAPTIC_DUST_COUNT) {
                    pPlayer.addItem(new ItemStack(ModItems.SYNAPTIC_DUST.get()));
                    value--;
                    pLevel.setBlock(pPos, pState.setValue(SYNAPTIC_DUST_COUNT, value), 3);
                }
                this.displayState(value, pPlayer);
            } else {
                this.displayState(value, pPlayer);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void displayState(int value, Player pPlayer){
        Component text;
        Component area;
        if(value<4){
            text = Component.literal(value + "/" + MAX_SYNAPTIC_DUST_COUNT + "   ").withStyle(ChatFormatting.RED);
            area = Component.literal("1x1").withStyle(ChatFormatting.RED);
        }else if(value < 8){
            text = Component.literal(value + "/" + MAX_SYNAPTIC_DUST_COUNT + "   ").withStyle(ChatFormatting.YELLOW);
            area = Component.literal("3x3").withStyle(ChatFormatting.YELLOW);
        }else{
            text = Component.literal(value + "/" + MAX_SYNAPTIC_DUST_COUNT + "   ").withStyle(ChatFormatting.GREEN);
            area = Component.literal("5x5").withStyle(ChatFormatting.GREEN);
        }

        pPlayer.displayClientMessage(Component.literal("Charge: ").append(text).append("Safe area: ").append(area).append(" Chunks"), true);
    }

    @Override
    public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
        super.destroy(pLevel, pPos, pState);

        ItemStack drop = new ItemStack(ModItems.SYNAPTIC_DUST.get(), pState.getValue(SYNAPTIC_DUST_COUNT));
        popResource((Level) pLevel, pPos, drop);
    }
}
