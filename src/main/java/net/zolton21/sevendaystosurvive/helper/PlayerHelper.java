package net.zolton21.sevendaystosurvive.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class PlayerHelper {
    private static final String TAG_KEY = "synaptic_seal_block_placement";

    public static int getBlockPlacementCount(ServerPlayer player){
        CompoundTag tag = player.getPersistentData().getCompound(player.getStringUUID());
        return tag.getInt(TAG_KEY);
    }

    public static void incrementBlockPlacementCount(ServerPlayer player){
        CompoundTag tag = player.getPersistentData().getCompound(player.getStringUUID());
        int blockCount = tag.getInt(TAG_KEY);
        tag.putInt(TAG_KEY, blockCount + 1);
        player.getPersistentData().put(player.getUUID().toString(), tag);
    }

    public static void decrementBlockPlacementCount(ServerPlayer player){
        CompoundTag tag = player.getPersistentData().getCompound(player.getStringUUID());
        int blockCount = tag.getInt(TAG_KEY);
        tag.putInt(TAG_KEY, blockCount - 1);
        player.getPersistentData().put(player.getUUID().toString(), tag);
    }
}
