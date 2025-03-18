package net.zolton21.sevendaystosurvive.forge.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.forge.blockentity.ForgeSynapticSealBlockEntity;

public class ForgeBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(SevenDaysToSurvive.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<ForgeSynapticSealBlockEntity>> SYNAPTIC_SEAL =
            BLOCK_ENTITIES.register("synaptic_seal", () ->
                    BlockEntityType.Builder.of(ForgeSynapticSealBlockEntity::new,
                            ForgeBlockRegistry.SYNAPTIC_SEAL.get()).build(null));

    public static void register(){
        BLOCK_ENTITIES.register();
    }
}
