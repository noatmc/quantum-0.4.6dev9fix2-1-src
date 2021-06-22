package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Anchor extends Module {
  private final Setting<Integer> pitch = register(new Setting("Pitch", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(90)));
  
  private final Setting<Boolean> pull = register(new Setting("Pull", Boolean.valueOf(true)));
  
  private Vec3d Center = Vec3d.ZERO;
  
  public static boolean AnchorING;
  
  int holeblocks;
  
  public Anchor() {
    super("Anchor", "a", Module.Category.COMBAT, true, false, false);
  }
  
  @SubscribeEvent
  public void onUpdate(TickEvent.ClientTickEvent event) {
    if (nullCheck())
      return; 
    if (mc.player.rotationPitch >= ((Integer)this.pitch.getValue()).intValue())
      if (isBlockHole(getPlayerPos().down(1)) || isBlockHole(getPlayerPos().down(2)) || isBlockHole(getPlayerPos().down(3)) || isBlockHole(getPlayerPos().down(4))) {
        AnchorING = true;
        if (!((Boolean)this.pull.getValue()).booleanValue()) {
          mc.player.motionX = 0.0D;
          mc.player.motionZ = 0.0D;
        } else {
          this.Center = GetCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
          double XDiff = Math.abs(this.Center.x - mc.player.posX);
          double ZDiff = Math.abs(this.Center.z - mc.player.posZ);
          if (XDiff <= 0.1D && ZDiff <= 0.1D) {
            this.Center = Vec3d.ZERO;
          } else {
            double MotionX = this.Center.x - mc.player.posX;
            double MotionZ = this.Center.z - mc.player.posZ;
            mc.player.motionX = MotionX / 2.0D;
            mc.player.motionZ = MotionZ / 2.0D;
          } 
        } 
      } else {
        AnchorING = false;
      }  
  }
  
  public void onDisable() {
    AnchorING = false;
    this.holeblocks = 0;
  }
  
  public boolean isBlockHole(BlockPos blockpos) {
    this.holeblocks = 0;
    if (mc.world.getBlockState(blockpos.add(0, 3, 0)).getBlock() == Blocks.AIR)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, 2, 0)).getBlock() == Blocks.AIR)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, 1, 0)).getBlock() == Blocks.AIR)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, 0, 0)).getBlock() == Blocks.AIR)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK)
      this.holeblocks++; 
    if (mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockpos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK)
      this.holeblocks++; 
    if (this.holeblocks >= 9)
      return true; 
    return false;
  }
  
  public Vec3d GetCenter(double posX, double posY, double posZ) {
    double x = Math.floor(posX) + 0.5D;
    double y = Math.floor(posY);
    double z = Math.floor(posZ) + 0.5D;
    return new Vec3d(x, y, z);
  }
  
  public BlockPos getPlayerPos() {
    return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
  }
}
