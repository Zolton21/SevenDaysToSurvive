package net.zolton21.sevendaystosurvive.client;

import net.zolton21.sevendaystosurvive.blockentity.SynapticSealBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SynapticSealBlockRenderer extends GeoBlockRenderer<SynapticSealBlockEntity> {
    public SynapticSealBlockRenderer() {
        super(new SynapticSealBlockModel());
    }
}
