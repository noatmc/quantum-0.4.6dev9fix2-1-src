package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;

public class ViewModChanger extends Module {
  public final Setting<Float> size = register(new Setting("Size", Float.valueOf(10.0F), Float.valueOf(0.0F), Float.valueOf(15.0F)));
  
  public final Setting<Float> offsetX = register(new Setting("OffsetX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> offsetY = register(new Setting("OffsetY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> offsetZ = register(new Setting("OffsetZ", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> offhandX = register(new Setting("OffhandX", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> offhandY = register(new Setting("OffhandY", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> offhandZ = register(new Setting("OffhandZ", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  public final Setting<Float> armPitch = register(new Setting("Arm Pitch", Float.valueOf(0.0F), Float.valueOf(-1.0F), Float.valueOf(1.0F)));
  
  private static ViewModChanger INSTANCE = new ViewModChanger();
  
  public ViewModChanger() {
    super("ViewMod", "Changes ViewModelChanger of items", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static ViewModChanger getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ViewModChanger(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
