package me.alpha432.oyvey.features.modules.movement;

import java.text.DecimalFormat;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;

public class StepTwo extends Module {
  private static StepTwo instance;
  
  Setting<Double> height = register(new Setting("Height", Double.valueOf(2.5D), Double.valueOf(0.5D), Double.valueOf(2.5D)));
  
  Setting<Mode> mode = register(new Setting("Mode", Mode.Vanilla));
  
  private int ticks = 0;
  
  public StepTwo() {
    super("StepT", "s", Module.Category.MOVEMENT, true, false, false);
    instance = this;
  }
  
  public static StepTwo getInstance() {
    if (instance == null)
      instance = new StepTwo(); 
    return instance;
  }
  
  public void onToggle() {
    mc.player.stepHeight = 0.6F;
  }
  
  public void onUpdate() {
    if (mc.world == null || mc.player == null)
      return; 
    if (mc.player.isInWater() || mc.player.isInLava() || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown())
      return; 
    if (((Speed)OyVey.moduleManager.getModuleByClass(Speed.class)).isEnabled())
      return; 
    if (this.mode.getValue() == Mode.Normal) {
      double[] dir = forward(0.1D);
      boolean twofive = false;
      boolean two = false;
      boolean onefive = false;
      boolean one = false;
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4D, dir[1])).isEmpty())
        twofive = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9D, dir[1])).isEmpty())
        two = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4D, dir[1])).isEmpty())
        onefive = true; 
      if (mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0D, dir[1])).isEmpty() && !mc.world.getCollisionBoxes((Entity)mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6D, dir[1])).isEmpty())
        one = true; 
      if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0F || mc.player.moveStrafing != 0.0F) && mc.player.onGround) {
        if (one && ((Double)this.height.getValue()).doubleValue() >= 1.0D) {
          double[] oneOffset = { 0.42D, 0.753D };
          for (int i = 0; i < oneOffset.length; i++)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + oneOffset[i], mc.player.posZ, mc.player.onGround)); 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
          this.ticks = 1;
        } 
        if (onefive && ((Double)this.height.getValue()).doubleValue() >= 1.5D) {
          double[] oneFiveOffset = { 0.42D, 0.75D, 1.0D, 1.16D, 1.23D, 1.2D };
          for (int i = 0; i < oneFiveOffset.length; i++)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + oneFiveOffset[i], mc.player.posZ, mc.player.onGround)); 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5D, mc.player.posZ);
          this.ticks = 1;
        } 
        if (two && ((Double)this.height.getValue()).doubleValue() >= 2.0D) {
          double[] twoOffset = { 0.42D, 0.78D, 0.63D, 0.51D, 0.9D, 1.21D, 1.45D, 1.43D };
          for (int i = 0; i < twoOffset.length; i++)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + twoOffset[i], mc.player.posZ, mc.player.onGround)); 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0D, mc.player.posZ);
          this.ticks = 2;
        } 
        if (twofive && ((Double)this.height.getValue()).doubleValue() >= 2.5D) {
          double[] twoFiveOffset = { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D };
          for (int i = 0; i < twoFiveOffset.length; i++)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + twoFiveOffset[i], mc.player.posZ, mc.player.onGround)); 
          mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5D, mc.player.posZ);
          this.ticks = 2;
        } 
      } 
    } 
    if (this.mode.getValue() == Mode.Vanilla) {
      DecimalFormat df = new DecimalFormat("#");
      mc.player.stepHeight = Float.parseFloat(df.format(this.height.getValue()));
    } 
  }
  
  public String getDisplayInfo() {
    return this.mode.currentEnumName();
  }
  
  public void onDisable() {
    mc.player.stepHeight = 0.5F;
  }
  
  public static double[] forward(double speed) {
    float forward = mc.player.movementInput.moveForward;
    float side = mc.player.movementInput.moveStrafe;
    float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
    if (forward != 0.0F) {
      if (side > 0.0F) {
        yaw += ((forward > 0.0F) ? -45 : 45);
      } else if (side < 0.0F) {
        yaw += ((forward > 0.0F) ? 45 : -45);
      } 
      side = 0.0F;
      if (forward > 0.0F) {
        forward = 1.0F;
      } else if (forward < 0.0F) {
        forward = -1.0F;
      } 
    } 
    double sin = Math.sin(Math.toRadians((yaw + 90.0F)));
    double cos = Math.cos(Math.toRadians((yaw + 90.0F)));
    double posX = forward * speed * cos + side * speed * sin;
    double posZ = forward * speed * sin - side * speed * cos;
    return new double[] { posX, posZ };
  }
  
  public enum Mode {
    Vanilla, Normal;
  }
}
