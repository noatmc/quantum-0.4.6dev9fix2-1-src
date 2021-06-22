package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.InventoryUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class BowSpam extends Module {
  private final Timer timer = new Timer();
  
  public Setting<Mode> mode = register(new Setting("Mode", Mode.FAST));
  
  public Setting<Boolean> bowbomb = register(new Setting("BowBomb", Boolean.valueOf(false), v -> (this.mode.getValue() != Mode.BOWBOMB)));
  
  public Setting<Boolean> allowOffhand = register(new Setting("Offhand", Boolean.valueOf(true), v -> (this.mode.getValue() != Mode.AUTORELEASE)));
  
  public Setting<Integer> ticks = register(new Setting("Ticks", Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(20), v -> (this.mode.getValue() == Mode.BOWBOMB || this.mode.getValue() == Mode.FAST), "Speed"));
  
  public Setting<Integer> delay = register(new Setting("Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> (this.mode.getValue() == Mode.AUTORELEASE), "Speed"));
  
  public Setting<Boolean> tpsSync = register(new Setting("TpsSync", Boolean.valueOf(true)));
  
  public Setting<Boolean> autoSwitch = register(new Setting("AutoSwitch", Boolean.valueOf(false)));
  
  public Setting<Boolean> onlyWhenSave = register(new Setting("OnlyWhenSave", Boolean.valueOf(true), v -> ((Boolean)this.autoSwitch.getValue()).booleanValue()));
  
  public Setting<Target> targetMode = register(new Setting("Target", Target.LOWEST, v -> ((Boolean)this.autoSwitch.getValue()).booleanValue()));
  
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(3.0F), Float.valueOf(0.0F), Float.valueOf(6.0F), v -> ((Boolean)this.autoSwitch.getValue()).booleanValue(), "Range of the target"));
  
  public Setting<Float> health = register(new Setting("Lethal", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> ((Boolean)this.autoSwitch.getValue()).booleanValue(), "When should it switch?"));
  
  public Setting<Float> ownHealth = register(new Setting("OwnHealth", Float.valueOf(20.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> ((Boolean)this.autoSwitch.getValue()).booleanValue(), "Own Health."));
  
  private boolean offhand = false;
  
  private boolean switched = false;
  
  private int lastHotbarSlot = -1;
  
  public BowSpam() {
    super("BowSpam", "Spams your bow", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    this.lastHotbarSlot = mc.player.inventory.currentItem;
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (((Boolean)this.autoSwitch.getValue()).booleanValue() && InventoryUtil.findHotbarBlock(ItemBow.class) != -1 && ((Float)this.ownHealth.getValue()).floatValue() <= EntityUtil.getHealth((Entity)mc.player) && (!((Boolean)this.onlyWhenSave.getValue()).booleanValue() || EntityUtil.isSafe((Entity)mc.player))) {
      EntityPlayer target = getTarget();
      AutoCrystal crystal;
      if (target != null && (!(crystal = (AutoCrystal)OyVey.moduleManager.getModuleByClass(AutoCrystal.class)).isOn() || !InventoryUtil.holdingItem(ItemEndCrystal.class))) {
        Vec3d pos = target.getPositionVector();
        double xPos = pos.x;
        double yPos = pos.y;
        double zPos = pos.z;
        if (mc.player.canEntityBeSeen((Entity)target)) {
          yPos += target.eyeHeight;
        } else if (EntityUtil.canEntityFeetBeSeen((Entity)target)) {
          yPos += 0.1D;
        } else {
          return;
        } 
        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)) {
          this.lastHotbarSlot = mc.player.inventory.currentItem;
          InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
          mc.gameSettings.keyBindUseItem.pressed = true;
          this.switched = true;
        } 
        OyVey.rotationManager.lookAtVec3d(xPos, yPos, zPos);
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemBow)
          this.switched = true; 
      } 
    } else if (event.getStage() == 0 && this.switched && this.lastHotbarSlot != -1) {
      InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
      mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
      this.switched = false;
    } else {
      mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
    } 
    if (this.mode.getValue() == Mode.FAST && (this.offhand || mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && mc.player.isHandActive()) {
      float f = mc.player.getItemInUseMaxCount();
      float f2 = ((Integer)this.ticks.getValue()).intValue();
      float f3 = ((Boolean)this.tpsSync.getValue()).booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F;
      if (f >= f2 * f3) {
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
        mc.player.stopActiveHand();
      } 
    } 
  }
  
  public void onUpdate() {
    float f, f2, f3;
    this.offhand = (mc.player.getHeldItemOffhand().getItem() == Items.BOW && ((Boolean)this.allowOffhand.getValue()).booleanValue());
    switch ((Mode)this.mode.getValue()) {
      case AUTORELEASE:
        if ((!this.offhand && !(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow)) || !this.timer.passedMs((int)(((Integer)this.delay.getValue()).intValue() * (((Boolean)this.tpsSync.getValue()).booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F))))
          break; 
        mc.playerController.onStoppedUsingItem((EntityPlayer)mc.player);
        this.timer.reset();
        break;
      case BOWBOMB:
        if ((!this.offhand && !(mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow)) || !mc.player.isHandActive())
          break; 
        f = mc.player.getItemInUseMaxCount();
        f2 = ((Integer)this.ticks.getValue()).intValue();
        f3 = ((Boolean)this.tpsSync.getValue()).booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F;
        if (f < f2 * f3)
          break; 
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY - 0.0624D, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
        mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY - 999.0D, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
        mc.player.stopActiveHand();
        break;
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    CPacketPlayerDigging packet;
    if (event.getStage() == 0 && ((Boolean)this.bowbomb.getValue()).booleanValue() && this.mode.getValue() != Mode.BOWBOMB && event.getPacket() instanceof CPacketPlayerDigging && (packet = (CPacketPlayerDigging)event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM && (this.offhand || mc.player.inventory.getCurrentItem().getItem() instanceof ItemBow) && mc.player.getItemInUseMaxCount() >= 20 && !mc.player.onGround) {
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 0.10000000149011612D, mc.player.posZ, false));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 10000.0D, mc.player.posZ, true));
    } 
  }
  
  private EntityPlayer getTarget() {
    double maxHealth = 36.0D;
    EntityPlayer target = null;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (player == null || EntityUtil.isDead((Entity)player) || EntityUtil.getHealth((Entity)player) > ((Float)this.health.getValue()).floatValue() || player.equals(mc.player) || OyVey.friendManager.isFriend(player) || mc.player.getDistanceSq((Entity)player) > MathUtil.square(((Float)this.range.getValue()).floatValue()) || (!mc.player.canEntityBeSeen((Entity)player) && !EntityUtil.canEntityFeetBeSeen((Entity)player)))
        continue; 
      if (target == null) {
        target = player;
        maxHealth = EntityUtil.getHealth((Entity)player);
      } 
      if (this.targetMode.getValue() == Target.CLOSEST && mc.player.getDistanceSq((Entity)player) < mc.player.getDistanceSq((Entity)target)) {
        target = player;
        maxHealth = EntityUtil.getHealth((Entity)player);
      } 
      if (this.targetMode.getValue() != Target.LOWEST || EntityUtil.getHealth((Entity)player) >= maxHealth)
        continue; 
      target = player;
      maxHealth = EntityUtil.getHealth((Entity)player);
    } 
    return target;
  }
  
  public enum Mode {
    FAST, AUTORELEASE, BOWBOMB;
  }
  
  public enum Target {
    CLOSEST, LOWEST;
  }
}
