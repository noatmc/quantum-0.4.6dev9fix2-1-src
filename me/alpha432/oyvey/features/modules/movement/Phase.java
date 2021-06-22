package me.alpha432.oyvey.features.modules.movement;

import com.google.common.eventbus.Subscribe;
import io.netty.util.internal.ConcurrentSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.PushEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.EntityUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Phase extends Module {
  public Setting<Mode> mode = register(new Setting("Mode", Mode.Phase));
  
  public Setting<Integer> xMove = register(new Setting("MoveX", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Integer> yMove = register(new Setting("MoveY", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1000), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> sneakpackets = register(new Setting("SneakPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> extra = register(new Setting("ExtraPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Integer> offset = register(new Setting("ExtraOffset", Integer.valueOf(1337), Integer.valueOf(-1337), Integer.valueOf(1337), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.extra.getValue()).booleanValue())));
  
  public Setting<Boolean> fallPacket = register(new Setting("FallPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> teleporter = register(new Setting("Teleport", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> boundingBox = register(new Setting("BoundingBox", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Integer> teleportConfirm = register(new Setting("TPConfirm", Integer.valueOf(2), Integer.valueOf(0), Integer.valueOf(4), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> ultraPacket = register(new Setting("DoublePacket", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> updates = register(new Setting("Update", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> setOnMove = register(new Setting("SetMove", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> cliperino = register(new Setting("NoClip", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.setOnMove.getValue()).booleanValue())));
  
  public Setting<Boolean> scanPackets = register(new Setting("ScanPackets", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> resetConfirm = register(new Setting("Reset", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> posLook = register(new Setting("PosLook", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase)));
  
  public Setting<Boolean> cancel = register(new Setting("Cancel", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  public Setting<Boolean> cancelType = register(new Setting("SetYaw", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.posLook.getValue()).booleanValue() && ((Boolean)this.cancel.getValue()).booleanValue())));
  
  public Setting<Boolean> onlyY = register(new Setting("OnlyY", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  public Setting<Integer> cancelPacket = register(new Setting("Packets", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(20), v -> (this.mode.getValue() == Mode.Phase && ((Boolean)this.posLook.getValue()).booleanValue())));
  
  private final Set<CPacketPlayer> packets = (Set<CPacketPlayer>)new ConcurrentSet();
  
  private static Phase INSTANCE = new Phase();
  
  private boolean teleport = true;
  
  private int teleportIds = 0;
  
  private int posLookPackets;
  
  public Setting<Boolean> flight = register(new Setting("Flight", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Integer> flightMode = register(new Setting("FMode", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(1), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> doAntiFactor = register(new Setting("Factorize", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Double> antiFactor = register(new Setting("AntiFactor", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(3.0D), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Double> extraFactor = register(new Setting("ExtraFactor", Double.valueOf(1.0D), Double.valueOf(0.1D), Double.valueOf(3.0D), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> strafeFactor = register(new Setting("StrafeFactor", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Integer> loops = register(new Setting("Loops", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(10), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> clearTeleMap = register(new Setting("ClearMap", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Integer> mapTime = register(new Setting("ClearTime", Integer.valueOf(30), Integer.valueOf(1), Integer.valueOf(500), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> clearIDs = register(new Setting("ClearIDs", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> setYaw = register(new Setting("SetYaw", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> setID = register(new Setting("SetID", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> setMove = register(new Setting("SetMove", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> nocliperino = register(new Setting("NoClip", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> sendTeleport = register(new Setting("Teleport", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> resetID = register(new Setting("ResetID", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> setPos = register(new Setting("SetPos", Boolean.valueOf(false), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  public Setting<Boolean> invalidPacket = register(new Setting("InvalidPacket", Boolean.valueOf(true), v -> (this.mode.getValue() == Mode.Packetfly)));
  
  private final Map<Integer, IDtime> teleportmap = new ConcurrentHashMap<>();
  
  private int flightCounter = 0;
  
  private int teleportID = 0;
  
  public Phase() {
    super("Packetfly", "phase && pfly", Module.Category.MOVEMENT, true, false, false);
    setInstance();
  }
  
  public static Phase getInstance() {
    if (INSTANCE == null)
      INSTANCE = new Phase(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onTick() {
    if (this.mode.getValue() == Mode.Packetfly)
      this.teleportmap.entrySet().removeIf(idTime -> (((Boolean)this.clearTeleMap.getValue()).booleanValue() && ((IDtime)idTime.getValue()).getTimer().passedS(((Integer)this.mapTime.getValue()).intValue()))); 
  }
  
  @Subscribe
  public void onUpdate() {
    if (((Boolean)this.sneakpackets.getValue()).booleanValue()) {
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    } 
  }
  
  public void onDisable() {
    if (this.mode.getValue() == Mode.Phase) {
      this.packets.clear();
      this.posLookPackets = 0;
      if (mc.player != null) {
        if (((Boolean)this.resetConfirm.getValue()).booleanValue())
          this.teleportIds = 0; 
        mc.player.noClip = false;
      } 
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (this.mode.getValue() == Mode.Phase) {
      if (((Boolean)this.setOnMove.getValue()).booleanValue() && event.getStage() == 0 && !mc.isSingleplayer() && this.mode.getValue() == Mode.Phase) {
        event.setX(mc.player.motionX);
        event.setY(mc.player.motionY);
        event.setZ(mc.player.motionZ);
        if (((Boolean)this.cliperino.getValue()).booleanValue())
          mc.player.noClip = true; 
      } 
      if (event.getStage() != 0 || mc.isSingleplayer() || this.mode.getValue() != Mode.Phase)
        return; 
      if (!((Boolean)this.boundingBox.getValue()).booleanValue() && !((Boolean)this.updates.getValue()).booleanValue())
        doPhase(event); 
    } 
    if (this.mode.getValue() == Mode.Packetfly && (
      (Boolean)this.setMove.getValue()).booleanValue() && this.flightCounter != 0) {
      event.setX(mc.player.motionX);
      event.setY(mc.player.motionY);
      event.setZ(mc.player.motionZ);
      if (((Boolean)this.nocliperino.getValue()).booleanValue() && checkHitBoxes())
        mc.player.noClip = true; 
    } 
  }
  
  @SubscribeEvent
  public void onPush(PushEvent event) {
    if (this.mode.getValue() == Mode.Phase && 
      event.getStage() == 1)
      event.setCanceled(true); 
  }
  
  @SubscribeEvent
  public void onPushOutOfBlocks(PushEvent event) {
    if (this.mode.getValue() == Mode.Packetfly && 
      event.getStage() == 1)
      event.setCanceled(true); 
  }
  
  @SubscribeEvent
  public void onMove(UpdateWalkingPlayerEvent event) {
    if (this.mode.getValue() == Mode.Phase) {
      if (fullNullCheck() || event.getStage() != 0 || this.mode.getValue() != Mode.Phase)
        return; 
      if (((Boolean)this.boundingBox.getValue()).booleanValue()) {
        doBoundingBox();
      } else if (((Boolean)this.updates.getValue()).booleanValue()) {
        doPhase((MoveEvent)null);
      } 
    } 
    if (this.mode.getValue() == Mode.Packetfly) {
      if (event.getStage() == 1)
        return; 
      mc.player.setVelocity(0.0D, 0.0D, 0.0D);
      double speed = 0.0D;
      boolean checkCollisionBoxes = checkHitBoxes();
      speed = (mc.player.movementInput.jump && (checkCollisionBoxes || !EntityUtil.isMoving())) ? ((((Boolean)this.flight.getValue()).booleanValue() && !checkCollisionBoxes) ? ((((Integer)this.flightMode.getValue()).intValue() == 0) ? (resetCounter(10) ? -0.032D : 0.062D) : (resetCounter(20) ? -0.032D : 0.062D)) : 0.062D) : (mc.player.movementInput.sneak ? -0.062D : (!checkCollisionBoxes ? (resetCounter(4) ? (((Boolean)this.flight.getValue()).booleanValue() ? -0.04D : 0.0D) : 0.0D) : 0.0D));
      if (((Boolean)this.doAntiFactor.getValue()).booleanValue() && checkCollisionBoxes && EntityUtil.isMoving() && speed != 0.0D)
        speed /= ((Double)this.antiFactor.getValue()).doubleValue(); 
      double[] strafing = getMotion((((Boolean)this.strafeFactor.getValue()).booleanValue() && checkCollisionBoxes) ? 0.031D : 0.26D);
      for (int i = 1; i < ((Integer)this.loops.getValue()).intValue() + 1; i++) {
        mc.player.motionX = strafing[0] * i * ((Double)this.extraFactor.getValue()).doubleValue();
        mc.player.motionY = speed * i;
        mc.player.motionZ = strafing[1] * i * ((Double)this.extraFactor.getValue()).doubleValue();
        sendPackets(mc.player.motionX, mc.player.motionY, mc.player.motionZ, ((Boolean)this.sendTeleport.getValue()).booleanValue());
      } 
    } 
  }
  
  private void doPhase(MoveEvent event) {
    if (this.mode.getValue() == Mode.Phase) {
      if (!((Boolean)this.boundingBox.getValue()).booleanValue()) {
        double[] dirSpeed = getMotion(this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D));
        double posX = mc.player.posX + dirSpeed[0];
        double posY = mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D)) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? (((Integer)this.yMove.getValue()).intValue() / 10000.0D) : ((((Integer)this.yMove.getValue()).intValue() - 1) / 10000.0D)) : 2.0E-8D);
        double posZ = mc.player.posZ + dirSpeed[1];
        CPacketPlayer.PositionRotation packetPlayer = new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw, mc.player.rotationPitch, false);
        this.packets.add(packetPlayer);
        mc.player.connection.sendPacket((Packet)packetPlayer);
        if (((Integer)this.teleportConfirm.getValue()).intValue() != 3) {
          mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds - 1));
          this.teleportIds++;
        } 
        if (((Boolean)this.extra.getValue()).booleanValue()) {
          CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(mc.player.posX, ((Integer)this.offset.getValue()).intValue() + mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true);
          this.packets.add(packet);
          mc.player.connection.sendPacket((Packet)packet);
        } 
        if (((Integer)this.teleportConfirm.getValue()).intValue() != 1) {
          mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds + 1));
          this.teleportIds++;
        } 
        if (((Boolean)this.ultraPacket.getValue()).booleanValue()) {
          CPacketPlayer.PositionRotation packet2 = new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw, mc.player.rotationPitch, false);
          this.packets.add(packet2);
          mc.player.connection.sendPacket((Packet)packet2);
        } 
        if (((Integer)this.teleportConfirm.getValue()).intValue() == 4) {
          mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(this.teleportIds));
          this.teleportIds++;
        } 
        if (((Boolean)this.fallPacket.getValue()).booleanValue())
          mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING)); 
        mc.player.setPosition(posX, posY, posZ);
        boolean bl = this.teleport = (!((Boolean)this.teleporter.getValue()).booleanValue() || !this.teleport);
        if (event != null) {
          event.setX(0.0D);
          event.setY(0.0D);
          event.setX(0.0D);
        } else {
          mc.player.motionX = 0.0D;
          mc.player.motionY = 0.0D;
          mc.player.motionZ = 0.0D;
        } 
      } 
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SNEAKING));
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
    } 
  }
  
  private void doBoundingBox() {
    if (this.mode.getValue() == Mode.Phase) {
      double[] dirSpeed = getMotion(this.teleport ? 0.02250000089406967D : 0.02239999920129776D);
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.player.posZ + dirSpeed[1], mc.player.rotationYaw, mc.player.rotationPitch, false));
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.PositionRotation(mc.player.posX, -1337.0D, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
      mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
      mc.player.setPosition(mc.player.posX + dirSpeed[0], mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 1.0E-8D) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? 0.0625D : 0.0624D) : 2.0E-8D), mc.player.posZ + dirSpeed[1]);
      this.teleport = !this.teleport;
      mc.player.motionZ = 0.0D;
      mc.player.motionY = 0.0D;
      mc.player.motionX = 0.0D;
      mc.player.noClip = this.teleport;
    } 
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (this.mode.getValue() == Mode.Packetfly) {
      CPacketPlayer packet;
      if (event.getPacket() instanceof CPacketPlayer && !this.packets.remove(packet = (CPacketPlayer)event.getPacket()))
        event.setCanceled(true); 
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Receive event) {
    if (this.mode.getValue() == Mode.Phase && (
      (Boolean)this.posLook.getValue()).booleanValue() && event.getPacket() instanceof SPacketPlayerPosLook) {
      SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
      if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain)) {
        if (this.teleportIds <= 0)
          this.teleportIds = packet.getTeleportId(); 
        if (((Boolean)this.cancel.getValue()).booleanValue() && ((Boolean)this.cancelType.getValue()).booleanValue()) {
          packet.yaw = mc.player.rotationYaw;
          packet.pitch = mc.player.rotationPitch;
          return;
        } 
        if (((Boolean)this.cancel.getValue()).booleanValue() && this.posLookPackets >= ((Integer)this.cancelPacket.getValue()).intValue() && (!((Boolean)this.onlyY.getValue()).booleanValue() || (!mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindRight.isKeyDown() && !mc.gameSettings.keyBindLeft.isKeyDown() && !mc.gameSettings.keyBindBack.isKeyDown()))) {
          this.posLookPackets = 0;
          event.setCanceled(true);
        } 
        this.posLookPackets++;
      } 
    } 
    if (this.mode.getValue() == Mode.Packetfly && 
      event.getPacket() instanceof SPacketPlayerPosLook && !fullNullCheck()) {
      SPacketPlayerPosLook packet = (SPacketPlayerPosLook)event.getPacket();
      BlockPos pos;
      if (mc.player.isEntityAlive() && mc.world.isBlockLoaded(pos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ), false) && !(mc.currentScreen instanceof net.minecraft.client.gui.GuiDownloadTerrain) && ((Boolean)this.clearIDs.getValue()).booleanValue())
        this.teleportmap.remove(Integer.valueOf(packet.getTeleportId())); 
      if (((Boolean)this.setYaw.getValue()).booleanValue()) {
        packet.yaw = mc.player.rotationYaw;
        packet.pitch = mc.player.rotationPitch;
      } 
      if (((Boolean)this.setID.getValue()).booleanValue())
        this.teleportID = packet.getTeleportId(); 
    } 
  }
  
  @SubscribeEvent
  public void onPacketReceive(PacketEvent.Send event) {
    if (this.mode.getValue() == Mode.Phase && (
      (Boolean)this.scanPackets.getValue()).booleanValue() && event.getPacket() instanceof CPacketPlayer) {
      CPacketPlayer packetPlayer = (CPacketPlayer)event.getPacket();
      if (this.packets.contains(packetPlayer)) {
        this.packets.remove(packetPlayer);
      } else {
        event.setCanceled(true);
      } 
    } 
  }
  
  private double[] getMotion(double speed) {
    float moveForward = mc.player.movementInput.moveForward;
    float moveStrafe = mc.player.movementInput.moveStrafe;
    float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (moveForward != 0.0F) {
      if (moveStrafe > 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? -45 : 45);
      } else if (moveStrafe < 0.0F) {
        rotationYaw += ((moveForward > 0.0F) ? 45 : -45);
      } 
      moveStrafe = 0.0F;
      if (moveForward > 0.0F) {
        moveForward = 1.0F;
      } else if (moveForward < 0.0F) {
        moveForward = -1.0F;
      } 
    } 
    double posX = moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
    double posZ = moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
    return new double[] { posX, posZ };
  }
  
  private boolean checkHitBoxes() {
    return !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().expand(-0.0625D, -0.0625D, -0.0625D)).isEmpty();
  }
  
  private boolean resetCounter(int counter) {
    if (++this.flightCounter >= counter) {
      this.flightCounter = 0;
      return true;
    } 
    return false;
  }
  
  private void sendPackets(double x, double y, double z, boolean teleport) {
    Vec3d vec = new Vec3d(x, y, z);
    Vec3d position = mc.player.getPositionVector().add(vec);
    Vec3d outOfBoundsVec = outOfBoundsVec(vec, position);
    packetSender((CPacketPlayer)new CPacketPlayer.Position(position.x, position.y, position.z, mc.player.onGround));
    if (((Boolean)this.invalidPacket.getValue()).booleanValue())
      packetSender((CPacketPlayer)new CPacketPlayer.Position(outOfBoundsVec.x, outOfBoundsVec.y, outOfBoundsVec.z, mc.player.onGround)); 
    if (((Boolean)this.setPos.getValue()).booleanValue())
      mc.player.setPosition(position.x, position.y, position.z); 
    teleportPacket(position, teleport);
  }
  
  private void teleportPacket(Vec3d pos, boolean shouldTeleport) {
    if (shouldTeleport) {
      mc.player.connection.sendPacket((Packet)new CPacketConfirmTeleport(++this.teleportID));
      this.teleportmap.put(Integer.valueOf(this.teleportID), new IDtime(pos, new Timer()));
    } 
  }
  
  private Vec3d outOfBoundsVec(Vec3d offset, Vec3d position) {
    return position.add(0.0D, 1337.0D, 0.0D);
  }
  
  private void packetSender(CPacketPlayer packet) {
    this.packets.add(packet);
    mc.player.connection.sendPacket((Packet)packet);
  }
  
  private void clean() {
    this.teleportmap.clear();
    this.flightCounter = 0;
    if (((Boolean)this.resetID.getValue()).booleanValue())
      this.teleportID = 0; 
    this.packets.clear();
  }
  
  public static class IDtime {
    private final Vec3d pos;
    
    private final Timer timer;
    
    public IDtime(Vec3d pos, Timer timer) {
      this.pos = pos;
      this.timer = timer;
      this.timer.reset();
    }
    
    public Vec3d getPos() {
      return this.pos;
    }
    
    public Timer getTimer() {
      return this.timer;
    }
  }
  
  public enum Mode {
    Phase, Packetfly, Alternative;
  }
}
