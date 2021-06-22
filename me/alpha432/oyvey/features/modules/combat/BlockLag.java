package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockLag extends Module {
  private static BlockLag INSTANCE;
  
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.OBSIDIAN));
  
  private final Setting<Boolean> smartTp = register(new Setting("SmartTP", Boolean.valueOf(true)));
  
  private final Setting<Integer> tpMin = register(new Setting("TPMin", Integer.valueOf(3), Integer.valueOf(3), Integer.valueOf(10), v -> ((Boolean)this.smartTp.getValue()).booleanValue()));
  
  private final Setting<Integer> tpMax = register(new Setting("TPMax", Integer.valueOf(25), Integer.valueOf(10), Integer.valueOf(40), v -> ((Boolean)this.smartTp.getValue()).booleanValue()));
  
  private final Setting<Boolean> noVoid = register(new Setting("NoVoid", Boolean.valueOf(true), v -> ((Boolean)this.smartTp.getValue()).booleanValue()));
  
  private final Setting<Integer> tpHeight = register(new Setting("TPHeight", Integer.valueOf(2), Integer.valueOf(-40), Integer.valueOf(40), v -> !((Boolean)this.smartTp.getValue()).booleanValue()));
  
  private final Setting<Boolean> keepInside = register(new Setting("Center", Boolean.valueOf(true)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(false)));
  
  private final Setting<Boolean> sneaking = register(new Setting("Sneak", Boolean.valueOf(false)));
  
  private final Setting<Boolean> offground = register(new Setting("Offground", Boolean.valueOf(false)));
  
  private final Setting<Boolean> chat = register(new Setting("Chat Msgs", Boolean.valueOf(true)));
  
  private final Setting<Boolean> tpdebug = register(new Setting("Debug", Boolean.valueOf(false), v -> (((Boolean)this.chat.getValue()).booleanValue() && ((Boolean)this.smartTp.getValue()).booleanValue())));
  
  private BlockPos burrowPos;
  
  private int lastBlock;
  
  private int blockSlot;
  
  private Class block;
  
  private String name;
  
  public BlockLag() {
    super("SelfFill", "sf", Module.Category.COMBAT, true, false, false);
    INSTANCE = this;
  }
  
  public static BlockLag getInstance() {
    if (INSTANCE == null)
      INSTANCE = new BlockLag(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    this.burrowPos = new BlockPos(mc.player.posX, Math.ceil(mc.player.posY), mc.player.posZ);
    this.blockSlot = findBlockSlot();
    this.lastBlock = mc.player.inventory.currentItem;
    if (!doChecks() || this.blockSlot == -1) {
      disable();
      return;
    } 
    if (((Boolean)this.keepInside.getValue()).booleanValue()) {
      double x = mc.player.posX - Math.floor(mc.player.posX);
      double z = mc.player.posZ - Math.floor(mc.player.posZ);
      if (x <= 0.3D || x >= 0.7D)
        x = (x > 0.5D) ? 0.69D : 0.31D; 
      if (z < 0.3D || z > 0.7D)
        z = (z > 0.5D) ? 0.69D : 0.31D; 
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(Math.floor(mc.player.posX) + x, mc.player.posY, Math.floor(mc.player.posZ) + z, mc.player.onGround));
      mc.player.setPosition(Math.floor(mc.player.posX) + x, mc.player.posY, Math.floor(mc.player.posZ) + z);
    } 
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41999998688698D, mc.player.posZ, !((Boolean)this.offground.getValue()).booleanValue()));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7531999805211997D, mc.player.posZ, !((Boolean)this.offground.getValue()).booleanValue()));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00133597911214D, mc.player.posZ, !((Boolean)this.offground.getValue()).booleanValue()));
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16610926093821D, mc.player.posZ, !((Boolean)this.offground.getValue()).booleanValue()));
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((this.burrowPos.getX() + 0.5F), (this.burrowPos.getY() + 0.5F), (this.burrowPos.getZ() + 0.5F)));
      OyVey.rotationManager.setPlayerRotations(angle[0], angle[1]);
    } 
    InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
    BlockUtil.placeBlock(this.burrowPos, (this.blockSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, true, ((Boolean)this.sneaking.getValue()).booleanValue());
    InventoryUtil.switchToHotbarSlot(this.lastBlock, false);
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, ((Boolean)this.smartTp.getValue()).booleanValue() ? adaptiveTpHeight() : (((Integer)this.tpHeight.getValue()).intValue() + mc.player.posY), mc.player.posZ, !((Boolean)this.offground.getValue()).booleanValue()));
    disable();
  }
  
  private int findBlockSlot() {
    switch ((Mode)this.mode.getValue()) {
      case ECHEST:
        this.block = BlockEnderChest.class;
        this.name = "Ender Chests";
        break;
      case OBSIDIAN:
        this.block = BlockObsidian.class;
        this.name = "Obsidian";
        break;
      case SOULSAND:
        this.block = BlockSoulSand.class;
        this.name = "Soul Sand";
        break;
    } 
    int slot = InventoryUtil.findHotbarBlock(this.block);
    if (slot == -1) {
      if (InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), this.block))
        return -2; 
      if (((Boolean)this.chat.getValue()).booleanValue())
        Command.sendMessage("§7" + (String)this.displayName.getValue() + ":§c No " + this.name + " to use"); 
    } 
    return slot;
  }
  
  private int adaptiveTpHeight() {
    int airblock = (((Boolean)this.noVoid.getValue()).booleanValue() && ((Integer)this.tpMax.getValue()).intValue() * -1 + this.burrowPos.getY() < 0) ? (this.burrowPos.getY() * -1) : (((Integer)this.tpMax.getValue()).intValue() * -1);
    while (airblock < ((Integer)this.tpMax.getValue()).intValue()) {
      if (Math.abs(airblock) < ((Integer)this.tpMin.getValue()).intValue() || !mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock)) || !mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, airblock + 1))) {
        airblock++;
        continue;
      } 
      if (((Boolean)this.tpdebug.getValue()).booleanValue())
        Command.sendMessage(Integer.toString(airblock)); 
      return this.burrowPos.getY() + airblock;
    } 
    return 69420;
  }
  
  private boolean doChecks() {
    if (!fullNullCheck()) {
      if (((Boolean)this.smartTp.getValue()).booleanValue() && 
        adaptiveTpHeight() == 69420) {
        if (((Boolean)this.chat.getValue()).booleanValue())
          Command.sendMessage("§7" + (String)this.displayName.getValue() + ":§c Not enough room"); 
        return false;
      } 
      if (mc.world.getBlockState(this.burrowPos).getBlock().equals(Blocks.OBSIDIAN))
        return false; 
      if (!mc.world.isAirBlock(this.burrowPos.offset(EnumFacing.UP, 2))) {
        if (((Boolean)this.chat.getValue()).booleanValue())
          Command.sendMessage("§7" + (String)this.displayName.getValue() + ":§c Not enough room"); 
        return false;
      } 
      for (Entity entity : mc.world.loadedEntityList) {
        if (!(entity instanceof net.minecraft.entity.item.EntityItem) && !entity.equals(mc.player) && (
          new AxisAlignedBB(this.burrowPos)).intersects(entity.getEntityBoundingBox())) {
          if (((Boolean)this.chat.getValue()).booleanValue())
            Command.sendMessage("§7" + (String)this.displayName.getValue() + ":§c Not enough room"); 
          return false;
        } 
      } 
      return true;
    } 
    return false;
  }
  
  public enum Mode {
    OBSIDIAN, ECHEST, SOULSAND;
  }
}
