package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SevenDaysToSurvive.MOD_ID);

    public static final RegistryObject<Item> SYNAPTIC_DUST = ITEMS.register("synaptic_dust", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
