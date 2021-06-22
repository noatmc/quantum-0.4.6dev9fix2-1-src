package me.alpha432.oyvey.manager;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.player.TimerSpeed;

public class TimerManager extends Feature {
  private float timer = 1.0F;
  
  private TimerSpeed module;
  
  public void init() {
    this.module = OyVey.moduleManager.<TimerSpeed>getModuleByClass(TimerSpeed.class);
  }
  
  public void unload() {
    this.timer = 1.0F;
    mc.timer.tickLength = 50.0F;
  }
  
  public void update() {
    if (this.module != null && this.module.isEnabled())
      this.timer = this.module.speed; 
    mc.timer.tickLength = 50.0F / ((this.timer <= 0.0F) ? 0.1F : this.timer);
  }
  
  public float getTimer() {
    return this.timer;
  }
  
  public void setTimer(float timer) {
    if (timer > 0.0F)
      this.timer = timer; 
  }
  
  public void reset() {
    this.timer = 1.0F;
  }
}
