package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;

public class MultiTask extends Module {
  private static MultiTask INSTANCE = new MultiTask();
  
  public MultiTask() {
    super("MultiTask", "mt", Module.Category.PLAYER, false, false, false);
    setInstance();
  }
  
  public static MultiTask getInstance() {
    if (INSTANCE == null)
      INSTANCE = new MultiTask(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
