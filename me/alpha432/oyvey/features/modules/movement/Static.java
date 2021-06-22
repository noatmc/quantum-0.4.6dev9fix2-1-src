package me.alpha432.oyvey.features.modules.movement;

import java.lang.reflect.Field;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.manager.Mapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Timer;

public class Static extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.Freeze));
  
  public Static() {
    super("AntiVoid", "av", Module.Category.MOVEMENT, false, false, false);
  }
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (mc.player.posY <= 0.0D)
      switch ((Mode)this.mode.getValue()) {
        case Float:
          mc.player.motionY = 0.5D;
          break;
        case Freeze:
          mc.player.motionY = 0.0D;
          break;
        case SlowFall:
          mc.player.motionY /= 4.0D;
          break;
        case TP:
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
          break;
      }  
  }
  
  private void setTimer(float value) {
    try {
      Field timer = Minecraft.class.getDeclaredField(Mapping.timer);
      timer.setAccessible(true);
      Field tickLength = Timer.class.getDeclaredField(Mapping.tickLength);
      tickLength.setAccessible(true);
      tickLength.setFloat(timer.get(mc), 50.0F / value);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public String getDisplayInfo() {
    return " " + this.mode.getValue();
  }
  
  public enum Mode {
    Float, Freeze, SlowFall, TP;
  }
}
