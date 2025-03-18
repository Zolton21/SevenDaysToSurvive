package net.zolton21.sevendaystosurvive.forge.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.forge.blockentity.ForgeSynapticSealBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SynapticSealBlockModel extends DefaultedBlockGeoModel<ForgeSynapticSealBlockEntity> {

    public SynapticSealBlockModel() {
        super(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    }

    @Override
    public RenderType getRenderType(ForgeSynapticSealBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
