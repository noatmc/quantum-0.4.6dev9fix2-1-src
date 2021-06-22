package me.alpha432.oyvey.features.modules.misc;

import com.google.common.eventbus.Subscribe;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiDesync extends Module {
  private final Setting<Boolean> crystal;
  
  private final Setting<Boolean> sneakp;
  
  public AntiDesync() {
    super("AntiDesync", "ad", Module.Category.MISC, true, false, false);
    this.crystal = register(new Setting("Crystal", Boolean.valueOf(true)));
    this.sneakp = register(new Setting("SneakPacket", Boolean.valueOf(false)));
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent event) {
    if (((Boolean)this.crystal.getValue()).booleanValue() && 
      event.getPacket() instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
      if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
        try {
          for (Entity e : (Wrapper.getWorld()).loadedEntityList) {
            if (e instanceof net.minecraft.entity.item.EntityEnderCrystal && e.getDistance(packet.getX(), packet.getY(), packet.getZ()) <= 6.0D)
              e.setDead(); 
          } 
        } catch (Exception e) {
          e.printStackTrace();
        }  
    } 
  }
  
  @Subscribe
  public void onUpdate() {
    if (((Boolean)this.sneakp.getValue()).booleanValue()) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    } 
  }
}
