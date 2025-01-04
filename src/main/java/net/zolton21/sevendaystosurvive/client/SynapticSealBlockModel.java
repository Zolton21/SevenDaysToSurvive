package net.zolton21.sevendaystosurvive.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.blockentity.SynapticSealBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SynapticSealBlockModel extends DefaultedBlockGeoModel<SynapticSealBlockEntity> {
    private final ResourceLocation MODEL = buildFormattedModelPath(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    private final ResourceLocation TEXTURE = buildFormattedTexturePath(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    private final ResourceLocation ANIMATIONS = buildFormattedAnimationPath(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));


    public SynapticSealBlockModel() {
        super(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    }

    @Override
    public ResourceLocation getAnimationResource(SynapticSealBlockEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public ResourceLocation getModelResource(SynapticSealBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(SynapticSealBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public RenderType getRenderType(SynapticSealBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
