package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.item.SynapticSealItem;

public class ItemRegistry {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SevenDaysToSurvive.MOD_ID);

    public static final RegistryObject<BlockItem> SYNAPTIC_SEAL =
            ITEMS.register("synaptic_seal", () -> new SynapticSealItem(BlockRegistry.SYNAPTIC_SEAL.get(), new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
