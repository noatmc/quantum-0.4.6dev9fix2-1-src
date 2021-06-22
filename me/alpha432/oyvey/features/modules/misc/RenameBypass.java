package me.alpha432.oyvey.features.modules.misc;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;

public class RenameBypass extends Module {
  private final Setting<Integer> slotid;
  
  public RenameBypass() {
    super("RenameBypass", "rb", Module.Category.MISC, true, false, false);
    this.slotid = register(new Setting("slotid", Integer.valueOf(36), Integer.valueOf(0), Integer.valueOf(44)));
  }
  
  public void onEnable() {
    if (mc.world != null) {
      mc.playerController.windowClick(0, ((Integer)this.slotid.getValue()).intValue(), 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      mc.playerController.windowClick(0, ((Integer)this.slotid.getValue()).intValue(), 0, ClickType.PICKUP, (EntityPlayer)mc.player);
      disable();
    } 
  }
}
