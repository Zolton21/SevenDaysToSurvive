package net.zolton21.sevendaystosurvive.events;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.zolton21.sevendaystosurvive.SevenDaysToSurvive;
import net.zolton21.sevendaystosurvive.helper.PlayerHelper;
import net.zolton21.sevendaystosurvive.registries.ModBlocks;

@Mod.EventBusSubscriber(modid = SevenDaysToSurvive.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {

    /*@SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event){
        if(event.getPlacedBlock().getBlock() == ModBlocks.SYNAPTIC_SEAL_BLOCK.get() && event.getEntity() instanceof Player player) {
            if (player instanceof ServerPlayer) {
                if(PlayerHelper.getBlockPlacementCount((ServerPlayer) player) < 2){
                    PlayerHelper.incrementBlockPlacementCount((ServerPlayer) player);
                }else{
                    event.setCanceled(true);
                }
                player.displayClientMessage(Component.literal("Synaptic Seal:" + PlayerHelper.getBlockPlacementCount((ServerPlayer) player) + "/" + 2), true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockRemoved(BlockEvent.BreakEvent event) {
        if(event.getState().getBlock() == ModBlocks.SYNAPTIC_SEAL_BLOCK.get()) {
            Player player = event.getPlayer();
            if(player instanceof ServerPlayer){
                if(PlayerHelper.getBlockPlacementCount((ServerPlayer) player) > 0) {
                    PlayerHelper.decrementBlockPlacementCount((ServerPlayer) player);
                }else{
                    event.setCanceled(true);
                }
                event.getPlayer().displayClientMessage(Component.literal("Synaptic Seal:" + PlayerHelper.getBlockPlacementCount((ServerPlayer) player) + "/" + 2), true);
            }
        }
    }*/

}