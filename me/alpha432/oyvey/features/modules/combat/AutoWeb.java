package me.alpha432.oyvey.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.command.Command;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoWeb extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.PLACE));
  
  private final Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(250), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("BlocksPerTick", Integer.valueOf(30), Integer.valueOf(1), Integer.valueOf(30), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> packet = register(new Setting("PacketPlace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> antiSelf = register(new Setting("AntiSelf", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> lowerbody = register(new Setting("Feet", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> upperBody = register(new Setting("Face", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<Boolean> ylower = register(new Setting("Y-1", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.PLACE)));
  
  private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.UNTRAPPED, v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> disable = register(new Setting("AutoDisable", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Double> targetRange = register(new Setting("TargetRange", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(20.0D), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(6.0D), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MISC)));
  
  private final Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> (((Boolean)this.render.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> Rainbow = register(new Setting("CSync", Boolean.valueOf(false), v -> (((Boolean)this.render.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), v -> (((Boolean)this.render.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> rainbowhue = register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.rainbow.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER && ((Boolean)this.rainbow.getValue()).booleanValue())));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(false), v -> (((Boolean)this.render.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.outline.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.outline.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.outline.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  public Setting<Boolean> cRainbow = register(new Setting("OL-Rainbow", Boolean.valueOf(false), v -> (((Boolean)this.outline.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.outline.getValue()).booleanValue() && this.setting.getValue() == Settings.RENDER)));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public static boolean isPlacing = false;
  
  private final Timer timer = new Timer();
  
  public EntityPlayer target;
  
  private boolean didPlace = false;
  
  private boolean isSneaking;
  
  private int lastHotbarSlot;
  
  private int placements = 0;
  
  private boolean smartRotate = false;
  
  private BlockPos startPos = null;
  
  private BlockPos renderPos = null;
  
  public AutoWeb() {
    super("AutoWeb", "Traps other players in webs", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck())
      return; 
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.lastHotbarSlot = mc.player.inventory.currentItem;
  }
  
  public void onTick() {
    if (((Integer)this.eventMode.getValue()).intValue() == 3) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2) {
      this.smartRotate = (((Boolean)this.rotate.getValue()).booleanValue() && ((Integer)this.blocksPerPlace.getValue()).intValue() == 1);
      doTrap();
    } 
  }
  
  public void onUpdate() {
    if (((Integer)this.eventMode.getValue()).intValue() == 1) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.info.getValue()).booleanValue() && this.target != null)
      return this.target.getName(); 
    return null;
  }
  
  public void onDisable() {
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
  }
  
  private void doTrap() {
    if (check())
      return; 
    doWebTrap();
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void doWebTrap() {
    List<Vec3d> placeTargets = getPlacements();
    placeList(placeTargets);
  }
  
  private List<Vec3d> getPlacements() {
    ArrayList<Vec3d> list = new ArrayList<>();
    Vec3d baseVec = this.target.getPositionVector();
    if (((Boolean)this.ylower.getValue()).booleanValue())
      list.add(baseVec.add(0.0D, -1.0D, 0.0D)); 
    if (((Boolean)this.lowerbody.getValue()).booleanValue())
      list.add(baseVec); 
    if (((Boolean)this.upperBody.getValue()).booleanValue())
      list.add(baseVec.add(0.0D, 1.0D, 0.0D)); 
    return list;
  }
  
  private void placeList(List<Vec3d> list) {
    list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
    list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
    for (Vec3d vec3d3 : list) {
      BlockPos position = new BlockPos(vec3d3);
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if ((placeability != 3 && placeability != 1) || (((Boolean)this.antiSelf.getValue()).booleanValue() && MathUtil.areVec3dsAligned(mc.player.getPositionVector(), vec3d3)))
        continue; 
      placeBlock(position);
    } 
  }
  
  private boolean check() {
    isPlacing = false;
    this.didPlace = false;
    this.placements = 0;
    int obbySlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
    if (isOff())
      return true; 
    if (((Boolean)this.disable.getValue()).booleanValue() && !this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    if (obbySlot == -1) {
      if (((Boolean)this.info.getValue()).booleanValue())
        Command.sendMessage("<" + getDisplayName() + "> §cYou are out of Webs."); 
      disable();
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.target = getTarget(((Double)this.targetRange.getValue()).doubleValue(), (this.targetMode.getValue() == TargetMode.UNTRAPPED));
    return (this.target == null || (OyVey.moduleManager.isModuleEnabled("Freecam") && !((Boolean)this.freecam.getValue()).booleanValue()) || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()));
  }
  
  private EntityPlayer getTarget(double range, boolean trapped) {
    EntityPlayer target = null;
    double distance = Math.pow(range, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, range) || (trapped && player.isInWeb) || (EntityUtil.getRoundedBlockPos((Entity)mc.player).equals(EntityUtil.getRoundedBlockPos((Entity)player)) && ((Boolean)this.antiSelf.getValue()).booleanValue()) || OyVey.speedManager.getPlayerSpeed(player) > ((Integer)this.blocksPerPlace.getValue()).intValue())
        continue; 
      if (target == null) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) >= distance)
        continue; 
      target = player;
      distance = mc.player.getDistanceSq((Entity)player);
    } 
    return target;
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(((Double)this.range.getValue()).doubleValue())) {
      isPlacing = true;
      int originalSlot = mc.player.inventory.currentItem;
      int webSlot = InventoryUtil.findHotbarBlock(BlockWeb.class);
      if (webSlot == -1)
        toggle(); 
      if (this.smartRotate) {
        mc.player.inventory.currentItem = (webSlot == -1) ? webSlot : webSlot;
        mc.playerController.updateController();
        this.isSneaking = BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
      } else {
        mc.player.inventory.currentItem = (webSlot == -1) ? webSlot : webSlot;
        mc.playerController.updateController();
        this.isSneaking = BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), ((Boolean)this.packet.getValue()).booleanValue(), this.isSneaking);
        mc.player.inventory.currentItem = originalSlot;
        mc.playerController.updateController();
      } 
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  public void onLogout() {
    disable();
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue())
      RenderUtil.drawBoxESP(this.renderPos, ((Boolean)this.Rainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.cRainbow.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true); 
  }
  
  public enum TargetMode {
    CLOSEST, UNTRAPPED;
  }
  
  public enum Settings {
    PLACE, MISC, RENDER;
  }
}
