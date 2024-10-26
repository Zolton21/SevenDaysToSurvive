package net.zolton21.sevendaystosurvive.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SynapticSealBlock extends Block {

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public SynapticSealBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, Boolean.valueOf(true)));
    }

    //@Override
    public void neighborChanged(BlockState state, Level pLevel, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if(!pLevel.isClientSide) {
            boolean isPowered = pLevel.hasNeighborSignal(pos);
            pLevel.setBlock(pos, state.setValue(ACTIVE, isPowered), 3);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(ACTIVE);
    }
}
