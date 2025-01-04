package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.blockentity.SynapticSealBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SevenDaysToSurvive.MOD_ID);

    public static final RegistryObject<BlockEntityType<SynapticSealBlockEntity>> SYNAPTIC_SEAL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("synaptic_seal_block_entity", () ->
                    BlockEntityType.Builder.of(SynapticSealBlockEntity::new,
                            ModBlocks.SYNAPTIC_SEAL_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
