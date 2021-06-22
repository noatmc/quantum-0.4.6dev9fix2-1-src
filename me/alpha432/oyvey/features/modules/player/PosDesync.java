package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PosDesync extends Module {
  public PosDesync() {
    super("PosDesync", "pd", Module.Category.PLAYER, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.Position || event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.PositionRotation || event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer.Rotation || event.getPacket() instanceof net.minecraft.network.play.client.CPacketConfirmTeleport)
      event.setCanceled(true); 
  }
}
