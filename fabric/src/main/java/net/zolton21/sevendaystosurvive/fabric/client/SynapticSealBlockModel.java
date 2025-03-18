package net.zolton21.sevendaystosurvive.fabric.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.fabric.blockentity.FabricSynapticSealBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SynapticSealBlockModel extends DefaultedBlockGeoModel<FabricSynapticSealBlockEntity> {

    public SynapticSealBlockModel() {
        super(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"));
    }

    @Override
    public RenderType getRenderType(FabricSynapticSealBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
