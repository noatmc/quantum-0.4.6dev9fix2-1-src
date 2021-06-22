package me.alpha432.oyvey.features.modules.render;

import java.awt.Color;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChorusESP extends Module {
  private final Setting<Integer> alpha;
  
  private final Setting<Integer> red;
  
  private final Setting<Integer> green;
  
  private final Setting<Integer> blue;
  
  private final Timer timer;
  
  private BlockPos chorusPos;
  
  public ChorusESP() {
    super("ChorusESP", "cesp", Module.Category.RENDER, true, false, false);
    this.alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.red = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.blue = register(new Setting("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
    this.timer = new Timer();
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
      if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
        this.chorusPos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        this.timer.reset();
      } 
    } 
  }
  
  public void onRender3D() {
    if (this.chorusPos != null) {
      if (this.timer.passedMs(2000L)) {
        this.chorusPos = null;
        return;
      } 
      RenderUtil.drawBoxRealth(this.chorusPos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue()), ((Integer)this.alpha.getValue()).intValue());
    } 
  }
}
