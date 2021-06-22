package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MotionUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.EntityLivingBase;

public class YPort extends Module {
  public Setting<Boolean> useTimer;
  
  private final Setting<Double> yPortSpeed;
  
  public Setting<Boolean> stepyport;
  
  private Timer timer;
  
  private float stepheight;
  
  public YPort() {
    super("YPort", "yp", Module.Category.MOVEMENT, true, false, false);
    this.useTimer = register(new Setting("UseTimer", Boolean.valueOf(false)));
    this.yPortSpeed = register(new Setting("Speed", Double.valueOf(0.1D), Double.valueOf(0.0D), Double.valueOf(1.0D)));
    this.stepyport = register(new Setting("Step", Boolean.valueOf(true)));
    this.timer = new Timer();
    this.stepheight = 2.0F;
  }
  
  public void onDisable() {
    this.timer.reset();
    EntityUtil.resetTimer();
  }
  
  public void onUpdate() {
    if (mc.player.isSneaking() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || OyVey.moduleManager.isModuleEnabled("Strafe"))
      return; 
    if (mc.player == null || mc.world == null) {
      disable();
      return;
    } 
    handleYPortSpeed();
    if ((!mc.player.isOnLadder() || mc.player.isInWater() || mc.player.isInLava()) && ((Boolean)this.stepyport.getValue()).booleanValue()) {
      Step.mc.player.stepHeight = this.stepheight;
      StepTwo.mc.player.stepHeight = this.stepheight;
      return;
    } 
  }
  
  public void onToggle() {
    Step.mc.player.stepHeight = 0.6F;
    StepTwo.mc.player.stepHeight = 0.6F;
    mc.player.motionY = -3.0D;
  }
  
  private void handleYPortSpeed() {
    if (!MotionUtil.isMoving((EntityLivingBase)mc.player) || (mc.player.isInWater() && mc.player.isInLava()) || mc.player.collidedHorizontally)
      return; 
    if (mc.player.onGround) {
      if (((Boolean)this.useTimer.getValue()).booleanValue())
        EntityUtil.setTimer(1.15F); 
      mc.player.jump();
      MotionUtil.setSpeed((EntityLivingBase)mc.player, MotionUtil.getBaseMoveSpeed() + ((Double)this.yPortSpeed.getValue()).doubleValue());
    } else {
      mc.player.motionY = -1.0D;
      EntityUtil.resetTimer();
    } 
  }
}
