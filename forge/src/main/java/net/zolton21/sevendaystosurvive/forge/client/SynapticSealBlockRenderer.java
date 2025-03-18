package net.zolton21.sevendaystosurvive.forge.client;

import net.zolton21.sevendaystosurvive.forge.blockentity.ForgeSynapticSealBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SynapticSealBlockRenderer extends GeoBlockRenderer<ForgeSynapticSealBlockEntity> {
    public SynapticSealBlockRenderer() {
        super(new SynapticSealBlockModel());
    }
}
