package net.zolton21.sevendaystosurvive.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.zolton21.sevendaystosurvive.helper.ICommonEntityDataSaver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ModCommonEntityDataSaverMixin implements ICommonEntityDataSaver {
    private CompoundTag persistentData;

    public CompoundTag getPersistentData() {
        if(this.persistentData == null){
            this.persistentData = new CompoundTag();
        }
        return this.persistentData;
    }

    @Inject(method = "saveWithoutId(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"))
    protected void writeNBT(CompoundTag tag, CallbackInfoReturnable cir){
        if(this.persistentData != null){
            tag.put("sevendaystosurvive.fabric.persistent_data", this.persistentData);
        }
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("HEAD"))
    protected void readNBT(CompoundTag tag, CallbackInfo ci){
        if(tag.contains("sevendaystosurvive.fabric.persistent_data")){
            this.persistentData = tag.getCompound("sevendaystosurvive.fabric.persistent_data");
        }
    }
}
