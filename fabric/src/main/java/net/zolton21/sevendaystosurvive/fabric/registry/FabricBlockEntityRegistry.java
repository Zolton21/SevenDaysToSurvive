package net.zolton21.sevendaystosurvive.fabric.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.fabric.blockentity.FabricSynapticSealBlockEntity;

public class FabricBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(SevenDaysToSurvive.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<FabricSynapticSealBlockEntity>> SYNAPTIC_SEAL =
            BLOCK_ENTITIES.register("synaptic_seal", () ->
                    BlockEntityType.Builder.of(FabricSynapticSealBlockEntity::new,
                            FabricBlockRegistry.SYNAPTIC_SEAL.get()).build(null));

    public static void register(){
        BLOCK_ENTITIES.register();
    }
}
