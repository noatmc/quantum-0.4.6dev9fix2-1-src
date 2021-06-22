package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.event.events.PerspectiveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect extends Module {
  public Setting<Float> aspect = register(new Setting("Aspect", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(3.0F)));
  
  public Aspect() {
    super("Aspect", "a", Module.Category.RENDER, true, false, false);
  }
  
  @SubscribeEvent
  public void onPerspectiveEvent(PerspectiveEvent event) {
    event.setAspect(((Float)this.aspect.getValue()).floatValue());
  }
}
