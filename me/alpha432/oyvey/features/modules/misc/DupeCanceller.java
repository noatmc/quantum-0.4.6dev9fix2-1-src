package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DupeCanceller extends Module {
  private int PacketsCanelled;
  
  public DupeCanceller() {
    super("DupeCancel", "dc", Module.Category.MISC, true, false, false);
    this.PacketsCanelled = 0;
  }
  
  public void onDisable() {
    super.onDisable();
    this.PacketsCanelled = 0;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketInput)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.Position)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.PositionRotation)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.Rotation)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerAbilities)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerDigging)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItem)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketUseEntity)
      event.setCanceled(true); 
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketVehicleMove)
      event.setCanceled(true); 
  }
}
