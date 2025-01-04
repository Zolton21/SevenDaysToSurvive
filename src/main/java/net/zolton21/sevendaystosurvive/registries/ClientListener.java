package net.zolton21.sevendaystosurvive.registries;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.client.SynapticSealBlockRenderer;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.registry.BlockRegistry;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientListener {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.SYNAPTIC_SEAL_BLOCK_ENTITY.get(), (context) -> {
            return new SynapticSealBlockRenderer();
        });
    }

    @SubscribeEvent
    public static void registerRenderers(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer((Block) BlockRegistry.GECKO_HABITAT.get(), RenderType.translucent());
    }
}
