package net.zolton21.sevendaystosurvive.forge.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.forge.block.ForgeSynapticSealBlock;

import java.util.function.Supplier;

public class ForgeBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(SevenDaysToSurvive.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> SYNAPTIC_SEAL = registerBlock("synaptic_seal",
            () -> new ForgeSynapticSealBlock(BlockBehaviour.Properties.copy(Blocks.OBSIDIAN).noOcclusion()));

    private static <T extends Block> RegistrySupplier<T> registerBlock(String name, Supplier<T> block){
        RegistrySupplier<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    public static void register(){
        BLOCKS.register();
    }
}
