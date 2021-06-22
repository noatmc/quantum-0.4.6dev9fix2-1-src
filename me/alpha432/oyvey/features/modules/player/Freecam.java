package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.MathUtil;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Freecam extends Module {
  public Setting<Double> speed;
  
  public Setting<Boolean> view;
  
  public Setting<Boolean> packet;
  
  public Setting<Boolean> disable;
  
  public Freecam() {
    super("Freecam", "Look around freely.", Module.Category.PLAYER, true, false, false);
    this.speed = register(new Setting("Speed", Double.valueOf(0.5D), Double.valueOf(0.1D), Double.valueOf(5.0D)));
    this.view = register(new Setting("3D", Boolean.valueOf(false)));
    this.packet = register(new Setting("Packet", Boolean.valueOf(true)));
    this.disable = register(new Setting("Logout/Off", Boolean.valueOf(true)));
    setInstance();
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public static Freecam getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Freecam(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    if (!Feature.fullNullCheck()) {
      this.oldBoundingBox = mc.player.getEntityBoundingBox();
      mc.player.setEntityBoundingBox(new AxisAlignedBB(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.posX, mc.player.posY, mc.player.posZ));
      if (mc.player.getRidingEntity() != null) {
        this.riding = mc.player.getRidingEntity();
        mc.player.dismountRidingEntity();
      } 
      (this.entity = new EntityOtherPlayerMP((World)mc.world, mc.getSession().getProfile())).copyLocationAndAnglesFrom((Entity)mc.player);
      this.entity.rotationYaw = mc.player.rotationYaw;
      this.entity.rotationYawHead = mc.player.rotationYawHead;
      this.entity.inventory.copyInventory(mc.player.inventory);
      mc.world.addEntityToWorld(69420, (Entity)this.entity);
      this.position = mc.player.getPositionVector();
      this.yaw = mc.player.rotationYaw;
      this.pitch = mc.player.rotationPitch;
      mc.player.noClip = true;
    } 
  }
  
  public void onDisable() {
    if (!Feature.fullNullCheck()) {
      mc.player.setEntityBoundingBox(this.oldBoundingBox);
      if (this.riding != null)
        mc.player.startRiding(this.riding, true); 
      if (this.entity != null)
        mc.world.removeEntity((Entity)this.entity); 
      if (this.position != null)
        mc.player.setPosition(this.position.x, this.position.y, this.position.z); 
      mc.player.rotationYaw = this.yaw;
      mc.player.rotationPitch = this.pitch;
      mc.player.noClip = false;
    } 
  }
  
  public void onUpdate() {
    mc.player.noClip = true;
    mc.player.setVelocity(0.0D, 0.0D, 0.0D);
    mc.player.jumpMovementFactor = ((Double)this.speed.getValue()).floatValue();
    double[] dir = MathUtil.directionSpeed(((Double)this.speed.getValue()).doubleValue());
    if (mc.player.movementInput.moveStrafe != 0.0F || mc.player.movementInput.moveForward != 0.0F) {
      mc.player.motionX = dir[0];
      mc.player.motionZ = dir[1];
    } else {
      mc.player.motionX = 0.0D;
      mc.player.motionZ = 0.0D;
    } 
    mc.player.setSprinting(false);
    if (((Boolean)this.view.getValue()).booleanValue() && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown())
      mc.player.motionY = ((Double)this.speed.getValue()).doubleValue() * -MathUtil.degToRad(mc.player.rotationPitch) * mc.player.movementInput.moveForward; 
    if (mc.gameSettings.keyBindJump.isKeyDown()) {
      EntityPlayerSP player = mc.player;
      player.motionY += ((Double)this.speed.getValue()).doubleValue();
    } 
    if (mc.gameSettings.keyBindSneak.isKeyDown()) {
      EntityPlayerSP player2 = mc.player;
      player2.motionY -= ((Double)this.speed.getValue()).doubleValue();
    } 
  }
  
  public void onLogout() {
    if (((Boolean)this.disable.getValue()).booleanValue())
      disable(); 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && (event.getPacket() instanceof net.minecraft.network.play.client.CPacketPlayer || event.getPacket() instanceof net.minecraft.network.play.client.CPacketInput))
      event.setCanceled(true); 
  }
  
  private static Freecam INSTANCE = new Freecam();
  
  private AxisAlignedBB oldBoundingBox;
  
  private EntityOtherPlayerMP entity;
  
  private Vec3d position;
  
  private Entity riding;
  
  private float yaw;
  
  private float pitch;
}
