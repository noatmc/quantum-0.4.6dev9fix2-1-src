package me.alpha432.oyvey.features.modules.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Flatten extends Module {
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false)));
  
  private final Setting<Boolean> autoDisable = register(new Setting("AutoDisable", Boolean.valueOf(true)));
  
  private final Setting<Boolean> targetNullDisable = register(new Setting("NullTargetDisable", Boolean.valueOf(false)));
  
  private final Vec3d[] offsetsDefault = new Vec3d[] { new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(-1.0D, 0.0D, 0.0D) };
  
  private int offsetStep = 0;
  
  private int oldSlot = -1;
  
  private boolean placing = false;
  
  public Flatten() {
    super("FeetFloor", "f", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    this.oldSlot = mc.player.inventory.currentItem;
  }
  
  public void onDisable() {
    this.oldSlot = -1;
  }
  
  public void onUpdate() {
    EntityPlayer closest_target = findClosestTarget();
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (closest_target == null && ((Boolean)this.targetNullDisable.getValue()).booleanValue()) {
      disable();
      return;
    } 
    List<Vec3d> place_targets = new ArrayList<>();
    Collections.addAll(place_targets, this.offsetsDefault);
    int blocks_placed = 0;
    while (blocks_placed < ((Integer)this.blocksPerTick.getValue()).intValue()) {
      if (this.offsetStep >= place_targets.size()) {
        this.offsetStep = 0;
        break;
      } 
      this.placing = true;
      BlockPos offset_pos = new BlockPos(place_targets.get(this.offsetStep));
      BlockPos target_pos = (new BlockPos(closest_target.getPositionVector())).down().add(offset_pos.getX(), offset_pos.getY(), offset_pos.getZ());
      boolean should_try_place = mc.world.getBlockState(target_pos).getMaterial().isReplaceable();
      for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(target_pos))) {
        if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !(entity instanceof net.minecraft.entity.item.EntityXPOrb)) {
          should_try_place = false;
          break;
        } 
      } 
      if (should_try_place) {
        place(target_pos, obbySlot, this.oldSlot);
        blocks_placed++;
      } 
      this.offsetStep++;
      this.placing = false;
    } 
    if (((Boolean)this.autoDisable.getValue()).booleanValue())
      disable(); 
  }
  
  private void place(BlockPos pos, int slot, int oldSlot) {
    mc.player.inventory.currentItem = slot;
    mc.playerController.updateController();
    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), mc.player.isSneaking());
    mc.player.inventory.currentItem = oldSlot;
    mc.playerController.updateController();
  }
  
  private EntityPlayer findClosestTarget() {
    if (mc.world.playerEntities.isEmpty())
      return null; 
    EntityPlayer closestTarget = null;
    for (EntityPlayer target : mc.world.playerEntities) {
      if (target == mc.player || !target.isEntityAlive())
        continue; 
      if (OyVey.friendManager.isFriend(target.getName()))
        continue; 
      if (target.getHealth() <= 0.0F)
        continue; 
      if (mc.player.getDistance((Entity)target) > 5.0F)
        continue; 
      if (closestTarget != null && 
        mc.player.getDistance((Entity)target) > mc.player.getDistance((Entity)closestTarget))
        continue; 
      closestTarget = target;
    } 
    return closestTarget;
  }
}
