package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.ItemUtil;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoFish extends Module {
  private int rodSlot;
  
  public AutoFish() {
    super("AutoFish", "af", Module.Category.MISC, true, false, false);
    this.rodSlot = -1;
  }
  
  public void onEnable() {
    if (isNull()) {
      setEnabled(false);
      return;
    } 
    this.rodSlot = ItemUtil.getItemFromHotbar((Item)Items.FISHING_ROD);
  }
  
  @SubscribeEvent
  public void onPacket(PacketEvent event) {
    if (event.getPacket() instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
      if (packet.getCategory() == SoundCategory.NEUTRAL && packet.getSound() == SoundEvents.ENTITY_BOBBER_SPLASH) {
        int startSlot = mc.player.inventory.currentItem;
        mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(this.rodSlot));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        if (startSlot != -1)
          mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(startSlot)); 
      } 
    } 
  }
}
