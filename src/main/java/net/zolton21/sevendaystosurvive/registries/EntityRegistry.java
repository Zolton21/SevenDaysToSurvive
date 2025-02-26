package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.world.entity.AdvancedZombie;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SevenDaysToSurvive.MOD_ID);

    public static final RegistryObject<EntityType<AdvancedZombie>> ADVANCED_ZOMBIE =
            ENTITIES.register("advanced_zombie", () -> EntityType.Builder.of(AdvancedZombie::new, MobCategory.MONSTER).sized(0.6F, 1.95F).build("advanced_zombie"));



    public static void register(IEventBus bus){
        ENTITIES.register(bus);
    }
}
