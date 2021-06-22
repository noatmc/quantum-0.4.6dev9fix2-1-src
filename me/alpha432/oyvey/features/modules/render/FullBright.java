package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class FullBright extends Module {
  private final Setting<Mode> mode;
  
  float oldBright;
  
  public FullBright() {
    super("FullBright", "", Module.Category.RENDER, true, false, false);
    this.mode = register(new Setting("Mode", Mode.Gamma));
  }
  
  public void onUpdate() {
    if (nullCheck())
      return; 
    if (this.mode.getValue() == Mode.Potion)
      mc.player.addPotionEffect(new PotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 80950, 1, false, false))); 
  }
  
  public void onEnable() {
    if (nullCheck())
      return; 
    this.oldBright = mc.gameSettings.gammaSetting;
    if (this.mode.getValue() == Mode.Gamma)
      mc.gameSettings.gammaSetting = 100.0F; 
  }
  
  public void onDisable() {
    mc.player.removePotionEffect(MobEffects.NIGHT_VISION);
    if (this.mode.getValue() == Mode.Gamma)
      mc.gameSettings.gammaSetting = this.oldBright; 
  }
  
  public enum Mode {
    Gamma, Potion;
  }
}
