package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class WebTP extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));
  
  public WebTP() {
    super("WebTP", "", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (mc.player.isInWeb) {
      int i;
      switch ((Mode)this.mode.getValue()) {
        case Normal:
          for (i = 0; i < 10; i++)
            mc.player.motionY--; 
          break;
        case Vanilla:
          mc.player.isInWeb = false;
          break;
      } 
    } 
  }
  
  public enum Mode {
    Normal, Vanilla;
  }
}
