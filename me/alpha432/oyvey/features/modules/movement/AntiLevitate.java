package me.alpha432.oyvey.features.modules.movement;

import java.util.Objects;
import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.potion.Potion;

public class AntiLevitate extends Module {
  public AntiLevitate() {
    super("AntiLevitate", "Removes shulker levitation", Module.Category.MOVEMENT, false, false, false);
  }
  
  public void onUpdate() {
    if (mc.player.isPotionActive(Objects.<Potion>requireNonNull(Potion.getPotionFromResourceLocation("levitation"))))
      mc.player.removeActivePotionEffect(Potion.getPotionFromResourceLocation("levitation")); 
  }
}
