package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEndCrystal;

public class FastPlace extends Module {
  private final Setting<Boolean> Block;
  
  private final Setting<Boolean> Crystal;
  
  private final Setting<Boolean> Firework;
  
  private final Setting<Boolean> SpawnEgg;
  
  private final Setting<Integer> delay;
  
  public FastPlace() {
    super("FastUse", "fu", Module.Category.PLAYER, true, false, false);
    this.Block = register(new Setting("Blocks", Boolean.valueOf(false)));
    this.Crystal = register(new Setting("Crystals", Boolean.valueOf(false)));
    this.Firework = register(new Setting("Fireworks", Boolean.valueOf(false)));
    this.SpawnEgg = register(new Setting("SpawnEgg", Boolean.valueOf(false)));
    this.delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(3)));
  }
  
  public void onUpdate() {
    if (fullNullCheck())
      return; 
    if (mc.player.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBlock && !((Boolean)this.Block.getValue()).booleanValue())
      return; 
    if (InventoryUtil.holdingItem(ItemEndCrystal.class) && !((Boolean)this.Crystal.getValue()).booleanValue())
      return; 
    if (InventoryUtil.getHeldItem(Items.FIREWORKS) && !((Boolean)this.Firework.getValue()).booleanValue())
      return; 
    if (InventoryUtil.getHeldItem(Items.SPAWN_EGG) && !((Boolean)this.SpawnEgg.getValue()).booleanValue())
      return; 
    mc.rightClickDelayTimer = ((Integer)this.delay.getValue()).intValue();
  }
}
