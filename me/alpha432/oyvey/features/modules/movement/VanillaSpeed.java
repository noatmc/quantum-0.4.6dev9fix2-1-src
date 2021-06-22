package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.MovementUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VanillaSpeed extends Module {
  private final Setting<Float> speed;
  
  public VanillaSpeed() {
    super("VanillaSpeed", "vs", Module.Category.MOVEMENT, true, false, false);
    this.speed = register(new Setting("Speed", Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(10.0F)));
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    double[] calc = MovementUtil.directionSpeed(((Float)this.speed.getValue()).floatValue() / 10.0D);
    event.setMotionX(calc[0]);
    event.setMotionZ(calc[1]);
  }
}
