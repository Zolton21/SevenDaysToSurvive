package net.zolton21.sevendaystosurvive.fabric;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.zolton21.sevendaystosurvive.fabric.client.SynapticSealBlockRenderer;
import net.zolton21.sevendaystosurvive.fabric.registry.FabricBlockEntityRegistry;

public class SevenDaysToSurviveFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(FabricBlockEntityRegistry.SYNAPTIC_SEAL.get(), SynapticSealBlockRenderer::new);
    }
}
