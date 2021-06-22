package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sprint extends Module {
  private static Sprint INSTANCE = new Sprint();
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.LEGIT));
  
  public Sprint() {
    super("Sprint", "Modifies sprinting", Module.Category.MOVEMENT, false, false, false);
    setInstance();
  }
  
  public static Sprint getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Sprint(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onSprint(MoveEvent event) {
    if (event.getStage() == 1 && this.mode.getValue() == Mode.RAGE && (mc.player.movementInput.moveForward != 0.0F || mc.player.movementInput.moveStrafe != 0.0F))
      event.setCanceled(true); 
  }
  
  public void onUpdate() {
    switch ((Mode)this.mode.getValue()) {
      case RAGE:
        if ((!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown()) || mc.player.isSneaking() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6.0F)
          break; 
        mc.player.setSprinting(true);
        break;
      case LEGIT:
        if (!mc.gameSettings.keyBindForward.isKeyDown() || mc.player.isSneaking() || mc.player.isHandActive() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6.0F || mc.currentScreen != null)
          break; 
        mc.player.setSprinting(true);
        break;
    } 
  }
  
  public void onDisable() {
    if (!nullCheck())
      mc.player.setSprinting(false); 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public enum Mode {
    LEGIT, RAGE;
  }
}
