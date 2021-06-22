package me.alpha432.oyvey.features.modules.movement;

import java.util.List;
import java.util.Objects;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LongJump extends Module {
  private final Setting<Integer> timeout = register(new Setting("TimeOut", Integer.valueOf(2000), Integer.valueOf(0), Integer.valueOf(5000)));
  
  private final Setting<Float> boost = register(new Setting("Boost", Float.valueOf(4.48F), Float.valueOf(1.0F), Float.valueOf(20.0F)));
  
  private final Setting<Boolean> instantDisable = register(new Setting("InstantOff", Boolean.valueOf(false)));
  
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.Direct));
  
  private final Setting<Boolean> lagOff = register(new Setting("LagOff", Boolean.valueOf(false)));
  
  private final Setting<Boolean> autoOff = register(new Setting("AutoOff", Boolean.valueOf(false)));
  
  private final Setting<Boolean> disableStrafe = register(new Setting("DisableStrafe", Boolean.valueOf(false)));
  
  private final Setting<Boolean> strafeOff = register(new Setting("StrafeOff", Boolean.valueOf(false)));
  
  private final Setting<Boolean> step = register(new Setting("SetStep", Boolean.valueOf(false)));
  
  private final Timer timer = new Timer();
  
  private int stage;
  
  private int lastHDistance;
  
  private int airTicks;
  
  private int headStart;
  
  private int groundTicks;
  
  private double moveSpeed;
  
  private double lastDist;
  
  private boolean isSpeeding;
  
  private boolean beganJump = false;
  
  private boolean ableJump;
  
  private int lastMotionX;
  
  private int lastMotionZ;
  
  public boolean isAbleJump() {
    return this.ableJump;
  }
  
  public LongJump() {
    super("LongJump", "lj", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onEnable() {
    this.timer.reset();
    this.headStart = 4;
    this.groundTicks = 0;
    this.stage = 0;
    this.beganJump = false;
    if (Strafe.getInstance().isOn() && ((Boolean)this.disableStrafe.getValue()).booleanValue())
      Strafe.getInstance().disable(); 
  }
  
  public void onDisable() {
    OyVey.timerManager.setTimer(1.0F);
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (((Boolean)this.lagOff.getValue()).booleanValue() && event.getPacket() instanceof net.minecraft.network.play.server.SPacketPlayerPosLook)
      disable(); 
    this.ableJump = (this.mode.getValue() == Mode.Instant || this.mode.getValue() == Mode.Crazy);
    SPacketEntityVelocity packet = (SPacketEntityVelocity)event.getPacket();
    this.lastMotionX = packet.getMotionX();
    this.lastMotionZ = packet.getMotionZ();
    double motionX = packet.getMotionX() / 8000.0D;
    double motionZ = packet.getMotionZ() / 8000.0D;
    if (this.mode.getValue() == Mode.Motion || this.mode.getValue() == Mode.Crazy)
      mc.player.setVelocity(motionX, mc.player.motionY, motionZ); 
    if (((Boolean)this.instantDisable.getValue()).booleanValue())
      disable(); 
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (event.getStage() != 0)
      return; 
    if (((Boolean)this.instantDisable.getValue()).booleanValue())
      disable(); 
    if (!this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
      event.setX(0.0D);
      event.setY(0.0D);
      event.setZ(0.0D);
      return;
    } 
    if (((Boolean)this.step.getValue()).booleanValue())
      mc.player.stepHeight = 0.6F; 
    doVirtue(event);
  }
  
  @SubscribeEvent
  public void onTickEvent(TickEvent.ClientTickEvent event) {
    if (Feature.fullNullCheck() || event.phase != TickEvent.Phase.START)
      return; 
    if (Strafe.getInstance().isOn() && ((Boolean)this.strafeOff.getValue()).booleanValue()) {
      disable();
      return;
    } 
    if (this.mode.getValue() == Mode.Tick)
      doNormal((UpdateWalkingPlayerEvent)null); 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0)
      return; 
    if (!this.timer.passedMs(((Integer)this.timeout.getValue()).intValue())) {
      event.setCanceled(true);
      return;
    } 
    doNormal(event);
  }
  
  private void doNormal(UpdateWalkingPlayerEvent event) {
    float direction;
    float xDir;
    float zDir;
    EntityPlayerSP player14;
    EntityPlayerSP player15;
    if (((Boolean)this.autoOff.getValue()).booleanValue() && this.beganJump && mc.player.onGround) {
      disable();
      return;
    } 
    switch ((Mode)this.mode.getValue()) {
      case Virtue:
        if (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) {
          double xDist = mc.player.posX - mc.player.prevPosX;
          double zDist = mc.player.posZ - mc.player.prevPosZ;
          this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
          break;
        } 
        event.setCanceled(true);
        break;
      case Tick:
        if (event != null)
          return; 
      case Direct:
        if (EntityUtil.isInLiquid() || EntityUtil.isOnLiquid())
          break; 
        if (mc.player.onGround)
          this.lastHDistance = 0; 
        direction = mc.player.rotationYaw + ((mc.player.moveForward < 0.0F) ? '´' : false) + ((mc.player.moveStrafing > 0.0F) ? (-90.0F * ((mc.player.moveForward < 0.0F) ? -0.5F : ((mc.player.moveForward > 0.0F) ? 0.5F : 1.0F))) : 0.0F) - ((mc.player.moveStrafing < 0.0F) ? (-90.0F * ((mc.player.moveForward < 0.0F) ? -0.5F : ((mc.player.moveForward > 0.0F) ? 0.5F : 1.0F))) : 0.0F);
        xDir = (float)Math.cos((direction + 90.0F) * Math.PI / 180.0D);
        zDir = (float)Math.sin((direction + 90.0F) * Math.PI / 180.0D);
        if (!mc.player.collidedVertically) {
          this.airTicks++;
          this.isSpeeding = true;
          if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(0.0D, 2.147483647E9D, 0.0D, false)); 
          this.groundTicks = 0;
          if (!mc.player.collidedVertically) {
            if (mc.player.motionY == -0.07190068807140403D) {
              EntityPlayerSP player = mc.player;
              player.motionY *= 0.3499999940395355D;
            } else if (mc.player.motionY == -0.10306193759436909D) {
              EntityPlayerSP player2 = mc.player;
              player2.motionY *= 0.550000011920929D;
            } else if (mc.player.motionY == -0.13395038817442878D) {
              EntityPlayerSP player3 = mc.player;
              player3.motionY *= 0.6700000166893005D;
            } else if (mc.player.motionY == -0.16635183030382D) {
              EntityPlayerSP player4 = mc.player;
              player4.motionY *= 0.6899999976158142D;
            } else if (mc.player.motionY == -0.19088711097794803D) {
              EntityPlayerSP player5 = mc.player;
              player5.motionY *= 0.7099999785423279D;
            } else if (mc.player.motionY == -0.21121925191528862D) {
              EntityPlayerSP player6 = mc.player;
              player6.motionY *= 0.20000000298023224D;
            } else if (mc.player.motionY == -0.11979897632390576D) {
              EntityPlayerSP player7 = mc.player;
              player7.motionY *= 0.9300000071525574D;
            } else if (mc.player.motionY == -0.18758479151225355D) {
              EntityPlayerSP player8 = mc.player;
              player8.motionY *= 0.7200000286102295D;
            } else if (mc.player.motionY == -0.21075983825251726D) {
              EntityPlayerSP player9 = mc.player;
              player9.motionY *= 0.7599999904632568D;
            } 
            if (mc.player.motionY < -0.2D && mc.player.motionY > -0.24D) {
              EntityPlayerSP player10 = mc.player;
              player10.motionY *= 0.7D;
            } 
            if (mc.player.motionY < -0.25D && mc.player.motionY > -0.32D) {
              EntityPlayerSP player11 = mc.player;
              player11.motionY *= 0.8D;
            } 
            if (mc.player.motionY < -0.35D && mc.player.motionY > -0.8D) {
              EntityPlayerSP player12 = mc.player;
              player12.motionY *= 0.98D;
            } 
            if (mc.player.motionY < -0.8D && mc.player.motionY > -1.6D) {
              EntityPlayerSP player13 = mc.player;
              player13.motionY *= 0.99D;
            } 
          } 
          OyVey.timerManager.setTimer(0.85F);
          double[] speedVals = { 
              0.420606D, 0.417924D, 0.415258D, 0.412609D, 0.409977D, 0.407361D, 0.404761D, 0.402178D, 0.399611D, 0.39706D, 
              0.394525D, 0.392D, 0.3894D, 0.38644D, 0.383655D, 0.381105D, 0.37867D, 0.37625D, 0.37384D, 0.37145D, 
              0.369D, 0.3666D, 0.3642D, 0.3618D, 0.35945D, 0.357D, 0.354D, 0.351D, 0.348D, 0.345D, 
              0.342D, 0.339D, 0.336D, 0.333D, 0.33D, 0.327D, 0.324D, 0.321D, 0.318D, 0.315D, 
              0.312D, 0.309D, 0.307D, 0.305D, 0.303D, 0.3D, 0.297D, 0.295D, 0.293D, 0.291D, 
              0.289D, 0.287D, 0.285D, 0.283D, 0.281D, 0.279D, 0.277D, 0.275D, 0.273D, 0.271D, 
              0.269D, 0.267D, 0.265D, 0.263D, 0.261D, 0.259D, 0.257D, 0.255D, 0.253D, 0.251D, 
              0.249D, 0.247D, 0.245D, 0.243D, 0.241D, 0.239D, 0.237D };
          if (mc.gameSettings.keyBindForward.pressed) {
            try {
              mc.player.motionX = xDir * speedVals[this.airTicks - 1] * 3.0D;
              mc.player.motionZ = zDir * speedVals[this.airTicks - 1] * 3.0D;
            } catch (ArrayIndexOutOfBoundsException e) {
              return;
            } 
            break;
          } 
          mc.player.motionX = 0.0D;
          mc.player.motionZ = 0.0D;
          break;
        } 
        OyVey.timerManager.setTimer(1.0F);
        this.airTicks = 0;
        this.groundTicks++;
        this.headStart--;
        player14 = mc.player;
        player14.motionX /= 13.0D;
        player15 = mc.player;
        player15.motionZ /= 13.0D;
        if (this.groundTicks == 1) {
          updatePosition(mc.player.posX, mc.player.posY, mc.player.posZ);
          updatePosition(mc.player.posX + 0.0624D, mc.player.posY, mc.player.posZ);
          updatePosition(mc.player.posX, mc.player.posY + 0.419D, mc.player.posZ);
          updatePosition(mc.player.posX + 0.0624D, mc.player.posY, mc.player.posZ);
          updatePosition(mc.player.posX, mc.player.posY + 0.419D, mc.player.posZ);
          break;
        } 
        if (this.groundTicks > 2) {
          this.groundTicks = 0;
          mc.player.motionX = xDir * 0.3D;
          mc.player.motionZ = zDir * 0.3D;
          mc.player.motionY = 0.42399999499320984D;
          this.beganJump = true;
        } 
        break;
    } 
  }
  
  private void doVirtue(MoveEvent event) {
    if (this.mode.getValue() == Mode.Virtue && (mc.player.moveForward != 0.0F || (mc.player.moveStrafing != 0.0F && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()))) {
      if (this.stage == 0) {
        this.moveSpeed = ((Float)this.boost.getValue()).floatValue() * getBaseMoveSpeed();
      } else {
        event.setY(mc.player.motionY = 0.42D);
        this.moveSpeed *= 2.149D;
        if (this.stage == 2) {
          double difference = 0.66D * (this.lastDist - getBaseMoveSpeed());
          this.moveSpeed = this.lastDist - difference;
        } else {
          this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
        } 
      } 
      setMoveSpeed(event, this.moveSpeed = Math.max(getBaseMoveSpeed(), this.moveSpeed));
      List<AxisAlignedBB> collidingList = mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, mc.player.motionY, 0.0D));
      List<AxisAlignedBB> collidingList2 = mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(0.0D, -0.4D, 0.0D));
      if (!mc.player.collidedVertically && (collidingList.size() > 0 || collidingList2.size() > 0))
        event.setY(mc.player.motionY = -0.001D); 
      this.stage++;
    } else if (this.stage > 0) {
      disable();
    } 
  }
  
  private void invalidPacket() {
    updatePosition(0.0D, 2.147483647E9D, 0.0D);
  }
  
  private void updatePosition(double x, double y, double z) {
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(x, y, z, mc.player.onGround));
  }
  
  private Block getBlock(BlockPos pos) {
    return mc.world.getBlockState(pos).getBlock();
  }
  
  private double getDistance(EntityPlayer player, double distance) {
    List<AxisAlignedBB> boundingBoxes = player.world.getCollisionBoxes((Entity)player, player.getEntityBoundingBox().offset(0.0D, -distance, 0.0D));
    if (boundingBoxes.isEmpty())
      return 0.0D; 
    double y = 0.0D;
    for (AxisAlignedBB boundingBox : boundingBoxes) {
      if (boundingBox.maxY > y)
        y = boundingBox.maxY; 
    } 
    return player.posY - y;
  }
  
  private void setMoveSpeed(MoveEvent event, double speed) {
    MovementInput movementInput = mc.player.movementInput;
    double forward = movementInput.moveForward;
    double strafe = movementInput.moveStrafe;
    float yaw = mc.player.rotationYaw;
    if (forward == 0.0D && strafe == 0.0D) {
      event.setX(0.0D);
      event.setZ(0.0D);
    } else {
      if (forward != 0.0D) {
        if (strafe > 0.0D) {
          yaw += ((forward > 0.0D) ? -45 : 45);
        } else if (strafe < 0.0D) {
          yaw += ((forward > 0.0D) ? 45 : -45);
        } 
        strafe = 0.0D;
        if (forward > 0.0D) {
          forward = 1.0D;
        } else if (forward < 0.0D) {
          forward = -1.0D;
        } 
      } 
      double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
      double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
      event.setX(forward * speed * cos + strafe * speed * sin);
      event.setZ(forward * speed * sin - strafe * speed * cos);
    } 
  }
  
  private double getBaseMoveSpeed() {
    double baseSpeed = 0.2873D;
    if (mc.player != null && mc.player.isPotionActive(MobEffects.SPEED)) {
      int amplifier = ((PotionEffect)Objects.<PotionEffect>requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getAmplifier();
      baseSpeed *= 1.0D + 0.2D * (amplifier + 1);
    } 
    return baseSpeed;
  }
  
  public enum Mode {
    Virtue, Direct, Tick, Motion, Instant, Crazy;
  }
}
