package net.zolton21.sevendaystosurvive.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.blockentity.SynapticSealBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SynapticSealBlockModel extends DefaultedBlockGeoModel<SynapticSealBlockEntity> {

    public SynapticSealBlockModel() {
        super(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    }

    @Override
    public RenderType getRenderType(SynapticSealBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
