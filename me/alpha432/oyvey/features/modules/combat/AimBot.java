package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RotationUtil;
import net.minecraft.entity.player.EntityPlayer;

public class AimBot extends Module {
  private final Setting<Mode> mode;
  
  private final Setting<Float> range;
  
  private final Setting<Boolean> onlyBow;
  
  EntityPlayer aimTarget;
  
  RotationUtil aimbotRotation;
  
  public AimBot() {
    super("AimBot", "ab", Module.Category.COMBAT, true, false, false);
    this.mode = register(new Setting("Rotate", Mode.None));
    this.range = register(new Setting("Range", Float.valueOf(8.0F), Float.valueOf(0.0F), Float.valueOf(20.0F)));
    this.onlyBow = register(new Setting("Bow Only", Boolean.valueOf(true)));
    this.aimTarget = null;
    this.aimbotRotation = null;
  }
  
  public enum Mode {
    Legit, Packet, None;
  }
}
