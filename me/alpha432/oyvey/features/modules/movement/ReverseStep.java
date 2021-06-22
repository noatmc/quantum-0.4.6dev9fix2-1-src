package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;

public class ReverseStep extends Module {
  private final Setting<Integer> speed = register(new Setting("Speed", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(20)));
  
  private final Setting<Boolean> inliquid = register(new Setting("Liquid", Boolean.valueOf(false)));
  
  private final Setting<Cancel> canceller = register(new Setting("CancelType", Cancel.None));
  
  private static ReverseStep INSTANCE = new ReverseStep();
  
  public ReverseStep() {
    super("ReverseStep", "rs", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  public static ReverseStep getInstance() {
    if (INSTANCE == null)
      INSTANCE = new ReverseStep(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (mc.player.isSneaking() || mc.player.isDead || mc.player.collidedHorizontally || !mc.player.onGround || (mc.player.isInWater() && !((Boolean)this.inliquid.getValue()).booleanValue()) || (mc.player.isInLava() && !((Boolean)this.inliquid.getValue()).booleanValue()) || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown() || OyVey.moduleManager.isModuleEnabled("Burrow") || mc.player.noClip || OyVey.moduleManager.isModuleEnabled("Packetfly") || OyVey.moduleManager.isModuleEnabled("Phase") || (mc.gameSettings.keyBindSneak.isKeyDown() && this.canceller.getValue() == Cancel.Shift) || (mc.gameSettings.keyBindSneak.isKeyDown() && this.canceller.getValue() == Cancel.Both) || (mc.gameSettings.keyBindJump.isKeyDown() && this.canceller.getValue() == Cancel.Space) || (mc.gameSettings.keyBindJump.isKeyDown() && this.canceller.getValue() == Cancel.Both) || OyVey.moduleManager.isModuleEnabled("Strafe"))
      return; 
    for (double y = 0.0D; y < 90.5D; y += 0.01D) {
      if (!mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -y, 0.0D)).isEmpty()) {
        mc.player.motionY = (-((Integer)this.speed.getValue()).intValue() / 10.0F);
        break;
      } 
    } 
  }
  
  public enum Cancel {
    None, Space, Shift, Both;
  }
}
