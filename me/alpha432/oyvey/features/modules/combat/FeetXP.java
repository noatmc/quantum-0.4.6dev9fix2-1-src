package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.mixin.mixins.accessors.ICPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FeetXP extends Module {
  public FeetXP() {
    super("FeetXP", "fxp", Module.Category.COMBAT, true, false, false);
  }
  
  @SubscribeEvent
  public void onPacket(PacketEvent event) {
    if (nullCheck())
      return; 
    if (event.getPacket() instanceof CPacketPlayer && mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemExpBottle) {
      CPacketPlayer packet = (CPacketPlayer)event.getPacket();
      ((ICPacketPlayer)packet).setPitch(90.0F);
    } 
  }
}
