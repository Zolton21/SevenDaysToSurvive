package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerHelper {
    private static final String TAG_KEY = "synaptic_seal_block_protection";

    public static boolean isPlayerProtected(ServerPlayer player){
        CompoundTag tag = ((ICommonEntityDataSaver)player).getPersistentData().getCompound(player.getStringUUID());
        return tag.getBoolean(TAG_KEY);
    }

    public static void changePlayerProtectionState(ServerPlayer player, boolean isProtected){
        CompoundTag tag = ((ICommonEntityDataSaver)player).getPersistentData().getCompound(player.getStringUUID());
        tag.putBoolean(TAG_KEY, isProtected);
    }
}
