package me.alpha432.oyvey.features.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.PlayerUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Surround extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.PLACE));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Integer> blocksPerTick = register(new Setting("BlocksPerTick", Integer.valueOf(20), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> noGhost = register(new Setting("PacketPlace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> floor = register(new Setting("Floor", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> alwaysHelp = register(new Setting("AlwaysHelp", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> helpingBlocks = register(new Setting("HelpingBlocks", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Center> centerPlayer = register(new Setting("Center", Center.None, v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Integer> retryer = register(new Setting("Retries", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> rainbowhue = register(new Setting("RainbowHue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public final Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  public final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  public final Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cRed = register(new Setting("cRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cGreen = register(new Setting("cGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cBlue = register(new Setting("cBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cAlpha = register(new Setting("cAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.RENDER && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Timer timer = new Timer();
  
  private final Timer retryTimer = new Timer();
  
  private final Set<Vec3d> extendingBlocks = new HashSet<>();
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private List<BlockPos> placeVectors = new ArrayList<>();
  
  private int isSafe;
  
  public static boolean isPlacing = false;
  
  private BlockPos startPos;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private int lastHotbarSlot;
  
  private boolean isSneaking;
  
  private int placements = 0;
  
  private int extenders = 1;
  
  private int obbySlot = -1;
  
  private boolean offHand = false;
  
  Vec3d center = Vec3d.ZERO;
  
  public Surround() {
    super("Surround", "Surrounds you with Obsidian", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      disable(); 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.center = PlayerUtil.getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
    switch ((Center)this.centerPlayer.getValue()) {
      case TP:
        mc.player.motionX = 0.0D;
        mc.player.motionZ = 0.0D;
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(this.center.x, this.center.y, this.center.z, true));
        mc.player.setPosition(this.center.x, this.center.y, this.center.z);
        break;
      case NCP:
        mc.player.motionX = (this.center.x - mc.player.posX) / 2.0D;
        mc.player.motionZ = (this.center.z - mc.player.posZ) / 2.0D;
        break;
    } 
    this.retries.clear();
    this.retryTimer.reset();
  }
  
  public void onTick() {
    if (((Integer)this.eventMode.getValue()).intValue() == 3)
      doFeetPlace(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2)
      doFeetPlace(); 
  }
  
  public void onUpdate() {
    if (((Integer)this.eventMode.getValue()).intValue() == 1)
      doFeetPlace(); 
    if (check())
      return; 
    this;
    this;
    boolean onWeb = (mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.WEB);
    this;
    if (!BlockUtil.isSafe((Entity)mc.player, onWeb ? 1 : 0, ((Boolean)this.floor.getValue()).booleanValue())) {
      this;
      this;
      placeBlocks(mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(mc.player.getPositionVector(), onWeb ? 1 : 0, ((Boolean)this.floor.getValue()).booleanValue()), ((Boolean)this.helpingBlocks.getValue()).booleanValue(), false, false);
    } else {
      this;
      if (!BlockUtil.isSafe((Entity)mc.player, onWeb ? 0 : -1, false) && ((Boolean)this.alwaysHelp.getValue()).booleanValue()) {
        this;
        this;
        placeBlocks(mc.player.getPositionVector(), BlockUtil.getUnsafeBlockArray(mc.player.getPositionVector(), onWeb ? 0 : -1, false), false, false, true);
      } 
    } 
    boolean inEChest = (mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST);
    if (mc.player.posY - (int)mc.player.posY < 0.7D)
      inEChest = false; 
    processExtendingBlocks();
    if (this.didPlace)
      this.timer.reset(); 
    if (this.isSafe == 2)
      this.placeVectors = new ArrayList<>(); 
  }
  
  public void onDisable() {
    if (nullCheck())
      return; 
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && (this.isSafe == 0 || this.isSafe == 1)) {
      this.placeVectors = rushehacj();
      for (BlockPos pos : this.placeVectors) {
        if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir))
          continue; 
        RenderUtil.drawBoxESP(pos, ((Boolean)this.rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)this.rainbowhue.getValue()).intValue()) : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
      } 
    } 
  }
  
  public String getDisplayInfo() {
    switch (this.isSafe) {
      case 0:
        return ChatFormatting.RED + "Unsafe";
      case 1:
        return ChatFormatting.YELLOW + "Safe";
    } 
    return ChatFormatting.GREEN + "Safe";
  }
  
  private void doFeetPlace() {
    if (check())
      return; 
    if (!EntityUtil.isSafeOy((Entity)mc.player, 0, true)) {
      this.isSafe = 0;
      placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray2((Entity)mc.player, 0, true), true, false, false);
    } else if (!EntityUtil.isSafeOy((Entity)mc.player, -1, false)) {
      this.isSafe = 1;
      placeBlocks(mc.player.getPositionVector(), EntityUtil.getUnsafeBlockArray2((Entity)mc.player, -1, false), false, false, true);
    } else {
      this.isSafe = 2;
    } 
    processExtendingBlocks();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void processExtendingBlocks() {
    if (this.extendingBlocks.size() == 2 && this.extenders < 1) {
      Vec3d[] array = new Vec3d[2];
      int i = 0;
      Iterator<Vec3d> iterator = this.extendingBlocks.iterator();
      while (iterator.hasNext()) {
        Vec3d vec3d = iterator.next();
        i++;
      } 
      int placementsBefore = this.placements;
      if (areClose(array) != null)
        placeBlocks(areClose(array), BlockUtil.getUnsafeBlockArray(areClose(array), 0, ((Boolean)this.floor.getValue()).booleanValue()), ((Boolean)this.helpingBlocks.getValue()).booleanValue(), false, true); 
      if (placementsBefore < this.placements)
        this.extendingBlocks.clear(); 
    } else if (this.extendingBlocks.size() > 2 || this.extenders >= 1) {
      this.extendingBlocks.clear();
    } 
  }
  
  private Vec3d areClose(Vec3d[] vec3ds) {
    int matches = 0;
    for (Vec3d vec3d : vec3ds) {
      this;
      for (Vec3d pos : BlockUtil.getUnsafeBlockArray(mc.player.getPositionVector(), 0, ((Boolean)this.floor.getValue()).booleanValue())) {
        if (vec3d.equals(pos))
          matches++; 
      } 
    } 
    if (matches == 2)
      return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds[1])); 
    return null;
  }
  
  private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean isExtending) {
    boolean gotHelp = true;
    for (Vec3d vec3d : vec3ds) {
      gotHelp = true;
      BlockPos position = (new BlockPos(pos)).add(vec3d.x, vec3d.y, vec3d.z);
      switch (BlockUtil.isPositionPlaceable(position, false)) {
        case 1:
          if (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < ((Integer)this.retryer.getValue()).intValue()) {
            placeBlock(position);
            this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
            this.retryTimer.reset();
            break;
          } 
          if (OyVey.speedManager.getSpeedKpH() != 0.0D || isExtending || this.extenders >= 1)
            break; 
          placeBlocks(mc.player.getPositionVector().add(vec3d), EntityUtil.getUnsafeBlockArrayFromVec3d2(mc.player.getPositionVector().add(vec3d), 0, true), hasHelpingBlocks, false, true);
          this.extendingBlocks.add(vec3d);
          this.extenders++;
          break;
        case 2:
          if (!hasHelpingBlocks)
            break; 
          gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
        case 3:
          if (gotHelp)
            placeBlock(position); 
          if (!isHelping)
            break; 
          return true;
      } 
    } 
    return false;
  }
  
  private boolean check() {
    if (nullCheck())
      return true; 
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (obbySlot == -1 && eChestSot == -1)
      toggle(); 
    this.offHand = InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class);
    isPlacing = false;
    this.didPlace = false;
    this.extenders = 1;
    this.placements = 0;
    this.obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
    if (isOff())
      return true; 
    if (this.retryTimer.passedMs(2500L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (this.obbySlot == -1 && !this.offHand && echestSlot == -1) {
      Command.sendMessage("<" + getDisplayName() + "> " + ChatFormatting.RED + "No Obsidian in hotbar disabling...");
      disable();
      return true;
    } 
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != this.obbySlot && mc.player.inventory.currentItem != echestSlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    if (!this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    return !this.timer.passedMs(((Integer)this.delay.getValue()).intValue());
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerTick.getValue()).intValue()) {
      int originalSlot = mc.player.inventory.currentItem;
      int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
      int eChestSot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (obbySlot == -1 && eChestSot == -1)
        toggle(); 
      isPlacing = true;
      mc.player.inventory.currentItem = (obbySlot == -1) ? eChestSot : obbySlot;
      mc.playerController.updateController();
      this.isSneaking = BlockUtil.placeBlock(pos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.noGhost.getValue()).booleanValue(), this.isSneaking);
      mc.player.inventory.currentItem = originalSlot;
      mc.playerController.updateController();
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  private List<BlockPos> rushehacj() {
    if (((Boolean)this.floor.getValue()).booleanValue())
      return Arrays.asList(new BlockPos[] { (new BlockPos(mc.player.getPositionVector())).add(0, -1, 0), (new BlockPos(mc.player.getPositionVector())).add(1, 0, 0), (new BlockPos(mc.player.getPositionVector())).add(-1, 0, 0), (new BlockPos(mc.player.getPositionVector())).add(0, 0, -1), (new BlockPos(mc.player.getPositionVector())).add(0, 0, 1) }); 
    return Arrays.asList(new BlockPos[] { (new BlockPos(mc.player.getPositionVector())).add(1, 0, 0), (new BlockPos(mc.player.getPositionVector())).add(-1, 0, 0), (new BlockPos(mc.player.getPositionVector())).add(0, 0, -1), (new BlockPos(mc.player.getPositionVector())).add(0, 0, 1) });
  }
  
  public enum Settings {
    PLACE, MISC, RENDER;
  }
  
  public enum Center {
    TP, NCP, None;
  }
}
