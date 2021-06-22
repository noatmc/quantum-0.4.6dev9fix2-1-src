package me.alpha432.oyvey.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.client.ClickGui;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.mixin.mixins.accessors.AccessorCPacketUseEntity;
import me.alpha432.oyvey.util.BlockUtil;
import me.alpha432.oyvey.util.ColorUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.ItemUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CrystalAura extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.Place));
  
  private final Setting<Integer> placeDelay;
  
  private final Setting<Float> placeRange;
  
  private final Setting<Integer> breakDelay;
  
  public Setting<Float> breakRange;
  
  private final Setting<Boolean> cancelcrystal;
  
  private final Setting<Boolean> antiWeakness;
  
  private final Setting<Boolean> antiWeaknessSilent;
  
  private final Setting<Boolean> switchBack;
  
  private final Setting<InfoMode> infomode;
  
  private final Setting<Boolean> offhandS;
  
  public Setting<Boolean> text;
  
  public Setting<RenderMode> render;
  
  public Setting<Boolean> colorSync;
  
  public Setting<Boolean> box;
  
  private final Setting<Integer> red;
  
  private final Setting<Integer> green;
  
  private final Setting<Integer> blue;
  
  private final Setting<Integer> alpha;
  
  private final Setting<Integer> boxAlpha;
  
  public Setting<Boolean> outline;
  
  private final Setting<Float> lineWidth;
  
  public Setting<Boolean> customOutline;
  
  private final Setting<Integer> cRed;
  
  private final Setting<Integer> cGreen;
  
  private final Setting<Integer> cBlue;
  
  private final Setting<Integer> cAlpha;
  
  private final Setting<Integer> colorRed;
  
  private final Setting<Integer> colorGreen;
  
  private final Setting<Integer> colorBlue;
  
  private final Setting<Integer> topAlpha;
  
  private final Setting<Integer> colorRedDown;
  
  private final Setting<Integer> colorGreenDown;
  
  private final Setting<Integer> colorBlueDown;
  
  private final Setting<Integer> downAlpha;
  
  private final Setting<Integer> outlineAlpha;
  
  private final Setting<Float> outlineWidth;
  
  private final Setting<Integer> clawRed;
  
  private final Setting<Integer> clawGreen;
  
  private final Setting<Integer> clawBlue;
  
  private final Setting<Integer> clawAlpha;
  
  private final Setting<Float> range;
  
  public Setting<Rotate> rotate;
  
  public Setting<Integer> rotations;
  
  public Setting<Boolean> rotateFirst;
  
  public Setting<Raytrace> raytrace;
  
  public Setting<Float> placetrace;
  
  public Setting<Float> breaktrace;
  
  private final Setting<Float> breakWallRange;
  
  private final Setting<Float> minDamage;
  
  private final Setting<Float> maxSelf;
  
  private final Setting<Float> lethalMult;
  
  private final Setting<Float> armorScale;
  
  private final Setting<Boolean> second;
  
  private final Setting<Boolean> autoSwitch;
  
  private final Map<Integer, Integer> attackMap;
  
  private final List<BlockPos> placedList;
  
  private final Timer breakTimer;
  
  private final Timer placeTimer;
  
  private final Timer renderTimer;
  
  private int rotationPacketsSpoofed;
  
  public static EntityPlayer currentTarget;
  
  private BlockPos renderPos;
  
  private double renderDamage;
  
  private BlockPos placePos;
  
  private boolean offHand;
  
  public boolean rotating;
  
  private float pitch;
  
  private float yaw;
  
  private boolean offhand;
  
  public CrystalAura() {
    super("CrystalAura", "ca", Module.Category.COMBAT, true, false, false);
    this.placeDelay = register(new Setting("Delay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> (this.setting.getValue() == Settings.Place)));
    this.placeRange = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Place)));
    this.breakDelay = register(new Setting("BDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(200), v -> (this.setting.getValue() == Settings.Break)));
    this.breakRange = register(new Setting("BRange", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Break)));
    this.cancelcrystal = register(new Setting("SetDead", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Break)));
    this.antiWeakness = register(new Setting("AntiWeakness", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Break)));
    this.antiWeaknessSilent = register(new Setting("SilentWeakness", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Break && ((Boolean)this.antiWeakness.getValue()).booleanValue())));
    this.switchBack = register(new Setting("SwitchBack", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Break && ((Boolean)this.antiWeakness.getValue()).booleanValue())));
    this.infomode = register(new Setting("Info", InfoMode.Target, v -> (this.setting.getValue() == Settings.Render)));
    this.offhandS = register(new Setting("OffhandSwing", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Render)));
    this.text = register(new Setting("DamageText", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Render)));
    this.render = register(new Setting("RenderMode", RenderMode.Box, v -> (this.setting.getValue() == Settings.Render)));
    this.colorSync = register(new Setting("Rainbow", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.box = register(new Setting("Box", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.red = register(new Setting("BoxRed", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.green = register(new Setting("BoxGreen", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.blue = register(new Setting("BoxBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.alpha = register(new Setting("BoxAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.box.getValue()).booleanValue())));
    this.outline = register(new Setting("Outline", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box)));
    this.lineWidth = register(new Setting("OutlineWidth", Float.valueOf(0.1F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.outline.getValue()).booleanValue())));
    this.customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.outline.getValue()).booleanValue())));
    this.cRed = register(new Setting("CustomRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
    this.cGreen = register(new Setting("CustomGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
    this.cBlue = register(new Setting("CustomBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
    this.cAlpha = register(new Setting("CustomAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Box && ((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
    this.colorRed = register(new Setting("TopRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.colorGreen = register(new Setting("TopGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.colorBlue = register(new Setting("TopBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.topAlpha = register(new Setting("TopAlpha", Integer.valueOf(6), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.colorRedDown = register(new Setting("DownRed", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.colorGreenDown = register(new Setting("DownGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.colorBlueDown = register(new Setting("DownBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.downAlpha = register(new Setting("DownAlpha", Integer.valueOf(46), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.outlineAlpha = register(new Setting("LAlpha", Integer.valueOf(200), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.outlineWidth = register(new Setting("LWidth", Float.valueOf(1.0F), Float.valueOf(0.0F), Float.valueOf(5.0F), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Gradient)));
    this.clawRed = register(new Setting("ClawRed", Integer.valueOf(80), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw)));
    this.clawGreen = register(new Setting("ClawGreen", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw)));
    this.clawBlue = register(new Setting("ClawBlue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw)));
    this.clawAlpha = register(new Setting("ClawAlpha", Integer.valueOf(120), Integer.valueOf(0), Integer.valueOf(255), v -> (this.setting.getValue() == Settings.Render && this.render.getValue() == RenderMode.Claw)));
    this.range = register(new Setting("TargetRange", Float.valueOf(9.5F), Float.valueOf(0.0F), Float.valueOf(16.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.rotate = register(new Setting("Rotate", Rotate.OFF, v -> (this.setting.getValue() == Settings.Misc)));
    this.rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> (this.setting.getValue() == Settings.Misc && this.rotate.getValue() != Rotate.OFF)));
    this.rotateFirst = register(new Setting("FirstRotation", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Misc && this.rotate.getValue() != Rotate.OFF)));
    this.raytrace = register(new Setting("Raytrace", Raytrace.None, v -> (this.setting.getValue() == Settings.Misc)));
    this.placetrace = register(new Setting("Placetrace", Float.valueOf(5.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.Misc && this.raytrace.getValue() != Raytrace.None && this.raytrace.getValue() != Raytrace.Break)));
    this.breaktrace = register(new Setting("Breaktrace", Float.valueOf(5.5F), Float.valueOf(0.0F), Float.valueOf(10.0F), v -> (this.setting.getValue() == Settings.Misc && this.raytrace.getValue() != Raytrace.None && this.raytrace.getValue() != Raytrace.Place)));
    this.breakWallRange = register(new Setting("WallRange", Float.valueOf(4.5F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.minDamage = register(new Setting("MinDamage", Float.valueOf(0.7F), Float.valueOf(0.0F), Float.valueOf(30.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.maxSelf = register(new Setting("MaxSelf", Float.valueOf(18.5F), Float.valueOf(0.0F), Float.valueOf(36.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.lethalMult = register(new Setting("LethalMult", Float.valueOf(0.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.armorScale = register(new Setting("ArmorBreak", Float.valueOf(100.0F), Float.valueOf(0.0F), Float.valueOf(100.0F), v -> (this.setting.getValue() == Settings.Misc)));
    this.second = register(new Setting("Second", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Misc)));
    this.autoSwitch = register(new Setting("AutoSwitch", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.Misc)));
    this.attackMap = new HashMap<>();
    this.placedList = new ArrayList<>();
    this.breakTimer = new Timer();
    this.placeTimer = new Timer();
    this.renderTimer = new Timer();
    this.rotationPacketsSpoofed = 0;
    this.renderPos = null;
    this.renderDamage = 0.0D;
    this.placePos = null;
    this.offHand = false;
    this.rotating = false;
    this.pitch = 0.0F;
    this.yaw = 0.0F;
  }
  
  public void onToggle() {
    this.placedList.clear();
    this.breakTimer.reset();
    this.placeTimer.reset();
    this.renderTimer.reset();
    currentTarget = null;
    this.attackMap.clear();
    this.renderPos = null;
    this.offhand = false;
    this.rotating = false;
  }
  
  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onTick(TickEvent.ClientTickEvent event) {
    if (isNull())
      return; 
    if (this.renderTimer.passedMs(500L)) {
      this.placedList.clear();
      this.renderPos = null;
      this.renderTimer.reset();
    } 
    this.offhand = (((ItemStack)mc.player.inventory.offHandInventory.get(0)).getItem() == Items.END_CRYSTAL);
    currentTarget = EntityUtil.getClosestPlayer(((Float)this.range.getValue()).floatValue());
    if (currentTarget == null)
      return; 
    doPlace();
    if (event.phase == TickEvent.Phase.START)
      doBreak(); 
  }
  
  private void doBreak() {
    Entity maxCrystal = null;
    Entity crystal = null;
    double maxDamage = 0.5D;
    int size = mc.world.loadedEntityList.size();
    for (int i = 0; i < size; i++) {
      Entity entity = mc.world.loadedEntityList.get(i);
      if (entity instanceof EntityEnderCrystal && 
        mc.player.getDistance(entity) < (mc.player.canEntityBeSeen(entity) ? (Float)this.breakRange.getValue() : (Float)this.breakWallRange.getValue()).floatValue()) {
        float targetDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase)currentTarget);
        if (targetDamage > ((Float)this.minDamage.getValue()).floatValue() || targetDamage * ((Float)this.lethalMult.getValue()).floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, ((Float)this.armorScale.getValue()).floatValue())) {
          float selfDamage = EntityUtil.calculate(entity.posX, entity.posY, entity.posZ, (EntityLivingBase)mc.player);
          if (selfDamage <= ((Float)this.maxSelf.getValue()).floatValue() && selfDamage + 2.0F <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage && maxDamage <= targetDamage) {
            maxDamage = targetDamage;
            crystal = entity;
            maxCrystal = crystal;
          } 
        } 
      } 
    } 
    if (crystal != null && this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue())) {
      mc.getConnection().sendPacket((Packet)new CPacketUseEntity(crystal));
      mc.player.swingArm(((Boolean)this.offhandS.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
      this.breakTimer.reset();
    } 
    if (maxCrystal != null && this.breakTimer.hasReached(((Integer)this.breakDelay.getValue()).intValue())) {
      int lastSlot = -1;
      if (((Boolean)this.antiWeakness.getValue()).booleanValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
        boolean swtch = (!mc.player.isPotionActive(MobEffects.STRENGTH) || ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH))).getAmplifier() != 2);
        int swordSlot = ItemUtil.getItemSlot(Items.DIAMOND_SWORD);
        if (swtch && swordSlot != -1) {
          lastSlot = mc.player.inventory.currentItem;
          if (((Boolean)this.antiWeaknessSilent.getValue()).booleanValue()) {
            mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(swordSlot));
          } else {
            mc.player.inventory.currentItem = swordSlot;
          } 
        } 
      } 
      mc.getConnection().sendPacket((Packet)new CPacketUseEntity(maxCrystal));
      this.attackMap.put(Integer.valueOf(maxCrystal.getEntityId()), Integer.valueOf(this.attackMap.containsKey(Integer.valueOf(maxCrystal.getEntityId())) ? (((Integer)this.attackMap.get(Integer.valueOf(maxCrystal.getEntityId()))).intValue() + 1) : 1));
      mc.player.swingArm(EnumHand.OFF_HAND);
      if (lastSlot != -1 && ((Boolean)this.switchBack.getValue()).booleanValue())
        if (((Boolean)this.antiWeaknessSilent.getValue()).booleanValue()) {
          mc.getConnection().sendPacket((Packet)new CPacketHeldItemChange(lastSlot));
        } else {
          mc.player.inventory.currentItem = lastSlot;
        }  
      this.breakTimer.reset();
    } 
  }
  
  private void doPlace() {
    BlockPos placePos = null;
    double maxDamage = 0.5D;
    List<BlockPos> sphere = BlockUtil.getSphereRealth(((Float)this.placeRange.getValue()).floatValue(), true);
    int size = sphere.size();
    for (int i = 0; i < size; i++) {
      BlockPos pos = sphere.get(i);
      if (BlockUtil.canPlaceCrystalRealth(pos, ((Boolean)this.second.getValue()).booleanValue())) {
        float targetDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, (EntityLivingBase)currentTarget);
        if (targetDamage > ((Float)this.minDamage.getValue()).floatValue() || targetDamage * ((Float)this.lethalMult.getValue()).floatValue() > currentTarget.getHealth() + currentTarget.getAbsorptionAmount() || ItemUtil.isArmorUnderPercent(currentTarget, ((Float)this.armorScale.getValue()).floatValue())) {
          float selfDamage = EntityUtil.calculate(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, (EntityLivingBase)mc.player);
          if (selfDamage <= ((Float)this.maxSelf.getValue()).floatValue() && selfDamage + 2.0F <= mc.player.getHealth() + mc.player.getAbsorptionAmount() && selfDamage < targetDamage && maxDamage <= targetDamage) {
            maxDamage = targetDamage;
            placePos = pos;
            this.renderPos = pos;
            this.renderDamage = targetDamage;
          } 
        } 
      } 
    } 
    boolean flag = false;
    if (!this.offhand && mc.player.inventory.getCurrentItem().getItem() != Items.END_CRYSTAL) {
      flag = true;
      if (!((Boolean)this.autoSwitch.getValue()).booleanValue() || (mc.player.inventory.getCurrentItem().getItem() == Items.GOLDEN_APPLE && mc.player.isHandActive()))
        return; 
    } 
    if (placePos != null) {
      if (this.placeTimer.passedMs(((Integer)this.placeDelay.getValue()).intValue())) {
        if (flag) {
          int slot = ItemUtil.getItemFromHotbar(Items.END_CRYSTAL);
          if (slot == -1)
            return; 
          mc.player.inventory.currentItem = slot;
        } 
        this.placedList.add(placePos);
        mc.getConnection().sendPacket((Packet)new CPacketPlayerTryUseItemOnBlock(placePos, EnumFacing.UP, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
        this.placeTimer.reset();
      } 
      this.renderPos = placePos;
    } 
    for (BlockPos pos : BlockUtil.possiblePlacePositionsCa(((Float)this.placeRange.getValue()).floatValue())) {
      if (!BlockUtil.rayTracePlaceCheck(pos, ((this.raytrace.getValue() == Raytrace.Place || this.raytrace.getValue() == Raytrace.Both) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square(((Float)this.placetrace.getValue()).floatValue())), 1.0F));
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (event.getPacket() instanceof SPacketSpawnObject) {
      SPacketSpawnObject packet = (SPacketSpawnObject)event.getPacket();
      if (packet.getType() == 51 && this.placedList.contains(new BlockPos(packet.getX(), packet.getY() - 1.0D, packet.getZ()))) {
        AccessorCPacketUseEntity use = (AccessorCPacketUseEntity)new CPacketUseEntity();
        use.setEntityId(packet.getEntityID());
        use.setAction(CPacketUseEntity.Action.ATTACK);
        mc.getConnection().sendPacket((Packet)use);
        mc.player.swingArm(((Boolean)this.offhandS.getValue()).booleanValue() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        this.breakTimer.reset();
        return;
      } 
    } 
    if (event.getPacket() instanceof SPacketSoundEffect) {
      SPacketSoundEffect packet = (SPacketSoundEffect)event.getPacket();
      if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE)
        (new ArrayList(mc.world.loadedEntityList)).forEach(e -> {
              if (e instanceof EntityEnderCrystal && e.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36.0D)
                e.setDead(); 
            }); 
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (this.rotate.getValue() != Rotate.OFF && this.rotating && event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
      packet2.yaw = this.yaw;
      packet2.pitch = this.pitch;
      this.rotationPacketsSpoofed++;
      if (this.rotationPacketsSpoofed >= ((Integer)this.rotations.getValue()).intValue()) {
        this.rotating = false;
        this.rotationPacketsSpoofed = 0;
      } 
    } 
    BlockPos pos = null;
    CPacketUseEntity packet;
    if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal)
      pos = packet.getEntityFromWorld((World)AutoCrystal.mc.world).getPosition(); 
    if (event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
      EntityEnderCrystal crystal = (EntityEnderCrystal)packet.getEntityFromWorld((World)AutoCrystal.mc.world);
      if (EntityUtil.isCrystalAtFeet(crystal, ((Float)this.range.getValue()).floatValue()) && pos != null) {
        rotateToPos(pos);
        BlockUtil.placeCrystalOnBlock2(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, false);
      } 
    } 
    if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld((World)AutoCrystal.mc.world) instanceof EntityEnderCrystal && ((Boolean)this.cancelcrystal.getValue()).booleanValue()) {
      ((Entity)Objects.<Entity>requireNonNull(packet.getEntityFromWorld((World)AutoCrystal.mc.world))).setDead();
      AutoCrystal.mc.world.removeEntityFromWorld(packet.entityId);
    } 
  }
  
  private void rotateToPos(BlockPos pos) {
    float[] angle;
    switch ((Rotate)this.rotate.getValue()) {
      case OFF:
        this.rotating = false;
        break;
      case Place:
      case All:
        angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
        if (this.rotate.getValue() != Rotate.OFF) {
          OyVey.rotationManager.setPlayerRotations(angle[0], angle[1]);
          break;
        } 
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.rotating = true;
        break;
    } 
  }
  
  public void onRender3D(Render3DEvent event) {
    if (this.renderPos != null && this.render.getValue() != RenderMode.None && (((Boolean)this.box.getValue()).booleanValue() || ((Boolean)this.text.getValue()).booleanValue() || ((Boolean)this.outline.getValue()).booleanValue())) {
      if (this.render.getValue() == RenderMode.Gradient) {
        RenderUtil.drawGradientFilledBoxVulcan(this.renderPos, new Color(((Integer)this.colorRedDown.getValue()).intValue(), ((Integer)this.colorGreenDown.getValue()).intValue(), ((Integer)this.colorBlueDown.getValue()).intValue(), ((Integer)this.downAlpha.getValue()).intValue()), new Color(((Integer)this.colorRed.getValue()).intValue(), ((Integer)this.colorGreen.getValue()).intValue(), ((Integer)this.colorBlue.getValue()).intValue(), ((Integer)this.topAlpha.getValue()).intValue()));
        RenderUtil.prepare(7);
        RenderUtil.drawBoundingBoxBottom2(this.renderPos, ((Float)this.outlineWidth.getValue()).floatValue(), ((Integer)this.colorRedDown.getValue()).intValue(), ((Integer)this.colorGreenDown.getValue()).intValue(), ((Integer)this.colorBlueDown.getValue()).intValue(), ((Integer)this.outlineAlpha.getValue()).intValue());
        RenderUtil.release();
        if (((Boolean)this.text.getValue()).booleanValue())
          RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""); 
      } 
      if (this.render.getValue() == RenderMode.Box) {
        RenderUtil.drawBoxESP(this.renderPos, ((Boolean)this.colorSync.getValue()).booleanValue() ? ColorUtil.rainbow(((Integer)(ClickGui.getInstance()).rainbowHue.getValue()).intValue()) : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), ((Boolean)this.colorSync.getValue()).booleanValue() ? getCurrentColor() : new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
        if (((Boolean)this.text.getValue()).booleanValue())
          RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""); 
      } 
      if (this.render.getValue() == RenderMode.Claw) {
        RenderUtil.drawBoxBlockPos(this.renderPos, 0.0D, 0.0D, 0.0D, new Color(((Integer)this.clawRed.getValue()).intValue(), ((Integer)this.clawGreen.getValue()).intValue(), ((Integer)this.clawBlue.getValue()).intValue()), ((Integer)this.clawAlpha.getValue()).intValue(), RenderMode.Claw);
        if (((Boolean)this.text.getValue()).booleanValue())
          RenderUtil.drawText(this.renderPos, ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""); 
      } 
    } 
  }
  
  public Color getCurrentColor() {
    return new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue());
  }
  
  public String getDisplayInfo() {
    if (currentTarget != null) {
      if (this.infomode.getValue() == InfoMode.Target)
        return currentTarget.getName(); 
      if (this.infomode.getValue() == InfoMode.Damage)
        return ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""; 
      if (this.infomode.getValue() == InfoMode.Both)
        return currentTarget.getName() + ", " + ((Math.floor(this.renderDamage) == this.renderDamage) ? (String)Integer.valueOf((int)this.renderDamage) : String.format("%.1f", new Object[] { Double.valueOf(this.renderDamage) })) + ""; 
    } 
    return null;
  }
  
  public enum Settings {
    Place, Break, Render, Misc;
  }
  
  public enum InfoMode {
    Target, Damage, Both;
  }
  
  public enum Rotate {
    OFF, Place, Break, All;
  }
  
  public enum Raytrace {
    None, Place, Break, Both;
  }
  
  public enum RenderMode {
    Box, Gradient, Flat, Claw, None;
  }
}
