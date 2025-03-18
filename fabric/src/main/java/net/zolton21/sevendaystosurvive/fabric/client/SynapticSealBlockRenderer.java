package net.zolton21.sevendaystosurvive.fabric.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.zolton21.sevendaystosurvive.fabric.blockentity.FabricSynapticSealBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SynapticSealBlockRenderer extends GeoBlockRenderer<FabricSynapticSealBlockEntity> {
    public SynapticSealBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new SynapticSealBlockModel());
    }
}
