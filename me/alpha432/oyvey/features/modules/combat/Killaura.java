package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.DamageUtil;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.MathUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Killaura extends Module {
  private final Setting<Settings> setting = register(new Setting("Settings", Settings.MAIN));
  
  public static Entity target;
  
  private final Timer timer = new Timer();
  
  private final Setting<Boolean> delay = register(new Setting("HitDelay", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Boolean> onlySharp = register(new Setting("SwordOnly", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Float> raytrace = register(new Setting("Raytrace", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(7.0F), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Boolean> tps = register(new Setting("TpsSync", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.MAIN)));
  
  private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST, v -> (this.setting.getValue() == Settings.MAIN)));
  
  public Setting<Float> health = register(new Setting("Health", Float.valueOf(6.0F), Float.valueOf(0.1F), Float.valueOf(36.0F), v -> (this.targetMode.getValue() == TargetMode.SMART)));
  
  public Setting<Boolean> players = register(new Setting("Players", Boolean.valueOf(true), v -> (this.setting.getValue() == Settings.TARGETS)));
  
  public Setting<Boolean> mobs = register(new Setting("Mobs", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.TARGETS)));
  
  public Setting<Boolean> animals = register(new Setting("Animals", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.TARGETS)));
  
  public Setting<Boolean> vehicles = register(new Setting("Entities", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.TARGETS)));
  
  public Setting<Boolean> projectiles = register(new Setting("Projectiles", Boolean.valueOf(false), v -> (this.setting.getValue() == Settings.TARGETS)));
  
  public Killaura() {
    super("KillAura", "ka", Module.Category.COMBAT, true, false, false);
  }
  
  public void onTick() {
    if (!((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue())
      doKillaura(); 
  }
  
  private void doKillaura() {
    if (((Boolean)this.onlySharp.getValue()).booleanValue() && !EntityUtil.holdingWeapon((EntityPlayer)mc.player)) {
      target = null;
      return;
    } 
    if (this.targetMode.getValue() != TargetMode.FOCUS || target == null || (mc.player.getDistanceSq(target) >= MathUtil.square(((Float)this.range.getValue()).floatValue()) && !EntityUtil.canEntityFeetBeSeen(target) && mc.player.getDistanceSq(target) >= MathUtil.square(((Float)this.raytrace.getValue()).floatValue())))
      target = getTarget(); 
    int wait = !((Boolean)this.delay.getValue()).booleanValue() ? 0 : (int)(DamageUtil.getCooldownByWeapon((EntityPlayer)mc.player) * (((Boolean)this.tps.getValue()).booleanValue() ? OyVey.serverManager.getTpsFactor() : 1.0F));
    if (!this.timer.passedMs(wait))
      return; 
    target = getTarget();
    if (target == null)
      return; 
    if (((Boolean)this.rotate.getValue()).booleanValue())
      OyVey.rotationManager.lookAtEntity(target); 
    EntityUtil.attackEntity(target, ((Boolean)this.packet.getValue()).booleanValue(), true);
    this.timer.reset();
  }
  
  private Entity getTarget() {
    Entity target = null;
    double distance = ((Float)this.range.getValue()).floatValue();
    double maxHealth = 36.0D;
    for (Entity entity : mc.world.loadedEntityList) {
      if (((!((Boolean)this.players.getValue()).booleanValue() || !(entity instanceof EntityPlayer)) && (!((Boolean)this.animals.getValue()).booleanValue() || !EntityUtil.isPassive(entity)) && (!((Boolean)this.mobs.getValue()).booleanValue() || !EntityUtil.isMobAggressive(entity)) && (!((Boolean)this.vehicles.getValue()).booleanValue() || !EntityUtil.isVehicle(entity)) && (!((Boolean)this.projectiles.getValue()).booleanValue() || !EntityUtil.isProjectile(entity))) || (entity instanceof net.minecraft.entity.EntityLivingBase && EntityUtil.isntValid(entity, distance)) || (!mc.player.canEntityBeSeen(entity) && !EntityUtil.canEntityFeetBeSeen(entity) && mc.player.getDistanceSq(entity) > MathUtil.square(((Float)this.raytrace.getValue()).floatValue())))
        continue; 
      if (target == null) {
        target = entity;
        distance = mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
        continue;
      } 
      if (entity instanceof EntityPlayer && DamageUtil.isArmorLow((EntityPlayer)entity, 18)) {
        target = entity;
        break;
      } 
      if (this.targetMode.getValue() == TargetMode.SMART && EntityUtil.getHealth(entity) < ((Float)this.health.getValue()).floatValue()) {
        target = entity;
        break;
      } 
      if (this.targetMode.getValue() != TargetMode.HEALTH && mc.player.getDistanceSq(entity) < distance) {
        target = entity;
        distance = mc.player.getDistanceSq(entity);
        maxHealth = EntityUtil.getHealth(entity);
      } 
      if (this.targetMode.getValue() != TargetMode.HEALTH || EntityUtil.getHealth(entity) >= maxHealth)
        continue; 
      target = entity;
      distance = mc.player.getDistanceSq(entity);
      maxHealth = EntityUtil.getHealth(entity);
    } 
    return target;
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.info.getValue()).booleanValue() && target instanceof EntityPlayer)
      return target.getName(); 
    return null;
  }
  
  public enum Settings {
    MAIN, TARGETS;
  }
  
  public enum TargetMode {
    FOCUS, CLOSEST, HEALTH, SMART;
  }
}
