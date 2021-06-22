package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Animations extends Module {
  private final Setting<Mode> mode;
  
  private final Setting<Swing> swing;
  
  private final Setting<Boolean> slow;
  
  public Animations() {
    super("Animations", "Change animations", Module.Category.RENDER, true, false, false);
    this.mode = register(new Setting("Mode", Mode.Low));
    this.swing = register(new Setting("Swing", Swing.Mainhand));
    this.slow = register(new Setting("Slow", Boolean.valueOf(true)));
  }
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (this.swing.getValue() == Swing.Offhand)
      mc.player.swingingHand = EnumHand.OFF_HAND; 
    if (this.mode.getValue() == Mode.High && 
      mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9D) {
      mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0F;
      mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
    } 
  }
  
  public void onEnable() {
    if (((Boolean)this.slow.getValue()).booleanValue())
      mc.player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 255000)); 
  }
  
  public void onDisable() {
    if (((Boolean)this.slow.getValue()).booleanValue())
      mc.player.removePotionEffect(MobEffects.MINING_FATIGUE); 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    Packet<?> raw = event.getPacket();
    if (raw instanceof CPacketAnimation) {
      CPacketAnimation packet = (CPacketAnimation)raw;
      if (this.swing.getValue() == Swing.Packet)
        event.setCanceled(true); 
    } 
  }
  
  private enum Swing {
    Mainhand, Offhand, Packet;
  }
  
  private enum Mode {
    Low, High;
  }
}
