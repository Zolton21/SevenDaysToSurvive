package net.zolton21.sevendaystosurvive.forge.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.forge.item.ForgeSynapticSealItem;

public class ForgeItemRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(SevenDaysToSurvive.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<BlockItem> SYNAPTIC_SEAL =
            ITEMS.register("synaptic_seal", () -> new ForgeSynapticSealItem(ForgeBlockRegistry.SYNAPTIC_SEAL.get(), new Item.Properties()));

    public static void register() {
        ITEMS.register();
    }
}
