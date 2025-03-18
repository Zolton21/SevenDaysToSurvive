package net.zolton21.sevendaystosurvive.fabric.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FabricSynapticSealItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public FabricSynapticSealItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal"))));
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return () -> new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(new ResourceLocation(SevenDaysToSurvive.MOD_ID, "synaptic_seal")));
    }
}
