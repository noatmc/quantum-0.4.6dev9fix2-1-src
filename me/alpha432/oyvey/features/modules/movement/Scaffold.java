package me.alpha432.oyvey.features.modules.movement;

import java.util.Arrays;
import java.util.List;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Scaffold extends Module {
  private final Setting<Mode> mode;
  
  public Setting<Boolean> rotation;
  
  private final Setting<Boolean> swing;
  
  private final Setting<Boolean> bSwitch;
  
  private final Setting<Boolean> center;
  
  private final Setting<Boolean> keepY;
  
  private final Setting<Boolean> sprint;
  
  private final Setting<Boolean> replenishBlocks;
  
  private final Setting<Boolean> down;
  
  private final Setting<Float> expand;
  
  private final List<Block> invalid;
  
  private final Timer timerMotion;
  
  private final Timer itemTimer;
  
  private BlockData blockData;
  
  private int lastY;
  
  private BlockPos pos;
  
  private boolean teleported;
  
  private final Timer timer;
  
  public Scaffold() {
    super("Scaffold", "Places Blocks underneath you.", Module.Category.MOVEMENT, true, false, false);
    this.mode = register(new Setting("Mode", Mode.Legit));
    this.rotation = register(new Setting("Rotate", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Fast)));
    this.swing = register(new Setting("Swing Arm", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
    this.bSwitch = register(new Setting("Switch", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
    this.center = register(new Setting("Center", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
    this.keepY = register(new Setting("KeepYLevel", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
    this.sprint = register(new Setting("UseSprint", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Legit)));
    this.replenishBlocks = register(new Setting("ReplenishBlocks", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Legit)));
    this.down = register(new Setting("Down", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Legit)));
    this.expand = register(new Setting("Expand", Float.valueOf(1.0F), Float.valueOf(1.0F), Float.valueOf(6.0F), v -> (this.mode.getValue() == Mode.Legit)));
    this.invalid = Arrays.asList(new Block[] { 
          Blocks.ENCHANTING_TABLE, Blocks.FURNACE, Blocks.CARPET, Blocks.CRAFTING_TABLE, Blocks.TRAPPED_CHEST, (Block)Blocks.CHEST, Blocks.DISPENSER, Blocks.AIR, (Block)Blocks.WATER, (Block)Blocks.LAVA, 
          (Block)Blocks.FLOWING_WATER, (Block)Blocks.FLOWING_LAVA, Blocks.SNOW_LAYER, Blocks.TORCH, Blocks.ANVIL, Blocks.JUKEBOX, Blocks.STONE_BUTTON, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.NOTEBLOCK, 
          Blocks.STONE_PRESSURE_PLATE, Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.WOODEN_PRESSURE_PLATE, Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, (Block)Blocks.RED_MUSHROOM, (Block)Blocks.BROWN_MUSHROOM, (Block)Blocks.YELLOW_FLOWER, (Block)Blocks.RED_FLOWER, Blocks.ANVIL, (Block)Blocks.CACTUS, 
          Blocks.LADDER, Blocks.ENDER_CHEST });
    this.timerMotion = new Timer();
    this.itemTimer = new Timer();
    this.timer = new Timer();
  }
  
  public void onEnable() {
    this.timer.reset();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayerPost(UpdateWalkingPlayerEvent event) {
    if (this.mode.getValue() == Mode.Fast) {
      if (isOff() || fullNullCheck() || event.getStage() == 0)
        return; 
      if (!mc.gameSettings.keyBindJump.isKeyDown())
        this.timer.reset(); 
      BlockPos playerBlock;
      if (BlockUtil.isScaffoldPos((playerBlock = EntityUtil.getPlayerPosWithEntity()).add(0, -1, 0)))
        if (BlockUtil.isValidBlock(playerBlock.add(0, -2, 0))) {
          place(playerBlock.add(0, -1, 0), EnumFacing.UP);
        } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0))) {
          place(playerBlock.add(0, -1, 0), EnumFacing.EAST);
        } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 0))) {
          place(playerBlock.add(0, -1, 0), EnumFacing.WEST);
        } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, -1))) {
          place(playerBlock.add(0, -1, 0), EnumFacing.SOUTH);
        } else if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1))) {
          place(playerBlock.add(0, -1, 0), EnumFacing.NORTH);
        } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
          if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1)))
            place(playerBlock.add(0, -1, 1), EnumFacing.NORTH); 
          place(playerBlock.add(1, -1, 1), EnumFacing.EAST);
        } else if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 1))) {
          if (BlockUtil.isValidBlock(playerBlock.add(-1, -1, 0)))
            place(playerBlock.add(0, -1, 1), EnumFacing.WEST); 
          place(playerBlock.add(-1, -1, 1), EnumFacing.SOUTH);
        } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
          if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1)))
            place(playerBlock.add(0, -1, 1), EnumFacing.SOUTH); 
          place(playerBlock.add(1, -1, 1), EnumFacing.WEST);
        } else if (BlockUtil.isValidBlock(playerBlock.add(1, -1, 1))) {
          if (BlockUtil.isValidBlock(playerBlock.add(0, -1, 1)))
            place(playerBlock.add(0, -1, 1), EnumFacing.EAST); 
          place(playerBlock.add(1, -1, 1), EnumFacing.NORTH);
        }  
    } 
  }
  
  public void onUpdate() {
    if (this.mode.getValue() == Mode.Legit) {
      if (OyVey.moduleManager.isModuleEnabled("Sprint") && (((
        (Boolean)this.down.getValue()).booleanValue() && mc.gameSettings.keyBindSneak.isKeyDown()) || !((Boolean)this.sprint.getValue()).booleanValue())) {
        mc.player.setSprinting(false);
        Sprint.getInstance().disable();
      } 
      if (((Boolean)this.replenishBlocks.getValue()).booleanValue() && !(mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && getBlockCountHotbar() <= 0 && this.itemTimer.passedMs(100L))
        for (int i = 9; i < 45; i++) {
          if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
            if (is.getItem() instanceof ItemBlock && !this.invalid.contains(Block.getBlockFromItem(is.getItem())) && 
              i < 36)
              swap(getItemSlot(mc.player.inventoryContainer, is.getItem()), 44); 
          } 
        }  
      if (((Boolean)this.keepY.getValue()).booleanValue()) {
        if ((!isMoving((EntityLivingBase)mc.player) && mc.gameSettings.keyBindJump.isKeyDown()) || mc.player.collidedVertically || mc.player.onGround)
          this.lastY = MathHelper.floor(mc.player.posY); 
      } else {
        this.lastY = MathHelper.floor(mc.player.posY);
      } 
      this.blockData = null;
      double x = mc.player.posX;
      double z = mc.player.posZ;
      double y = ((Boolean)this.keepY.getValue()).booleanValue() ? this.lastY : mc.player.posY;
      double forward = mc.player.movementInput.moveForward;
      double strafe = mc.player.movementInput.moveStrafe;
      float yaw = mc.player.rotationYaw;
      if (!mc.player.collidedHorizontally) {
        double[] coords = getExpandCoords(x, z, forward, strafe, yaw);
        x = coords[0];
        z = coords[1];
      } 
      if (canPlace(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - ((mc.gameSettings.keyBindSneak.isKeyDown() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), mc.player.posZ)).getBlock())) {
        x = mc.player.posX;
        z = mc.player.posZ;
      } 
      BlockPos blockBelow = new BlockPos(x, y - 1.0D, z);
      if (mc.gameSettings.keyBindSneak.isKeyDown() && ((Boolean)this.down.getValue()).booleanValue())
        blockBelow = new BlockPos(x, y - 2.0D, z); 
      this.pos = blockBelow;
      if (mc.world.getBlockState(blockBelow).getBlock() == Blocks.AIR)
        this.blockData = getBlockData2(blockBelow); 
      if (this.blockData != null) {
        if (getBlockCountHotbar() <= 0 || (!((Boolean)this.bSwitch.getValue()).booleanValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)))
          return; 
        int heldItem = mc.player.inventory.currentItem;
        if (((Boolean)this.bSwitch.getValue()).booleanValue())
          for (int j = 0; j < 9; j++) {
            mc.player.inventory.getStackInSlot(j);
            if (mc.player.inventory.getStackInSlot(j).getCount() != 0 && mc.player.inventory.getStackInSlot(j).getItem() instanceof ItemBlock && !this.invalid.contains(((ItemBlock)mc.player.inventory.getStackInSlot(j).getItem()).getBlock())) {
              mc.player.inventory.currentItem = j;
              break;
            } 
          }  
        if (this.mode.getValue() == Mode.Legit)
          if (mc.gameSettings.keyBindJump.isKeyDown() && mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F && !mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            if (!this.teleported && ((Boolean)this.center.getValue()).booleanValue()) {
              this.teleported = true;
              BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
              mc.player.setPosition(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            } 
            if (((Boolean)this.center.getValue()).booleanValue() && !this.teleported)
              return; 
            mc.player.motionY = 0.41999998688697815D;
            mc.player.motionZ = 0.0D;
            mc.player.motionX = 0.0D;
            if (this.timerMotion.sleep(1500L))
              mc.player.motionY = -0.28D; 
          } else {
            this.timerMotion.reset();
            if (this.teleported && ((Boolean)this.center.getValue()).booleanValue())
              this.teleported = false; 
          }  
        if (mc.playerController.processRightClickBlock(mc.player, mc.world, this.blockData.position, this.blockData.face, new Vec3d(this.blockData.position.getX() + Math.random(), this.blockData.position.getY() + Math.random(), this.blockData.position.getZ() + Math.random()), EnumHand.MAIN_HAND) != EnumActionResult.FAIL)
          if (((Boolean)this.swing.getValue()).booleanValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
          } else {
            mc.player.connection.sendPacket((Packet)new CPacketAnimation(EnumHand.MAIN_HAND));
          }  
        mc.player.inventory.currentItem = heldItem;
      } 
    } 
  }
  
  public static void swap(int slot, int hotbarNum) {
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, hotbarNum, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.windowClick(mc.player.inventoryContainer.windowId, slot, 0, ClickType.PICKUP, (EntityPlayer)mc.player);
    mc.playerController.updateController();
  }
  
  public static int getItemSlot(Container container, Item item) {
    int slot = 0;
    for (int i = 9; i < 45; i++) {
      if (container.getSlot(i).getHasStack()) {
        ItemStack is = container.getSlot(i).getStack();
        if (is.getItem() == item)
          slot = i; 
      } 
    } 
    return slot;
  }
  
  public static boolean isMoving(EntityLivingBase entity) {
    return (entity.moveForward != 0.0F || entity.moveStrafing != 0.0F);
  }
  
  public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW) {
    BlockPos underPos = new BlockPos(x, mc.player.posY - ((mc.gameSettings.keyBindSneak.isKeyDown() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), z);
    Block underBlock = mc.world.getBlockState(underPos).getBlock();
    double xCalc = -999.0D, zCalc = -999.0D;
    double dist = 0.0D;
    double expandDist = (((Float)this.expand.getValue()).floatValue() * 2.0F);
    while (!canPlace(underBlock)) {
      xCalc = x;
      zCalc = z;
      dist++;
      if (dist > expandDist)
        dist = expandDist; 
      xCalc += (forward * 0.45D * Math.cos(Math.toRadians((YAW + 90.0F))) + strafe * 0.45D * Math.sin(Math.toRadians((YAW + 90.0F)))) * dist;
      zCalc += (forward * 0.45D * Math.sin(Math.toRadians((YAW + 90.0F))) - strafe * 0.45D * Math.cos(Math.toRadians((YAW + 90.0F)))) * dist;
      if (dist == expandDist)
        break; 
      underPos = new BlockPos(xCalc, mc.player.posY - ((mc.gameSettings.keyBindSneak.isKeyDown() && ((Boolean)this.down.getValue()).booleanValue()) ? 2 : true), zCalc);
      underBlock = mc.world.getBlockState(underPos).getBlock();
    } 
    return new double[] { xCalc, zCalc };
  }
  
  public boolean canPlace(Block block) {
    return ((block instanceof net.minecraft.block.BlockAir || block instanceof net.minecraft.block.BlockLiquid) && mc.world != null && mc.player != null && this.pos != null && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(this.pos)).isEmpty());
  }
  
  private int getBlockCountHotbar() {
    int blockCount = 0;
    for (int i = 36; i < 45; i++) {
      if (mc.player.inventoryContainer.getSlot(i).getHasStack()) {
        ItemStack is = mc.player.inventoryContainer.getSlot(i).getStack();
        Item item = is.getItem();
        if (is.getItem() instanceof ItemBlock && 
          !this.invalid.contains(((ItemBlock)item).getBlock()))
          blockCount += is.getCount(); 
      } 
    } 
    return blockCount;
  }
  
  private BlockData getBlockData2(BlockPos pos) {
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, -1, 0)).getBlock()))
      return new BlockData(pos.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(1, 0, 0)).getBlock()))
      return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 0, 1)).getBlock()))
      return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 0, -1)).getBlock()))
      return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos.add(0, 1, 0)).getBlock()))
      return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN); 
    BlockPos pos2 = pos.add(-1, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock()))
      return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock()))
      return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock()))
      return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock()))
      return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock()))
      return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos3 = pos.add(1, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock()))
      return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock()))
      return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock()))
      return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock()))
      return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock()))
      return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos4 = pos.add(0, 0, 1);
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock()))
      return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock()))
      return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock()))
      return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock()))
      return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock()))
      return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos5 = pos.add(0, 0, -1);
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock()))
      return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock()))
      return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock()))
      return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock()))
      return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock()))
      return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos6 = pos.add(-2, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, -1, 0)).getBlock()))
      return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 1, 0)).getBlock()))
      return new BlockData(pos2.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(1, 0, 0)).getBlock()))
      return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, 1)).getBlock()))
      return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos2.add(0, 0, -1)).getBlock()))
      return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos7 = pos.add(2, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, -1, 0)).getBlock()))
      return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 1, 0)).getBlock()))
      return new BlockData(pos3.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(1, 0, 0)).getBlock()))
      return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, 1)).getBlock()))
      return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos3.add(0, 0, -1)).getBlock()))
      return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos8 = pos.add(0, 0, 2);
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, -1, 0)).getBlock()))
      return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 1, 0)).getBlock()))
      return new BlockData(pos4.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(1, 0, 0)).getBlock()))
      return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, 1)).getBlock()))
      return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos4.add(0, 0, -1)).getBlock()))
      return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos9 = pos.add(0, 0, -2);
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, -1, 0)).getBlock()))
      return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 1, 0)).getBlock()))
      return new BlockData(pos5.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(1, 0, 0)).getBlock()))
      return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, 1)).getBlock()))
      return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos5.add(0, 0, -1)).getBlock()))
      return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos10 = pos.add(0, -1, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, -1, 0)).getBlock()))
      return new BlockData(pos10.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 1, 0)).getBlock()))
      return new BlockData(pos10.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos10.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(1, 0, 0)).getBlock()))
      return new BlockData(pos10.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 0, 1)).getBlock()))
      return new BlockData(pos10.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos10.add(0, 0, -1)).getBlock()))
      return new BlockData(pos10.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos11 = pos10.add(1, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, -1, 0)).getBlock()))
      return new BlockData(pos11.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 1, 0)).getBlock()))
      return new BlockData(pos11.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos11.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(1, 0, 0)).getBlock()))
      return new BlockData(pos11.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 0, 1)).getBlock()))
      return new BlockData(pos11.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos11.add(0, 0, -1)).getBlock()))
      return new BlockData(pos11.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos12 = pos10.add(-1, 0, 0);
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, -1, 0)).getBlock()))
      return new BlockData(pos12.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 1, 0)).getBlock()))
      return new BlockData(pos12.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos12.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(1, 0, 0)).getBlock()))
      return new BlockData(pos12.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 0, 1)).getBlock()))
      return new BlockData(pos12.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos12.add(0, 0, -1)).getBlock()))
      return new BlockData(pos12.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos13 = pos10.add(0, 0, 1);
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, -1, 0)).getBlock()))
      return new BlockData(pos13.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos13.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 1, 0)).getBlock()))
      return new BlockData(pos13.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(1, 0, 0)).getBlock()))
      return new BlockData(pos13.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 0, 1)).getBlock()))
      return new BlockData(pos13.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos13.add(0, 0, -1)).getBlock()))
      return new BlockData(pos13.add(0, 0, -1), EnumFacing.SOUTH); 
    BlockPos pos14 = pos10.add(0, 0, -1);
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, -1, 0)).getBlock()))
      return new BlockData(pos14.add(0, -1, 0), EnumFacing.UP); 
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 1, 0)).getBlock()))
      return new BlockData(pos14.add(0, 1, 0), EnumFacing.DOWN); 
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(-1, 0, 0)).getBlock()))
      return new BlockData(pos14.add(-1, 0, 0), EnumFacing.EAST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(1, 0, 0)).getBlock()))
      return new BlockData(pos14.add(1, 0, 0), EnumFacing.WEST); 
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 0, 1)).getBlock()))
      return new BlockData(pos14.add(0, 0, 1), EnumFacing.NORTH); 
    if (!this.invalid.contains(mc.world.getBlockState(pos14.add(0, 0, -1)).getBlock()))
      return new BlockData(pos14.add(0, 0, -1), EnumFacing.SOUTH); 
    return null;
  }
  
  private static class BlockData {
    public BlockPos position;
    
    public EnumFacing face;
    
    public BlockData(BlockPos position, EnumFacing face) {
      this.position = position;
      this.face = face;
    }
  }
  
  public void place(BlockPos posI, EnumFacing face) {
    BlockPos pos = posI;
    if (face == EnumFacing.UP) {
      pos = pos.add(0, -1, 0);
    } else if (face == EnumFacing.NORTH) {
      pos = pos.add(0, 0, 1);
    } else if (face == EnumFacing.SOUTH) {
      pos = pos.add(0, 0, -1);
    } else if (face == EnumFacing.EAST) {
      pos = pos.add(-1, 0, 0);
    } else if (face == EnumFacing.WEST) {
      pos = pos.add(1, 0, 0);
    } 
    int oldSlot = mc.player.inventory.currentItem;
    int newSlot = -1;
    for (int i = 0; i < 9; ) {
      ItemStack stack = mc.player.inventory.getStackInSlot(i);
      if (InventoryUtil.isNull(stack) || !(stack.getItem() instanceof ItemBlock) || !Block.getBlockFromItem(stack.getItem()).getDefaultState().isFullBlock()) {
        i++;
        continue;
      } 
      newSlot = i;
    } 
    if (newSlot == -1)
      return; 
    boolean crouched = false;
    Block block;
    if (!mc.player.isSneaking() && BlockUtil.blackList.contains(block = mc.world.getBlockState(pos).getBlock())) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      crouched = true;
    } 
    if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock)) {
      mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(newSlot));
      mc.player.inventory.currentItem = newSlot;
      mc.playerController.updateController();
    } 
    if (mc.gameSettings.keyBindJump.isKeyDown()) {
      mc.player.motionX *= 0.3D;
      mc.player.motionZ *= 0.3D;
      mc.player.jump();
      if (this.timer.passedMs(1500L)) {
        mc.player.motionY = -0.28D;
        this.timer.reset();
      } 
    } 
    if (((Boolean)this.rotation.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Rotation(angle[0], MathHelper.normalizeAngle((int)angle[1], 360), mc.player.onGround));
    } 
    mc.playerController.processRightClickBlock(mc.player, mc.world, pos, face, new Vec3d(0.5D, 0.5D, 0.5D), EnumHand.MAIN_HAND);
    mc.player.swingArm(EnumHand.MAIN_HAND);
    mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(oldSlot));
    mc.player.inventory.currentItem = oldSlot;
    mc.playerController.updateController();
    if (crouched)
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING)); 
  }
  
  public enum Mode {
    Legit, Fast;
  }
}
